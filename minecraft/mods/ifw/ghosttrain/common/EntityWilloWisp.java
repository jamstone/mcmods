package mods.ifw.ghosttrain.common;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityWilloWisp extends EntityFlying implements IMob {

    public EntityWilloWisp(World par1World) {
        super(par1World);
        this.texture = "/mods/ifw_ghosttrain/textures/models/willoWisp.png";
        this.setSize(0.5f, 0.5f); // size determines vanishing point of

        this.isImmuneToFire = true;
        this.experienceValue = 1;
    }

    @Override
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2) {

        return super.attackEntityFrom(par1DamageSource, par2);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
//        this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
    }

    @Override
    public int getMaxHealth() {
        return 1;
    }

    public int getAttackStrength(Entity par1Entity) {
        return 1;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote && this.rand.nextInt(100) < 50) {
            float hueShift = rand.nextFloat() * 0.3f;
            this.worldObj.spawnParticle("reddust", this.posX, this.posY, this.posZ, 0.5f, 1 + hueShift, 1 - hueShift);
        }
        if (!this.worldObj.isRemote) {
            if (worldObj.isAirBlock((int) Math.round(this.posX), (int) Math.round(this.posY), (int) Math.round(this.posZ)) || worldObj.getBlockId((int) Math.round(this.posX), (int) Math.round(this.posY), (int) Math.round(this.posZ)) == GhostTrain.blockBrightSpace.blockID) {
                worldObj.setBlock((int) Math.round(this.posX), (int) Math.round(this.posY), (int) Math.round(this.posZ), GhostTrain.blockBrightSpace.blockID, 0, 3);
            }
        }
//        byte var1 = this.dataWatcher.getWatchableObjectByte(16);
    }

    @Override
    protected void updateEntityActionState() {
        if ((!this.worldObj.isRemote) && (this.worldObj.difficultySetting == 0)) {
            setDead();
        }

        despawnEntity();

        this.rotationPitch = 90;
        this.rotationYaw += 30f;
        this.motionY += 0.005d * Math.sin(this.ticksExisted / 3d);
        this.motionX += 0.01d * Math.sin(this.ticksExisted / 6d);
        this.motionZ += 0.01d * Math.cos(this.ticksExisted / 6d);
    }

    @Override
    public int getMaxSpawnedInChunk() {
        return 2;
    }

    @Override
    public int getBrightnessForRender(float par1) {
        return 240;
    }

    @Override
    public boolean shouldRenderInPass(int i) {
        return i == 1;
    }
}
