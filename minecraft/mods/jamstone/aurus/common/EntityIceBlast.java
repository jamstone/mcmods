package mods.jamstone.aurus.common;
//package jamstone.aurus.common;
//
//import net.minecraft.src.AxisAlignedBB;
//import net.minecraft.src.Block;
//import net.minecraft.src.Entity;
//import net.minecraft.src.EntityFireball;
//import net.minecraft.src.EntityLiving;
//import net.minecraft.src.MathHelper;
//import net.minecraft.src.MovingObjectPosition;
//import net.minecraft.src.World;
//
//public class EntityIceBlast extends EntityProjectile {
//    public EntityIceBlast(World par1World) {
//	super(par1World);
//    }
//
//    @cpw.mods.fml.common.asm.SideOnly(cpw.mods.fml.common.Side.CLIENT)
//    public EntityIceBlast(World par1World, double par2, double par4,
//	    double par6, double par8, double par10, double par12) {
//	super(par1World, par2, par4, par6, par8, par10, par12);
//    }
//
//    public EntityIceBlast(World par1World, EntityLiving par2EntityLiving,
//	    double par3, double par5, double par7) {
//	super(par2EntityLiving, par3, par5, par7, 0);
//    }
//
//    public EntityIceBlast(World par1World, EntityLiving originEntity,
//	    EntityLiving targetEntity, boolean targetFeet, double offX,
//	    double offY, double offZ) {
//	super(originEntity, 0D, 0D, 0D, 0);
//
//	double speed = 0.05D;
//
//	double dX = targetEntity.posX - originEntity.posX + offX;
//	double dY = (targetFeet ? targetEntity.boundingBox.minY
//		: targetEntity.posY) - originEntity.posY + offY;
//	double dZ = targetEntity.posZ - originEntity.posZ + offZ;
//
//	double hyp = MathHelper.sqrt_double(dX * dX + dY * dY + dZ * dZ);
//
//	this.accelerationX = (dX / hyp * speed);
//	this.accelerationY = (dY / hyp * speed);
//	this.accelerationZ = (dZ / hyp * speed);
//    }
//
//    public EntityIceBlast(World par1World, EntityLiving par2EntityLiving,
//	    EntityLiving par3EntityLiving, boolean targetFeet) {
//	this(par1World, par2EntityLiving, par3EntityLiving, targetFeet, 0D, 0D,
//		0D);
//    }
//
//    protected void onImpact(MovingObjectPosition mop) {
//	if (!this.worldObj.isRemote) {
//	    int x, y, z;
//	    if (mop.entityHit != null) {
//		if ((this.ticksExisted < 100 && mop.entityHit == this.shootingEntity)
//			|| (mop.entityHit instanceof EntityIceBlast)) {
//		    return;
//		}
//
//		x = (int) mop.entityHit.posX;
//		y = (int) mop.entityHit.posY;
//		z = (int) mop.entityHit.posZ;
//	    } else {
//		x = (int) this.posX;
//		y = (int) this.posY;
//		z = (int) this.posZ;
//	    }
//
//	    World par1World = this.worldObj;
//
//	    int block = Block.ice.blockID;
//
//	    int[][] indices = { { 0, -2, 0 }, { -1, -1, -1 }, { -1, -1, 0 },
//		    { -1, -1, 1 }, { 0, -1, -1 }, { 0, -1, 0 }, { 0, -1, 1 },
//		    { 1, -1, -1 }, { 1, -1, 0 }, { 1, -1, 1 }, { -3, 0, 0 },
//		    { -2, 0, -2 }, { -2, 0, 0 }, { -2, 0, 2 }, { -1, 0, -1 },
//		    { -1, 0, 0 }, { -1, 0, 1 }, { 0, 0, -3 }, { 0, 0, -2 },
//		    { 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 0, 0, 2 },
//		    { 0, 0, 3 }, { 1, 0, -1 }, { 1, 0, 0 }, { 1, 0, 1 },
//		    { 2, 0, -2 }, { 2, 0, 0 }, { 2, 0, 2 }, { 3, 0, 0 },
//		    { -2, 1, 0 }, { -1, 1, -1 }, { -1, 1, 0 }, { -1, 1, 1 },
//		    { 0, 1, -2 }, { 0, 1, -1 }, { 0, 1, 0 }, { 0, 1, 1 },
//		    { 0, 1, 2 }, { 1, 1, -1 }, { 1, 1, 0 }, { 1, 1, 1 },
//		    { 2, 1, 0 }, { -2, 2, 0 }, { -1, 2, -1 }, { -1, 2, 0 },
//		    { -1, 2, 1 }, { 0, 2, -2 }, { 0, 2, -1 }, { 0, 2, 0 },
//		    { 0, 2, 1 }, { 0, 2, 2 }, { 1, 2, -1 }, { 1, 2, 0 },
//		    { 1, 2, 1 }, { 2, 2, 0 }, { -1, 3, 0 }, { 0, 3, -1 },
//		    { 0, 3, 0 }, { 0, 3, 1 }, { 1, 3, 0 }, { -1, 4, 0 },
//		    { 0, 4, -1 }, { 0, 4, 0 }, { 0, 4, 1 }, { 1, 4, 0 },
//		    { -1, 5, 0 }, { 0, 5, -1 }, { 0, 5, 0 }, { 0, 5, 1 },
//		    { 1, 5, 0 }, { 0, 6, 0 }, { 0, 7, 0 }, { 0, 8, 0 } };
//
//	    for (int i = 0; i < indices.length; i++) {
//		par1World.setBlockWithNotify(x + indices[i][0], y
//			+ indices[i][1], z + indices[i][2], block);
//	    }
//
//	    setDead();
//	}
//    }
//}
