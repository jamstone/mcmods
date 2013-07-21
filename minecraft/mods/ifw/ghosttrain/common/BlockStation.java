package mods.ifw.ghosttrain.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class BlockStation extends BlockContainer {
    @SideOnly(Side.CLIENT)
    private Icon[] icons;

    protected BlockStation(int par1) {
        super(par1, Material.glass);
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.250F, 0.0F, 0.250F, 0.75F, 1.0F, 0.75F);
        this.setLightValue(1.0F);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister) {
        icons = new Icon[4];

        icons[0] = par1IconRegister.registerIcon(GhostTrain.modid + ":blockStation0bot");
        icons[1] = par1IconRegister.registerIcon(GhostTrain.modid + ":blockStation0top");
        icons[2] = par1IconRegister.registerIcon(GhostTrain.modid + ":blockStation1bot");
        icons[3] = par1IconRegister.registerIcon(GhostTrain.modid + ":blockStation1top");
    }

    @SideOnly(Side.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return icons[par2];
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType() {
        return 1;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        if (par1World.getBlockId(par2, par3 - 1, par4) != this.blockID) {
            par1World.setBlock(par2, par3 + 1, par4, this.blockID, 1, 3);
        }
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4) {
        return super.canPlaceBlockAt(par1World, par2, par3, par4);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, int par5) {
        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
        if (par1World.getBlockId(par2, par3 + 1, par4) != this.blockID && par1World.getBlockId(par2, par3 - 1, par4) != this.blockID) {
            par1World.setBlockToAir(par2, par3, par4);
        }
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
        TileEntityStation tileEntity = (TileEntityStation) par1World.getBlockTileEntity(par2, par3, par4);
        if (tileEntity == null || par5EntityPlayer.isSneaking()) {
            return false;
        }

        tileEntity.findPathBETA(par1World, par2, par3, par4);

        //code to open gui explained later
        // par5EntityPlayer.openGui(Tiny.instance, 0, world, x, y, z);
        return true;


//        return super.onBlockActivated(par1World, par2, par3, par4,
//                par5EntityPlayer, par6, par7, par8, par9);

    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World par1World) {
        TileEntityStation te = new TileEntityStation(par1World);
        return te;
    }

}
