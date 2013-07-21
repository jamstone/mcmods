package mods.ifw.ghosttrain.common;

import mods.ifw.pathfinding.AStarNode;
import mods.ifw.pathfinding.AStarStatic;
import mods.ifw.pathfinding.minecart.IMinecartPathedEntity;
import mods.ifw.pathfinding.minecart.MinecartPathPlanner;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;
import java.util.LinkedList;

public class TileEntityStation extends TileEntity implements IMinecartPathedEntity {
    /**
     * my actual fields for this tile entity.
     */
    private int size = 16;

    //private ArrayList<MinecartPathStep>[] paths;
    private ChunkCoordinates[] destinations = new ChunkCoordinates[size];
    private boolean[] foundPath = new boolean[size];
    // when a player removes a destination, that location is blacklisted, and no destinations within 64 blocks will be accepted.
    private ChunkCoordinates[] blacklist = new ChunkCoordinates[size];
    private int lastDestinationIndex;
    private int lastFoundPathIndex;
    // normally the tilenetity will search for new paths in sequence, but the player can request a path get priority.
    private int pathToFindIndex;

    public ArrayList<AStarNode> path = null;
    public EntityPlayer rider = null;
    public EntityMinecart ghostTrain = null;
    public LinkedList<ChunkCoordinates> added = new LinkedList<ChunkCoordinates>();

    AStarNode lastAs = null;
    AStarNode lastlastAs = null;

    protected String customName;
    private boolean searching;
    private MinecartPathPlanner pathPlanner;

    TileEntityStation(World world) {
        worldObj = world;
        pathPlanner = new MinecartPathPlanner(worldObj, this);
    }

    public void setCustomName(String par1Str) {
        this.customName = par1Str;
    }

    public void updateEntity() {
        if (ghostTrain != null && (ghostTrain.fallDistance > 0.5 || ghostTrain.isDead)) {
            ghostTrain.setDead();
            ghostTrain = null;
        }

        if (path != null) {

            if (ghostTrain == null) {
                ghostTrain = EntityMinecart.createMinecart(worldObj, path.get(path.size() - 1).x + 1, path.get(path.size() - 1).y + 2, path.get(path.size() - 1).z + 1, 0);
                worldObj.spawnEntityInWorld(ghostTrain);
            }
            AStarNode as = path.get(path.size() - 1);
            while (as.getDistanceTo((int) ghostTrain.posX, (int) ghostTrain.posY, (int) ghostTrain.posZ) < AStarStatic.getDistanceBetween(0, 0, 0, 0, 0, 12)) {
                path.remove(path.size() - 1);

                addCartPathBlock(as.x + 1, as.y, as.z + 1, Block.blockRedstone.blockID);
                if (this.worldObj.getBlockId(as.x + 1, as.y + 1, as.z + 1) != Block.blockRedstone.blockID) {
                    worldObj.setBlock(as.x + 1, as.y + 1, as.z + 1, Block.railPowered.blockID);
                }
                if (lastlastAs != null && lastlastAs.getDirectionTo(lastAs) != lastAs.getDirectionTo(as)) {
                    worldObj.setBlock(lastAs.x + 1, lastAs.y + 1, lastAs.z + 1, Block.rail.blockID);
                }

                lastlastAs = lastAs;
                lastAs = as;
                if (path.size() < 1) {
                    path = null;
                    break;
                }
                as = path.get(path.size() - 1);
                path.remove(path.size() - 1);

                addCartPathBlock(as.x + 1, as.y, as.z + 1, Block.blockRedstone.blockID);
                if (this.worldObj.getBlockId(as.x + 1, as.y + 1, as.z + 1) != Block.blockRedstone.blockID) {
                    worldObj.setBlock(as.x + 1, as.y + 1, as.z + 1, Block.railPowered.blockID);
                }
                if (lastlastAs != null && lastlastAs.getDirectionTo(lastAs) != lastAs.getDirectionTo(as)) {
                    worldObj.setBlock(lastAs.x + 1, lastAs.y + 1, lastAs.z + 1, Block.rail.blockID);
                }

                lastlastAs = lastAs;
                lastAs = as;
                if (path.size() < 1) {
                    path = null;
                    break;
                }
                as = path.get(path.size() - 1);
            }
        }

        if (!added.isEmpty() && lastAs != null) {
            ChunkCoordinates cc = added.peekFirst();
            while (lastAs.getDistanceTo(cc.posX, cc.posY, cc.posZ) > AStarStatic.getDistanceBetween(0, 0, 0, 0, 0, 16) || path == null) {
                added.removeFirst();
                worldObj.setBlockToAir(cc.posX, cc.posY + 1, cc.posZ);
                worldObj.setBlockToAir(cc.posX, cc.posY, cc.posZ);
                if (added.size() < 1) {
                    break;
                }
                cc = added.peekFirst();
            }
        }
    }

