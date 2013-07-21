package mods.ifw.ghosttrain.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderWilloWisp extends RenderLiving {

    public RenderWilloWisp(ModelBase par1ModelBase, float par2) {
        super(par1ModelBase, par2);
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

            this.mainModel.render(par1EntityLiving, 0, 0, 0, 0, 0, var14);
            this.mainModel.render(par1EntityLiving, 0, 0, 0, 0, 0, var14);
            this.mainModel.render(par1EntityLiving, 0, 0, 0, 0, 0, var14);

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        } catch (Exception var25) {
            var25.printStackTrace();
        }

        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
    }
}