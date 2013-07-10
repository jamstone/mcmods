package mods.ifw.aurus.common;
//package ifw.aurus.common;
//
//import java.util.Random;
//
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockIce;
//import net.minecraft.block.material.Material;
//import net.minecraft.creativetab.CreativeTabs;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityLiving;
//import net.minecraft.tileentity.TileEntityMobSpawner;
//import net.minecraft.world.World;
//
//public class TestBlock extends BlockIce {
//
//	Random random = new Random();
//
//	public TestBlock(int par1) {
//		this(par1, Material.ice);
//	}
//
//	public TestBlock(int par1, Material par2Material) {
//		super(par1, 67);
//
//		setHardness(0.5F);
//		setLightOpacity(3);
//		setStepSound(soundGlassFootstep);
//
//		setCreativeTab(CreativeTabs.tabBlock);
//	}
//
//	@Override
//	public void onEntityCollidedWithBlock(World par1World, int par2, int par3,
//			int par4, Entity entity) {
//
//	}
//
//	@Override
//	public void onBlockPlacedBy(World world, int x, int y, int z,
//			EntityLiving entity) {
//		int block = mod_aurus.nestID;
//
//		int[][] indices = { { -1, 0, -1 }, { -1, 0, 0 }, { -1, 0, 1 },
//				{ 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 1, 0, -1 },
//				{ 1, 0, 0 }, { 1, 0, 1 },
//
//				{ -2, 1, -2 }, { -2, 1, -1 }, { -2, 1, 0 }, { -2, 1, 1 },
//				{ -2, 1, 2 }, { -1, 1, -2 }, { -1, 1, -1 }, { -1, 1, 1 },
//				{ -1, 1, 2 }, { 0, 1, -2 }, { 0, 1, 2 }, { 1, 1, -2 },
//				{ 1, 1, -1 }, { 1, 1, 1 }, { 1, 1, 2 }, { 2, 1, -2 },
//				{ 2, 1, -1 }, { 2, 1, 0 }, { 2, 1, 1 }, { 2, 1, 2 },
//
//				{ -3, 2, -2 }, { -3, 2, -1 }, { -3, 2, 0 }, { -3, 2, 1 },
//				{ -3, 2, 2 }, { -2, 2, -3 }, { -2, 2, -2 }, { -2, 2, 2 },
//				{ -2, 2, 3 }, { -1, 2, -3 }, { -1, 2, 3 }, { 0, 2, -3 },
//				{ 0, 2, 3 }, { 1, 2, -3 }, { 1, 2, 3 }, { 2, 2, -3 },
//				{ 2, 2, -2 }, { 2, 2, 2 }, { 2, 2, 3 }, { 3, 2, -2 },
//				{ 3, 2, -1 }, { 3, 2, 0 }, { 3, 2, 1 }, { 3, 2, 2 },
//
//				{ -3, 3, -2 }, { -3, 3, -1 }, { -3, 3, 0 }, { -3, 3, 1 },
//				{ -3, 3, 2 }, { -2, 3, -3 }, { -2, 3, 3 }, { -1, 3, -3 },
//				{ -1, 3, 3 }, { 0, 3, -3 }, { 0, 3, 3 }, { 1, 3, -3 },
//				{ 1, 3, 3 }, { 2, 3, -3 }, { 2, 3, 3 }, { 3, 3, -2 },
//				{ 3, 3, -1 }, { 3, 3, 0 }, { 3, 3, 1 }, { 3, 3, 2 },
//
//				{ -3, 4, -2 }, { -3, 4, -1 }, { -3, 4, 0 }, { -3, 4, 1 },
//				{ -3, 4, 2 }, { -2, 4, -3 }, { -2, 4, 3 }, { -1, 4, -3 },
//				{ -1, 4, 3 }, { 0, 4, -3 }, { 0, 4, 3 }, { 1, 4, -3 },
//				{ 1, 4, 3 }, { 2, 4, -3 }, { 2, 4, 3 }, { 3, 4, -2 },
//				{ 3, 4, -1 }, { 3, 4, 0 }, { 3, 4, 1 }, { 3, 4, 2 },
//
//				{ -3, 5, -2 }, { -3, 5, -1 }, { -3, 5, 0 }, { -3, 5, 1 },
//				{ -3, 5, 2 }, { -2, 5, -3 }, { -2, 5, 3 }, { -1, 5, -3 },
//				{ -1, 5, 3 }, { 0, 5, -3 }, { 0, 5, 3 }, { 1, 5, -3 },
//				{ 1, 5, 3 }, { 2, 5, -3 }, { 2, 5, 3 }, { 3, 5, -2 },
//				{ 3, 5, -1 }, { 3, 5, 0 }, { 3, 5, 1 }, { 3, 5, 2 },
//
//				{ -3, 6, -2 }, { -3, 6, -1 }, { -3, 6, 0 }, { -3, 6, 1 },
//				{ -3, 6, 2 }, { -2, 6, -3 }, { -2, 6, 3 }, { -1, 6, -3 },
//				{ -1, 6, 3 }, { 0, 6, -3 }, { 0, 6, 3 }, { 1, 6, -3 },
//				{ 1, 6, 3 }, { 2, 6, -3 }, { 2, 6, 3 }, { 3, 6, -2 },
//				{ 3, 6, -1 }, { 3, 6, 0 }, { 3, 6, 1 }, { 3, 6, 2 },
//
//				{ -3, 7, -2 }, { -3, 7, -1 }, { -3, 7, 0 }, { -3, 7, 1 },
//				{ -3, 7, 2 }, { -2, 7, -3 }, { -2, 7, -2 }, { -2, 7, -1 },
//				{ -2, 7, 0 }, { -2, 7, 1 }, { -2, 7, 2 }, { -2, 7, 3 },
//				{ -1, 7, -3 }, { -1, 7, -2 }, { -1, 7, -1 }, { -1, 7, 1 },
//				{ -1, 7, 2 }, { -1, 7, 3 }, { 0, 7, -3 }, { 0, 7, -2 },
//				{ 0, 7, 2 }, { 0, 7, 3 }, { 1, 7, -3 }, { 1, 7, -2 },
//				{ 1, 7, -1 }, { 1, 7, 1 }, { 1, 7, 2 }, { 1, 7, 3 },
//				{ 2, 7, -3 }, { 2, 7, -2 }, { 2, 7, -1 }, { 2, 7, 0 },
//				{ 2, 7, 1 }, { 2, 7, 2 }, { 2, 7, 3 }, { 3, 7, -2 },
//				{ 3, 7, -1 }, { 3, 7, 0 }, { 3, 7, 1 }, { 3, 7, 2 },
//
//				{ -1, 8, -1 }, { -1, 8, 0 }, { -1, 8, 1 }, { 0, 8, -1 },
//				{ 0, 8, 0 }, { 0, 8, 1 }, { 1, 8, -1 }, { 1, 8, 0 },
//				{ 1, 8, 1 },
//
//				{ 0, 9, 0 }
//
//		};
//
//		for (int i = 0; i < indices.length; i++) {
//			world.setBlockWithNotify(x + indices[i][0], y + indices[i][1], z
//					+ indices[i][2], block);
//		}
//
//		world.setBlockWithNotify(x, y + 5, z, Block.oreDiamond.blockID);
//		world.setBlockWithNotify(x, y + 4, z, Block.mobSpawner.blockID);
//		TileEntityMobSpawner spawner = (TileEntityMobSpawner) world
//				.getBlockTileEntity(x, y + 4, z);
//		if (spawner != null) {
//			spawner.setMobID("Fledgeling");
//		}
//		world.setBlockWithNotify(x, y, z, 0);
//	}
//}
