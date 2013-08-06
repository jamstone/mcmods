package mods.jamstone.pathfinding;

import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Runnable worker class for finding an AstarPath
 * is prone to crashes when no path can be found.
 *
 * @author AtomicStryker, butchered by jamstone
 */

public class AStarWorker extends Thread {
    /**
     * How long a pathfinding thread is allowed to run before the path is deemed
     * impossible to calculate. A reasonably difficult path will always be finished
     * in under one millisecond. Value in milliseconds. 500L default.
     */
    private final long SEARCH_TIME_LIMIT = 500L;

    /**
     * How many cubes will the worker check for a path before giving up
     */
    protected AStarPathPlanner boss;

    public final ArrayList<AStarNode> closedNodes;
    private AStarNode startNode;
    protected AStarNode targetNode;
    private boolean allowDropping;
    protected World worldObj;
    protected long timeLimit;
    private final PriorityQueue<AStarNode> queue;

    public AStarWorker(AStarPathPlanner creator) {
        boss = creator;
        closedNodes = new ArrayList<AStarNode>();
        queue = new PriorityQueue<AStarNode>(500);
    }

    @Override
    public void run() {
        timeLimit = System.currentTimeMillis() + SEARCH_TIME_LIMIT + AStarStatic.getDistanceBetween(startNode.x, startNode.y, startNode.z, targetNode.x, targetNode.y, targetNode.z);
        ArrayList<AStarNode> result = null;
        long time = System.nanoTime();
        result = getPath(startNode, targetNode, allowDropping);
        time = System.nanoTime() - time;

        if (result == null) {
            //System.out.println(getClass()+" finished. No path.");
            System.out.println("Total time in Seconds: " + time / 1000000000D);
            boss.onNoPathAvailable();
        } else {
            //System.out.println(getClass()+" finished. Path Length: "+result.size());
            System.out.println("Total time in Seconds: " + time / 1000000000D);
            boss.onFoundPath(this, result);
        }
    }

    protected boolean shouldInterrupt() {
        return System.currentTimeMillis() > timeLimit;
    }

    /**
     * Setup some pointers for the seaching run
     *
     * @param winput World to search in
     * @param start  Starting Node
     * @param end    Target Node of the Path
     * @param mode   true if dropping more than 1 Block is allowed
     */
    public void setup(World winput, AStarNode start, AStarNode end, boolean mode) {
        worldObj = winput;
        startNode = start;
        targetNode = end;
        allowDropping = mode;
        //System.out.println("Start Node: "+start.x+", "+start.y+", "+start.z);
        //System.out.println("Target Node: "+end.x+", "+end.y+", "+end.z);
    }

    public ArrayList<AStarNode> getPath(AStarNode start, AStarNode end, boolean searchMode) {
        queue.offer(start);
        targetNode = end;

        AStarNode current = start;

        while (!current.equals(end)) {
            closedNodes.add(queue.poll());

            if (queue.isEmpty() || shouldInterrupt()) {
                //System.out.println("Path search aborted, interrupted: "+shouldInterrupt());
                return null;
            }
            current = queue.peek();
            //System.out.println("current Node is now "+current.x+", "+current.y+", "+current.z+" of cost "+current.getF());
        }

        ArrayList<AStarNode> foundpath = new ArrayList<AStarNode>();
        foundpath.add(current);
        while (current != start) {
            foundpath.add(current.parent);
            current = current.parent;
        }

        //System.out.println("Path search success, visited "+checkedCubes+" Nodes, pathpoints: "+foundpath.size()+", abs. distance "+AStarStatic.getDistanceBetweenNodes(start, end));
        return foundpath;
    }

    private void addToBinaryHeap(AStarNode input) {
        queue.offer(input);
    }
}