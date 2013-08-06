package mods.jamstone.aurus.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class EntityProjectile extends Entity {
    private int xTile = -1;
    private int yTile = -1;
    private int zTile = -1;

    private int inTile = 0;

    private boolean inGround = false;

    public EntityLiving shootingEntity;

    private int ticksAlive;
    private int ticksInAir = 0;

    public double accelerationX;
    public double accelerationY;
    public double accelerationZ;

    public EntityProjectile(World par1World) {
        super(par1World);
        this.setSize(1.0F, 1.0F);
        this.renderDistanceWeight = 4;
    }

    public EntityProjectile(World world, double x, double y, double z,
                            double xVel, double yVel, double zVel) {
        this(world);

        double d = MathHelper.sqrt_double(xVel * xVel + yVel * yVel + zVel
                * zVel);

        this.accelerationX = xVel / d * 0.1D;
        this.accelerationY = yVel / d * 0.1D;
        this.accelerationZ = zVel / d * 0.1D;

        this.setLocationAndAngles(x + this.accelerationX, y
                + this.accelerationY, z + this.accelerationZ, this.rotationYaw,
                this.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public EntityProjectile(EntityLiving originEntity, double targetX,
                            double targetY, double targetZ, double variation) {
        this(originEntity.worldObj);
        this.shootingEntity = originEntity;

        this.yOffset = 0.0F;
        this.motionX = this.motionY = this.motionZ = 0.0D;

        double originX = originEntity.posX + originEntity.motionX;
        double originY = originEntity.posY + originEntity.height * 0.75f
                + originEntity.motionY;
        double originZ = originEntity.posZ + originEntity.motionZ;

        targetX += this.rand.nextGaussian() * variation;
        targetY += this.rand.nextGaussian() * variation;
        targetZ += this.rand.nextGaussian() * variation;

        double dX = targetX - originX;
        double dY = targetY - originY;
        double dZ = targetZ - originZ;

        double hyp = MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);

        this.accelerationX = dX / hyp * 0.1D;
        this.accelerationY = dY / hyp * 0.1D;
        this.accelerationZ = dZ / hyp * 0.1D;

        double offX = dX / hyp * (originEntity.width + this.width) * 1.1;
        double offY = dY / hyp * (originEntity.width + this.width) * 1.1;
        double offZ = dZ / hyp * (originEntity.width + this.width) * 1.1;

        this.setLocationAndAngles(originX + offX, originY + offY, originZ
                + offZ, originEntity.rotationYaw, originEntity.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public EntityProjectile(EntityLiving originEntity,
                            EntityLiving targetEntity, boolean targetFeet) {
        this(originEntity, targetEntity.posX, targetEntity.boundingBox.minY
                + (targetFeet ? 0 : targetEntity.height * 0.75f),
                targetEntity.posZ, 0);
    }

    public EntityProjectile(EntityLiving originEntity, EntityLiving targetEntity) {
        this(originEntity, targetEntity, false);
    }

    @Override
    protected void entityInit() {
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Checks if the entity is in range to render by using the past in distance and comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double par1) {
        double var3 = this.boundingBox.getAverageEdgeLength() * 4.0D;
        var3 *= 64.0D;
        return par1 < var3 * var3;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate() {

        if (this.ticksExisted > 80) {
            this.setDead();
            return;
        }

        if (!this.worldObj.isRemote
                && (this.shootingEntity != null && this.shootingEntity.isDead || !this.worldObj
                .blockExists((int) this.posX, (int) this.posY,
                        (int) this.posZ))) {
            this.setDead();
        } else {
            super.onUpdate();

            if (this.inGround) {
                int collidingBlockType = this.worldObj.getBlockId(this.xTile,
                        this.yTile, this.zTile);

                if (collidingBlockType == this.inTile) {
                    ++this.ticksAlive;

                    if (this.ticksAlive == 600) {
                        this.setDead();
                    }

                    return;
                }

                this.inGround = false;
                this.motionX *= (this.rand.nextFloat() * 0.2F);
                this.motionY *= (this.rand.nextFloat() * 0.2F);
                this.motionZ *= (this.rand.nextFloat() * 0.2F);
                this.ticksAlive = 0;
                this.ticksInAir = 0;
            } else {
                ++this.ticksInAir;
            }

            // the following code is stolen from EntityFireball and has not been
            // renamed or deciphered yet.

            Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks(vec3, vec31);
            vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null) {
                vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;

            for (int j = 0; j < list.size(); ++j) {
                Entity entity1 = (Entity) list.get(j);

                if (entity1.canBeCollidedWith() && (!entity1.isEntityEqual(this.shootingEntity) || this.ticksInAir >= 25)) {
                    float f = 0.3F;
                    AxisAlignedBB axisalignedbb = entity1.boundingBox.expand((double) f, (double) f, (double) f);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb.calculateIntercept(vec3, vec31);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null) {
                this.onImpact(movingobjectposition);
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionZ, this.motionX) * 180.0D / Math.PI) + 90.0F;

            for (this.rotationPitch = (float) (Math.atan2((double) f1, this.motionY) * 180.0D / Math.PI) - 90.0F; this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f2 = this.getMotionFactor();

            if (this.isInWater()) {
                for (int k = 0; k < 4; ++k) {
                    float f3 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f3, this.posY - this.motionY * (double) f3, this.posZ - this.motionZ * (double) f3, this.motionX, this.motionY, this.motionZ);
                }

                f2 = 0.8F;
            }

            this.motionX += this.accelerationX;
            this.motionY += this.accelerationY;
            this.motionZ += this.accelerationZ;
            this.motionX *= (double) f2;
            this.motionY *= (double) f2;
            this.motionZ *= (double) f2;
            if (ticksExisted == 1) {
                worldObj.spawnParticle("largeexplode", posX - 5 * motionX, posY - 5 * motionY + 0.5, posZ - 5 * motionZ,
                        1.5D, 0.0D, 0.0D);
            } else {
                this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
            }
            this.setPosition(this.posX, this.posY, this.posZ);
        }
    }

    /**
     * Return the motion factor for this projectile. The factor is multiplied by the original motion.
     */
    protected float getMotionFactor() {
        return 0.95F;
    }

    protected void hitEntityLiving(EntityLiving entity) {
        int damage = entity instanceof EntityAuru ? 17 : 4;
        entity.attackEntityFrom(
                DamageSource.causeMobDamage(this.shootingEntity), damage);
    }

    protected void hitBlock(int x, int y, int z, int sideHit) {
    }

    /**
     * Called when this EntityProjectile hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition mop) {
        if (mop.entityHit != null && this.shootingEntity != null) {
            if (this.worldObj.isRemote
                    || !(mop.entityHit instanceof EntityLiving)
                    || mop.entityHit.entityId == this.shootingEntity.entityId) {
                return;
            }

            hitEntityLiving((EntityLiving) mop.entityHit);
            this.setDead();

        } else {
            hitBlock(mop.blockX, mop.blockY, mop.blockZ, mop.sideHit);
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        par1NBTTagCompound.setShort("xTile", (short) this.xTile);
        par1NBTTagCompound.setShort("yTile", (short) this.yTile);
        par1NBTTagCompound.setShort("zTile", (short) this.zTile);
        par1NBTTagCompound.setByte("inTile", (byte) this.inTile);
        par1NBTTagCompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        par1NBTTagCompound.setTag(
                "direction",
                this.newDoubleNBTList(new double[]{this.motionX,
                        this.motionY, this.motionZ}));
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        this.xTile = par1NBTTagCompound.getShort("xTile");
        this.yTile = par1NBTTagCompound.getShort("yTile");
        this.zTile = par1NBTTagCompound.getShort("zTile");
        this.inTile = par1NBTTagCompound.getByte("inTile") & 255;
        this.inGround = par1NBTTagCompound.getByte("inGround") == 1;

        if (par1NBTTagCompound.hasKey("direction")) {
            NBTTagList var2 = par1NBTTagCompound.getTagList("direction");
            this.motionX = ((NBTTagDouble) var2.tagAt(0)).data;
            this.motionY = ((NBTTagDouble) var2.tagAt(1)).data;
            this.motionZ = ((NBTTagDouble) var2.tagAt(2)).data;
        } else {
            this.setDead();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through
     * this Entity.
     */
    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public float getCollisionBorderSize() {
        return 0.75F;
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {
        this.setBeenAttacked();

        if (par1DamageSource.getEntity() != null && !this.worldObj.isRemote) {
            Vec3 var3 = par1DamageSource.getEntity().getLookVec();

            if (var3 != null) {
                this.motionX = var3.xCoord;
                this.motionY = var3.yCoord;
                this.motionZ = var3.zCoord;
                this.accelerationX = this.motionX * 0.1D;
                this.accelerationY = this.motionY * 0.1D;
                this.accelerationZ = this.motionZ * 0.1D;
            }

            if (!this.worldObj.isRemote
                    && par1DamageSource.getEntity() instanceof EntityLiving) {
                this.shootingEntity = (EntityLiving) par1DamageSource
                        .getEntity();
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.5F;
    }

    /**
     * Gets how bright this entity is.
     */
    @Override
    public float getBrightness(float par1) {
        return 1.0F;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
