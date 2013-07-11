package mods.ifw.aurus.common;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBogGas extends Block {
	public BlockBogGas(int par1) {
		this(par1, Material.leaves);
	}

	public BlockBogGas(int par1, Material par2Material) {
		super(par1, par2Material);

		setLightOpacity(1);

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

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess,
			int par2, int par3, int par4, int par5) {
		return false;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World,
			int par2, int par3, int par4) {
		return null;
	}

	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3,
			int par4, Entity entity) {
		if ((entity instanceof EntityLiving)) {
			EntityLiving entityliving = (EntityLiving) entity;
			PotionEffect pe = new PotionEffect(Potion.blindness.id, 60, 0);
			entityliving.addPotionEffect(pe);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		super.updateTick(world, x, y, z, random);
		if (!world.isRemote) {
			if (((x + 2 * z) % 10 == 0 && z % 2 == 0)
					&& world.getClosestPlayer(x, y, z, 32) == null) {
				world.scheduleBlockUpdate(x, y, z, this.blockID,
						random.nextInt(100) + 100);
				return;
			}
			if (random.nextInt(100) < 50) {
				if (world.isAirBlock(x, y - 1, z) && random.nextInt(100) < 75)
					world.setBlock(x, y - 1, z, Aurus.bogGasID);
				if (world.isAirBlock(x - 1, y, z) && random.nextInt(100) < 40)
					world.setBlock(x - 1, y, z, Aurus.bogGasID);
				if (world.isAirBlock(x + 1, y, z) && random.nextInt(100) < 40)
					world.setBlock(x + 1, y, z, Aurus.bogGasID);
				if (world.isAirBlock(x, y, z - 1) && random.nextInt(100) < 40)
					world.setBlock(x, y, z - 1, Aurus.bogGasID);
				if (world.isAirBlock(x, y, z + 1) && random.nextInt(100) < 40) {
					world.setBlock(x, y, z + 1, Aurus.bogGasID);
				}
			}

			world.setBlock(x, y, z, 0);
		}
	}

	@Override
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID,
				new Random().nextInt(20) + 20);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World par1World, int par2, int par3,
			int par4, Random par5Random) {
		for (int var6 = 0; var6 < 1; var6++) {
			double var7 = par2 + par5Random.nextFloat();
			double var9 = par3 + par5Random.nextFloat();
			double var11 = par4 + par5Random.nextFloat();
			double var13 = 0D;
			double var15 = 0D;
			double var17 = 0D;
			int var19 = par5Random.nextInt(2) * 2 - 1;
			var13 = (par5Random.nextFloat() - 0.5D) * 0.5D;
			var15 = (par5Random.nextFloat() - 0.5D) * 0.5D;
			var17 = (par5Random.nextFloat() - 0.5D) * 0.5D;

			// par1World.spawnParticle("reddust", var7, var9, var11, var13,
			// var15, var17);
			par1World.spawnParticle("mobSpellAmbient", var7, var9, var11,
					0.3 + var13 / 10, var15, 0.2 + var17 / 10);
		}
	}

	@Override
	public int idPicked(World par1World, int par2, int par3, int par4) {
		return 0;
	}
}