    private void addCartPathBlock(int x, int y, int z, int blockID) {
        this.worldObj.setBlock(x, y, z, blockID);
        added.addLast(new ChunkCoordinates(x, y, z));
    }


    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
//        this.dispenserContents = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
            int j = nbttagcompound1.getByte("Slot") & 255;

//            if (j >= 0 && j < this.dispenserContents.length)
//            {
//                this.dispenserContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
//            }
        }

        if (par1NBTTagCompound.hasKey("CustomName")) {
            this.customName = par1NBTTagCompound.getString("CustomName");
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);
        NBTTagList nbttaglist = new NBTTagList();

//        for (int i = 0; i < this.dispenserContents.length; ++i)
//        {
//            if (this.dispenserContents[i] != null)
//            {
//                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
//                nbttagcompound1.setByte("Slot", (byte)i);
//                this.dispenserContents[i].writeToNBT(nbttagcompound1);
//                nbttaglist.appendTag(nbttagcompound1);
//            }
//        }
//
//        par1NBTTagCompound.setTag("Items", nbttaglist);
//
//        if (this.isInvNameLocalized())
//        {
//            par1NBTTagCompound.setString("CustomName", this.customName);
//        }
    }

    public void findPathBetween(int x1, int y1, int z1, int x2, int y2, int z2) {
        if (!searching) {
            pathPlanner.getPath(x1, y1, z1, x2, y2, z2, null);

            searching = true;
            System.out.println("Finding path.");
        }
    }

    public void findPathBETA(World par1World, int par2, int par3, int par4) {
        if (par1World.isRemote) {
            return;
        }

        // nearest biome

        BiomeGenBase targetBiome = BiomeGenBase.desert;
        System.out.println("Finding nearest " + targetBiome.biomeName);

        ChunkPosition cpos = null;

        int startX = par2;
        int startZ = par4;

        int chunkRadius = 1;
        int maxRange = 64;

        while (chunkRadius < maxRange) {
            for (int i = -chunkRadius; i < chunkRadius && chunkRadius < maxRange; i++) {
                for (int j = -chunkRadius; j < chunkRadius && chunkRadius < maxRange; j++) {
                    if (Math.abs(i) != chunkRadius && Math.abs(j) != chunkRadius) {
                        continue;
                    }
                    if (par1World.getWorldChunkManager().getBiomeGenAt(startX + i * 16, startZ + j * 16) == targetBiome) {
                        cpos = new ChunkPosition(startX + i * 16, par1World.getHeightValue(startX + i * 16, startZ + j * 16) + 4, startZ + j * 16);
                        chunkRadius = maxRange + 1;
                        break;
                    }
                }
            }
            chunkRadius++;
        }
        if (cpos != null) {
            System.out.printf("Biome located at (%d, %d) is %s.%n", cpos.x, cpos.z, targetBiome.biomeName);
        } else {
            System.out.println("failed to find " + targetBiome.biomeName);
            return;
        }

        int x = cpos.x;
        int y = cpos.y;
        int z = cpos.z;

        this.findPathBetween(par2 + 2, par3, par4, x, y, z);

//        AStarNode start = new AStarNode(par2, par3, par4, null);
//        AStarNode goal = new AStarNode(x, y, z, null);
//
//        MinecartPathWorker mcpp = new MinecartPathWorker(null);
//        mcpp.setup(par1World, start, goal, null);
//
//        ArrayList<AStarNode> path = mcpp.getPath(start, goal);

    }

    /**
     * Do not give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void onFoundPath(ArrayList<AStarNode> result) {
        searching = false;

        path = result;

        System.out.println("Found path.");
    }

    @Override
    public void onNoPathAvailable() {
        searching = false;
        path = null;

        System.out.println("Couldn't find path.");
    }
}
