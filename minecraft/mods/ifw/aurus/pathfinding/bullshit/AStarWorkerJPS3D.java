package mods.ifw.aurus.pathfinding.bullshit;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;

// TODO: add a AABB field for use in 'isViable'

public class AStarWorkerJPS3D extends AStarWorker {
    /**
     * Important preset value. Determines after how many non-jump-nodes in a
     * direction an abort is executed, in order to prevent near-infinite loops
     * in cases of unobstructed space pathfinding. After this distance is met,
     * the reached node is perceived as jump node by default and treated as
     * such.
     */
    private final static int MAX_SKIP_DISTANCE = AStarStatic
            .getDistanceBetween(0, 0, 0, 0, 0, 24);
    private final PriorityQueue<AStarNode> openQueue;
    public TreeMap<String, AStarNode> closedNodesTree = new TreeMap<String, AStarNode>();
    public Direction plane = Direction.UNE;
    private AStarNode startNode;
    private AStarNode targetNode;
    private int sorts = 0;
    private int checks = 0;
    private AxisAlignedBB bounds;
    private int width = 1;
    private int height = 1;
    // resolution determines the number of blocks in a block. 2*2*2 for example
    // means 8 blocks will be checked in a block, and each step in the path will
    // be at least 2 blocks apart. This means that the path can no longer fit
    // through 1xN holes, or into odd 2xN holes but it will resolve a lot
    // faster.
    // this is a good practice when dealing with open air or long distances.
    private int resolution = 1;
    private AStarNode currentNode;

    public AStarWorkerJPS3D(AStarPathPlanner creator) {
        super(creator);
        openQueue = new PriorityQueue<AStarNode>();
    }

    // For some reason, getBlocksMovement is backwards?
    public static boolean isPassableBlock(World worldObj, int ix, int iy, int iz) {
        int id = worldObj.getBlockId(ix, iy, iz);
        if (id != 0 && Block.blocksList[id] != null) {
            return Block.blocksList[id].getBlocksMovement(worldObj, ix, iy, iz);
//            if (!Block.blocksList[id].blockMaterial.isSolid()) {
//                return true;
////            } else if (Block.blocksList[id].renderAsNormalBlock()) {
////                return false;
//            } else {
//                return Block.blocksList[id].getBlocksMovement(worldObj, ix, iy, iz);
//            }
        }

        return true;
    }

    public void setup(World winput, AStarNode start_, AStarNode end_,
                      AxisAlignedBB boundingBox) {
        super.setup(winput, start_, end_, false);

        if (boundingBox != null) {
            width = (int) Math.ceil(Math.max(boundingBox.maxX
                    - boundingBox.minX, boundingBox.maxZ - boundingBox.minZ));
            height = (int) Math.ceil(boundingBox.maxY - boundingBox.minY);
        }

    }

