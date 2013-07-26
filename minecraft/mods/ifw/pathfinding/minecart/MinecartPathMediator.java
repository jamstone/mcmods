package mods.ifw.pathfinding.minecart;

import mods.ifw.pathfinding.AStarNode;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Mediates communication between the entity and the thread.
 *
 * @author AtomicStryker, heavily modified by ifw.
 */

public class MinecartPathMediator {
    private MinecartPathThread worker;
    private World worldObj;
    private IMinecartPathedEntity pathedEntity;

    private AStarNode start = null;
    private AStarNode end = null;

    public MinecartPathMediator(World world, IMinecartPathedEntity ent) {
        worldObj = world;
        pathedEntity = ent;
    }

    public boolean isBusy() {
        if (worker == null) {
            return false;
        }
        return worker.getState() != Thread.State.NEW;
    }

    public void getPath(int startx, int starty, int startz, int destx, int desty, int destz, AxisAlignedBB boundingBox) {
        start = new AStarNode(startx, starty, startz, null);
        end = new AStarNode(destx, desty, destz, null);
        getPath(start, end, boundingBox);
    }

    public synchronized void getPath(AStarNode startNode, AStarNode endNode, AxisAlignedBB boundingBox) {
        worker = new MinecartPathThread(this);
        start = startNode;
        end = endNode;
        worker.setup(worldObj, start, end, boundingBox);
        worker.start();
    }

    public void onFoundPath(MinecartPathThread aStarWorker, ArrayList<AStarNode> result) {
        if (aStarWorker.equals(worker)) // disregard solutions from abandoned workers
        {
            if (pathedEntity != null) {
                pathedEntity.onFoundPath(result);
            }
        }
        start = end = null;
    }

    public void onNoPathAvailable() {
        if (pathedEntity != null) {
            pathedEntity.onNoPathAvailable();
        }
        start = end = null;
    }

    /**
     * Passed to the pathing entity whenever the thread finds an unloaded chunk.
     *
     * @param chunkCoordIntPairs
     */
    public void onChunkQueueRequest(ArrayList<ChunkCoordIntPair> chunkCoordIntPairs) {
        if (pathedEntity != null) {
            pathedEntity.onChunkQueueRequest(chunkCoordIntPairs);
        }
    }

    /**
     * Returns the thread's current node, or null if the thread hasn't started. Used to allow the entity to keep chunks loaded for the pathfinder.
     *
     * @return
     */

    public AStarNode getCurrentNode() {
        if (isBusy()) {
            return worker.currentNode;
        }
        return null;
    }

}