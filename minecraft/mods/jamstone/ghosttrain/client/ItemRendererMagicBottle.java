package mods.jamstone.ghosttrain.client;

import mods.jamstone.ghosttrain.common.ItemMagicBottle;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;


public class ItemRendererMagicBottle implements IItemRenderer {
    private static RenderItem renderItem = new RenderItem();

    @Override
    public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
        return true;//type == ItemRenderType.EQUIPPED;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
                                         ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
        Icon icon = itemStack.getIconIndex();
//        renderItem.doRenderItem (, 0, icon, 16, 16);

        ItemMagicBottle item = (ItemMagicBottle) itemStack.getItem();
        if (item.mobID != null) {
            Entity entity = EntityList.createEntityByName(item.mobID, null);
//            entity.setWorld(par0MobSpawnerBaseLogic.getSpawnerWorld());
            float f1 = 0.4375F;
            GL11.glPushMatrix();
//            GL11.glRotatef((float)(par0MobSpawnerBaseLogic.field_98284_d + (par0MobSpawnerBaseLogic.field_98287_c - par0MobSpawnerBaseLogic.field_98284_d) * (double)par7) * 10.0F, 0.0F, 1.0F, 0.0F);
            //  GL11.glRotatef(-30.0F, 1.0F, 0.0F, 0.0F);
            //   GL11.glTranslatef(0.0F, -0.4F, 0.0F);
            GL11.glScalef(f1, f1, f1);
            GL11.glTranslatef(1.0F, -0.0F, 0.5F);
//            entity.setLocationAndAngles(par1, par3, par5, 0.0F, 0.0F);
            RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
            GL11.glPopMatrix();

        }


        // Rendering code goes here
    }
}

