package mods.jamstone.ghosttrain.common;

import mods.jamstone.pathfinding.AStarNode;
import mods.jamstone.pathfinding.AStarStatic;
import mods.jamstone.pathfinding.minecart.IMinecartPathedEntity;
import mods.jamstone.pathfinding.minecart.MinecartPathMediator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
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

public class TileEntityStation extends TileEntity implements IInventory, IMinecartPathedEntity {
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
    int biomeSearchIndex = 0;

    private ForgeChunkManager.Ticket bigTicket = null;
    private int chunkDelay = 0;
    private int pathsFound;

    private ItemStack[] inv;

    private EntityWilloWisp currentGuide = null;

    public TileEntityStation() {
        this(null);
    }

    public TileEntityStation(World world) {
        setWorldObj(world);
        inv = new ItemStack[9];

    }

    @Override
    public void setWorldObj(World par1World) {
        this.worldObj = par1World;
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
                foundPathFor[biomeSearchIndex] = false;
                biomeSearchIndex++;
            }

            if (foundPath != null) {

                pathsToClosestBiomes[biomeSearchIndex] = foundPath;
                foundPathFor[biomeSearchIndex] = true;
                pathsFound++;
                biomeSearchIndex++;

                foundPath = null;

                worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 3, 3);
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
                    worldObj.setBlockMetadataWithNotify(xCoord, yCoord + 1, zCoord, 1, 3);
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

        return s + (biomeSearchIndex < BiomeGenBase.biomeList.length ? "Distance left to " + BiomeGenBase.biomeList[biomeSearchIndex].biomeName + ": " + distanceLeftToSearchTarget() + "\n" : "");
    }

    public int distanceLeftToSearchTarget() {
        int distance = -1;
        if (searching != false) {
            ChunkCoordinates target = getCurrentSearchTarget();
            AStarNode currentNode = pathMediator.getCurrentNode();
            if (currentNode != null) {
                ChunkCoordinates current = new ChunkCoordinates(currentNode.x, currentNode.y, currentNode.z);
                distance = (int) Math.sqrt(target.getDistanceSquaredToChunkCoordinates(current));
            }
        }
        return distance;
    }

    private ChunkCoordinates getCurrentSearchTarget() {
        return closestBiomes[biomeSearchIndex][0];
    }

    private void spawnPathGuide(ArrayList<AStarNode> path) {
        if (currentGuide != null && currentGuide.getDistanceSq(xCoord, yCoord, zCoord) < 32 * 32) {
            currentGuide.setDead();
            currentGuide = null;
        }

        EntityWilloWisp ww = new EntityWilloWisp(worldObj);
        ww.setPath(new ArrayList<AStarNode>(path));


        AStarNode as = path.get(path.size() - 1);
        AStarNode firstAs = as;
        AStarNode lastAs = as;
        while (as.getDistanceTo(firstAs) < AStarStatic.getDistanceBetween(0, 0, 0, 8, 8, 8)) {
            path.remove(path.size() - 1);

            lastAs = as;
            if (path.size() < 1) {
                path = null;
                break;
            }
            as = path.get(path.size() - 1);
        }
        if (lastAs != null) {
            ww.setPosition(lastAs.x + 0.5, lastAs.y + 2, lastAs.z + 0.5);
        }

        worldObj.spawnEntityInWorld(ww);
        currentGuide = ww;
    }

    private void findPaths() {
        if (biomeSearchIndex >= BiomeGenBase.biomeList.length) {
            return;
        }
        while (foundPathFor[biomeSearchIndex] != null || getCurrentSearchTarget() == null) {
            biomeSearchIndex++;
            if (biomeSearchIndex >= BiomeGenBase.biomeList.length) {
                return;
            }
        }

        ArrayList<ChunkCoordinates> biomeCoords;
        biomeCoords = biomeLocations(biomeSearchIndex);

        ChunkCoordinates cpos = biomeCoords.size() > 0 ? new ChunkCoordinates(biomeCoords.get(0).posX, 65, biomeCoords.get(0).posZ) : null;
        if (cpos != null) {
            System.out.printf("Biome located at (%d, %d) is %s.%n", cpos.posX, cpos.posZ, worldObj.getBiomeGenForCoords(cpos.posX, cpos.posZ).biomeName);

            System.out.println("Searching...");
            int xDiff = cpos.posX - xCoord;
            int zDiff = cpos.posZ - zCoord;
            if (Math.abs(xDiff) > Math.abs(zDiff)) {
                xDiff = (int) Math.copySign(1, xDiff);
                zDiff = 0;
            } else {
                zDiff = (int) Math.copySign(1, zDiff);
                xDiff = 0;
            }

            this.findPathBetween(xCoord + xDiff, yCoord, zCoord + zDiff, cpos.posX, cpos.posY, cpos.posZ);
        }
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

        worldObj.setBlockToAir(xCoord, yCoord + 2, zCoord);
        worldObj.setBlock(xCoord, yCoord + 2, zCoord, BlockStation.signPost.blockID);
        TileEntitySign sign = (TileEntitySign) (worldObj.getBlockTileEntity(xCoord, yCoord + 2, zCoord));
        if (sign != null) {
            sign.signText[0] = "Cart to";
            sign.signText[1] = BiomeGenBase.biomeList[pathIndex].biomeName;
            sign.signText[2] = "~";
            sign.signText[3] = (int) Math.sqrt(closestBiomes[pathIndex][0].getDistanceSquared(xCoord, yCoord, zCoord)) + " meters.";
        }

        spawnPathGuide(new ArrayList<AStarNode>(path));

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

    @Override
    public int getSizeInventory() {
        return inv.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv[slot];
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inv[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) {
            stack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            if (stack.stackSize <= amt) {
                setInventorySlotContents(slot, null);
            } else {
                stack = stack.splitStack(amt);
                if (stack.stackSize == 0) {
                    setInventorySlotContents(slot, null);
                }
            }
        }
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) {
            setInventorySlotContents(slot, null);
        }
        return stack;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);

        NBTTagList tagList = tagCompound.getTagList("Inventory");
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tag = (NBTTagCompound) tagList.tagAt(i);
            byte slot = tag.getByte("Slot");
            if (slot >= 0 && slot < inv.length) {
                inv[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);

        NBTTagList itemList = new NBTTagList();
        for (int i = 0; i < inv.length; i++) {
            ItemStack stack = inv[i];
            if (stack != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", (byte) i);
                stack.writeToNBT(tag);
                itemList.appendTag(tag);
            }
        }
        tagCompound.setTag("Inventory", itemList);
    }

    @Override
    public String getInvName() {
        return "jamstone.stationinventory";
    }

    @Override
    public boolean isInvNameLocalized() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Do not give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer) {
        return this.worldObj.getBlockTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : par1EntityPlayer.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openChest() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeChest() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isStackValidForSlot(int i, ItemStack itemstack) {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
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
//        player.openGui(GhostTrain.instance, 0, world, x, y, z);
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
