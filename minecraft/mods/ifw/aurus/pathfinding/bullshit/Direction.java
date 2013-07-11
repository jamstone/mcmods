package mods.ifw.aurus.pathfinding.bullshit;

public enum Direction {
	O(0, 0, 0, 0), U(0, 1, 0, 1), UN(0, 1, 1, 2), UNE(1, 1, 1, 3), UE(1, 1, 0,
			2), USE(1, 1, -1, 3), US(0, 1, -1, 2), USW(-1, 1, -1, 3), UW(-1, 1,
			0, 2), UNW(-1, 1, 1, 3), N(0, 0, 1, 1), NE(1, 0, 1, 2), E(1, 0, 0,
			1), SE(1, 0, -1, 2), S(0, 0, -1, 1), SW(-1, 0, -1, 2), W(-1, 0, 0,
			1), NW(-1, 0, 1, 2), D(0, -1, 0, 1), DN(0, -1, 1, 2), DNE(1, -1, 1,
			3), DE(1, -1, 0, 2), DSE(1, -1, -1, 3), DS(0, -1, -1, 2), DSW(-1,
			-1, -1, 3), DW(-1, -1, 0, 2), DNW(-1, -1, 1, 3);

	public final int x;
	public final int y;
	public final int z;
	public final Weight weight;

	Direction(int ix, int iy, int iz, int iweight) {
		this.x = ix;
		this.y = iy;
		this.z = iz;
		this.weight = iweight == 0 ? Weight.D0 : iweight == 1 ? Weight.D1
				: iweight == 2 ? Weight.D2 : Weight.D3;
	}

	public static Direction getDirection(int dx, int dy, int dz) {
		dx = dx != 0 ? (int) Math.copySign(1, dx) : 0;
		dy = dy != 0 ? (int) Math.copySign(1, dy) : 0;
		dz = dz != 0 ? (int) Math.copySign(1, dz) : 0;

		switch (dy) {
		case 1:
			switch (dx) {
			case -1:
				switch (dz) {
				case -1:
					return USW;
				case 0:
					return UW;
				case 1:
					return UNW;
				}
			case 0:
				switch (dz) {
				case -1:
					return US;
				case 0:
					return U;
				case 1:
					return UN;
				}
			case 1:
				switch (dz) {
				case -1:
					return USE;
				case 0:
					return UE;
				case 1:
					return UNE;
				}
			}
		case 0:
			switch (dx) {
			case -1:
				switch (dz) {
				case -1:
					return SW;
				case 0:
					return W;
				case 1:
					return NW;
				}
			case 0:
				switch (dz) {
				case -1:
					return S;
				case 0:
					return O;
				case 1:
					return N;
				}
			case 1:
				switch (dz) {
				case -1:
					return SE;
				case 0:
					return E;
				case 1:
					return NE;
				}
			}
		case -1:
			switch (dx) {
			case -1:
				switch (dz) {
				case -1:
					return DSW;
				case 0:
					return DW;
				case 1:
					return DNW;
				}
			case 0:
				switch (dz) {
				case -1:
					return DS;
				case 0:
					return D;
				case 1:
					return DN;
				}
			case 1:
				switch (dz) {
				case -1:
					return DSE;
				case 0:
					return DE;
				case 1:
					return DNE;
				}
			}
		}
		return null;
	}

	public static Direction getOppositeDirection(int dx, int dy, int dz) {
		return getDirection(-dx, -dy, -dz);
	}

	public Direction getOppositeDirection() {
		return getOppositeDirection(x, y, z);
	}
}