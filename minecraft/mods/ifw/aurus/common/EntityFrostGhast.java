package mods.ifw.aurus.common;
//package ifw.aurus.common;
//
//import java.util.List;
//import java.util.Random;
//import net.minecraft.src.AxisAlignedBB;
//import net.minecraft.src.DamageSource;
//import net.minecraft.src.DataWatcher;
//import net.minecraft.src.Entity;
//import net.minecraft.src.EntityFlying;
//import net.minecraft.src.EntityLiving;
//import net.minecraft.src.EntityPlayer;
//import net.minecraft.src.IMob;
//import net.minecraft.src.Item;
//import net.minecraft.src.MathHelper;
//import net.minecraft.src.World;
//
//public class EntityFrostGhast extends EntityFlying implements IMob {
//	public int courseChangeCooldown = 0;
//	public double waypointX;
//	public double waypointY;
//	public double waypointZ;
//	private Entity targetedEntity = null;
//
//	private int aggroCooldown = 0;
//	public int prevAttackCounter = 0;
//	public int attackCounter = 0;
//
//	public EntityFrostGhast(World par1World) {
//		super(par1World);
//		this.texture = "/mob/ghast.png";
//		setSize(4.0F, 4.0F);
//		this.isImmuneToFire = true;
//		this.experienceValue = 5;
//	}
//
//	public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
//		if (("fireball".equals(par1DamageSource.getDamageType()))
//				&& ((par1DamageSource.getEntity() instanceof EntityPlayer))) {
//			super.attackEntityFrom(par1DamageSource, 1000);
//			((EntityPlayer) par1DamageSource.getEntity())
//					.triggerAchievement(net.minecraft.src.AchievementList.ghast);
//			return true;
//		}
//
//		return super.attackEntityFrom(par1DamageSource, par2);
//	}
//
//	protected void entityInit() {
//		super.entityInit();
//		this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
//	}
//
//	public int getMaxHealth() {
//		return 10;
//	}
//
//	public void onUpdate() {
//		super.onUpdate();
//		byte var1 = this.dataWatcher.getWatchableObjectByte(16);
//		this.texture = (var1 == 1 ? "/mob/ghast_fire.png" : "/mob/ghast.png");
//	}
//
//	protected void updateEntityActionState() {
//		if ((!this.worldObj.isRemote) && (this.worldObj.difficultySetting == 0)) {
//			setDead();
//		}
//
//		despawnEntity();
//		this.prevAttackCounter = this.attackCounter;
//		double var1 = this.waypointX - this.posX;
//		double var3 = this.waypointY - this.posY;
//		double var5 = this.waypointZ - this.posZ;
//		double var7 = var1 * var1 + var3 * var3 + var5 * var5;
//
//		if ((var7 < 1D) || (var7 > 3600.0D)) {
//			this.waypointX = (this.posX + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//			this.waypointY = (this.posY + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//			this.waypointZ = (this.posZ + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//		}
//
//		if (this.courseChangeCooldown-- <= 0) {
//			this.courseChangeCooldown += this.rand.nextInt(5) + 2;
//			var7 = MathHelper.sqrt_double(var7);
//
//			if (isCourseTraversable(this.waypointX, this.waypointY,
//					this.waypointZ, var7)) {
//				this.motionX += var1 / var7 * 0.1D;
//				this.motionY += var3 / var7 * 0.1D;
//				this.motionZ += var5 / var7 * 0.1D;
//			} else {
//				this.waypointX = this.posX;
//				this.waypointY = this.posY;
//				this.waypointZ = this.posZ;
//			}
//		}
//
//		if ((this.targetedEntity != null) && (this.targetedEntity.isDead)) {
//			this.targetedEntity = null;
//		}
//
//		if ((this.targetedEntity == null) || (this.aggroCooldown-- <= 0)) {
//			this.targetedEntity = this.worldObj
//					.getClosestVulnerablePlayerToEntity(this, 100.0D);
//
//			if (this.targetedEntity != null) {
//				this.aggroCooldown = 20;
//			}
//		}
//
//		double var9 = 64.0D;
//
//		if ((this.targetedEntity != null)
//				&& (this.targetedEntity.getDistanceSqToEntity(this) < var9
//						* var9)) {
//			double var11 = this.targetedEntity.posX - this.posX;
//			double var13 = this.targetedEntity.boundingBox.minY
//					+ this.targetedEntity.height / 2F
//					- (this.posY + this.height / 2F);
//			double var15 = this.targetedEntity.posZ - this.posZ;
//			this.renderYawOffset = (this.rotationYaw = -(float) Math.atan2(
//					var11, var15) * 180.0F / 3.1415927F);
//
//			if (canEntityBeSeen(this.targetedEntity)) {
//				this.attackCounter += 1;
//
//				if (this.attackCounter == 20) {
//					double n = this.rand.nextDouble() * 2.0D - 1D;
//
//					EntityIceBlast var16 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true, 0D,
//							0D + this.rand.nextDouble() * 2.0D - 1D, 0D);
//					EntityIceBlast var17 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true,
//							2.0D, 0D + this.rand.nextDouble() * 2.0D - 1D, 2.0D);
//					EntityIceBlast var18 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true,
//							-2.0D, 0D + this.rand.nextDouble() * 2.0D - 1D,
//							2.0D);
//					EntityIceBlast var19 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true, 0D,
//							0D + this.rand.nextDouble() * 2.0D - 1D, -3.0D);
//					EntityIceBlast var20 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true,
//							-2.0D, 0D + this.rand.nextDouble() * 2.0D - 1D,
//							-2.0D);
//					EntityIceBlast var21 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true,
//							2.0D, 0D + this.rand.nextDouble() * 2.0D - 1D,
//							-2.0D);
//					EntityIceBlast var22 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true, 0D,
//							0D + this.rand.nextDouble() * 2.0D - 1D, 3.0D);
//					EntityIceBlast var23 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true,
//							3.0D, 0D + this.rand.nextDouble() * 2.0D - 1D, 0D);
//					EntityIceBlast var24 = new EntityIceBlast(this.worldObj,
//							this, (EntityLiving) this.targetedEntity, true,
//							-3.0D, 0D + this.rand.nextDouble() * 2.0D - 1D, 0D);
//
//					this.worldObj.spawnEntityInWorld(var16);
//					this.worldObj.spawnEntityInWorld(var17);
//					this.worldObj.spawnEntityInWorld(var18);
//					this.worldObj.spawnEntityInWorld(var19);
//					this.worldObj.spawnEntityInWorld(var20);
//					this.worldObj.spawnEntityInWorld(var21);
//					this.worldObj.spawnEntityInWorld(var22);
//					this.worldObj.spawnEntityInWorld(var23);
//					this.worldObj.spawnEntityInWorld(var24);
//					this.attackCounter = -20;
//				}
//			} else if (this.attackCounter > 0) {
//				this.attackCounter -= 1;
//			}
//		} else {
//			this.renderYawOffset = (this.rotationYaw = -(float) Math.atan2(
//					this.motionX, this.motionZ) * 180.0F / 3.1415927F);
//
//			if (this.attackCounter > 0) {
//				this.attackCounter -= 1;
//			}
//		}
//
//		if (!this.worldObj.isRemote) {
//			byte var21 = this.dataWatcher.getWatchableObjectByte(16);
//			byte var12 = (byte) (this.attackCounter > 10 ? 1 : 0);
//
//			if (var21 != var12) {
//				this.dataWatcher.updateObject(16, Byte.valueOf(var12));
//			}
//		}
//	}
//
//	private boolean isCourseTraversable(double par1, double par3, double par5,
//			double par7) {
//		double var9 = (this.waypointX - this.posX) / par7;
//		double var11 = (this.waypointY - this.posY) / par7;
//		double var13 = (this.waypointZ - this.posZ) / par7;
//		AxisAlignedBB var15 = this.boundingBox.copy();
//
//		for (int var16 = 1; var16 < par7; var16++) {
//			var15.offset(var9, var11, var13);
//
//			if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty()) {
//				return false;
//			}
//		}
//
//		return true;
//	}
//
//	protected String getLivingSound() {
//		return "mob.ghast.moan";
//	}
//
//	protected String getHurtSound() {
//		return "mob.ghast.scream";
//	}
//
//	protected String getDeathSound() {
//		return "mob.ghast.death";
//	}
//
//	protected int getDropItemId() {
//		return Item.gunpowder.shiftedIndex;
//	}
//
//	protected void dropFewItems(boolean par1, int par2) {
//		int var3 = this.rand.nextInt(2) + this.rand.nextInt(1 + par2);
//
//		for (int var4 = 0; var4 < var3; var4++) {
//			dropItem(Item.ghastTear.shiftedIndex, 1);
//		}
//
//		var3 = this.rand.nextInt(3) + this.rand.nextInt(1 + par2);
//
//		for (int var4 = 0; var4 < var3; var4++) {
//			dropItem(Item.gunpowder.shiftedIndex, 1);
//		}
//	}
//
//	protected float getSoundVolume() {
//		return 10.0F;
//	}
//
//	public boolean getCanSpawnHere() {
//		return (this.rand.nextInt(20) == 0) && (super.getCanSpawnHere())
//				&& (this.worldObj.difficultySetting > 0);
//	}
//
//	public int getMaxSpawnedInChunk() {
//		return 1;
//	}
//}
