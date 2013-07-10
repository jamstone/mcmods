package mods.ifw.aurus.common;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderAuru extends RenderLiving {
	public RenderAuru(ModelBase par1ModelBase, float par2) {
		super(par1ModelBase, par2);
		shadowSize = 0.5f;
	}

	public void renderAuru(EntityAuru entity, double time, double speed,
			double rotationAngle, float yaw, float pitch) {
		super.doRenderLiving(entity, time, speed, rotationAngle, yaw, pitch);
		shadowSize = 0.5f;
	}

	@Override
	protected int getColorMultiplier(EntityLiving par1EntityLiving, float par2,
			float par3) {
		return ((EntityAuru) par1EntityLiving).color;
	}

	protected float getWingRotation(EntityAuru entity, float ticks) {
		// float var3 = par1EntityChicken.field_70888_h +
		// (par1EntityChicken.field_70886_e - par1EntityChicken.field_70888_h) *
		// par2;
		// float var4 = par1EntityChicken.field_70884_g +
		// (par1EntityChicken.destPos - par1EntityChicken.field_70884_g) * par2;
		float dR = entity.lastWingRotation
				+ (entity.wingRotation - entity.lastWingRotation) * ticks;
		float f = (MathHelper.sin(dR) + 1.0F);

		// state handling
		// 0-1000: flying
		// 1001-2000: grounded
		// 2001-3000: sleeping
		// 3001-4000: watching while laying down

		switch (entity.model) {
		case EntityAuru.MODEL_FLYING:
			break;
		case EntityAuru.MODEL_WALKING:
			f += 1000;
			break;
		case EntityAuru.MODEL_SLEEPING:
			f += 2000;
			break;
		case EntityAuru.MODEL_LEERING:
			f += 3000;
			break;
		}

		return f;
	}

	/**
	 * Defines what float the third param in setRotationAngles of ModelBase is
	 */
	@Override
	protected float handleRotationFloat(EntityLiving entity, float ticks) {
		return this.getWingRotation((EntityAuru) entity, ticks);
	}

	@Override
	public void doRenderLiving(EntityLiving entity, double time, double speed,
			double rotationAngle, float yaw, float pitch) {
		this.renderAuru((EntityAuru) entity, time, speed, rotationAngle,
				yaw, pitch);
	}

	/**
	 * Actually renders the given argument. This is a synthetic bridge method,
	 * always casting down its argument and then handing it off to a worker
	 * function which does the actual work. In all probabilty, the class Render
	 * is generic (Render<T extends Entity) and this method has signature public
	 * void doRender(T entity, double d, double d1, double d2, float f, float
	 * f1). But JAD is pre 1.5 so doesn't do that.
	 */
	@Override
	public void doRender(Entity entity, double time, double speed,
			double rotationAngle, float yaw, float pitch) {
		this.renderAuru((EntityAuru) entity, time, speed, rotationAngle,
				yaw, pitch);
	}
}
