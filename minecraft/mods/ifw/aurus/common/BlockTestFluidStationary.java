package mods.ifw.aurus.common;
//package ifw.aurus.common;
//
//import java.util.ArrayList;
//
//import net.minecraft.src.Block;
//import net.minecraft.src.Material;
//import net.minecraft.src.World;
//
//public class BlockTestFluidStationary extends BlockBogWaterStationary {
//
//    public BlockTestFluidStationary(int id) {
//	super(id);
//	// TODO Auto-generated constructor stub
//    }
//
//    public BlockTestFluidStationary(int par1, Material par2Material) {
//	super(par1, par2Material);
//	// TODO Auto-generated constructor stub
//    }
//
//    @Override
//    public void onNeighborBlockChange(World par1World, int x, int y, int z,
//	    int id) {
//	// TODO Auto-generated method stub
//
//	this.premptiveCheckForHarden(par1World, x, y, z);
//
//	super.onNeighborBlockChange(par1World, x, y, z, id);
//    }
//
//    @Override
//    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
//	// TODO Auto-generated method stub
//
//	this.premptiveCheckForHarden(par1World, par2, par3, par4);
//
//	if (par1World.getBlockId(par2, par3, par4) == this.blockID) {
//	    par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID,
//		    this.tickRate());
//	}
//    }
//
//    public ArrayList<int[]> blocksOfMaterialAround(World world, int x, int y,
//	    int z, Material material) {
//
//	ArrayList<int[]> blocks = new ArrayList<int[]>();
//
//	if (world.getBlockMaterial(x, y, z - 1) == material) {
//	    blocks.add(new int[] { x, y, z - 1 });
//	}
//
//	if (world.getBlockMaterial(x, y, z + 1) == material) {
//	    blocks.add(new int[] { x, y, z + 1 });
//	}
//
//	if (world.getBlockMaterial(x - 1, y, z) == material) {
//	    blocks.add(new int[] { x - 1, y, z });
//	}
//
//	if (world.getBlockMaterial(x + 1, y, z) == material) {
//	    blocks.add(new int[] { x + 1, y, z });
//	}
//
//	if (world.getBlockMaterial(x, y - 1, z) == material) {
//	    blocks.add(new int[] { x, y - 1, z });
//	}
//
//	if (world.getBlockMaterial(x, y + 1, z) == material) {
//	    blocks.add(new int[] { x, y + 1, z });
//	}
//
//	return blocks;
//    }
//
//    public ArrayList<int[]> blocksOfIdAround(World world, int x, int y, int z,
//	    int id) {
//	ArrayList<int[]> blocks = new ArrayList<int[]>();
//
//	if (world.getBlockId(x, y, z - 1) == id) {
//	    blocks.add(new int[] { x, y, z - 1 });
//	}
//
//	if (world.getBlockId(x, y, z + 1) == id) {
//	    blocks.add(new int[] { x, y, z + 1 });
//	}
//
//	if (world.getBlockId(x - 1, y, z) == id) {
//	    blocks.add(new int[] { x - 1, y, z });
//	}
//
//	if (world.getBlockId(x + 1, y, z) == id) {
//	    blocks.add(new int[] { x + 1, y, z });
//	}
//
//	if (world.getBlockId(x, y - 1, z) == id) {
//	    blocks.add(new int[] { x, y - 1, z });
//	}
//
//	if (world.getBlockId(x, y + 1, z) == id) {
//	    blocks.add(new int[] { x, y + 1, z });
//	}
//
//	return blocks;
//    }
//
//    public void premptiveCheckForHarden(World world, int x, int y, int z) {
//	if (world.getBlockId(x, y, z) == this.blockID) {
//	    if (this.blockMaterial != Material.lava) {
//		ArrayList<int[]> lavaBlocks = blocksOfMaterialAround(world, x,
//			y, z, Material.lava);
//		ArrayList<int[]> cobblestoneBlocks = blocksOfIdAround(world, x,
//			y, z, Block.cobblestone.blockID);
//		ArrayList<int[]> blocksThatAppearToHaveBeenLavaBlocks = new ArrayList<int[]>();
//
//		for (int i = 0; i < lavaBlocks.size(); i++) {
//		    int x1 = lavaBlocks.get(i)[0];
//		    int y1 = lavaBlocks.get(i)[1];
//		    int z1 = lavaBlocks.get(i)[2];
//
//		    int metadata = world.getBlockMetadata(x1, y1, z1);
//
//		    if (metadata == 0) {
//			world.setBlockWithNotify(x1, y1, z1,
//				Block.glass.blockID);
//		    } else {
//			world.setBlockWithNotify(x1, y1, z1, Block.sand.blockID);
//		    }
//		}
//
//		for (int i = 0; i < cobblestoneBlocks.size(); i++) {
//		    int x1 = lavaBlocks.get(i)[0];
//		    int y1 = lavaBlocks.get(i)[1];
//		    int z1 = lavaBlocks.get(i)[2];
//		    if (blocksOfMaterialAround(world, x1, y1, z1, Material.lava)
//			    .size() > 0) {
//			blocksThatAppearToHaveBeenLavaBlocks.add(new int[] {
//				x1, y1, z1 });
//		    }
//		}
//
//		for (int i = 0; i < blocksThatAppearToHaveBeenLavaBlocks.size(); i++) {
//		    int x1 = lavaBlocks.get(i)[0];
//		    int y1 = lavaBlocks.get(i)[1];
//		    int z1 = lavaBlocks.get(i)[2];
//
//		    world.setBlockWithNotify(x1, y1, z1, Block.sand.blockID);
//		}
//	    }
//	}
//    }
//
//}
