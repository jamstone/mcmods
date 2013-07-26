package mods.ifw.ghosttrain.common;

import mods.ifw.pathfinding.AStarNode;
import mods.ifw.pathfinding.minecart.IMinecartPathedEntity;
import mods.ifw.pathfinding.minecart.MinecartPathMediator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ForgeChunkManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class TileEntityStation extends TileEntity implements IMinecartPathedEntity {
    /**
     * my actual fields for this tile entity.
     */
    private int size = BiomeGenBase.biomeList.length;

    //private ArrayList<MinecartPathStep>[] paths;
    private ChunkCoordinates[] destinations = new ChunkCoordinates[size];
    private Boolean[] foundPathFor = new Boolean[size];
    // when a player removes a destination, that location is blacklisted, and no destinations within 64 blocks will be accepted.
    private ChunkCoordinates[] blacklist = new ChunkCoordinates[size];
    private int lastDestinationIndex;
    private int lastFoundPathIndex;
    // normally the tilenetity will search for new paths in sequence, but the player can request a path get priority.
    private int pathToFindIndex;

    public ArrayList<AStarNode> foundPath = null;
    public LinkedList<ChunkCoordinates> added = new LinkedList<ChunkCoordinates>();

    protected String customName;

    private boolean searching = false;
    private boolean pathFailed = false;
    private ChunkCoordinates targetNode;

    private MinecartPathMediator pathMediator;
    LinkedBlockingQueue<ChunkCoordIntPair> chunkQueue = new LinkedBlockingQueue<ChunkCoordIntPair>();

    int chunkSearchRadius = 1;
    final int MAX_SEARCH_RADIUS = 64;
    final int MAX_CHUNKS_SEARCHED_PER_TICKS = 16;
    private int currentSearchX = -chunkSearchRadius;
    private int currentSearchZ = -chunkSearchRadius;

    public boolean surveyComplete = false;

    int biomesFound = 0;

    ChunkCoordinates[][] closestBiomes = new ChunkCoordinates[BiomeGenBase.biomeList.length][3];
    ArrayList<AStarNode>[] pathsToClosestBiomes = (ArrayList<AStarNode>[]) new ArrayList[BiomeGenBase.biomeList.length];
    int pathedIndex = 0;

    private ForgeChunkManager.Ticket bigTicket = null;
    private int chunkDelay = 0;
    private int pathsFound;


    TileEntityStation(World world) {
        worldObj = world;
        pathMediator = new MinecartPathMediator(worldObj, this);

    }

    public void setCustomName(String par1Str) {
        this.customName = par1Str;
    }

    public void updateEntity() {
        if (!worldObj.isRemote) {

            if (pathFailed) {
                pathFailed = false;
                // blacklist the last biome location, move it around, try again, etc.
                foundPathFor[pathedIndex] = false;
                pathedIndex++;
            }

            if (foundPath != null) {
//            worldObj.setBlock(xCoord, yCoord + 2, zCoord, BlockStation.signWall.blockID);
//            TileEntitySign sign = (TileEntitySign) (worldObj.getBlockTileEntity(xCoord, yCoord + 2, zCoord));
//            if (sign != null) {
//                sign.signText[1] = "Cart to";
//                sign.signText[2] = worldObj.getBiomeGenForCoords(targetNode.posX, targetNode.posZ).biomeName;
//            }

                pathsToClosestBiomes[pathedIndex] = foundPath;
                foundPathFor[pathedIndex] = true;
                pathsFound++;
                pathedIndex++;

                foundPath = null;

                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 0b11, 3);
            }

            if (!searching && bigTicket != null) {
                ForgeChunkManager.releaseTicket(bigTicket);
            }

            if (searching) {
                loadChunksFromQueue();
            } else {
                chunkDelay = 0;
                if (!surveyComplete) {
                    findBiomes();
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 0b01, 3);
                } else {
                    findPaths();
                }
            }

        }
    }

    public String getGUIString() {
        String s = "";
        ArrayList<ChunkCoordinates> locations = new ArrayList<ChunkCoordinates>();
        for (int i = 0; i < closestBiomes.length; i++) {
            locations.add(closestBiomes[i][0]);
        }

        for (int i = 0; i < locations.size(); i++) {
            ChunkCoordinates cc = locations.get(i);
            if (cc != null) {
                s += (foundPathFor[i] != null ? (foundPathFor[i] ? "[READY]" : "[FAILED]") : "[FRESH]") + " " + worldObj.getBiomeGenForCoords(cc.posX, cc.posZ).biomeName + " " + (int) Math.sqrt(cc.getDistanceSquared(xCoord, cc.posY, zCoord)) + " meters away at (" + cc.posX + ", " + cc.posZ + ").\n";
            }
        }

        return s;
    }

    private void spawnPathGuide(ArrayList<AStarNode> path) {
        EntityWilloWisp ww = new EntityWilloWisp(worldObj);
        ww.setPath(path);
        ww.setPosition(path.get(path.size() - 5).x + 0.5, path.get(path.size() - 5).y + 2, path.get(path.size() - 5).z + 0.5);
        worldObj.spawnEntityInWorld(ww);
    }

    private void findPaths() {
        if (pathedIndex >= BiomeGenBase.biomeList.length) {
            return;
        }
        while (foundPathFor[pathedIndex] != null || closestBiomes[pathedIndex][0] == null) {
            pathedIndex++;
            if (pathedIndex >= BiomeGenBase.biomeList.length) {
                return;
            }
        }

        ArrayList<ChunkCoordinates> biomeCoords;
        biomeCoords = biomeLocations(pathedIndex);

        ChunkCoordinates cpos = biomeCoords.size() > 0 ? new ChunkCoordinates(biomeCoords.get(0).posX, 65, biomeCoords.get(0).posZ) : null;
        if (cpos != null) {
            System.out.printf("Biome located at (%d, %d) is %s.%n", cpos.posX, cpos.posZ, worldObj.getBiomeGenForCoords(cpos.posX, cpos.posZ).biomeName);

            System.out.println("Searching...");
            this.findPathBetween(xCoord + 1, yCoord, zCoord, cpos.posX, cpos.posY, cpos.posZ);
        }
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);
//        NBTTagList nbttaglist = par1NBTTagCompound.getTagList("Items");
////        this.dispenserContents = new ItemStack[this.getSizeInventory()];
//
//        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
//            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.tagAt(i);
//            int j = nbttagcompound1.getByte("Slot") & 255;
//
////            if (j >= 0 && j < this.dispenserContents.length)
////            {
////                this.dispenserContents[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
////            }
//        }
//
//        if (par1NBTTagCompound.hasKey("CustomName")) {
//            this.customName = par1NBTTagCompound.getString("CustomName");
//        }
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
            pathMediator.getPath(x1, y1, z1, x2, y2, z2, null);

            searching = true;
            targetNode = new ChunkCoordinates(x2, y2, z2);
            System.out.println("Finding path.");
        }
    }

    public void findBiomes() {
        if (chunkSearchRadius >= MAX_SEARCH_RADIUS) {
            surveyComplete = true;
            System.out.printf("Found %d biomes.%n", biomesFound);
        }
        int chunksSearched = 0;
        while (chunkSearchRadius < MAX_SEARCH_RADIUS && chunksSearched < MAX_CHUNKS_SEARCHED_PER_TICKS) {
            for (; currentSearchX <= chunkSearchRadius && chunksSearched < MAX_CHUNKS_SEARCHED_PER_TICKS;
                 currentSearchX++) {
                for (; currentSearchZ <= chunkSearchRadius && chunksSearched < MAX_CHUNKS_SEARCHED_PER_TICKS;
                     currentSearchZ++) {

                    if (Math.abs(currentSearchX) != chunkSearchRadius && Math.abs(currentSearchZ) != chunkSearchRadius) {
                        continue;
                    }

                    chunksSearched++;

                    int x = xCoord + currentSearchX * 16;
                    int z = zCoord + currentSearchZ * 16;

                    int biomeID;
                    try {
                        biomeID = worldObj.getWorldChunkManager().getBiomeGenAt(x, z).biomeID;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("Oops, tried to access a chunk while it was initializing.");
                        return;
                    }

                    ChunkCoordinates biomeCoords = new ChunkCoordinates(x, 60, z);

                    if (biomeID == worldObj.getBiomeGenForCoords(xCoord, zCoord).biomeID && biomeCoords.getDistanceSquared(xCoord, yCoord, zCoord) < 10000) {
                        continue;
                    }

                    if (closestBiomes[biomeID][0] != null) {
                        if (closestBiomes[biomeID][0].getDistanceSquared(xCoord, yCoord, zCoord) > biomeCoords.getDistanceSquared(xCoord, yCoord, zCoord)) {
                            if (closestBiomes[biomeID][0].getDistanceSquaredToChunkCoordinates(biomeCoords) > 10000) {
                                closestBiomes[biomeID][2] = closestBiomes[biomeID][1];
                                closestBiomes[biomeID][1] = closestBiomes[biomeID][0];
                                closestBiomes[biomeID][0] = biomeCoords;
                            }
                        } else {
                            if (closestBiomes[biomeID][0].getDistanceSquaredToChunkCoordinates(biomeCoords) > 10000
                                    && (closestBiomes[biomeID][1] == null || closestBiomes[biomeID][1].getDistanceSquaredToChunkCoordinates(biomeCoords) > 10000)) {
                                closestBiomes[biomeID][2] = closestBiomes[biomeID][1];
                                closestBiomes[biomeID][1] = biomeCoords;
                            }
                        }
                    } else {
                        closestBiomes[biomeID][0] = biomeCoords;
                        biomesFound++;
                    }
                }
            }
            if (chunksSearched < MAX_CHUNKS_SEARCHED_PER_TICKS) {
                chunkSearchRadius++;
                currentSearchX = currentSearchZ = -chunkSearchRadius;
            }
        }
    }

    public ArrayList<ChunkCoordinates> biomeLocations(int biomeID) {
        ArrayList<ChunkCoordinates> biomes = new ArrayList<ChunkCoordinates>();
        if (closestBiomes[biomeID][0] != null) {
            biomes.add(closestBiomes[biomeID][0]);
        }
        if (closestBiomes[biomeID][1] != null) {
            biomes.add(closestBiomes[biomeID][1]);
        }
        if (closestBiomes[biomeID][2] != null) {
            biomes.add(closestBiomes[biomeID][2]);
        }

        return biomes;
    }

    public void findPathToRandomBiome(World par1World) {
        if (par1World.isRemote || biomesFound == 0 || pathsFound == 0) {
            return;
        }

        System.out.printf("Found %d biomes so far.%n", biomesFound);

        int pathIndex;
        do {
            pathIndex = worldObj.rand.nextInt(BiomeGenBase.biomeList.length);
        } while (foundPathFor[pathIndex] == null || !foundPathFor[pathIndex]);

        ArrayList<AStarNode> path = pathsToClosestBiomes[pathIndex];

        if (path.size() == 0) {
            System.out.println("Fucked something up. Biome is " + BiomeGenBase.biomeList[pathIndex].biomeName);
        } else {
            spawnPathGuide(new ArrayList<AStarNode>(path));
        }
    }

    public void findPathBETA(World par1World, int blockX, int blockY, int blockZ) {
        if (par1World.isRemote || searching) {
            return;
        }

        // nearest biome

        BiomeGenBase currentBiome = par1World.getWorldChunkManager().getBiomeGenAt(blockX, blockZ);

        BiomeGenBase targetBiome = BiomeGenBase.biomeList[par1World.rand.nextInt(BiomeGenBase.biomeList.length)];
        while (targetBiome == null || targetBiome == currentBiome) {
            targetBiome = BiomeGenBase.biomeList[par1World.rand.nextInt(BiomeGenBase.biomeList.length)];
        }
        System.out.println("Finding nearest " + targetBiome.biomeName);

        ChunkPosition cpos = null;

        int startX = blockX;
        int startZ = blockZ;

        int chunkRadius = 1;
        int maxRange = 64;

//        while (chunkRadius < maxRange) {
//            for (int i = -chunkRadius; i < chunkRadius && chunkRadius < maxRange; i++) {
//                for (int j = -chunkRadius; j < chunkRadius && chunkRadius < maxRange; j++) {
//                    if (Math.abs(i) != chunkRadius && Math.abs(j) != chunkRadius) {
//                        continue;
//                    }
//                    if (par1World.getWorldChunkManager().getBiomeGenAt(startX + i * 16, startZ + j * 16) == targetBiome) {
//                        cpos = new ChunkPosition(startX + i * 16, par1World.getHeightValue(startX + i * 16, startZ + j * 16) + 20, startZ + j * 16);
//                        chunkRadius = maxRange + 1;
//                        break;
//                    }
//                }
//            }
//            chunkRadius++;
//        }

        cpos = new ChunkPosition(startX + 50 * 16, par1World.getHeightValue(startX + 50 * 16, startZ + 50 * 16) + 20, startZ + 50 * 16);
        if (cpos != null) {
            System.out.printf("Biome located at (%d, %d) is %s.%n", cpos.x, cpos.z, targetBiome.biomeName);
        } else {
            System.out.println("failed to find " + targetBiome.biomeName);
            return;
        }

        int x = cpos.x;
        int y = cpos.y;
        int z = cpos.z;

//        keepChunksAliveBetween(blockX, blockZ, x, z);
//        forceChunksBetween(blockX, blockZ, x, z);
//        loadChunksForPathfinder();

        this.findPathBetween(blockX + 1, blockY, blockZ, x, y, z);

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

        foundPath = result;

        pathFailed = false;

        System.out.println("Found path.");
    }

    @Override
    public void onNoPathAvailable() {
        searching = false;
        foundPath = null;
        pathFailed = true;

        System.out.println("Couldn't find path.");
    }

    @Override
    public void onChunkQueueRequest(ArrayList<ChunkCoordIntPair> chunkCoordIntPairs) {
        chunkQueue.addAll(chunkCoordIntPairs);
    }

    /**
     * loads a 5x5 square of chunks around the requested coordinates.
     */
    public void loadChunksFromQueue() {
        if (!chunkQueue.isEmpty()) {
            ArrayList<ChunkCoordIntPair> chunks = new ArrayList<ChunkCoordIntPair>();
            chunkQueue.drainTo(chunks);

            Set setItems = new LinkedHashSet(chunks);
            chunks.clear();
            chunks.addAll(setItems);

//            for (ChunkCoordIntPair cc : new ArrayList<ChunkCoordIntPair>(chunks)) {
//                for (int i = cc.chunkXPos - 2; i <= cc.chunkXPos + 2; i++) {
//                    for (int j = cc.chunkZPos - 2; j <= cc.chunkZPos + 2; j++) {
//                        chunks.add(new ChunkCoordIntPair(i, j));
//                    }
//                }
//            }
//
//            setItems = new LinkedHashSet(chunks);
//            chunks.clear();
//            chunks.addAll(setItems);

            if (bigTicket != null) {
                ForgeChunkManager.releaseTicket(bigTicket);
            }

            bigTicket = ForgeChunkManager.requestTicket(GhostTrain.instance, worldObj, ForgeChunkManager.Type.NORMAL);

            for (ChunkCoordIntPair ccip : chunks) {
                worldObj.getBlockId(ccip.chunkXPos * 16, 65, ccip.chunkZPos * 16);
                ForgeChunkManager.forceChunk(bigTicket, ccip);
            }
        }
    }

    public void onActivatedByPlayer(World world, int x, int y, int z, EntityPlayer player) {
        if (!world.isRemote) {
            System.out.print(getGUIString());
            findPathToRandomBiome(world);
        }
    }


