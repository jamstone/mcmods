package mods.ifw.aurus.common;
//package ifw.aurus.common;
//
//import cpw.mods.fml.common.Side;
//import net.minecraft.src.AABBPool;
//import net.minecraft.src.AxisAlignedBB;
//import net.minecraft.src.Block;
//import net.minecraft.src.Entity;
//import net.minecraft.src.EntityLiving;
//import net.minecraft.src.IBlockAccess;
//import net.minecraft.src.Material;
//import net.minecraft.src.Potion;
//import net.minecraft.src.PotionEffect;
//import net.minecraft.src.World;
//
//public class BlockOfWeakness extends Block {
//	public BlockOfWeakness(int par1, int par2) {
//		super(par1, par2, Material.rock);
//
//		setCreativeTab(net.minecraft.src.CreativeTabs.tabBlock);
//		setHardness(5.6F);
//		setResistance(50.0F);
//		setStepSound(soundStoneFootstep);
//	}
//
//	public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2,
//			int par3, int par4) {
//		return 39168;
//	}
//
//	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World,
//			int par2, int par3, int par4) {
//		float var5 = 0.0050F;
//		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(par2 + var5,
//				par3 + var5, par4 + var5, par2 + 1 - var5, par3 + 1 - var5,
//				par4 + 1 - var5);
//	}
//
//	@cpw.mods.fml.common.asm.SideOnly(Side.CLIENT)
//	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World,
//			int par2, int par3, int par4) {
//		float var5 = 0.0050F;
//		return AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(par2 + var5,
//				par3 + var5, par4 + var5, par2 + 1 - var5, par3 + 1 - var5,
//				par4 + 1 - var5);
//	}
//
//	public void onEntityCollidedWithBlock(World par1World, int par2, int par3,
//			int par4, Entity entity) {
//		if ((entity instanceof EntityLiving)) {
//			EntityLiving entityliving = (EntityLiving) entity;
//			PotionEffect pe = entityliving.isEntityUndead() ? new PotionEffect(
//					Potion.damageBoost.id, 20, 1) : new PotionEffect(
//					Potion.weakness.id, 20, 1);
//			entityliving.addPotionEffect(pe);
//		}
//	}
//
//	public void onEntityWalking(World par1World, int par2, int par3, int par4,
//			Entity entity) {
//		if ((entity instanceof EntityLiving)) {
//			EntityLiving entityliving = (EntityLiving) entity;
//			PotionEffect pe = new PotionEffect(Potion.weakness.id, 5, 1);
//			entityliving.addPotionEffect(pe);
//		}
//	}
//}