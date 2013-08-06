package mods.jamstone.ghosttrain.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import java.util.Random;

public class BlockPhantomPower extends Block {
    public BlockPhantomPower(int par1) {
        this(par1, Material.ground);
    }

    public BlockPhantomPower(int par1, Material par2Material) {
        super(par1, par2Material);

        setLightOpacity(0);

        this.setLightValue(0.0F);

        setCreativeTab(CreativeTabs.tabBlock);
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

    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
        return true;
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

    public boolean canProvidePower() {
        return true;
    }

    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return 15;
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

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z,
                                  Random par5Random) {
        world.spawnParticle("reddust", x + par5Random.nextFloat(), y + par5Random.nextFloat(), z + par5Random.nextFloat(), -1, 1, 1);

    }

    @Override
    public int idPicked(World par1World, int par2, int par3, int par4) {
        return 0;
    }
}