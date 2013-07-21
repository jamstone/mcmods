package mods.ifw.aurus.common;

import mods.ifw.pathfinding.AStarNode;
import mods.ifw.pathfinding.minecart.MinecartPathWorker;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.ArrayList;

public class BlockPathFinder extends Block {

    public BlockPathFinder(int par1, Material par2Material) {
        super(par1, par2Material);
        setCreativeTab(CreativeTabs.tabBlock);
    }

    public BlockPathFinder(int par1, int par2, Material par3Material) {
        super(par1, par3Material);
        setCreativeTab(CreativeTabs.tabBlock);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraft.src.Block#onBlockActivated(net.minecraft.src.World,
     * int, int, int, net.minecraft.src.EntityPlayer, int, float, float, float)
     */
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3,
                                    int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
                                    float par8, float par9) {
        if (par1World.isRemote) {
            return false;
        }

        int scalar = 2;
        System.out.println("checking...");

        // nearest biome

        BiomeGenBase targetBiome = BiomeGenBase.mushroomIsland;
        ChunkPosition cpos = null;

        int startX = par2;
        int startZ = par4;

        int chunkRadius = 1;
        int maxRange = 640;

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
            return true;
        }
        int x = cpos.x;//par2 + (par1World.rand.nextInt(32) - 16) * scalar;
        int y = cpos.y;// + (par1World.rand.nextInt(32) - 16) * scalar;
        int z = cpos.z;//par4 + (par1World.rand.nextInt(32) - 16) * scalar;
//
//        while (!par1World.isAirBlock(x, y, z)) {
//            x = par2 + (par1World.rand.nextInt(32) - 16) * scalar;
//            y = par3;// + (par1World.rand.nextInt(32) - 16) * scalar;
//            z = par4 + (par1World.rand.nextInt(32) - 16) * scalar;
//        }
//
        AStarNode start = new AStarNode(par2, par3, par4, null);
        AStarNode goal = new AStarNode(x, y, z, null);
//
//        // JPPathFinder jp = new JPPathFinder(new AStarPath(par1World));
//
//        AStarWorkerJPS3D aswJP3D = new AStarWorkerJPS3D(null);
//
//        aswJP3D.setup(par1World, start, goal, null);

        MinecartPathWorker mcpp = new MinecartPathWorker(null);
        mcpp.setup(par1World, start, goal, null);

        ArrayList<AStarNode> path = mcpp.getPath(start, goal);

        if (path != null) {

            AStarNode lastAs = null;
            AStarNode lastlastAs = null;

            for (AStarNode as : path) {

                if (as.equals(start) || as.equals(goal)) {
                } else {
                    par1World.setBlock(as.x, as.y, as.z,
                            Block.blockRedstone.blockID);
                    if (par1World.getBlockId(as.x, as.y + 1, as.z) != Block.blockRedstone.blockID) {
                        par1World.setBlock(as.x, as.y + 1, as.z,
                                Block.railPowered.blockID);
                    }
                    if (lastlastAs != null && lastlastAs.getDirectionTo(lastAs) != lastAs.getDirectionTo(as)) {
                        par1World.setBlock(lastAs.x, lastAs.y + 1, lastAs.z,
                                Block.rail.blockID);
                    }
                }
                lastlastAs = lastAs;
                lastAs = as;
            }
        } else {
            System.out.println("Failed to find a path.");
        }

        return super.onBlockActivated(par1World, par2, par3, par4,
                par5EntityPlayer, par6, par7, par8, par9);
    }

}
