package mods.ifw.pathfinding;

public enum Weight {

    D0(0), D1(
            (int) (AStarStatic.getDistanceBetween(1, 0, 0, 0, 0, 0) * 100)), D2(
            (int) (AStarStatic.getDistanceBetween(1, 1, 0, 0, 0, 0) * 100)), D3(
            (int) (AStarStatic.getDistanceBetween(1, 1, 1, 0, 0, 0) * 100));

    public final int value;

    Weight(int iw) {
        this.value = iw;
    }
}
