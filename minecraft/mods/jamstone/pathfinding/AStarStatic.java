package mods.jamstone.pathfinding;

import java.util.ArrayList;

/**
 * Static parts of AStarPath calculation and translation
 *
 * @author AtomicStryker, heavily modified by jamstone
 */

public class AStarStatic {

    // used to calculate both weight and distance.
    public static int getDistanceBetween(int x1, int y1, int z1, int x2,
                                         int y2, int z2) {
        // Manhattan
        // return Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2);

        // Euclidean
//        return (int) Math.floor((Math.sqrt(Math.pow((x1 - x2), 2)
//                + Math.pow((y1 - y2), 2) + Math.pow((z1 - z2), 2)) * 10));

        // Chebyshev ???
        int D1 = 10;
        int D2 = 14;
        int D3 = 17;

        int dx = x1 > x2 ? x1 - x2 : x2 - x1;
        int dy = y1 > y2 ? y1 - y2 : y2 - y1;
        int dz = z1 > z2 ? z1 - z2 : z2 - z1;

        int dMax = dx > dy && dx > dz ? dx : (dy > dz ? dy : dz);
        int dMin = dx < dy && dx < dz ? dx : (dy < dz ? dy : dz);
        int dMid = dx + dy + dz - dMax - dMin;

        return D3 * dMin + D2 * (dMid - dMin) + D1 * (dMax - dMid);

        // return D1 * (dx + dy + dz) + (D3 - 2 * D2) * (dMax-dMid) + (D2 - 2 *
        // D1) * (dMid-dMin);
    }

    /**
     * Converts an ArrayList of AStarNodes into an MC style PathEntity
     *
     * @param input List of AStarNodes
     * @return MC pathing compatible PathEntity
     */
    public static AS_PathEntity translateAStarPathtoPathEntity(
            ArrayList<AStarNode> input) {
        AS_PathPoint[] points = new AS_PathPoint[input.size()];
        AStarNode reading;
        int i = 0;
        int size = input.size();
        // System.out.println("Translating AStar Path with "+size+" Hops:");

        while (size > 0) {
            reading = input.get(size - 1);
            points[i] = new AS_PathPoint(reading.x, reading.y, reading.z);
            points[i].isFirst = i == 0;
            points[i].setIndex(i);
            points[i].setTotalPathDistance(i);
            points[i].setDistanceToNext(1F);
            points[i].setDistanceToTarget(size);

            if (i > 0) {
                points[i].setPrevious(points[i - 1]);
            }
            // System.out.println("PathPoint: ["+reading.x+"|"+reading.y+"|"+reading.z+"]");

            input.remove(size - 1);
            size--;
            i++;
        }
        // System.out.println("Translated AStar PathEntity with length: "+
        // points.length);

        return new AS_PathEntity(points);
    }
}