package mods.ifw.aurus.common;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class CustomEvents {
	@ForgeSubscribe
	public void EntityItemPickup(EntityItemPickupEvent event) {
		if (event.item.getEntityItem().itemID == Aurus.dragonBlood.itemID) {
			int leftovers = event.item.getEntityItem().stackSize;
			for (; leftovers > 0; leftovers--) {
				if (event.entityPlayer.inventory
						.hasItem(Aurus.dragonBloodVial.itemID)) {
					int size = event.entityPlayer.inventory.getSizeInventory();
					ItemStack is = null;
					for (int i = 0; i < size; i++) {
						is = event.entityPlayer.inventory.getStackInSlot(i);
						if (is != null
								&& is.itemID == Aurus.dragonBloodVial.itemID
								&& is.isItemDamaged()) {
							is.damageItem(-1, event.entityPlayer);
							break;
						}
						is = null;
					}
					if (is == null) {
						if (event.entityPlayer.inventory
								.hasItem(Item.glassBottle.itemID)) {
							event.entityPlayer.inventory
									.consumeInventoryItem(Item.glassBottle.itemID);
							is = new ItemStack(Aurus.dragonBloodVial);
							is.setItemDamage(is.getMaxDamage() - 1);
							event.entityPlayer.inventory
									.addItemStackToInventory(is);
						} else {
							break;
						}
					}
				} else if (event.entityPlayer.inventory
						.hasItem(Item.glassBottle.itemID)) {
					event.entityPlayer.inventory
							.consumeInventoryItem(Item.glassBottle.itemID);
					ItemStack is = new ItemStack(Aurus.dragonBloodVial);
					is.setItemDamage(is.getMaxDamage());
					event.entityPlayer.inventory.addItemStackToInventory(is);
				} else {
					break;
				}
			}
			if (event.isCancelable()) {
				event.setCanceled(true);
			}
			if (leftovers == 0) {
				event.item.setDead();
			} else {
				event.item.getEntityItem().stackSize = leftovers;
			}
		}
	}
}
