package mods.jamstone.aurus.common;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

public class EntitySparkyProjectileSmall extends EntityProjectile {

    public EntitySparkyProjectileSmall(World par1World) {
        super(par1World);
    }

    public EntitySparkyProjectileSmall(World world, double x, double y,
                                       double z, double xVel, double yVel, double zVel) {
        super(world, x, y, z, xVel, yVel, zVel);
    }

    public EntitySparkyProjectileSmall(EntityLiving originEntity,
                                       double targetX, double targetY, double targetZ, double variation) {
        super(originEntity, targetX, targetY, targetZ, variation);
    }

    public EntitySparkyProjectileSmall(EntityLiving originEntity,
                                       EntityLiving targetEntity, boolean targetFeet) {
        super(originEntity, targetEntity, targetFeet);
    }

    public EntitySparkyProjectileSmall(EntityLiving originEntity,
                                       EntityLiving targetEntity) {
        super(originEntity, targetEntity);
    }

    @Override
    protected void hitEntityLiving(EntityLiving entity) {
        super.hitEntityLiving(entity);
        double x = this.posX;
        double y = this.posY;
        double z = this.posZ;

        entity.motionX += this.motionX * 2;
        entity.motionZ += this.motionZ * 2;
        entity.motionY += 0.5;

        this.worldObj.playSoundEffect((x + 0.5F), (y + 0.5F), (z + 0.5F),
                "random.fizz", 0.5F,
                2.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.8F);
    }

    @Override
    protected void hitBlock(int x, int y, int z, int sideHit) {
        super.hitBlock(x, y, z, sideHit);

        if ((x + 2 * z) % 5 == 0 || this.ticksExisted > 20) {

            y = this.worldObj.getHeightValue(x, z);

            this.worldObj.spawnEntityInWorld(new EntityLightningBolt(
                    this.worldObj, x, y, z));
        } else {
            this.worldObj
                    .playSoundEffect((x + 0.5F), (y + 0.5F), (z + 0.5F),
                            "random.fizz", 0.5F,
                            2.6F + (this.rand.nextFloat() - this.rand
                                    .nextFloat()) * 0.8F);
            for (int i = 0; i < 8; ++i) {
                this.worldObj.spawnParticle("largesmoke", x + Math.random(),
                        y + 1.2D, z + Math.random(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    @Override
    public void onUpdate() {
        if (this.ticksExisted > 80) {
            int y = this.worldObj.getHeightValue((int) posX, (int) posZ);

            this.worldObj.spawnEntityInWorld(new EntityLightningBolt(
                    this.worldObj, (int) posX, y, (int) posZ));
        }
        super.onUpdate();
    }
}
