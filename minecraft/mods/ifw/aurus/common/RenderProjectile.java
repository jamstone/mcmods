package mods.ifw.aurus.common;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderProjectile extends Render {

	protected float type;

	public RenderProjectile(float f) {
		this.setType(f);
	}

	@Override
	public void doRender(Entity var1, double var2, double var4, double var6,
			float var8, float var9) {
		this.doRenderProjectile(var1, var2, var4, var6, var8, var9);
	}

	public RenderProjectile setType(float f) {
		this.type = f;
		return this;
	}

	public void doRenderProjectile(Entity entity, double x, double y, double z,
			float var8, float var9) {
		EntityProjectile ep = (EntityProjectile) entity;

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y, (float) z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);

		// GL11.glScalef(1.0F, -1.0F, 1.0F);

		this.loadTexture("/mods/ifw_aurus/textures/models/Projectile.png");
		float textureW = 128.0f;
		float textureH = 64.0f;
		float size = 8.0f;

		float index = type;

		Tessellator tess = Tessellator.instance;
		float xMin = -0.25f;// this one is weird, sets the starting offset of
							// the texture's draw base on position 0 and scale
							// 1m
		float xMax = 0.25f;
		float yMin = 0.0f;// should be 0 I think
		float yMax = 0.5f;

		// The projectile is 8x8.

		float perXMin = ((index * size) % textureW) / textureW;// 0.5f;
		float perXMax = (((index * size) % textureW) + size) / textureW;// 1.0f
		float perYMax = (index * size - (index * size) % textureW) / textureH;// 0.0f;
		float perYMin = (index * size - (index * size) % textureW + size)
				/ textureH;// 1.0f;

		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F,
				0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		tess.startDrawingQuads();
		tess.setNormal(0.0F, 1.0F, 0.0F);

		tess.addVertexWithUV(xMin, yMin, 0, perXMin, perYMin); // U and V are a
																// percent of
																// the original
																// texture, not
																// a pixel
																// coordinate.
		tess.addVertexWithUV(xMax, yMin, 0, perXMax, perYMin);
		tess.addVertexWithUV(xMax, yMax, 0, perXMax, perYMax);
		tess.addVertexWithUV(xMin, yMax, 0, perXMin, perYMax);

		tess.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();

	}

}
