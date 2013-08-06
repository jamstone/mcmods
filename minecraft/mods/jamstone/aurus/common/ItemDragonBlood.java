package mods.jamstone.aurus.common;

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
        itemIcon = iconRegister.registerIcon("jamstone_aurus:auruBloodDrop");
    }

    @Override
    public int getEntityLifespan(ItemStack itemStack, World world) {
        return 800;
    }

}
