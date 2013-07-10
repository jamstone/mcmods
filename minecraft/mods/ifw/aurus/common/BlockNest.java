package mods.ifw.aurus.common;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockNest extends BlockBreakable {

	public BlockNest(int par1, String par2) {
		super(par1, par2, Material.leaves, false);
		setHardness(1.0f);
		setResistance(1.0F);
		setLightOpacity(2);
		setLightValue(1.0F);
		setStepSound(Block.soundClothFootstep);
		setCreativeTab(CreativeTabs.tabBlock);
	}

	// @Override
	// public String getTextureFile() {
	// return "/textures/nest.png";
	// }
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
	         this.blockIcon = par1IconRegister.registerIcon(Aurus.modid + ":blockNest");
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public int quantityDropped(Random par1Random) {
		return 0;
	}

	/**
	 * From the specified side and block metadata retrieves the blocks texture.
	 * Args: side, metadata
	 */
	// @Override
	// public int getBlockTextureFromSideAndMetadata(int side, int par2) {
	// return side == 0 || side == 1 ? 1 : 0;
	// }

	@Override
	@SideOnly(Side.CLIENT)
	/**
	 * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	 * coordinates.  Args: blockAccess, x, y, z, side
	 */
	public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess,
			int par2, int par3, int par4, int par5) {
		Material var6 = par1IBlockAccess.getBlockMaterial(par2, par3, par4);
		return var6 == this.blockMaterial ? false
				: (par5 == 1 ? true : super.shouldSideBeRendered(
						par1IBlockAccess, par2, par3, par4, par5));
	}

	@Override
	public int colorMultiplier(IBlockAccess par1IBlockAccess, int par2,
			int par3, int par4) {
		Random rand = new Random();
		int red = 220 + rand.nextInt(26);
		int green = red;
		int blue = red - 20;
		return 256 * 256 * red + 256 * green + blue;
	}

	public boolean isBroken(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addBlockDestroyEffects(World world, int x, int y, int z,
			int meta, EffectRenderer effectRenderer) {
		if (!this.blocksOfIdAround(world, x, y, z, this.blockID).isEmpty()) {
			world.spawnParticle("hugeexplosion", x + 0.5f, y + 0.5f, z + 0.5f,
					1.0D, 0.0D, 0.0D);
		}
		return true;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random par5Random) {
		// TODO Auto-generated method stub
		super.updateTick(world, x, y, z, par5Random);

		if (this.isBroken(world, x, y, z)) {
			this.removeBlock(world, x, y, z, this.blockID,
					world.getClosestPlayer(x, y, z, -1));
		}

	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int id,
			EntityPlayer player) {
		super.onBlockHarvested(world, x, y, z, id, player);

		world.setBlockMetadataWithNotify(x, y, z, 1, 3);

		ArrayList<int[]> blocks = this.blocksOfIdAround(world, x, y, z,
				this.blockID);

		for (int i = 0; i < blocks.size(); i++) {
			int x1 = blocks.get(i)[0];
			int y1 = blocks.get(i)[1];
			int z1 = blocks.get(i)[2];

			world.setBlockMetadataWithNotify(x1, y1, z1, 1, 3);
			world.scheduleBlockUpdate(x1, y1, z1, this.blockID,
					world.rand.nextInt(5) + 3);
			// this.removeBlock(world, x1, y1, z1, this.blockID, player);
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z,
			int id) {
	}

	private boolean removeBlock(World world, int x, int y, int z, int id,
			EntityPlayer player) {
		int meta = world.getBlockMetadata(x, y, z);

		this.onBlockHarvested(world, x, y, z, meta, player);

		boolean removedByPlayer = (this.removeBlockByPlayer(world, player, x,
				y, z));

		if (removedByPlayer) {
			this.onBlockDestroyedByPlayer(world, x, y, z, meta);
		}

		if (player != null) {
			this.harvestBlock(world, player, x, y, z, id);
		}

		return removedByPlayer;
	}

	public ArrayList<int[]> blocksOfIdAround(World world, int x, int y, int z,
			int id) {
		ArrayList<int[]> blocks = new ArrayList<int[]>();

		if (world.getBlockId(x, y, z - 1) == id
				&& world.getBlockMetadata(x, y, z - 1) != 1) {
			blocks.add(new int[] { x, y, z - 1 });
		}

		if (world.getBlockId(x, y, z + 1) == id
				&& world.getBlockMetadata(x, y, z + 1) != 1) {
			blocks.add(new int[] { x, y, z + 1 });
		}

		if (world.getBlockId(x - 1, y, z) == id
				&& world.getBlockMetadata(x - 1, y, z) != 1) {
			blocks.add(new int[] { x - 1, y, z });
		}

		if (world.getBlockId(x + 1, y, z) == id
				&& world.getBlockMetadata(x + 1, y, z) != 1) {
			blocks.add(new int[] { x + 1, y, z });
		}

		if (world.getBlockId(x, y - 1, z) == id
				&& world.getBlockMetadata(x, y - 1, z) != 1) {
			blocks.add(new int[] { x, y - 1, z });
		}

		if (world.getBlockId(x, y + 1, z) == id
				&& world.getBlockMetadata(x, y + 1, z) != 1) {
			blocks.add(new int[] { x, y + 1, z });
		}

		return blocks;
	}
}
