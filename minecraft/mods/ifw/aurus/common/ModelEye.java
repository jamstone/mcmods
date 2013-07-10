package mods.ifw.aurus.common;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelEye extends ModelBase {
	// fields
	ModelRenderer Eye;
	private float rotX, rotY, rotZ;

	public ModelEye() {
		textureWidth = 64;
		textureHeight = 96;

		Eye = new ModelRenderer(this, 0, 56);
		Eye.addBox(0F, 0F, 0F, 8, 8, 8);
		Eye.setRotationPoint(-4F, -8F, -4F);
		Eye.setTextureSize(64, 96);
		Eye.mirror = true;
		setRotation(Eye, 0F, 0F, 0F);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void render(Entity entity, float f, float f1, float f2, float f3,
			float f4, float f5) {
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);

		Eye.render(f5);
	}

	public void setRotationAngles(float f, float f1, float f2, float f3,
			float f4, float f5, Entity entity) {
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
