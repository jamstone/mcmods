package mods.ifw.pathfinding.minecart;

import mods.ifw.pathfinding.Direction;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: JY
 * Date: 19/07/13
 * Time: 6:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinecartPathStep {

    ArrayList<Direction> directions;
    ArrayList<Integer> ids;

    /**
     * This class will have a bunch of predefined steps. The D1_NESW step for example is a simple predefined redstone
     * block with a powered rail on top, so there would be two directions, O and U, and their ids would be the
     * redstoneblock id, and the powered rail id.  O : redstone, U : powered rail
     *
     * D2_UxDx is the same:
     * O : redstone
     * U : powered rail
     *
     * D2_NxSx is a little more complex:
     * O : redstone
     * U : rail
     * Last D1, for example E : any block
     * UE: rail
     */

}
