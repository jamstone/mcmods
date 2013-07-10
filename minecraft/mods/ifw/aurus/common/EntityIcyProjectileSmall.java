package mods.ifw.aurus.common;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityIcyProjectileSmall extends EntityProjectile {

	public EntityIcyProjectileSmall(World par1World) {
		super(par1World);
		// TODO Auto-generated constructor stub
	}

	public EntityIcyProjectileSmall(World world, double x, double y, double z,
			double xVel, double yVel, double zVel) {
		super(world, x, y, z, xVel, yVel, zVel);
		// TODO Auto-generated constructor stub
	}

	public EntityIcyProjectileSmall(EntityLiving originEntity, double targetX,
			double targetY, double targetZ, double variation) {
		super(originEntity, targetX, targetY, targetZ, variation);
		// TODO Auto-generated constructor stub
	}

	public EntityIcyProjectileSmall(EntityLiving originEntity,
			EntityLiving targetEntity, boolean targetFeet) {
		super(originEntity, targetEntity, targetFeet);
		// TODO Auto-generated constructor stub
	}

	public EntityIcyProjectileSmall(EntityLiving originEntity,
			EntityLiving targetEntity) {
		super(originEntity, targetEntity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void hitBlock(int x, int y, int z, int sideHit) {
		if (!this.worldObj.isRemote) {
			int dX = 0;
			int dY = 0;
			int dZ = 0;
			switch (sideHit) {
			case 0:
				--y;
				dY--;
				break;
			case 1:
				++y;
				dY++;
				break;
			case 2:
				--z;
				dZ--;
				break;
			case 3:
				++z;
				dZ++;
				break;
			case 4:
				--x;
				dX--;
				break;
			case 5:
				++x;
				dX++;
				break;
			}

			AxisAlignedBB aabb = this.boundingBox.copy();
			aabb = aabb.offset(this.motionX, this.motionY, this.motionZ);

			double radius = worldObj.rand.nextDouble()
					* worldObj.rand.nextDouble() * 6 + 1;

			aabb = aabb.expand(radius, radius, radius);

			ArrayList<AxisAlignedBB> AABBList = (ArrayList<AxisAlignedBB>) this.worldObj
					.getCollidingBlockBounds(aabb);

			for (AxisAlignedBB blockBox : AABBList) {
				if ((Math.pow(blockBox.minX - x, 2)
						+ Math.pow(blockBox.minY - y, 2) + Math.pow(
						blockBox.minZ - z, 2)) <= (Math.pow(radius, 2))) {
					MovingObjectPosition mop = this.worldObj.rayTraceBlocks(
							this.worldObj.getWorldVec3Pool().getVecFromPool(
									x - motionX * radius, y - motionY * radius,
									z - motionZ * radius),
							this.worldObj.getWorldVec3Pool().getVecFromPool(
									blockBox.minX + 0.0 - motionX * 2,
									blockBox.minY + 0.0 - motionY * 2,
									blockBox.minZ + 0.0 - motionZ * 2));

					if (mop == null
							|| (mop.blockX == (int) blockBox.minX
									&& mop.blockY == (int) blockBox.minY && mop.blockZ == (int) blockBox.minZ)) {
						if (this.worldObj.isBlockNormalCube(
								(int) blockBox.minX, (int) blockBox.minY,
								(int) blockBox.minZ)) {
							this.worldObj.setBlock((int) blockBox.minX,
									(int) blockBox.minY, (int) blockBox.minZ,
									Block.ice.blockID);
						} else if (this.worldObj.getBlockId(
								(int) blockBox.minX, (int) blockBox.minY,
								(int) blockBox.minZ) == Block.snow.blockID) {
							this.worldObj.setBlock((int) blockBox.minX,
									(int) blockBox.minY - 1,
									(int) blockBox.minZ, Block.ice.blockID);
						}
					}
				}
			}

			// for (int i = this.rand.nextInt(4) + 2; i >= 0; i--) {
			// if (this.worldObj
			// .isAirBlock(x + i * dX, y + i * dY, z + i * dZ)) {
			// this.worldObj.setBlock(x + i * dX, y + i * dY, z + i * dZ,
			// Block.ice.blockID);
			// this.worldObj.scheduleBlockUpdate(x + i * dX, y + i * dY, z
			// + i * dZ, Block.ice.blockID, 1);
			//
			// }
			// }
		}
		this.worldObj.playSoundEffect((x + 0.5F), (y + 0.5F), (z + 0.5F),
				"random.glass", 10.0F,
				1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
	}

	@Override
	protected void hitEntityLiving(EntityLiving entity) {
		super.hitEntityLiving(entity);
		PotionEffect slow = new PotionEffect(Potion.moveSlowdown.id, 120, 3);
		entity.addPotionEffect(slow);
		PotionEffect harm = new PotionEffect(Potion.harm.id, 1, 0);
		entity.addPotionEffect(harm);

		double x = this.posX;
		double y = this.posY;
		double z = this.posZ;

		this.worldObj.playSoundEffect((x + 0.5F), (y + 0.5F), (z + 0.5F),
				"random.glass", 10.0F,
				1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);

	}

	public boolean isRayOpen(double x1, double y1, double z1, double x2,
			double y2, double z2) {
		MovingObjectPosition mop = this.worldObj.rayTraceBlocks(this.worldObj
				.getWorldVec3Pool().getVecFromPool(x1, y1, z1), this.worldObj
				.getWorldVec3Pool().getVecFromPool(x2, y2, z2));

		return mop == null;
	}
}
