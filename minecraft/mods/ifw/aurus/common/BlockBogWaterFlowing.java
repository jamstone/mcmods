package mods.ifw.aurus.common;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlowing;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBogWaterFlowing extends BlockFlowing {
    public BlockBogWaterFlowing(int id) {
	this(id, Material.water);
    }

    public BlockBogWaterFlowing(int par1, Material par2Material) {
	super(par1, par2Material);

	setCreativeTab(CreativeTabs.tabBlock);
	setHardness(100.0F);
	setLightOpacity(6);
    }

//    @Override
//    public int tickRate() {
//	// TODO Auto-generated method stub
//	return 20;
//    }

    @Override
    public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2,
	    int par3, int par4) {
	// return 0xFF6611; recipe for colourless water
	return 0xAAFF11;
    }

    @Override
    public void onEntityCollidedWithBlock(World par1World, int par2, int par3,
	    int par4, Entity entity) {
	if (entity.ticksExisted % 3 == 0 && (entity instanceof EntityLiving)
		&& !(entity.ridingEntity instanceof EntityBoat)) {
	    // depth determines strength of hunger and weakness up to 3fold.
	    int intensity = par1World.getBlockMaterial(par2, par3 + 1, par4) == par1World
		    .getBlockMaterial(par2, par3, par4) ? par1World
		    .getBlockMaterial(par2, par3 + 2, par4) == par1World
		    .getBlockMaterial(par2, par3, par4) ? 3 : 2 : 1;

	    EntityLiving entityliving = (EntityLiving) entity;

	    PotionEffect weakness = entityliving.isEntityUndead() ? new PotionEffect(
		    Potion.damageBoost.id, 24, 1) : new PotionEffect(
		    Potion.weakness.id, 24, 1 * intensity);
	    PotionEffect hunger = new PotionEffect(Potion.hunger.id, 6,
		    8 * intensity);
	    entityliving.addPotionEffect(weakness);
	    entityliving.addPotionEffect(hunger);
	    if (entity instanceof EntityWaterMob
		    && new Random().nextInt(500) < 1) {
		PotionEffect harm = new PotionEffect(Potion.harm.id, 1, 0);
		entityliving.addPotionEffect(harm);
	    }
	}
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {

	if (!world.isRemote) {

	    Aurus.bogWaterUpdates++;
	    boolean airAround = false;
	    boolean waterNearby = false;
	    boolean changedToWater = false;

	    if (((x + 2 * z) % 5 == 0 && world.isAirBlock(x, y + 1, z))
		    || world.getClosestPlayer(x, y, z, 64) != null) {
		super.updateTick(world, x, y, z, random);

		int chance = 50;

		if (world.getBlockId(x - 1, y, z) == Block.waterMoving.blockID
			|| world.getBlockId(x - 1, y, z) == Block.waterStill.blockID) {
		    if (random.nextInt(100) < chance) {
			world.setBlock(x, y, z,
				Block.waterMoving.blockID,
				world.getBlockMetadata(x, y, z), 3);
			changedToWater = true;
		    } else {
			int b = world.getBlockMetadata(x - 1, y, z);
			world.setBlock(x - 1, y, z,
				Aurus.bogWaterFlowingID, b, 3);
		    }
		    waterNearby = true;
		}
		if (world.getBlockId(x + 1, y, z) == Block.waterMoving.blockID
			|| world.getBlockId(x + 1, y, z) == Block.waterStill.blockID) {
		    if (random.nextInt(100) < chance) {
				world.setBlock(x, y, z,
						Block.waterMoving.blockID,
						world.getBlockMetadata(x, y, z), 3);
			changedToWater = true;
		    } else {
			int b = world.getBlockMetadata(x + 1, y, z);
			world.setBlock(x + 1, y, z,
				Aurus.bogWaterFlowingID, b, 3);
		    }
		    waterNearby = true;
		}
		if (world.getBlockId(x, y - 1, z) == Block.waterMoving.blockID
			|| world.getBlockId(x, y - 1, z) == Block.waterStill.blockID) {
		    if (random.nextInt(100) < 1) {
				world.setBlock(x, y, z,
						Block.waterMoving.blockID,
						world.getBlockMetadata(x, y, z), 3);
			changedToWater = true;
		    } else {
			int b = world.getBlockMetadata(x, y - 1, z);
			world.setBlock(x, y - 1, z,
				Aurus.bogWaterFlowingID, b, 3);
		    }
		    waterNearby = true;
		}
		if (world.getBlockId(x, y + 1, z) == Block.waterMoving.blockID
			|| world.getBlockId(x, y + 1, z) == Block.waterStill.blockID) {
		    if (random.nextInt(100) < 99) {
				world.setBlock(x, y, z,
						Block.waterMoving.blockID,
						world.getBlockMetadata(x, y, z), 3);
			changedToWater = true;
		    } else {
			int b = world.getBlockMetadata(x, y + 1, z);
			world.setBlock(x, y + 1, z,
				Aurus.bogWaterFlowingID, b, 3);
		    }
		    waterNearby = true;
		}
		if (world.getBlockId(x, y, z - 1) == Block.waterMoving.blockID
			|| world.getBlockId(x, y, z - 1) == Block.waterStill.blockID) {
		    if (random.nextInt(100) < chance) {
				world.setBlock(x, y, z,
						Block.waterMoving.blockID,
						world.getBlockMetadata(x, y, z), 3);
			changedToWater = true;
		    } else {
			int b = world.getBlockMetadata(x, y, z - 1);
			world.setBlock(x, y, z - 1,
				Aurus.bogWaterFlowingID, b, 3);
		    }
		    waterNearby = true;
		}
		if (world.getBlockId(x, y, z + 1) == Block.waterMoving.blockID
			|| world.getBlockId(x, y, z + 1) == Block.waterStill.blockID) {
		    if (random.nextInt(100) < chance) {
				world.setBlock(x, y, z,
						Block.waterMoving.blockID,
						world.getBlockMetadata(x, y, z), 3);
			changedToWater = true;
		    } else {
			int b = world.getBlockMetadata(x, y, z + 1);
			world.setBlock(x, y, z + 1,
				Aurus.bogWaterFlowingID, b, 3);
		    }
		    waterNearby = true;
		}

		if (random.nextInt(5) == 0) {
		    if (world.isAirBlock(x, y + 1, z))
			world.setBlock(x, y + 1, z,
				Aurus.bogGasID);
		    if (world.isAirBlock(x - 1, y + 1, z))
			world.setBlock(x - 1, y + 1, z,
				Aurus.bogGasID);
		    if (world.isAirBlock(x + 1, y + 1, z))
			world.setBlock(x + 1, y + 1, z,
				Aurus.bogGasID);
		    if (world.isAirBlock(x, y + 1, z - 1))
			world.setBlock(x, y + 1, z - 1,
				Aurus.bogGasID);
		    if (world.isAirBlock(x, y + 1, z + 1)) {
			world.setBlock(x, y + 1, z + 1,
				Aurus.bogGasID);
		    }
		}

		if (world.isAirBlock(x - 1, y, z)
			|| world.isAirBlock(x + 1, y, z)
			|| world.isAirBlock(x, y, z - 1)
			|| world.isAirBlock(x, y, z + 1)) {
		    airAround = true;
		}

		if (airAround) {
		    return;
		} else if ((waterNearby && !changedToWater)
			|| ((x + 2 * z) % 5 == 0 && world.isAirBlock(x, y + 1,
				z))) {
		    world.scheduleBlockUpdate(x, y, z,
			    Aurus.bogWaterFlowingID, 200);
		} else {
		    world.scheduleBlockUpdate(x, y, z,
			    Aurus.bogWaterFlowingID, 600);
		}
	    }
	}
    }

    @Override
    public void randomDisplayTick(World world, int x, int y, int z,
	    Random par5Random) {
	if (world.isRemote) {
	    for (int var6 = par5Random.nextInt(5); var6 < 1; var6++) {
		double var7 = x + par5Random.nextFloat();
		double var9 = y + par5Random.nextFloat();
		double var11 = z + par5Random.nextFloat();
		double var13 = 0D;
		double var15 = 0D;
		double var17 = 0D;
		int var19 = par5Random.nextInt(2) * 2 - 1;
		var13 = (par5Random.nextFloat() - 0.5D) * 0.5D;
		var15 = (par5Random.nextFloat() - 0.5D) * 0.5D;
		var17 = (par5Random.nextFloat() - 0.5D) * 0.5D;

		world.spawnParticle("townaura", var7, var9, var11, var13,
			var15, var17);
	    }
	}
    }
}