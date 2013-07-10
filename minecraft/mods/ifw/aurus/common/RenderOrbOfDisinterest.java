package mods.ifw.aurus.common;
//package ifw.aurus.common;
//
//import net.minecraft.client.model.ModelBase;
//import net.minecraft.client.renderer.entity.RenderLiving;
//import net.minecraft.entity.EntityLiving;
//
//import org.lwjgl.opengl.GL11;
//
//import cpw.mods.fml.relauncher.Side;
//import cpw.mods.fml.relauncher.SideOnly;
//
//@SideOnly(Side.CLIENT)
//public class RenderOrbOfDisinterest extends RenderLiving {
//	private ModelBase scaleAmount;
//
//	public RenderOrbOfDisinterest(ModelBase par1ModelBase,
//			ModelBase par2ModelBase) {
//		super(par1ModelBase, 0.5F);
//		this.scaleAmount = par2ModelBase;
//	}
//
//	public RenderOrbOfDisinterest(ModelBase par1ModelBase,
//			ModelBase par2ModelBase, float par3) {
//		super(par1ModelBase, par3);
//		this.scaleAmount = par2ModelBase;
//	}
//
//	protected int shouldSlimeRenderPass(
//			EntityOrbOfDisinterest par1EntityOrbOfDisinterest, int par2,
//			float par3) {
//		if (par1EntityOrbOfDisinterest.func_82150_aj()) {
//			return 0;
//		}
//		if (par2 == 0) {
//			setRenderPassModel(this.scaleAmount);
//			GL11.glEnable(2977);
//			GL11.glEnable(3042);
//			GL11.glBlendFunc(770, 771);
//			return 1;
//		}
//
//		if (par2 == 1) {
//			GL11.glDisable(3042);
//			GL11.glColor4f(1F, 1F, 1F, 1F);
//		}
//
//		return -1;
//	}
//
//	protected void scaleSlime(
//			EntityOrbOfDisinterest par1EntityOrbOfDisinterest, float par2) {
//		GL11.glScalef(1F, 1F, 1F);
//	}
//
//	protected void preRenderCallback(EntityLiving par1EntityLiving, float par2) {
//		scaleSlime((EntityOrbOfDisinterest) par1EntityLiving, par2);
//	}
//
//	protected int shouldRenderPass(EntityLiving par1EntityLiving, int par2,
//			float par3) {
//		return shouldSlimeRenderPass((EntityOrbOfDisinterest) par1EntityLiving,
//				par2, par3);
//	}
//}