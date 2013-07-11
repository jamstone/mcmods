package mods.ifw.aurus.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemDragonBloodVial extends Item {

	ArrayList<Icon> icons = new ArrayList<Icon>();

	public ItemDragonBloodVial(int par1) {
		super(par1);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setMaxStackSize(1);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List list,
			boolean bool) {
		int percent = (int) (100 * (1 - (float) is.getItemDamage()
				/ this.getMaxDamage()));
		String guilt = percent > 24 ? (percent > 49 ? (percent > 74 ? (percent > 99 ? "Monster"
				: "Hemophiliac")
				: "Murderer")
				: "Beast")
				: "Stop";
		guilt = new Random().nextInt(100) < 5 ? guilt : "";
		list.add(percent + "% full. §4" + guilt);
//		guilt = new Random().nextInt(100) < 5 ? "§4" + guilt : percent + "% full.";
//		list.add(guilt);


	}

	public void registerIcons(IconRegister iconRegister) {
		icons.add(iconRegister.registerIcon("ifw_aurus:auruBloodVial1"));
		icons.add(iconRegister.registerIcon("ifw_aurus:auruBloodVial2"));
		icons.add(iconRegister.registerIcon("ifw_aurus:auruBloodVial3"));
		icons.add(iconRegister.registerIcon("ifw_aurus:auruBloodVial4"));
		itemIcon = icons.get(0);
	}

	@Override
	public Item getContainerItem() {
		return Item.glassBottle;
	}

	@Override
	public Icon getIconFromDamage(int par1) {
		return icons.get(3 - (int) Math.round((float) par1 * 3
				/ this.getMaxDamage()));
	}

	@Override
	public int getMaxDamage() {
		return 128;
	}

	@Override
	public boolean isDamageable() {
		return true;
	}

	@Override
	public boolean hasContainerItem() {
		return super.hasContainerItem();
	}

	@Override
	public boolean isRepairable() {
		return super.isRepairable();
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world,
			EntityPlayer player) {

		// world.playSoundAtEntity(player, "random.bow", 0.5F,
		// 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			player.dropPlayerItemWithRandomChoice(new ItemStack(
					Aurus.dragonBlood), false);
			is.damageItem(1, player);
		}

		if (is.stackSize == 0) {
			return new ItemStack(Item.glassBottle);
		}

		return is;
	}

}
