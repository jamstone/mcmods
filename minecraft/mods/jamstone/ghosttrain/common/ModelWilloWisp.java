package mods.jamstone.ghosttrain.common;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWilloWisp extends ModelBase {
    //fields
    ModelRenderer Criss;
    ModelRenderer Cross;

    public ModelWilloWisp() {
        textureWidth = 32;
        textureHeight = 32;

        Criss = new ModelRenderer(this, 0, 0);
        Criss.addBox(-4F, -8F, 0F, 8, 8, 0);
        Criss.setRotationPoint(0F, 0F, 0F);
        Criss.setTextureSize(32, 32);
        Criss.mirror = true;
        setRotation(Criss, 0F, 0F, 0F);
        Cross = new ModelRenderer(this, 0, 0);
        Cross.addBox(0F, -8F, -4F, 0, 8, 8);
        Cross.setRotationPoint(0F, 0F, 0F);
        Cross.setTextureSize(32, 32);
        Cross.mirror = true;
        setRotation(Cross, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        Criss.render(f5);
        Cross.render(f5);
    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
    }
}

