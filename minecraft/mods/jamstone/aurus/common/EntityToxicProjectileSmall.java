package mods.jamstone.aurus.common;

import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class EntityToxicProjectileSmall extends EntityProjectile {

	public EntityToxicProjectileSmall(World par1World) {
		super(par1World);
	}

	public EntityToxicProjectileSmall(World world, double x, double y,
			double z, double xVel, double yVel, double zVel) {
		super(world, x, y, z, xVel, yVel, zVel);
	}

	public EntityToxicProjectileSmall(EntityLiving originEntity,
			double targetX, double targetY, double targetZ, double variation) {
		super(originEntity, targetX, targetY, targetZ, variation);
	}

	public EntityToxicProjectileSmall(EntityLiving originEntity,
			EntityLiving targetEntity, boolean targetFeet) {
		super(originEntity, targetEntity, targetFeet);
	}

	public EntityToxicProjectileSmall(EntityLiving originEntity,
			EntityLiving targetEntity) {
		super(originEntity, targetEntity);
	}

	@Override
	protected void hitEntityLiving(EntityLiving entity) {
		super.hitEntityLiving(entity);
		PotionEffect poison = new PotionEffect(Potion.poison.id, 80, 2);
		entity.addPotionEffect(poison);
	}

	@Override
	protected void hitBlock(int x, int y, int z, int sideHit) {
		super.hitBlock(x, y, z, sideHit);
		if (!this.worldObj.isRemote) {
			switch (sideHit) {
			case 0:
				--y;
				break;
			case 1:
				++y;
				break;
			case 2:
				--z;
				break;
			case 3:
				++z;
				break;
			case 4:
				--x;
				break;
			case 5:
				++x;
			}

			int initialDecay = 3;
			if (this.worldObj.isAirBlock(x, y, z))
				this.worldObj.setBlock(x, y, z, Aurus.bogWaterFlowingID,
						initialDecay, 3);

			if (this.worldObj.isAirBlock(x - 1, y, z))
				this.worldObj.setBlock(x - 1, y, z,
						Aurus.bogWaterFlowingID, initialDecay + 1, 3);

			if (this.worldObj.isAirBlock(x, y, z - 1))
				this.worldObj.setBlock(x, y, z - 1,
						Aurus.bogWaterFlowingID, initialDecay + 1, 3);

			if (this.worldObj.isAirBlock(x + 1, y, z))
				this.worldObj.setBlock(x + 1, y, z,
						Aurus.bogWaterFlowingID, initialDecay + 1, 3);

			if (this.worldObj.isAirBlock(x, y, z + 1))
				this.worldObj.setBlock(x, y, z + 1,
						Aurus.bogWaterFlowingID, initialDecay + 1, 3);

			// this.worldObj.scheduleBlockUpdate(x, y, z,
			// mod_dragn.bogWaterFlowingID, 1);
			// this.worldObj.scheduleBlockUpdate(x - 1, y, z,
			// mod_dragn.bogWaterFlowingID, 1);
			// this.worldObj.scheduleBlockUpdate(x + 1, y, z,
			// mod_dragn.bogWaterFlowingID, 1);
			// this.worldObj.scheduleBlockUpdate(x, y, z - 1,
			// mod_dragn.bogWaterFlowingID, 1);
			// this.worldObj.scheduleBlockUpdate(x, y, z + 1,
			// mod_dragn.bogWaterFlowingID, 1);
		}
	}
}