    @Override
    public ArrayList<AStarNode> getPath(AStarNode start, AStarNode end,
                                        boolean bool) {
        setBounds(start.x, start.y, start.z, end.x, end.y, end.z, 12);

        // openQueue.offer(start);
        startNode = start;
        targetNode = end;

        if (!isViable(end.x, end.y, end.z)) {
            return null;
        }

        // planar restrictions: if the start node has one unobstructed column
        // connecting it to a plane on the final node, then we can start on the
        // final node's plane

        // TODO: only works on NE for some reason? blocks pathfinding in tight
        // cases otherwise for some reason.

        boolean openColumn = false;

        int x, y, z;

        // UN Plane
        if (openColumn == false) {
            openColumn = true;

            y = start.y;
            z = start.z;
            for (x = Math.min(start.x, end.x) + 1; x <= Math
                    .max(start.x, end.x); x++) {
                if (!isViable(x, y, z)) {
                    openColumn = false;
                    break;
                }
            }
            if (openColumn) {
                start = new AStarNode(end.x, y, z, startNode, end);
                plane = Direction.UN;
            }
        }
        // UE Plane
        if (openColumn == false) {
            openColumn = true;

            x = start.x;
            y = start.y;
            for (z = Math.min(start.z, end.z) + 1; z <= Math
                    .max(start.z, end.z); z++) {
                if (!isViable(x, y, z)) {
                    openColumn = false;
                    break;
                }
            }
            if (openColumn) {
                start = new AStarNode(x, y, end.z, startNode, end);
                plane = Direction.UE;
            }
        }

        // NE Plane
        if (openColumn == false) {
            openColumn = true;

            x = start.x;
            z = start.z;
            for (y = Math.min(start.y, end.y) + 1; y <= Math
                    .max(start.y, end.y); y++) {
                if (!isViable(x, y, z)) {
                    openColumn = false;
                    break;
                }
            }
            if (openColumn) {
                start = new AStarNode(x, end.y, z, startNode, end);
                plane = Direction.NE;
            }
        }

        // end of planar pruning

        ArrayList<Direction> neighbours = dumbNeighbourDirections();
        boolean found = false;
        if (!found) {
            if (isViable(start.x, start.y, start.z)) {
                found = true;
            } else {
                for (Direction dir : neighbours) {
                    if (isViableFrom(start.x + dir.x * resolution, start.y
                            + dir.y * resolution, start.z + dir.z * resolution,
                            dir)) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return null;
            }
        }

        ArrayList<Direction> successors = dumbNeighbourDirections();
        for (Direction dir : successors) {
            // if (dir.weight == Weight.D1 && !isViable(start.x, start.y,
            // start.z)) {
            // continue;
            // }
            if (isViableFrom(start.x + dir.x * resolution, start.y + dir.y
                    * resolution, start.z + dir.z * resolution, dir)) {
                openQueue.offer(new AStarNode(start.x + dir.x * resolution,
                        start.y + dir.y * resolution, start.z + dir.z
                        * resolution, start, end));
            }
        }

        System.out.println("Start Node " + start.x + ", " + start.y + ", "
                + start.z);
        System.out
                .println("Target Node " + end.x + ", " + end.y + ", " + end.z);

        while (!openQueue.isEmpty() && !shouldInterrupt()) {
            currentNode = openQueue.poll();

            // System.out.println("queue polled: "+currentNode);
            closeNode(currentNode);

            if (currentNode.equals(end) || identifySuccessors(currentNode)) {
                // we found the target! OH, MY!
                System.out.println("Checked " + checks);

                return backTrace(startNode);
            }
        }

        if (plane != Direction.UNE) {
            // plane restricted failure. try again with plane restrictions.

            System.out.println("Failed after " + checks);
            System.out.println("Removing planar restriction.");

            plane = Direction.UNE;

            closedNodesTree.clear();
            openQueue.clear();

            start = startNode;

            successors = dumbNeighbourDirections();
            for (Direction dir : successors) {
                // causes premature failures.
                // if (dir.weight == Weight.D1 && !isViable(start.x, start.y,
                // start.z)) {
                // continue;
                // }
                if (isViableFrom(start.x + dir.x * resolution, start.y + dir.y
                        * resolution, start.z + dir.z * resolution, dir)) {
                    openQueue.offer(new AStarNode(start.x + dir.x * resolution,
                            start.y + dir.y * resolution, start.z + dir.z
                            * resolution, start, end));
                }
            }

            System.out.println("Start Node " + start.x + ", " + start.y + ", "
                    + start.z);
            System.out.println("Target Node " + end.x + ", " + end.y + ", "
                    + end.z);

            while (!openQueue.isEmpty() && !shouldInterrupt()) {
                currentNode = openQueue.poll();

                // System.out.println("queue polled: "+currentNode);
                closeNode(currentNode);

                if (currentNode.equals(end) || identifySuccessors(currentNode)) {
                    // we found the target! OH, MY!
                    System.out.println("Checked " + checks);

                    return backTrace(startNode);
                }
            }
        }

        System.out.println("Failed after " + checks);

        return null;
    }

    @Override
    protected boolean shouldInterrupt() {
        if (this.boss == null) {
            return false;
        }
        return System.currentTimeMillis() > timeLimit;
    }

    /**
     * Finds all viable successor Nodes around a Node and does JPS in their
     * directions.
     *
     * @param node AStarNode to find Jump Nodes from
     * @return true if the target node was run over, false otherwise
     */
    private boolean identifySuccessors(AStarNode node) {
        Direction directionOfTrunk = null;
        ArrayList<Direction> successors = null;

        directionOfTrunk = node.getDirectionFrom(node.parent);
        successors = smartNeighbourDirections(node.x, node.y, node.z,
                directionOfTrunk);

        ArrayList<Direction> neighboursD1 = new ArrayList<Direction>();
        ArrayList<Direction> neighboursD2 = new ArrayList<Direction>();
        ArrayList<Direction> neighboursD3 = new ArrayList<Direction>();

        for (Direction dir : successors) {

            if (dir == null || dir == directionOfTrunk)
                continue;

            switch (dir.weight) {
                case D1:
                    neighboursD1.add(dir);
                    break;
                case D2:
                    neighboursD2.add(dir);
                    break;
                case D3:
                    neighboursD3.add(dir);
                    break;
                default:
                    break;
            }
        }

        successors = neighboursD3;
        successors.addAll(neighboursD2);
        successors.addAll(neighboursD1);
        if (directionOfTrunk != null)
            successors.add(directionOfTrunk);

        for (Direction dir : successors) {

            AStarNode jumpPoint = jump(node.x + dir.x * resolution, node.y
                    + dir.y * resolution, node.z + dir.z * resolution, node.x,
                    node.y, node.z, currentNode);

            if (jumpPoint != null && closedNodeAt(jumpPoint) == null) {
                addOrUpdateNode(jumpPoint);
            }

        }

        return false;
    }

    /**
     * Recursive Jumper as described by JPS algorithm
     *
     * @param probeX
     * @param probeY
     * @param probeZ
     * @param parentX
     * @param parentY
     * @param parentZ
     * @return AStarNode of Jumping Point found, or null if none encountered
     */
    private AStarNode jump(int probeX, int probeY, int probeZ, int parentX,
                           int parentY, int parentZ, AStarNode branchRoot) {

        Direction directionOfProbe = Direction.getDirection(probeX - parentX,
                probeY - parentY, probeZ - parentZ);

        // obstacles and distance limits result in a null response.
        if (!isViableFrom(probeX, probeY, probeZ, directionOfProbe)) {
            return null;
        }

        // if (directionOfProbe.weight == Weight.D2
        // && directionOfProbe.y == 0) {
        // if (!isViable(parentX + directionOfProbe.x * resolution,
        // parentY, parentZ)
        // || !isViable(parentX, parentY, parentZ
        // + directionOfProbe.z * resolution)) {
        // return null;
        // }
        // }

        int dist = AStarStatic.getDistanceBetween(parentX, parentY, parentZ,
                probeX, probeY, probeZ);

        ArrayList<Direction> neighbours = smartNeighbourDirections(probeX,
                probeY, probeZ, directionOfProbe);

        // smartNeighbours appends a null item to the end of the list if
        // one of the neighbours was forced. A forced neighbour means this
        // node is a jump point.

        // if this node has forced neighbours, extends beyond the designated
        // branch size, or is the target node, then return this node.

        if ((dist > MAX_SKIP_DISTANCE)
                // || (aabbTarget.intersectsWith(aabbNode))
                // || (targetNode.x == probeX && targetNode.z == probeZ &&
                // targetNode.y == probeY)
                || (targetNode.x / resolution == probeX / resolution
                && targetNode.z / resolution == probeZ / resolution && targetNode.y
                / resolution == probeY / resolution)
                || (neighbours.size() > 0 && neighbours
                .get(neighbours.size() - 1) == null)) {
            AStarNode jumpPoint = new AStarNode(probeX, probeY, probeZ,
                    branchRoot, targetNode);

            if (branchRoot != currentNode)
                addOrUpdateNode(jumpPoint);

            return jumpPoint;
        }

        // otherwise, recurse.

        // organize the neighbours by order. Lazy sort. Ignore same direction
        // neighbour. It will be dealt with last.

        ArrayList<Direction> neighboursD1 = new ArrayList<Direction>();
        ArrayList<Direction> neighboursD2 = new ArrayList<Direction>();
        ArrayList<Direction> neighboursD3 = new ArrayList<Direction>();

        for (Direction dir : neighbours) {

            if (dir == null || dir == directionOfProbe)
                continue;

            switch (dir.weight) {
                case D1:
                    neighboursD1.add(dir);
                    break;
                case D2:
                    neighboursD2.add(dir);
                    break;
                case D3:
                    neighboursD3.add(dir);
                    break;
                default:
                    break;
            }
        }

        neighbours = neighboursD3;
        neighbours.addAll(neighboursD2);
        neighbours.addAll(neighboursD1);

        // lower order branch searches must be completed before higher order
        // trunk searches. However, this is completed through a trick of the
        // recursion. The neighbour in the direction of the probe is rainchecked
        // here and searched at the end of the branch searches. This naturally
        // forces D3 checks first, then those D3 checks force D2 checks before
        // moving forward, and those D2 checks for D1 checks before moving
        // forward.

        AStarNode jumpPoint = null;
        AStarNode trunkNode = new AStarNode(probeX, probeY, probeZ, branchRoot,
                targetNode);

        for (Direction d : neighbours) {
            jumpPoint = jump(probeX + d.x * resolution, probeY + d.y
                    * resolution, probeZ + d.z * resolution, probeX, probeY,
                    probeZ, trunkNode);
        }
        if (jumpPoint != null) {
            return new AStarNode(probeX, probeY, probeZ, branchRoot, targetNode);
        }

        if (!isViable(probeX + directionOfProbe.x * resolution, probeY
                + directionOfProbe.y * resolution, probeZ + directionOfProbe.z
                * resolution)) {
            return null;
        } else {
            return jump(probeX + directionOfProbe.x * resolution, probeY
                    + directionOfProbe.y * resolution, probeZ
                    + directionOfProbe.z * resolution, probeX, probeY, probeZ,
                    branchRoot);
        }
    }

    // returns an array of directions to all adjacent nodes.
    private ArrayList<Direction> dumbNeighbourDirections() {
        return neighboursOnAPlane();
    }

    /**
     * Confines neighbour results to a specific plane, making this a 2D path.
     * behaves normally if plane is set to UNE.
     *
     * @return
     */

    private ArrayList<Direction> neighboursOnAPlane() {
        ArrayList<Direction> neighbours = new ArrayList<Direction>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (!(i == 0 && j == 0 && k == 0)
                            && (i * plane.x == i && j * plane.y == j && k
                            * plane.z == k)) {
                        neighbours.add(Direction.getDirection(i, j, k));
                    }
                }
            }
        }

        return neighbours;
    }

    private ArrayList<Direction> smartNeighbourDirections(int nodeX, int nodeY,
                                                          int nodeZ, Direction directionFromParent) {

        ArrayList<Direction> neighbourDirections = dumbNeighbourDirections();

        ArrayList<Direction> goodNeighbourDirections = new ArrayList<Direction>();

        boolean wasForced = false;
        int numAdded = 0;

        for (Direction directionToNeighbour : neighbourDirections) {

            // if (!isViable(nodeX + directionToNeighbour.x * resolution,
            // nodeY
            // + directionToNeighbour.y * resolution, nodeZ
            // + directionToNeighbour.z * resolution)) {
            // continue;
            // }

            if (isNaturalNeighbourOf(directionFromParent, directionToNeighbour)) {
                goodNeighbourDirections.add(directionToNeighbour);
                numAdded++;
                continue;
            }

            if (isNeighbourForced(nodeX, nodeY, nodeZ,
                    directionFromParent.getOppositeDirection(),
                    directionToNeighbour)) {
                goodNeighbourDirections.add(directionToNeighbour);
                wasForced = true;
            }
        }

        // this allows for us to determine if there were any forced neighbours
        // simply by popping the last element of the arraylist.
        if (wasForced) {
            goodNeighbourDirections.add(null);
        }

        return goodNeighbourDirections;
    }

    /**
     * Checks for obstacles that would prevent the adding of diagonal points.
     *
     * @param nodeX
     * @param nodeY
     * @param nodeZ
     * @param intendedDirection The intended direction of travel from the node.
     * @return
     */

    private boolean isNeighbourCompletelyBlocked(int nodeX, int nodeY,
                                                 int nodeZ, Direction intendedDirection) {
        boolean blocked = true;

        if (isViable(nodeX + intendedDirection.x * resolution, nodeY
                + intendedDirection.y * resolution, nodeZ + intendedDirection.z
                * resolution)) {

            ArrayList<Direction> naturalNeighbours = naturalNeighboursTowards(intendedDirection);
            for (Direction dir : naturalNeighbours) {
                if (dir != intendedDirection
                        && isViable(nodeX + dir.x * resolution, nodeY + dir.y
                        * resolution, nodeZ + dir.z * resolution)) {
                    blocked = false;
                }
            }
        }

        return blocked;
    }

    private ArrayList<Direction> naturalNeighboursTowards(
            Direction directionOfProbe) {
        ArrayList<Direction> neighbours = dumbNeighbourDirections();
        ArrayList<Direction> naturalNeighbours = new ArrayList<Direction>();

        for (Direction dir : neighbours) {
            if (isNaturalNeighbourOf(directionOfProbe, dir)) {
                naturalNeighbours.add(dir);
            }
        }

        return naturalNeighbours;
    }

    private boolean isNaturalNeighbourOf(Direction directionOfProbe,
                                         Direction directionOfNode) {
        /**
         * The deconstruction of all directions into natural neighbours is..
         *
         * (x, 0, 0)
         *
         * (0, y, 0)
         *
         * (0, 0, z)
         *
         * (x, y, 0)
         *
         * (0, y, z)
         *
         * (x, 0 ,z)
         *
         * (x, y, z)
         *
         * there will obviously be some repeats, but for the purpose of a logic
         * check, this isn't a problem.
         *
         */

        int xP = directionOfProbe.x;
        int yP = directionOfProbe.y;
        int zP = directionOfProbe.z;

        if (directionOfNode == Direction.getDirection(xP, 0, 0)
                || directionOfNode == Direction.getDirection(0, yP, 0)
                || directionOfNode == Direction.getDirection(0, 0, zP)
                || directionOfNode == Direction.getDirection(xP, yP, 0)
                || directionOfNode == Direction.getDirection(0, yP, zP)
                || directionOfNode == Direction.getDirection(xP, 0, zP)
                || directionOfNode == directionOfProbe) {
            return true;
        }

        return false;
    }

    private boolean isNeighbourForced(int nodeX, int nodeY, int nodeZ,
                                      Direction directionToParent, Direction directionToNeighbour) {

        ArrayList<Direction> potentialBlockers = this.edgesAndFacesBlocking(
                directionToParent, directionToNeighbour);

        // no possible way for this node to be blocked, so it can't be forced.
        if (potentialBlockers.size() == 0) {
            return false;
        }

        // if any blockers are open, then A shortest path isn't blocked.
        for (Direction direction : potentialBlockers) {
            if (isViable(nodeX + direction.x * resolution, nodeY + direction.y
                    * resolution, nodeZ + direction.z * resolution)) {
                return false;
            }
        }

        // if all the blockers were blocking, then the node is forced.
        return true;
    }

    // this method takes for granted that D1 nodes will never be asking for
    // faces.
    public ArrayList<Direction> facesBetween(Direction directionToParent,
                                             Direction directionToNode) {
        int x1 = directionToParent.x;
        int y1 = directionToParent.y;
        int z1 = directionToParent.z;
        int x2 = directionToNode.x;
        int y2 = directionToNode.y;
        int z2 = directionToNode.z;

        ArrayList<Direction> faces = new ArrayList<Direction>();
        // corners. if a corner shares any coords with the parent node, then add
        // a face for each shared coord. should never be more than 2
        if (directionToNode.weight == Weight.D3) {
            if (x1 == x2) {
                faces.add(Direction.getDirection(x1, 0, 0));
            }
            if (y1 == y2) {
                faces.add(Direction.getDirection(0, y1, 0));
            }
            if (z1 == z2) {
                faces.add(Direction.getDirection(0, 0, z1));
            }
        }
        // edges. if edges share only one side, there is only one face between
        // them, otherwise there will be a naturally occuring second face.
        else if (directionToNode.weight == Weight.D2) {
            int sharedSides = 0;
            if (x1 != 0 && x1 == x2) {
                faces.add(Direction.getDirection(x1, 0, 0));
                sharedSides++;
            }
            if (y1 != 0 && y1 == y2) {
                faces.add(Direction.getDirection(0, y1, 0));
                sharedSides++;
            }
            if (z1 != 0 && z1 == z2) {
                faces.add(Direction.getDirection(0, 0, z1));
                sharedSides++;
            }

            // my god this is not obvious at all. if an edge (D2) doesn't share
            // a side with the parent, then it has two faces: one which is
            // adjacent to the parent node, and one which is adjacent to the
            // test node. the side that will be adjacent to the parent will have
            // a value while the same coord on the test node will be zero, and
            // the opposite is true for the other face. thus:
            if (sharedSides > 0) {
                if (x1 != 0 && x2 == 0) {
                    faces.add(Direction.getDirection(x1, 0, 0));
                }
                if (y1 != 0 && y2 == 0) {
                    faces.add(Direction.getDirection(0, y1, 0));
                }
                if (z1 != 0 && z2 == 0) {
                    faces.add(Direction.getDirection(0, 0, z1));
                }

                if (x2 != 0 && x1 == 0) {
                    faces.add(Direction.getDirection(x2, 0, 0));
                }
                if (y2 != 0 && y1 == 0) {
                    faces.add(Direction.getDirection(0, y2, 0));
                }
                if (z2 != 0 && z1 == 0) {
                    faces.add(Direction.getDirection(0, 0, z2));
                }
            }
        }

        return faces;
    }

    // checks if a particular block is a suitable position to occupy. should be
    // in static. should have fields indicating height, width, and depth.

    public ArrayList<Direction> edgesAndFacesBlocking(
            Direction directionToParent, Direction directionToNode) {

        /**
         * This algorithm returns the directions on the shell of a 3x3x3 cube
         * which could block a shortest path between the parent and the node
         * trying to be reached. Through a bit of pen and paper work, it seems
         * that there is a convenient trick to finding this:
         *
         * Sum the parent coords and the node coords.
         *
         * This will result in 3 possible sums:
         *
         * 1. A sum with no value larger than 1, which is the only direction in
         * the way.
         *
         * 2. A sum with all nonzero values equal to +/- 2, which when reduced
         * to 1 will be the only direction.
         *
         * 3. A sum with ones and twos in it, which can be deconstructed into
         * two directions of type (1). The directions will be the difference
         * between the type (3) sum and the type (1) sum.
         *
         * eg. DSW (-1, -1, -1)
         *
         * + DE (-1, 0, 1)
         *
         * = [-2, -1, 0] = D (-1, 0, 0) + DS (-1, -1, 0)
         *
         *
         * Currently working perfectly for all opposite and adjacent planes.
         *
         * The logic for adding a direction automatically ignores any obviously
         * stupid results, such as corners, the zero direction, directions that
         * ARE the node being tested, and duplicate directions.
         */

        ArrayList<Direction> dirs = new ArrayList<Direction>();

        int xSum = directionToParent.x + directionToNode.x;
        int ySum = directionToParent.y + directionToNode.y;
        int zSum = directionToParent.z + directionToNode.z;

        int xDif = 0;
        int yDif = 0;
        int zDif = 0;

        Direction dirSum, dirDif;

        if (Math.abs(xSum) == 2) {
            xSum /= 2;
            xDif = xSum;
        }
        if (Math.abs(ySum) == 2) {
            ySum /= 2;
            yDif = ySum;
        }
        if (Math.abs(zSum) == 2) {
            zSum /= 2;
            zDif = zSum;
        }

        // D2 to D2 case, the edge produced should be deconstructed into two
        // faces.
        if (directionToParent.weight == Weight.D2
                && directionToNode.weight == Weight.D2) {
            if (xSum != 0) {
                xDif = xSum;
                xSum = 0;
            } else if (ySum != 0) {
                yDif = ySum;
                ySum = 0;
            } else if (zSum != 0) {
                zDif = zSum;
                zSum = 0;
            }
        }

        dirSum = Direction.getDirection(xSum, ySum, zSum);
        dirDif = Direction.getDirection(xDif, yDif, zDif);

        if (!(dirDif == Direction.O || dirDif == dirSum
                || dirDif == directionToParent || dirDif == directionToNode || dirDif.weight == Weight.D3)) {
            dirs.add(Direction.getDirection(xDif, yDif, zDif));
        }

        if (!(dirSum == Direction.O || dirSum == directionToParent
                || dirSum == directionToNode || dirSum.weight == Weight.D3))
            dirs.add(Direction.getDirection(xSum, ySum, zSum));

        return dirs;
    }

    private boolean isViable(int tx, int ty, int tz) {
        checks++;

        int w = Math.max(resolution, width);
        int h = Math.max(resolution, height);

        if (!bounds.intersectsWith(AxisAlignedBB.getBoundingBox(tx, ty, tz, tx
                + w, ty + h, tz + w))) {
            return false;
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                for (int k = 0; k < w; k++) {
                    if (!isPassableBlock(worldObj, tx + i, ty + j, tz + k)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean isViableSingle(int tx, int ty, int tz) {
        checks++;

        if (!bounds.intersectsWith(AxisAlignedBB.getBoundingBox(tx, ty, tz,
                tx + 1, ty + 1, tz + 1))) {
            return false;
        }

        int width = 1;
        int height = 1;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < width; k++) {
                    if (!worldObj.isAirBlock(tx + i, ty + j, tz + k)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean isViableFrom(int tx, int ty, int tz, Direction dir) {
        if (!isViable(tx, ty, tz)) {
            return false;
        }

        if (dir.weight != Weight.D1) {

            tx -= dir.x * resolution;
            ty -= dir.y * resolution;
            tz -= dir.z * resolution;

            boolean hasOpening = false;

            for (Direction d : naturalNeighboursTowards(dir)) {
                if (d.weight == Weight.D1
                        && d != dir
                        && isViable(tx + d.x * resolution, ty + d.y
                        * resolution, tz + d.z * resolution)) {
                    if (dir.weight != Weight.D3) {
                        hasOpening = true;
                        break;
                    } else {
                        // if the dir is a D3, then just a D1 opening isn't
                        // enough; it needs a D1 opening and its adjacent D2
                        // opening. Only then does it count.
                        for (Direction d2 : naturalNeighboursTowards(dir)) {
                            if (d2.weight == Weight.D2
                                    && (d2.x == d.x || d2.y == d.y || d2.z == d.z)
                                    && isViable(tx + d2.x * resolution, ty
                                    + d2.y * resolution, tz + d2.z
                                    * resolution)) {
                                hasOpening = true;
                                break;
                            }
                        }
                    }
                }
            }

            return hasOpening;
        }

        return true;
    }

    private boolean isViableFromSingle(int tx, int ty, int tz, Direction dir) {
        if (!isViableSingle(tx, ty, tz)) {
            return false;
        }

        if (dir.weight != Weight.D1) {

            tx -= dir.x;
            ty -= dir.y;
            tz -= dir.z;

            boolean hasOpening = false;

            for (Direction d : naturalNeighboursTowards(dir)) {
                if (d.weight == Weight.D1 && d != dir
                        && isViableSingle(tx + d.x, ty + d.y, tz + d.z)) {
                    if (dir.weight != Weight.D3) {
                        hasOpening = true;
                        break;
                    } else {
                        // if the dir is a D3, then just a D1 opening isn't
                        // enough; it needs a D1 opening and its adjacent D2
                        // opening. Only then does it count.
                        for (Direction d2 : naturalNeighboursTowards(dir)) {
                            if (d2.weight == Weight.D2
                                    && (d2.x == d.x || d2.y == d.y || d2.z == d.z)
                                    && isViableSingle(tx + d2.x, ty + d2.y, tz
                                    + d2.z)) {
                                hasOpening = true;
                                break;
                            }
                        }
                    }
                }
            }

            return hasOpening;
        }

        return true;
    }

    private void setBounds(int startX, int startY, int startZ, int endX,
                           int endY, int endZ, int padding) {

        int minX = startX <= endX ? startX - padding : endX - padding;
        int minY = startY <= endY ? startY - padding : endY - padding;
        int minZ = startZ <= endZ ? startZ - padding : endZ - padding;

        int maxX = startX > endX ? startX + padding : endX + padding;
        int maxY = startY > endY ? startY + padding : endY + padding;
        int maxZ = startZ > endZ ? startZ + padding : endZ + padding;

        bounds = AxisAlignedBB.getBoundingBox(minX, Math.max(minY, 0), minZ,
                maxX, maxY, maxZ);
    }

    /**
     * Traces the path back to our starting Node, interpolating new Nodes
     * inbetween the Jump Poins as we go.
     *
     * @param start Node we start to trace back from, should be target Node
     * @return list of adjacent AStarNodes from target to start Nodes
     */
    private ArrayList<AStarNode> backTrace(AStarNode start) {
        // System.out.println("Tracing...");
        // int loops = 0;

        ArrayList<AStarNode> foundpath = new ArrayList<AStarNode>();
        foundpath.add(currentNode);

        int x;
        int y;
        int z;
        int px;
        int py;
        int pz;
        int tx;
        int ty;
        int tz;
        Direction dir;

        while (!currentNode.equals(start)) {
            // if(loops > 100000){
            // System.out.println("Something is wrong.");
            // }
            // loops++;

            x = currentNode.x;
            y = currentNode.y;
            z = currentNode.z;
            px = currentNode.parent.x;
            py = currentNode.parent.y;
            pz = currentNode.parent.z;

            dir = currentNode.getDirectionTo(currentNode.parent);

            x += dir.x * resolution;
            y += dir.y * resolution;
            z += dir.z * resolution;

            if (start.x == x && start.y == y && start.z == z
                    && !isViable(x, y, z)) {
                System.out.printf(
                        "Path is obstructed somehow at (%d, %d, %d).\n", x, y,
                        z);
                break;
            }

            // if (isViable(x, y, z)) {

            // add interpolated nodes
            while (x != px || y != py || z != pz) {
                // loops++;

                // fix diagonal collisions in path so they walk around
                // corners.

                if (isDiagonalBlockedFrom(x, y, z, dir)) {
                    tx = x;
                    ty = y;
                    tz = z;

                    if (dir.weight != Weight.D1) {

                        tx -= dir.x * resolution;
                        ty -= dir.y * resolution;
                        tz -= dir.z * resolution;

                        boolean hasOpening = false;

                        for (Direction d : naturalNeighboursTowards(dir)) {
                            // loops++;

                            if (d.weight == Weight.D1
                                    && d != dir
                                    && isViable(tx + d.x * resolution, ty + d.y
                                    * resolution, tz + d.z * resolution)) {
                                if (dir.weight != Weight.D3) {
                                    hasOpening = true;

                                    foundpath.add(new AStarNode(tx + d.x
                                            * resolution,
                                            ty + d.y * resolution, tz + d.z
                                            * resolution, null));
                                } else {
                                    for (Direction d2 : naturalNeighboursTowards(dir)) {
                                        // loops++;

                                        if (d2.weight == Weight.D2
                                                && (d2.x == d.x || d2.y == d.y || d2.z == d.z)
                                                && isViable(tx + d2.x
                                                * resolution, ty + d2.y
                                                * resolution, tz + d2.z
                                                * resolution)) {
                                            hasOpening = true;

                                            foundpath.add(new AStarNode(tx
                                                    + d.x * resolution, ty
                                                    + d.y * resolution, tz
                                                    + d.z * resolution, null));

                                            foundpath.add(new AStarNode(tx
                                                    + d2.x * resolution, ty
                                                    + d2.y * resolution, tz
                                                    + d2.z * resolution, null));

                                            break;
                                        }
                                    }
                                }
                            }

                            if (hasOpening)
                                break;
                        }
                    }
                }

                foundpath.add(new AStarNode(x, y, z, null));
                x += dir.x * resolution;
                y += dir.y * resolution;
                z += dir.z * resolution;
            }

            // fix diagonal collisions in path so they walk around corners.
            if (isDiagonalBlockedFrom(x, y, z, dir)) {
                tx = x;
                ty = y;
                tz = z;

                if (dir.weight != Weight.D1) {

                    tx -= dir.x * resolution;
                    ty -= dir.y * resolution;
                    tz -= dir.z * resolution;

                    boolean hasOpening = false;

                    for (Direction d : naturalNeighboursTowards(dir)) {
                        // loops++;

                        if (d.weight == Weight.D1
                                && d != dir
                                && isViable(tx + d.x * resolution, ty + d.y
                                * resolution, tz + d.z * resolution)) {
                            if (dir.weight != Weight.D3) {
                                hasOpening = true;

                                foundpath.add(new AStarNode(tx + d.x
                                        * resolution, ty + d.y * resolution, tz
                                        + d.z * resolution, null));
                            } else {
                                for (Direction d2 : naturalNeighboursTowards(dir)) {
                                    // loops++;

                                    if (d2.weight == Weight.D2
                                            && (d2.x == d.x || d2.y == d.y || d2.z == d.z)
                                            && isViable(tx + d2.x * resolution,
                                            ty + d2.y * resolution, tz
                                            + d2.z * resolution)) {
                                        hasOpening = true;

                                        foundpath.add(new AStarNode(tx + d.x
                                                * resolution, ty + d.y
                                                * resolution, tz + d.z
                                                * resolution, null));

                                        foundpath.add(new AStarNode(tx + d2.x
                                                * resolution, ty + d2.y
                                                * resolution, tz + d2.z
                                                * resolution, null));

                                        break;
                                    }
                                }
                            }
                        }
                        if (hasOpening)
                            break;

                    }
                }
            }

            foundpath.add(currentNode.parent);
            currentNode = currentNode.parent;
            // }
        }
        // System.out.println("Done tracing. Nodes: " + foundpath.size() +
        // "; Loops: " + loops);

        return foundpath;
    }

    private ArrayList<AStarNode> backTraceSingles(AStarNode start) {
        ArrayList<AStarNode> foundpath = new ArrayList<AStarNode>();
        foundpath.add(currentNode);

        int x;
        int y;
        int z;
        int px;
        int py;
        int pz;
        int tx;
        int ty;
        int tz;
        Direction dir;

        while (!currentNode.equals(start)) {
            x = currentNode.x;
            y = currentNode.y;
            z = currentNode.z;
            px = currentNode.parent.x;
            py = currentNode.parent.y;
            pz = currentNode.parent.z;

            dir = currentNode.getDirectionTo(currentNode.parent);

            x += dir.x;
            y += dir.y;
            z += dir.z;

            // add interpolated nodes
            while (x != px || y != py || z != pz) {

                // fix diagonal collisions in path so they walk around corners.

                if (isDiagonalBlockedFromSingle(x, y, z, dir)) {
                    tx = x;
                    ty = y;
                    tz = z;

                    if (dir.weight != Weight.D1) {

                        tx -= dir.x;
                        ty -= dir.y;
                        tz -= dir.z;

                        boolean hasOpening = false;

                        for (Direction d : naturalNeighboursTowards(dir)) {
                            if (d.weight == Weight.D1
                                    && d != dir
                                    && isViableSingle(tx + d.x, ty + d.y, tz
                                    + d.z)) {
                                if (dir.weight != Weight.D3) {
                                    hasOpening = true;

                                    foundpath.add(new AStarNode(tx + d.x, ty
                                            + d.y, tz + d.z, null));
                                } else {
                                    for (Direction d2 : naturalNeighboursTowards(dir)) {
                                        if (d2.weight == Weight.D2
                                                && (d2.x == d.x || d2.y == d.y || d2.z == d.z)
                                                && isViableSingle(tx + d2.x, ty
                                                + d2.y, tz + d2.z)) {
                                            hasOpening = true;

                                            foundpath.add(new AStarNode(tx
                                                    + d.x, ty + d.y, tz + d.z,
                                                    null));

                                            foundpath.add(new AStarNode(tx
                                                    + d2.x, ty + d2.y, tz
                                                    + d2.z, null));

                                            /**
                                             * for (int i = 1; i <= resolution;
                                             * i++) { foundpath.add(new
                                             * AStarNode(tx + d.x * i, ty + d.y
                                             * * i, tz + d.z i, null)); }
                                             *
                                             * tx += d.x * resolution; ty += d.y
                                             * * resolution; tz += d.z *
                                             * resolution;
                                             *
                                             * Direction d1 = Direction
                                             * .getDirection(d2.x - d.x, d2.y -
                                             * d.y, d2.z - d.z);
                                             *
                                             * for (int i = 1; i <= resolution;
                                             * i++) { foundpath.add(new
                                             * AStarNode(tx + d1.x * i, ty +
                                             * d1.y i, tz + d1.z * i, null)); }
                                             */
                                            break;
                                        }
                                    }
                                }
                            }

                            if (hasOpening)
                                break;
                        }
                    }
                }

                foundpath.add(new AStarNode(x, y, z, null));
                x += dir.x;
                y += dir.y;
                z += dir.z;
            }

            // fix diagonal collisions in path so they walk around corners.
            if (isDiagonalBlockedFromSingle(x, y, z, dir)) {
                tx = x;
                ty = y;
                tz = z;

                if (dir.weight != Weight.D1) {

                    tx -= dir.x;
                    ty -= dir.y;
                    tz -= dir.z;

                    boolean hasOpening = false;

                    for (Direction d : naturalNeighboursTowards(dir)) {
                        if (d.weight == Weight.D1 && d != dir
                                && isViableSingle(tx + d.x, ty + d.y, tz + d.z)) {
                            if (dir.weight != Weight.D3) {
                                hasOpening = true;

                                foundpath.add(new AStarNode(tx + d.x, ty + d.y,
                                        tz + d.z, null));
                            } else {
                                for (Direction d2 : naturalNeighboursTowards(dir)) {
                                    if (d2.weight == Weight.D2
                                            && (d2.x == d.x || d2.y == d.y || d2.z == d.z)
                                            && isViableSingle(tx + d2.x, ty
                                            + d2.y, tz + d2.z)) {
                                        hasOpening = true;

                                        foundpath.add(new AStarNode(tx + d.x,
                                                ty + d.y, tz + d.z, null));

                                        foundpath.add(new AStarNode(tx + d2.x,
                                                ty + d2.y, tz + d2.z, null));

                                        /**
                                         * for (int i = 1; i <= resolution; i++)
                                         * { foundpath.add(new AStarNode(tx +
                                         * d.x * i, ty + d.y * i, tz + d.z * i,
                                         * null)); }
                                         *
                                         * tx += d.x * resolution; ty += d.y *
                                         * resolution; tz += d.z * resolution;
                                         *
                                         * Direction d1 =
                                         * Direction.getDirection( d2.x - d.x,
                                         * d2.y - d.y, d2.z - d.z);
                                         *
                                         * for (int i = 1; i <= resolution; i++)
                                         * { foundpath.add(new AStarNode(tx +
                                         * d1.x * i, ty + d1.y * i, tz + d1.z *
                                         * i, null)); }
                                         */
                                        break;
                                    }
                                }
                            }
                        }
                        if (hasOpening)
                            break;

                    }
                }
            }

            foundpath.add(currentNode.parent);
            currentNode = currentNode.parent;
        }
        return foundpath;
    }

    private boolean isDiagonalBlockedFrom(int tx, int ty, int tz, Direction dir) {
        tx -= dir.x * resolution;
        ty -= dir.y * resolution;
        tz -= dir.z * resolution;

        for (Direction d : naturalNeighboursTowards(dir)) {
            if (!isViable(tx + d.x * resolution, ty + d.y * resolution, tz
                    + d.z * resolution)) {
                return true;
            }
        }

        return false;
    }

    private boolean isDiagonalBlockedFromSingle(int tx, int ty, int tz,
                                                Direction dir) {
        tx -= dir.x;
        ty -= dir.y;
        tz -= dir.z;

        for (Direction d : naturalNeighboursTowards(dir)) {
            if (!isViableSingle(tx + d.x, ty + d.y, tz + d.z)) {
                return true;
            }
        }

        return false;
    }

    private void closeNode(AStarNode node) {
        int x = node.x;
        int y = node.y;
        int z = node.z;

        String key = x + " " + y + " " + z;

        closedNodesTree.put(key, node);
    }

    private void openNode(AStarNode node) {
        int x = node.x;
        int y = node.y;
        int z = node.z;

        String key = x + " " + y + " " + z;

        closedNodesTree.remove(key);
    }

    private AStarNode closedNodeAt(AStarNode node) {
        int x = node.x;
        int y = node.y;
        int z = node.z;

        String key = x + " " + y + " " + z;

        return closedNodesTree.get(key);
    }

    private void addOrUpdateNode(AStarNode newNode) {

        // Had to change to diagonal-first searches, which may be slower. The
        // paper describing JPS encourages lower order searchers first.

        // openQueue.offer(newNode);

        // this will find the absolute shortest path. ensure diagonal searches
        // are done first.
        AStarNode oldNode = closedNodeAt(newNode);
        if (oldNode != null) {
            // if the new node is better, update the old node, and reopen the
            // node to recalculate the child nodes' weights.
            if (oldNode.getF() > newNode.getF()) {
                openNode(newNode);
                openQueue.offer(newNode);
            }
            // if the old node is better, do nothing.

            // if there is no old node, just offer the new one.
        } else {
            openQueue.offer(newNode);
        }
    }
}

/**
 * jump method is recursive, and unfortunately the default implementation will
 * occasionally have some redundant searches.
 *
 * There are two layers to each search: trunk, and branch.
 *
 * The trunk search will always return the farthest node along the direction it
 * is searching.
 *
 * The branch searches are nested within trunk searches. 2nd order diagonal
 * trunks require a 1st order check for potential jump points. These points are
 * found, but not returned (wasting resources?). 3rd order diagonal trunks
 * require 2nd order diagonal searches, which also require 1st order searches,
 * and still will not return anything but the trunk node from which the branch
 * searches began.
 *
 * The returned node from this algorithm is intended to be added to the list of
 * open nodes, so it should be reasonable to add the results of branch searches,
 * but this requires tricky passing of variables to avoid skipping nodes so
 * never mind. KISS.
 *
 */

/**
 * smartNeighbours returns a list of adjacent coordinates as Directions that are
 * already pruned according to some incredibly complicated rules derived from
 * the logic behind 2D JPS. In short, if there is a path of equal or greater
 * distance between the previous node and the next node, prune it, we don't
 * care. So going east to west, we don't care about any blocks north, south, up,
 * or down. Unless the block north, south, up, or down is blocked, in which case
 * we certainly do care about the block that it is blocking.
 */
