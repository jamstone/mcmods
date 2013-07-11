package mods.ifw.aurus.common;

import java.util.ArrayList;
import java.util.Random;

import mods.ifw.aurus.pathfinding.bullshit.Direction;
import mods.ifw.aurus.pathfinding.bullshit.Weight;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockPathMarker extends Block {
	public BlockPathMarker(int par1) {
		this(par1, Material.air);
	}

	public BlockPathMarker(int par1, Material par2Material) {
		super(par1, par2Material);

		setLightOpacity(1);

		this.setLightValue(1.0F);

		setCreativeTab(CreativeTabs.tabBlock);
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2,
			int par3, int par4) {
		return false;
	}

	@Override
	public boolean canCollideCheck(int par1, boolean par2) {
		return (par2) && (par1 == 0);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int quantityDropped(Random par1Random) {
		return 0;
	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World,
			int par2, int par3, int par4) {
		return null;
	}

	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess,
			int par2, int par3, int par4, int par5) {
		return false;
	}

	public boolean isCollidable() {
		return false;
	}

	public void updateTick(World world, int x, int y, int z, Random random) {
		super.updateTick(world, x, y, z, random);
		if (!world.isRemote) {
			world.setBlock(x, y, z, 0);
		}
	}

	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID,
				par1World.rand.nextInt(50) + 75);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int par2, int par3, int par4,
			Random par5Random) {
		int id = world.getBlockId(par2, par3, par4);

		ArrayList<Direction> neighbours = new ArrayList<Direction>();

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (!(i == 0 && j == 0 && k == 0)
							&& world.getBlockId(par2 + i, par3 + j, par4 + k) == id) {
						Direction dir = Direction.getDirection(i, j, k);
						neighbours.add(dir);
						if (neighbours.size() > 2) {
							neighbours.clear();
							break;
						}
					}
				}
			}
		}

		neighbours.add(Direction.O);

		/*
		 * ArrayList<Direction> neighboursD1 = new ArrayList<Direction>();
		 * ArrayList<Direction> neighboursD2 = new ArrayList<Direction>();
		 * ArrayList<Direction> neighboursD3 = new ArrayList<Direction>();
		 * 
		 * for (int i = -1; i <= 1; i++) { for (int j = -1; j <= 1; j++) { for
		 * (int k = -1; k <= 1; k++) { if (!(i == 0 && j == 0 && k == 0) &&
		 * world.getBlockId(par2 + i, par3 + j, par4 + k) == id) { Direction dir
		 * = Direction.getDirection(i, j, k); switch (dir.weight) { case D1:
		 * neighboursD1.add(dir); break; case D2: neighboursD2.add(dir); break;
		 * case D3: neighboursD3.add(dir); break; }
		 * 
		 * } } } } neighbours = neighboursD1; neighbours.addAll(neighboursD2);
		 * neighbours.addAll(neighboursD3);
		 */

		for (int i = 0; i < neighbours.size(); i++) {

			Direction dir = neighbours.get(i);

			for (int j = 0; j < 2; j++) {

				float randomFloat = par5Random.nextFloat();

				double x = par2 + 0.5 + 0.5 * dir.x * randomFloat
						+ (par5Random.nextFloat() - 0.5D) * 0.1;
				double y = par3 + 0.5 + 0.5 * dir.y * randomFloat
						+ (par5Random.nextFloat() - 0.5D) * 0.1;
				double z = par4 + 0.5 + 0.5 * dir.z * randomFloat
						+ (par5Random.nextFloat() - 0.5D) * 0.1;

				double var13 = -10.0D;
				double var15 = 2.0D * j;
				double var17 = 1.0D * j;

				world.spawnParticle("reddust", x, y, z, var13, var15, var17);
			}
		}
	}

	@Override
	public int idPicked(World par1World, int par2, int par3, int par4) {
		return 0;
	}
}