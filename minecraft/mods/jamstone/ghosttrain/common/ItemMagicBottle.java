package mods.jamstone.ghosttrain.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ItemMagicBottle extends ItemGlassBottle {

    public String mobID = null;

    public ItemMagicBottle(int par1) {
        super(par1);
        this.setCreativeTab(CreativeTabs.tabBrewing);
    }

    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack) {
        return true;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Return an item rarity from EnumRarity
     */
    public EnumRarity getRarity(ItemStack par1ItemStack) {
        return EnumRarity.rare;
    }

    public Entity spawnBottledEntity(double x, double y, double z, World world) {
        if (!world.isRemote) {
            Entity entity = EntityList.createEntityByName(mobID, world); //May want to switch to createEntityByNBT, and capture the entity's NBT data instead of a string.
            entity.setPosition(x, y, z);
            world.spawnEntityInWorld(entity);
            mobID = null;
            return entity;
        }
        mobID = null;
        return null;
    }

    public String captureEntity(Entity entity) {
        mobID = EntityList.getEntityString(entity);
        entity.setDead();

        return mobID;
    }

    /**
     * Called when a player right clicks an entity with an item.
     */
    public boolean itemInteractionForEntity(ItemStack par1ItemStack, EntityLiving par2EntityLiving) {
        if (mobID == null && par2EntityLiving.getHealth() <= 2) {
            captureEntity(par2EntityLiving);
            return true;
        } else {
            return false;
        }

    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer) {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(par2World, par3EntityPlayer, true);

        if (movingobjectposition == null) {
            return par1ItemStack;
        } else {
            if (!par2World.isRemote && movingobjectposition.typeOfHit == EnumMovingObjectType.TILE && mobID != null) {
                double i = movingobjectposition.hitVec.xCoord;
                double j = movingobjectposition.hitVec.yCoord;
                double k = movingobjectposition.hitVec.zCoord;

                spawnBottledEntity(i, j, k, par2World);
            }

            return par1ItemStack;
        }
    }

}
