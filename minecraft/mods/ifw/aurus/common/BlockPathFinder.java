package mods.ifw.aurus.common;

import mods.ifw.aurus.pathfinding.bullshit.AStarNode;
import mods.ifw.aurus.pathfinding.bullshit.AStarWorkerJPS3D;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockPathFinder extends Block {

    public BlockPathFinder(int par1, Material par2Material) {
        super(par1, par2Material);
        //setCreativeTab(CreativeTabs.tabBlock);
    }

    public BlockPathFinder(int par1, int par2, Material par3Material) {
        super(par1, par3Material);
        //setCreativeTab(CreativeTabs.tabBlock);
    }

    /*
     * (non-Javadoc)
     *
     * @see net.minecraft.src.Block#onBlockActivated(net.minecraft.src.World,
     * int, int, int, net.minecraft.src.EntityPlayer, int, float, float, float)
     */
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3,
                                    int par4, EntityPlayer par5EntityPlayer, int par6, float par7,
                                    float par8, float par9) {
        if (par1World.isRemote) {
            return false;
        }

        int scalar = 2;

        int x = par2 + (par1World.rand.nextInt(32) - 16) * scalar;
        int y = par3;// + (par1World.rand.nextInt(32) - 16) * scalar;
        int z = par4 + (par1World.rand.nextInt(32) - 16) * scalar;

        while (!par1World.isAirBlock(x, y, z)) {
            x = par2 + (par1World.rand.nextInt(32) - 16) * scalar;
            y = par3;// + (par1World.rand.nextInt(32) - 16) * scalar;
            z = par4 + (par1World.rand.nextInt(32) - 16) * scalar;
        }

        AStarNode start = new AStarNode(par2, par3, par4, null);
        AStarNode goal = new AStarNode(x, y, z, null);

        // JPPathFinder jp = new JPPathFinder(new AStarPath(par1World));

        AStarWorkerJPS3D aswJP3D = new AStarWorkerJPS3D(null);

        aswJP3D.setup(par1World, start, goal, null);

        // Direction directionToParent = Direction.D;
        // Direction directionToNode = Direction.DSE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.SE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.USE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.DE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.E;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.UE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.DNE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.NE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());
        // directionToNode = Direction.UNE;
        // System.out.println(asw.edgesAndFacesBlocking(directionToParent,
        // directionToNode).toString());

        ArrayList<AStarNode> path = aswJP3D.getPath(start, goal, false);

        if (path != null) {

            for (AStarNode as : path) {
//				if (as.equals(start) || as.equals(goal)) {
//					par1World.setBlock(as.x, as.y, as.z,
//							Aurus.pathFinder.blockID);
//				} else {
//					par1World.setBlock(as.x, as.y, as.z,
//							Block.glowStone.blockID);
//				}

                if (as.equals(start) || as.equals(goal)) {
                } else {
                    par1World.setBlock(as.x, as.y, as.z,
                            Aurus.pathMarker.blockID);
                }
            }
        } else {
            System.out.println("Failed to find a path.");
        }

        return super.onBlockActivated(par1World, par2, par3, par4,
                par5EntityPlayer, par6, par7, par8, par9);
    }
}
