package mods.ifw.aurus.common;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderEye extends Render {

	ModelEye model;

	public RenderEye() {
		super();
		shadowSize = 0.25F;
		model = new ModelEye();
	}

	public RenderEye(ModelBase par1ModelBase) {
		super();
		model = (ModelEye) par1ModelBase;
		// setRenderPassModel(model);
	}

	protected void preRenderStuff(EntityEye par1EntityShadowGoliath, float par2) {
		// GL11.glEnable(GL11.GL_NORMALIZE);
		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
		// GL11.glScalef(scale, scale, scale);
	}

	protected void preRenderCallback(EntityLiving par1EntityLiving, float par2) {
		preRenderStuff((EntityEye) par1EntityLiving, par2);
	}

	@Override
	public void doRender(Entity var1, double var2, double var4, double var6,
			float var8, float var9) {
		this.loadDownloadableImageTexture(var1.skinUrl, var1.getTexture());
		this.doRenderLiving((EntityLiving) var1, var2, var4, var6, var8, var9);

	}

	/**
	 * Sets a simple glTranslate on a LivingEntity.
	 */
	protected void renderLivingAt(EntityLiving par1EntityLiving, double par2,
			double par4, double par6) {
		GL11.glTranslatef((float) par2, (float) par4, (float) par6);
	}

	public void doRenderLiving(EntityLiving par1EntityLiving, double par2,
			double par4, double par6, float par8, float par9) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		try {
			float yaw = par1EntityLiving.rotationYaw;
			float pitch = par1EntityLiving.rotationPitch;
			this.renderLivingAt(par1EntityLiving, par2, par4, par6);
			GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(90 - pitch, 1.0F, 0.0F, 0.0F);
			float var14 = 0.0625F;
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glScalef(-1.0F, -1.0F, 1.0F);

			// renders the eye at double brightness. combined with 240
			// getBrightness() leaves the eye well lit in any location.
			GL11.glColor4f(2.0F, 2.0F, 2.0F, 1.0F);

			// this.preRenderCallback(par1EntityLiving, par9);
			// GL11.glTranslatef(0.0F, -24.0F * var14 - 0.0078125F, 0.0F);

			// GL11.glEnable(GL11.GL_ALPHA_TEST);
			// this.mainModel.setLivingAnimations(par1EntityLiving, var16,
			// var15, par9);
			this.model.render(par1EntityLiving, 0, 0, 0, 0, 0, var14);
			// (ENTITY, LEG_SWING, LEG_SWING, ???, YAW, PITCH, SCALE);

			float var19;
			int var18;
			float var20;
			float var22;
			/*
			 * for (int var17 = 0; var17 < 4; ++var17) { var18 =
			 * this.shouldRenderPass(par1EntityLiving, var17, par9);
			 * 
			 * if (var18 > 0) {
			 * this.renderPassModel.setLivingAnimations(par1EntityLiving, var16,
			 * var15, par9); this.renderPassModel.render(par1EntityLiving,
			 * var16, var15, var13, var11 - var10, var12, var14);
			 * 
			 * if ((var18 & 240) == 16) { this.func_82408_c(par1EntityLiving,
			 * var17, par9); this.renderPassModel.render(par1EntityLiving,
			 * var16, var15, var13, var11 - var10, var12, var14); }
			 * 
			 * if ((var18 & 15) == 15) { var19 =
			 * (float)par1EntityLiving.ticksExisted + par9;
			 * this.loadTexture("%blur%/misc/glint.png");
			 * GL11.glEnable(GL11.GL_BLEND); var20 = 0.5F; GL11.glColor4f(var20,
			 * var20, var20, 1.0F); GL11.glDepthFunc(GL11.GL_EQUAL);
			 * GL11.glDepthMask(false);
			 * 
			 * for (int var21 = 0; var21 < 2; ++var21) {
			 * GL11.glDisable(GL11.GL_LIGHTING); var22 = 0.76F;
			 * GL11.glColor4f(0.5F * var22, 0.25F * var22, 0.8F * var22, 1.0F);
			 * GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			 * GL11.glMatrixMode(GL11.GL_TEXTURE); GL11.glLoadIdentity(); float
			 * var23 = var19 * (0.001F + (float)var21 * 0.003F) * 20.0F; float
			 * var24 = 0.33333334F; GL11.glScalef(var24, var24, var24);
			 * GL11.glRotatef(30.0F - (float)var21 * 60.0F, 0.0F, 0.0F, 1.0F);
			 * GL11.glTranslatef(0.0F, var23, 0.0F);
			 * GL11.glMatrixMode(GL11.GL_MODELVIEW);
			 * this.renderPassModel.render(par1EntityLiving, var16, var15,
			 * var13, var11 - var10, var12, var14); }
			 * 
			 * GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			 * GL11.glMatrixMode(GL11.GL_TEXTURE); GL11.glDepthMask(true);
			 * GL11.glLoadIdentity(); GL11.glMatrixMode(GL11.GL_MODELVIEW);
			 * GL11.glEnable(GL11.GL_LIGHTING); GL11.glDisable(GL11.GL_BLEND);
			 * GL11.glDepthFunc(GL11.GL_LEQUAL); }
			 * 
			 * GL11.glDisable(GL11.GL_BLEND); GL11.glEnable(GL11.GL_ALPHA_TEST);
			 * } }
			 * 
			 * GL11.glDepthMask(true);
			 * //this.renderEquippedItems(par1EntityLiving, par9); float var26 =
			 * par1EntityLiving.getBrightness(par9); //var18 =
			 * this.getColorMultiplier(par1EntityLiving, var26, par9);
			 * OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			 * GL11.glDisable(GL11.GL_TEXTURE_2D);
			 * OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
			 */
			/*
			 * if ((var18 >> 24 & 255) > 0 || par1EntityLiving.hurtTime > 0 ||
			 * par1EntityLiving.deathTime > 0) {
			 * GL11.glDisable(GL11.GL_TEXTURE_2D);
			 * GL11.glDisable(GL11.GL_ALPHA_TEST); GL11.glEnable(GL11.GL_BLEND);
			 * GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			 * GL11.glDepthFunc(GL11.GL_EQUAL);
			 * 
			 * if (par1EntityLiving.hurtTime > 0 || par1EntityLiving.deathTime >
			 * 0) { GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
			 * this.mainModel.render(par1EntityLiving, var16, var15, var13,
			 * var11 - var10, var12, var14);
			 * 
			 * for (int var27 = 0; var27 < 4; ++var27) { if
			 * (this.inheritRenderPass(par1EntityLiving, var27, par9) >= 0) {
			 * GL11.glColor4f(var26, 0.0F, 0.0F, 0.4F);
			 * this.renderPassModel.render(par1EntityLiving, var16, var15,
			 * var13, var11 - var10, var12, var14); } } }
			 * 
			 * if ((var18 >> 24 & 255) > 0) { var19 = (float)(var18 >> 16 & 255)
			 * / 255.0F; var20 = (float)(var18 >> 8 & 255) / 255.0F; float var29
			 * = (float)(var18 & 255) / 255.0F; var22 = (float)(var18 >> 24 &
			 * 255) / 255.0F; GL11.glColor4f(var19, var20, var29, var22);
			 * this.mainModel.render(par1EntityLiving, var16, var15, var13,
			 * var11 - var10, var12, var14);
			 * 
			 * for (int var28 = 0; var28 < 4; ++var28) { if
			 * (this.inheritRenderPass(par1EntityLiving, var28, par9) >= 0) {
			 * GL11.glColor4f(var19, var20, var29, var22);
			 * this.renderPassModel.render(par1EntityLiving, var16, var15,
			 * var13, var11 - var10, var12, var14); } } }
			 * 
			 * GL11.glDepthFunc(GL11.GL_LEQUAL); GL11.glDisable(GL11.GL_BLEND);
			 * GL11.glEnable(GL11.GL_ALPHA_TEST);
			 * GL11.glEnable(GL11.GL_TEXTURE_2D); }
			 */

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		} catch (Exception var25) {
			var25.printStackTrace();
		}

		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
		// this.passSpecialRender(par1EntityLiving, par2, par4, par6);
	}

	// public void doRender(Entity entity, double d, double d1, double d2, float
	// f, float f1)
	// {
	// GL11.glPushMatrix();
	//
	// GL11.glTranslatef((float)d, (float)d1, (float)d2);
	// GL11.glScalef(0.5F, 0.5F, 0.5F);
	// loadTexture("/textures/Eye.png");
	// model.render(entity, 0, 3.14f, 3.14f, 0, 0, 0);
	//
	// GL11.glPopMatrix();
	// }
}