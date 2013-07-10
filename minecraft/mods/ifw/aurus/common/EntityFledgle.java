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
//import net.minecraft.src.IMob;
//import net.minecraft.src.MathHelper;
//import net.minecraft.src.World;
//
//public class EntityFledgle extends EntityFlying implements IMob {
//    public int courseChangeCooldown = 0;
//    public double waypointX;
//    public double waypointY;
//    public double waypointZ;
//    private Entity targetedEntity = null;
//
//    private double angle;
//
//    public float rotY;
//
//    private int radius;
//    private int aggroCooldown = 0;
//    public int prevAttackCounter = 0;
//    public int attackCounter = 0;
//
//    public EntityFledgle(World par1World) {
//	super(par1World);
//	this.texture = "/textures/Eye.png";
//
//	this.isImmuneToFire = true;
//	this.experienceValue = 5;
//	
//	this.setSize(0.25F, 0.25F);
//    }
//
//    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
//	return super.attackEntityFrom(par1DamageSource, par2);
//    }
//
//    protected void entityInit() {
//	super.entityInit();
//	this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
//    }
//
//    public int getMaxHealth() {
//	return 10;
//    }
//
//    public void onUpdate() {
//	super.onUpdate();
//	if (this.targetedEntity != null) {
//	    double dX2 = this.targetedEntity.posX - this.posX;
//	    double dY2 = this.targetedEntity.posY - this.posY;
//	    double dZ2 = this.targetedEntity.posZ - this.posZ;
//
//	    // this.rotationYaw = ((EntityLiving)
//	    // this.targetedEntity).rotationYaw;
//
//	}
//	byte var1 = this.dataWatcher.getWatchableObjectByte(16);
//    }
//
//    protected void updateEntityActionState() {
//	if ((!this.worldObj.isRemote) && (this.worldObj.difficultySetting == 0)) {
//	    setDead();
//	}
//
//	despawnEntity();
//	this.prevAttackCounter = this.attackCounter;
//
//	double dX1 = this.waypointX - this.posX;
//	double dY1 = this.waypointY - this.posY;
//	double dZ1 = this.waypointZ - this.posZ;
//
//	double d1sq = dX1 * dX1 + dY1 * dY1 + dZ1 * dZ1;
//	// keep at a distance
//	if (d1sq > 64.0D) {
//	    double d1 = MathHelper.sqrt_double(d1sq);
//	    if (isCourseTraversable(this.waypointX, this.waypointY,
//		    this.waypointZ, d1)) {
//		this.motionX += dX1 / d1 * 0.04D;
//		this.motionY += dY1 / d1 * 0.04D;
//		this.motionZ += dZ1 / d1 * 0.04D;
//	    } else {
//		this.targetedEntity = null;
//	    }
//	} else if (this.targetedEntity == null) {
//	    this.waypointX = (this.posX + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//	    this.waypointY = (this.posY + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//	    this.waypointZ = (this.posZ + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//	} else if (d1sq < 16.0D) {
//	    this.waypointX = (this.posX - 4.0D * (this.targetedEntity.posX - this.posX));
//	    this.waypointY = (this.targetedEntity.posY + 3.0D);
//	    this.waypointZ = (this.posZ - 4.0D * (this.targetedEntity.posZ - this.posZ));
//	}
//
//	if (this.courseChangeCooldown-- <= 0) {
//	    this.courseChangeCooldown += this.rand.nextInt(5) + 2;
//	    if (this.targetedEntity != null) {
//		this.waypointX = this.targetedEntity.posX;
//		this.waypointY = (this.targetedEntity.posY + 1.5D);
//		this.waypointZ = this.targetedEntity.posZ;
//	    } else {
//		this.waypointX = (this.posX + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//		this.waypointY = (this.posY + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//		this.waypointZ = (this.posZ + (this.rand.nextFloat() * 2F - 1F) * 16.0F);
//	    }
//	}
//
//	if ((this.targetedEntity != null) && (this.targetedEntity.isDead)) {
//	    this.targetedEntity = null;
//	}
//
//	if ((this.targetedEntity == null) || (this.aggroCooldown-- <= 0)) {
//	    this.targetedEntity = this.worldObj
//		    .getClosestVulnerablePlayerToEntity(this, 100.0D);
//
//	    if (this.targetedEntity != null) {
//		this.aggroCooldown = 20;
//	    }
//	}
//
//	double var9 = 64.0D;
//
//	if (this.targetedEntity != null) {
//	    double dX2 = this.targetedEntity.posX - this.posX;
//	    double dY2 = this.targetedEntity.posY - this.posY;
//	    double dZ2 = this.targetedEntity.posZ - this.posZ;
//
//	    this.rotationYaw = -(float) (Math.atan2(dX2, dZ2)) * 180 / 3.14f;
//
//	    // if (canEntityBeSeen(this.targetedEntity)) {
//	    // this.attackCounter += 1;
//	    //
//	    // if (this.attackCounter == 20) {
//	    // this.attackCounter = -40;
//	    // }
//	    // } else if (this.attackCounter > 0) {
//	    // this.attackCounter -= 1;
//	    // }
//	} else {
//	    this.renderYawOffset = (this.rotationYaw = -(float) Math.atan2(
//		    this.motionX, this.motionZ) * 180.0F / 3.1415927F);
//
////	    if (this.attackCounter > 0) {
////		this.attackCounter -= 1;
////	    }
//	}
//
////	if (!this.worldObj.isRemote) {
////	    byte var21 = this.dataWatcher.getWatchableObjectByte(16);
////	    byte var12 = (byte) (this.attackCounter > 10 ? 1 : 0);
////
////	    if (var21 != var12) {
////		this.dataWatcher.updateObject(16, Byte.valueOf(var12));
////	    }
////	}
//    }
//
//    private boolean isCourseTraversable(double par1, double par3, double par5,
//	    double par7) {
//	double var9 = (this.waypointX - this.posX) / par7;
//	double var11 = (this.waypointY - this.posY) / par7;
//	double var13 = (this.waypointZ - this.posZ) / par7;
//	AxisAlignedBB var15 = this.boundingBox.copy();
//
//	for (int var16 = 1; var16 < par7; var16++) {
//	    var15.offset(var9, var11, var13);
//
//	    if (!this.worldObj.getCollidingBoundingBoxes(this, var15).isEmpty()) {
//		return false;
//	    }
//	}
//
//	return true;
//    }
//
//    protected String getLivingSound() {
//	return "";
//    }
//
//    protected String getHurtSound() {
//	return "mob.ghast.scream";
//    }
//
//    protected String getDeathSound() {
//	return "mob.ghast.death";
//    }
//
//    protected int getDropItemId() {
//	return 0;
//    }
//
//    protected void dropFewItems(boolean par1, int par2) {
//    }
//
//    protected float getSoundVolume() {
//	return 1.0F;
//    }
//
//    public boolean getCanSpawnHere() {
//	return (this.rand.nextInt(20) == 0) && (super.getCanSpawnHere())
//		&& (this.worldObj.difficultySetting > 0);
//    }
//
//    public int getMaxSpawnedInChunk() {
//	return 10;
//    }
//}