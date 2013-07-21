package mods.ifw.pathfinding;

/**
 * Path Node class for AstarPath
 *
 * @author AtomicStryker
 */

public class AStarNode implements Comparable<AStarNode>, Cloneable {
    final public int x;
    final public int y;
    final public int z;
    final AStarNode target;

    public AStarNode parent = null;

    /**
     * AStar G value, the total distance from the start Node to this Node
     */
    private int g_distanceFromStart;

    /**
     * AStar H value, cost to goal estimated value, sometimes called heuristic
     * value
     */
    private int h_distanceToGoal = 0;

    /**
     * AStarNode constructor
     *
     * @param ix   x coordinate
     * @param iy   y coordinate
     * @param iz   z coordinate
     * @param dist Node reaching distance from start
     * @param p    parent Node
     */
    public AStarNode(int ix, int iy, int iz, AStarNode p) {
        x = ix;
        y = iy;
        z = iz;
        setParent(p);
        target = null;
    }

    public AStarNode(int ix, int iy, int iz, AStarNode p, AStarNode t) {
        x = ix;
        y = iy;
        z = iz;
        setParent(p);
        target = t;
        h_distanceToGoal = t != null ? getDistanceTo(t) : 0;
    }

    public int getG() {
        return g_distanceFromStart;
    }

    public int getF() {
        return g_distanceFromStart * 1 + h_distanceToGoal * 1;
    }

    /**
     * Tries to update this Node instance with a new Nodechain to it, but checks
     * if that improves the Node cost first
     *
     * @param newDistanceFromStart new G distance if the update is accepted
     * @param newParentNode        new parent Node if the update is accepted
     * @return true if the new cost is lower and the update was accepted, false
     *         otherwise
     */

    public void setParent(AStarNode newParent) {
        parent = newParent;
        if (parent != null) {
            int dist = getDistanceTo(parent);
            g_distanceFromStart = parent.getG() + dist;
        } else {
            g_distanceFromStart = 0;
        }

    }

    public boolean setG(int newDistanceFromStart, AStarNode newParentNode) {
        if (newDistanceFromStart < g_distanceFromStart) {
            g_distanceFromStart = newDistanceFromStart;
            parent = newParentNode;
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(AStarNode o) {
        if (getF() < o.getF()) // lower cost = smaller natural value
        {
            return -1;
        } else if (getF() > o.getF()) // higher cost = higher natural value
        {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object checkagainst) {
        if (checkagainst instanceof AStarNode) {
            AStarNode check = (AStarNode) checkagainst;
            if (check.x == x && check.y == y && check.z == z) {
                return true;
            }
        }

        return false;
    }

    @Override
    public AStarNode clone() {
        return new AStarNode(x, y, z, parent, target);
    }

    @Override
    public int hashCode() {
        return (x << 16) ^ z ^ (y << 24);
    }

    @Override
    public String toString() {
        if (parent == null)
            return String.format("[%d|%d|%d], dist %d, F: %d", x, y, z,
                    g_distanceFromStart, getF());
        else
            return String.format(
                    "[%d|%d|%d], dist %d, parent [%d|%d|%d], F: %d", x, y, z,
                    g_distanceFromStart, parent.x, parent.y, parent.z, getF());
    }

    public Direction getDirectionFrom(AStarNode as) {
        return Direction.getDirection(x - as.x, y - as.y, z - as.z);
    }

    public Direction getDirectionTo(AStarNode as) {
        return Direction.getDirection(as.x - x, as.y - y, as.z - z);
    }

    public void setCost(AStarNode goal) {
        h_distanceToGoal = g_distanceFromStart + getDistanceTo(goal);
    }

    public void setCost(int distance, AStarNode goal) {
        g_distanceFromStart = distance;
        setCost(goal);
    }

    public int getDistanceTo(AStarNode as) {
        return AStarStatic.getDistanceBetween(x, y, z, as.x, as.y, as.z);
    }

    public int getDistanceTo(int targetX, int targetY, int targetZ) {
        return AStarStatic.getDistanceBetween(x, y, z, targetX, targetY,
                targetZ);
    }
}