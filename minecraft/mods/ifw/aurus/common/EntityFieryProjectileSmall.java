package mods.ifw.aurus.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.world.World;

public class EntityFieryProjectileSmall extends EntityProjectile {

    public EntityFieryProjectileSmall(World par1World) {
        super(par1World);
        // TODO Auto-generated constructor stub
    }

    public EntityFieryProjectileSmall(World world, double x, double y,
                                      double z, double xVel, double yVel, double zVel) {
        super(world, x, y, z, xVel, yVel, zVel);
        // TODO Auto-generated constructor stub
    }

    public EntityFieryProjectileSmall(EntityLiving originEntity,
                                      double targetX, double targetY, double targetZ, double variation) {
        super(originEntity, targetX, targetY, targetZ, variation);
        // TODO Auto-generated constructor stub
    }

    public EntityFieryProjectileSmall(EntityLiving originEntity,
                                      EntityLiving targetEntity, boolean targetFeet) {
        super(originEntity, targetEntity, targetFeet);
        // TODO Auto-generated constructor stub
    }

    public EntityFieryProjectileSmall(EntityLiving originEntity,
                                      EntityLiving targetEntity) {
        super(originEntity, targetEntity);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void hitBlock(int x, int y, int z, int sideHit) {
        if (!this.worldObj.isRemote) {
            if (this.worldObj.getBlockMaterial(x, y, z) == Material.water) {
                if (this.worldObj.setBlock(x, y, z, 0, 0, 3)) {
                    this.worldObj.notifyBlocksOfNeighborChange(x, y, z, 0);
                }
                return;
            } else if (this.worldObj.getBlockMaterial(x, y, z) == Material.ice) {
                if (!this.worldObj.isRemote) {
                    if (this.worldObj.setBlock(x, y, z,
                            Block.waterMoving.blockID)) {

                        this.worldObj.notifyBlocksOfNeighborChange(x, y, z,
                                Block.waterMoving.blockID);

                        this.worldObj.scheduleBlockUpdate(x, y, z,
                                Block.waterMoving.blockID, 8);
                    }
                    return;
                }
            }
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

            int initialDecay = 4;

            if (this.worldObj.isAirBlock(x, y, z))
                this.worldObj.setBlock(x, y, z,
                        Block.lavaMoving.blockID, initialDecay, 3);
            if (this.worldObj.isAirBlock(x - 1, y, z)
                    && this.rand.nextInt(5) == 0)
                this.worldObj.setBlock(x - 1, y, z,
                        Block.lavaMoving.blockID, initialDecay + 1, 3);
            if (this.worldObj.isAirBlock(x, y, z - 1)
                    && this.rand.nextInt(5) == 0)
                this.worldObj.setBlock(x, y, z - 1,
                        Block.lavaMoving.blockID, initialDecay + 1, 3);
            if (this.worldObj.isAirBlock(x + 1, y, z)
                    && this.rand.nextInt(5) == 0)
                this.worldObj.setBlock(x + 1, y, z,
                        Block.lavaMoving.blockID, initialDecay + 1, 3);
            if (this.worldObj.isAirBlock(x, y, z + 1)
                    && this.rand.nextInt(5) == 0)
                this.worldObj.setBlock(x, y, z + 1,
                        Block.lavaMoving.blockID, initialDecay + 1, 3);

            // this.worldObj.scheduleBlockUpdate(x, y, z,
            // Block.lavaMoving.blockID, 1);
            // this.worldObj.scheduleBlockUpdate(x - 1, y, z,
            // Block.lavaMoving.blockID, 1);
            // this.worldObj.scheduleBlockUpdate(x + 1, y, z,
            // Block.lavaMoving.blockID, 1);
            // this.worldObj.scheduleBlockUpdate(x, y, z - 1,
            // Block.lavaMoving.blockID, 1);
            // this.worldObj.scheduleBlockUpdate(x, y, z + 1,
            // Block.lavaMoving.blockID, 1);

        }
    }

    @Override
    protected void hitEntityLiving(EntityLiving entity) {
        // TODO Auto-generated method stub
        super.hitEntityLiving(entity);

        if (entity.attackEntityFrom(new EntityDamageSourceIndirect("fireball",
                this, this.shootingEntity).setProjectile(), 4)) {
            entity.setFire(5);
        }
    }
}
