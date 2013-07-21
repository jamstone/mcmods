package mods.ifw.ghosttrain.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockBrightSpace extends Block {
    public BlockBrightSpace(int par1) {
        this(par1, Material.air);
    }

    public BlockBrightSpace(int par1, Material par2Material) {
        super(par1, par2Material);

        setLightOpacity(0);

        this.setLightValue(1.0F);

        //setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2,
                                     int par3, int par4) {
        return true;
    }

    @Override
    public boolean canCollideCheck(int par1, boolean par2) {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public int quantityDropped(Random par1Random) {
        return 0;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World,
                                                         int par2, int par3, int par4) {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess,
                                        int par2, int par3, int par4, int par5) {
        return false;
    }

    public boolean isCollidable() {
        return false;
    }

    public void updateTick(World world, int x, int y, int z, Random random) {
        super.updateTick(world, x, y, z, random);
        if (world.getBlockMetadata(x, y, z) == 0) {
            world.setBlock(x, y, z, blockID, 1, 0);
            world.scheduleBlockUpdate(x, y, z, this.blockID, 10);
        } else {
            world.setBlockToAir(x, y, z);
        }
    }

    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        par1World.scheduleBlockUpdate(par2, par3, par4, this.blockID, 10);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int par2, int par3, int par4,
                                  Random par5Random) {

    }

    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return 0;
    }
}