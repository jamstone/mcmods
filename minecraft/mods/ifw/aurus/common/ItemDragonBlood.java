package mods.ifw.aurus.common;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDragonBlood extends Item {

    public ItemDragonBlood(int par1) {
        super(par1);
        //this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    public void registerIcons(IconRegister iconRegister) {
        itemIcon = iconRegister.registerIcon("ifw_aurus:auruBloodDrop");
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        // TODO Auto-generated method stub
        return 800;
    }

}