//    public void loadChunksForPathfinder() {
//        if (pathMediator.isBusy()) {
//
//            AStarNode currentNode = pathMediator.getCurrentNode();
//
//            if (currentNode != null) {// && (lastNode == null || currentNode.getDistanceTo(lastNode) > AStarStatic.getDistanceBetween(0, 0, 0, 10, 0, 10))) {
//                lastNode = currentNode;
//
////                if (bigTicket != null) {
////                    ForgeChunkManager.releaseTicket(bigTicket);
////                }
////
////                bigTicket = ForgeChunkManager.requestTicket(GhostTrain.instance, worldObj, ForgeChunkManager.Type.NORMAL);
//
//                if (currentNode != null) {
//                    int minChunkX = currentNode.x - 80 >> 4;
//                    int minChunkZ = currentNode.z - 80 >> 4;
//                    int maxChunkX = currentNode.x + 80 >> 4;
//                    int maxChunkZ = currentNode.z + 80 >> 4;
//
//                    for (int i = minChunkX; i <= maxChunkX; i++) {
//                        for (int j = minChunkZ; j <= maxChunkZ; j++) {
//                            worldObj.getBlockId(i * 16, 60, j * 16);
//                            // ForgeChunkManager.forceChunk(bigTicket, new ChunkCoordIntPair(i, j));
//                        }
//                    }
//                    //System.out.printf("Loaded %d chunks for pathfinder's current node, (%d, %d, %d).%n", bigTicket.getChunkList().size(), currentNode.x, currentNode.y, currentNode.z);
//                }
//            }
//
////            ArrayList<ChunkCoordIntPair> chunks = new ArrayList<ChunkCoordIntPair>();
////            chunkQueue.drainTo(chunks);
////
////            for (ChunkCoordIntPair ccip : chunks) {
////                worldObj.getBlockId(ccip.chunkXPos * 16, 60, ccip.chunkZPos * 16);
////                ForgeChunkManager.forceChunk(bigTicket, ccip);
////            }
//        }
//    }
}
