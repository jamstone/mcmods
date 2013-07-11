package mods.ifw.aurus.pathfinding.bullshit;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;

public class AStarWorker3D extends AStarWorker {
	public TreeMap<String, AStarNode> closedNodesTree = new TreeMap<String, AStarNode>();

	// this is the heap of jump points. jump points closest to the goal will
	// be opened first
	private PriorityQueue<AStarNode> openJumpPoints;

	private AStarNode startNode;
	private AStarNode targetNode;

	private int sorts = 0;
	private int checks = 0;

	private AxisAlignedBB bounds;

	/**
	 * Important preset value. Determines after how many non-jump-nodes in a
	 * direction an abort is executed, in order to prevent near-infinite loops
	 * in cases of unobstructed space pathfinding. After this distance is met,
	 * the reached node is perceived as jump node by default and treated as
	 * such.
	 */
	private final static int MAX_SKIP_DISTANCE = AStarStatic
			.getDistanceBetween(0, 0, 0, 0, 0, 32);

	private final PriorityQueue<AStarNode> openQueue;
	private AStarNode currentNode;

	public AStarWorker3D(AStarPathPlanner creator) {
		super(creator);
		openQueue = new PriorityQueue<AStarNode>();
	}

	@Override
	public ArrayList<AStarNode> getPath(AStarNode start, AStarNode end,
			boolean searchMode) {
		openQueue.offer(start);
		targetNode = end;
		// closedNodes.add(currentNode);
		// currentNode = start;

		setBounds(start.x, start.y, start.z, end.x, end.y, end.z, 12);

		if (!isViable(end.x, end.y, end.z)) {
			return null;
		}

		System.out.println("Start Node " + start.x + ", " + start.y + ", "
				+ start.z);
		System.out
				.println("Target Node " + end.x + ", " + end.y + ", " + end.z);

		while (!openQueue.isEmpty() && !shouldInterrupt()) {
			currentNode = openQueue.poll();

			closeNode(currentNode);

			if (currentNode.equals(end) || identifySuccessors(currentNode)) {
				// we found the target! OH, MY!
				System.out.println("Checked " + checks);

				return backTrace(start);
			}
		}

		return null;
	}

	private void closeNode(AStarNode node) {
		int x = node.x;
		int y = node.y;
		int z = node.z;

		String key = x + " " + y + " " + z;

		closedNodesTree.put(key, node);
	}

	private AStarNode closedNodeAt(AStarNode node) {
		int x = node.x;
		int y = node.y;
		int z = node.z;

		String key = x + " " + y + " " + z;

		return closedNodesTree.get(key);
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
	 * @param start
	 *            Node we start to trace back from, should be target Node
	 * @return list of adjacent AStarNodes from target to start Nodes
	 */
	private ArrayList<AStarNode> backTrace(AStarNode start) {
		ArrayList<AStarNode> foundpath = new ArrayList<AStarNode>();
		foundpath.add(currentNode);

		int x;
		int y;
		int z;
		int px;
		int py;
		int pz;
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
				foundpath.add(new AStarNode(x, y, z, null));
				x += dir.x;
				y += dir.y;
				z += dir.z;
			}

			foundpath.add(currentNode.parent);
			currentNode = currentNode.parent;
		}
		return foundpath;
	}

	/**
	 * Finds all viable successor Nodes around a Node and does JPS in their
	 * directions.
	 * 
	 * @param node
	 *            AStarNode to find Jump Nodes from
	 * @return true if the target node was run over, false otherwise
	 */
	private boolean identifySuccessors(AStarNode node) {
		Direction directionOfTrunk = null;
		if (node.parent != null)
			directionOfTrunk = node.parent.getDirectionTo(node);

		ArrayList<Direction> successors = dumbNeighbourDirections(node.x,
				node.y, node.z);

		ArrayList<Direction> neighboursD1 = new ArrayList<Direction>();
		ArrayList<Direction> neighboursD2 = new ArrayList<Direction>();
		ArrayList<Direction> neighboursD3 = new ArrayList<Direction>();

		for (Direction dir : successors) {

			if (dir == null || (node.parent != null && dir == directionOfTrunk.getOppositeDirection()))
				continue;
			AStarNode newNode = new AStarNode(node.x+dir.x, node.y+dir.y, node.z+dir.z, node, node.target);
			AStarNode oldNode = closedNodeAt(newNode);
			if(oldNode == null){
				openQueue.offer(newNode);
			}else{
				if(oldNode.getF() < newNode.getF()){
					newNode.setParent(oldNode.parent);
				}
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
			int parentY, int parentZ) {

		// obstacles and distance limits result in a null response.
		if (!isViable(probeX, probeY, probeZ)) {
			return null;
		}

		// TODO: fix this algorithm to take an ArrayList<AStarNode> argument, or
		// to return an ArrayList<AStarNode>. This will speed things up
		// immensely by reducing repeated viability checks, allowing all the
		// branch searches to be used for something by returning the forced
		// nodes as linked jump nodes properly.

		int dist = AStarStatic.getDistanceBetween(parentX, parentY, parentZ,
				probeX, probeY, probeZ);

		Direction directionOfProbe = Direction.getDirection(probeX - parentX,
				probeY - parentY, probeZ - parentZ);
		ArrayList<Direction> neighbours = smartNeighbourDirections(probeX,
				probeY, probeZ, directionOfProbe);

		// smartNeighbours appends a null item to the end of the list if
		// one of the neighbours was forced. A forced neighbour means this
		// node is a jump point.

		// if this node has forced neighbours, extends beyond the designated
		// branch size, or is the target node, then return this node.

		if ((dist > AStarStatic.getDistanceBetween(0, 0, 0, 0, 0, 25))
				|| (targetNode.x == probeX && targetNode.z == probeZ && targetNode.y == probeY)
				|| (neighbours.size() > 0 && neighbours
						.get(neighbours.size() - 1) == null)) {
			AStarNode jumpPoint = new AStarNode(probeX, probeY, probeZ,
					currentNode, targetNode);

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
			}
		}

		neighbours = neighboursD1;
		neighbours.addAll(neighboursD2);
		neighbours.addAll(neighboursD3);

		// lower order branch searches must be completed before higher order
		// trunk searches. However, this is completed through a trick of the
		// recursion. The neighbour in the direction of the probe is rainchecked
		// here and searched at the end of the branch searches. This naturally
		// forces D3 checks first, then those D3 checks force D2 checks before
		// moving forward, and those D2 checks for D1 checks before moving
		// forward.

		AStarNode jumpPoint;
		for (Direction d : neighbours) {
			jumpPoint = jump(probeX + d.x, probeY + d.y, probeZ + d.z, probeX,
					probeY, probeZ);
			if (jumpPoint != null) {
				return new AStarNode(probeX, probeY, probeZ, currentNode,
						targetNode);
			}
		}

		if (isViable(probeX + directionOfProbe.x, probeY + directionOfProbe.y,
				probeZ + directionOfProbe.z)) {
			return jump(probeX + directionOfProbe.x, probeY
					+ directionOfProbe.y, probeZ + directionOfProbe.z, probeX,
					probeY, probeZ);
		} else {
			return null;
		}

	}

	private void addOrUpdateNode(AStarNode newNode) {
		AStarNode oldNode = closedNodeAt(newNode);
		if (oldNode != null) {
			if (oldNode.getF() < newNode.getF()) {
				newNode.setParent(oldNode.parent);
			}
			if (oldNode.getF() > newNode.getF()) {
				oldNode.setParent(newNode.parent);
			}
		}
		openQueue.offer(newNode);
	}

	private ArrayList<Direction> smartNeighbourDirections(int nodeX, int nodeY,
			int nodeZ, Direction directionFromParent) {

		ArrayList<Direction> neighbourDirections = dumbNeighbourDirections(
				nodeX, nodeY, nodeZ);

		ArrayList<Direction> goodNeighbourDirections = new ArrayList<Direction>();

		boolean wasForced = false;
		int numAdded = 0;

		for (Direction directionToNeighbour : neighbourDirections) {

			if (!isViable(nodeX + directionToNeighbour.x, nodeY
					+ directionToNeighbour.y, nodeZ + directionToNeighbour.z)) {
				continue;
			}

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

				continue;
			}
		}

		// this allows for us to determine if there were any forced neighbours
		// simply by popping the last element of the arraylist.
		if (wasForced) {
			goodNeighbourDirections.add(null);
		}

		return goodNeighbourDirections;
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

	// private ArrayList<Direction> smartNeighbourDirections(int nodeX, int
	// nodeY,
	// int nodeZ, Direction directionFromParent) {
	//
	// ArrayList<Direction> neighbourDirections = dumbNeighbourDirections(
	// nodeX, nodeY, nodeZ);
	//
	// ArrayList<Direction> goodNeighbourDirections = new
	// ArrayList<Direction>();
	//
	// boolean wasForced = false;
	// int numAdded = 0;
	//
	// for (Direction directionToNeighbour : neighbourDirections) {
	// if(!isViable(nodeX + directionToNeighbour.x, nodeY +
	// directionToNeighbour.y, nodeZ
	// + directionToNeighbour.z)){
	// continue;
	// }
	//
	// // logic check for natural neighbours based entirely on position and
	// // angle of approach. nodes 'in front' of the angle of approach must
	// // be added as natural neighbours. others must be checked to
	// // determine forced neighbours.
	// //
	// // the order is important for these logic checks because they stop
	// // certain nodes from being processed by isNodeForced, nodes which
	// // would not behave properly in that method.
	//
	// boolean added = false;
	// switch (directionFromParent.weight) {
	// case D1:
	// if (directionFromParent.x == directionToNeighbour.x
	// && directionFromParent.y == directionToNeighbour.y
	// && directionFromParent.z == directionToNeighbour.z) {
	// goodNeighbourDirections.add(directionToNeighbour);
	// added = true;
	// }
	// break;
	// case D2:
	// if ((directionFromParent.x == 0 && directionToNeighbour.x == 0
	// && directionToNeighbour.y != -directionFromParent.y &&
	// directionToNeighbour.z != -directionFromParent.z)
	// || (directionFromParent.y == 0
	// && directionToNeighbour.y == 0
	// && directionToNeighbour.x != -directionFromParent.x &&
	// directionToNeighbour.z != -directionFromParent.z)
	// || (directionFromParent.z == 0
	// && directionToNeighbour.z == 0
	// && directionToNeighbour.y != -directionFromParent.y &&
	// directionToNeighbour.x != -directionFromParent.x)) {
	// goodNeighbourDirections.add(directionToNeighbour);
	// added = true;
	// }
	// break;
	// case D3:
	// if (!(directionToNeighbour.x == -directionFromParent.x
	// || directionToNeighbour.y == -directionFromParent.y ||
	// directionToNeighbour.z == -directionFromParent.z)) {
	// goodNeighbourDirections.add(directionToNeighbour);
	// added = true;
	// }
	// }
	//
	// if (added) {
	// numAdded++;
	// continue;
	// }
	//
	// if (
	// // is forced neighbour based on blocking rules
	// isNodeForced(nodeX + directionToNeighbour.x, nodeY
	// + directionToNeighbour.y, nodeZ + directionToNeighbour.z,
	// directionFromParent.getOppositeDirection(),
	// directionToNeighbour)) {
	// goodNeighbourDirections.add(directionToNeighbour);
	// wasForced = true;
	//
	// continue;
	// }
	// }
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
			if (isViable(nodeX + direction.x, nodeY + direction.y, nodeZ
					+ direction.z)) {
				return false;
			}
		}

		// if all the blockers were blocking, then the node is forced.
		return true;
	}

	// private boolean isNodeForced(int nodeX, int nodeY, int nodeZ,
	// Direction directionFromParent, Direction directionToNeighbour) {
	// boolean forced = false;
	// if (!isViable(nodeX, nodeY, nodeZ)
	// || directionFromParent == directionToNeighbour) {
	// return forced;
	// }
	//
	// ArrayList<Direction> potentialBlockers = new ArrayList<Direction>();
	//
	// switch (directionFromParent.weight) {
	// case D0:
	// return false;
	// // 1D case
	// case D1:
	// // return false for blocks in the first two walls of blocks
	// // check for an obstacle one coord closer to parent, return true
	// // if found
	// if (directionToNeighbour.weight == Weight.D1
	// || (directionFromParent.x != 0 && directionToNeighbour.x !=
	// directionFromParent.x)
	// || (directionFromParent.y != 0 && directionToNeighbour.y !=
	// directionFromParent.y)
	// || (directionFromParent.z != 0 && directionToNeighbour.z !=
	// directionFromParent.z)) {
	// return false;
	// }
	//
	// Direction dir = Direction.getDirection(directionToNeighbour.x
	// - directionFromParent.x, directionToNeighbour.y
	// - directionFromParent.y, directionToNeighbour.z
	// - directionFromParent.z);
	// // there should be a loop here?
	// if (dir != Direction.O) {
	// potentialBlockers.add(dir);
	// }
	//
	// break;
	// case D2:
	// // return false for blocks where the sum of the coords of the
	// // direction from the parent and the direction to the node are
	// // smaller than the sum of the coords in the direction from the
	// // parent. this is because it would be backtracking and jump point
	// // hates backtracking. only consider coords that are nonzero.
	// if ((directionFromParent.x == 0 && directionToNeighbour.x != 0)
	// || (directionFromParent.y == 0 && directionToNeighbour.y != 0)
	// || (directionFromParent.z == 0 && directionToNeighbour.z != 0)
	//
	// || !((directionFromParent.x == 0 && (directionToNeighbour.y ==
	// directionFromParent.y || directionToNeighbour.z ==
	// directionFromParent.z))
	// || (directionFromParent.y == 0 && (directionToNeighbour.x ==
	// directionFromParent.x || directionToNeighbour.z ==
	// directionFromParent.z)) || (directionFromParent.z == 0 &&
	// (directionToNeighbour.x == directionFromParent.x ||
	// directionToNeighbour.y == directionFromParent.y)))) {
	// return false;
	// }
	// // corners:
	// if (directionToNeighbour.weight == Weight.D3) {
	// // for the opposite corner case
	// if (-directionFromParent.x != directionToNeighbour.x
	// && -directionFromParent.y != directionToNeighbour.y
	// && -directionFromParent.z != directionToNeighbour.z) {
	// potentialBlockers = facesBetween(directionFromParent,
	// directionToNeighbour);
	// } else {
	// potentialBlockers = edgesAndFacesBlocking(
	// directionFromParent, directionToNeighbour);
	// }
	// } else {
	// potentialBlockers = edgesAndFacesBlocking(directionFromParent,
	// directionToNeighbour);
	// }
	// break;
	// case D3:
	// // return false, as above
	// if (Math.abs(directionFromParent.x + directionToNeighbour.x
	// + directionFromParent.y + directionToNeighbour.y
	// + directionFromParent.z + directionToNeighbour.z) < Math
	// .abs(directionFromParent.x + directionFromParent.y
	// + directionFromParent.z)) {
	// return false;
	// }
	//
	// switch (directionToNeighbour.weight) {
	// // D1 case should never make it this far
	// case D1:
	// return false;
	// case D2:
	// case D3:
	// // all the logic here is done in the following method.
	// // basically, it conveniently works out that all the blocks I'll
	// // be testing only need to determine if a small handful of
	// // blocks around the current node are passable in order to
	// // determine if they are forced to go through the middle.
	// potentialBlockers = edgesAndFacesBlocking(directionFromParent,
	// directionToNeighbour);
	// }
	//
	// }
	//
	// // if ANY of these spaces is open, then the pathway to the
	// // parent is not obstructed enough to warrant going through the
	// // midpoint.
	//
	// forced = true;
	// for (Direction direction : potentialBlockers) {
	// if (isViable(nodeX + direction.x, nodeY + direction.y, nodeZ
	// + direction.z)) {
	// forced = false;
	// break;
	// }
	// }
	//
	// return forced;
	// }

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

	// public ArrayList<Direction> facesBetween(Direction directionToParent,
	// Direction directionToNode) {
	// int x1 = directionToParent.x;
	// int y1 = directionToParent.y;
	// int z1 = directionToParent.z;
	// int x2 = directionToNode.x;
	// int y2 = directionToNode.y;
	// int z2 = directionToNode.z;
	//
	// ArrayList<Direction> faces = new ArrayList<Direction>();
	// // corners. if a corner shares any coords with the parent node, then add
	// // a face for each shared coord. should never be more than 2
	// if (directionToNode.weight == Weight.D3) {
	// if (x1 == x2) {
	// faces.add(Direction.getDirection(x1, 0, 0));
	// }
	// if (y1 == y2) {
	// faces.add(Direction.getDirection(0, y1, 0));
	// }
	// if (z1 == z2) {
	// faces.add(Direction.getDirection(0, 0, z1));
	// }
	// }
	// // edges. if edges share only one side, there is only one face between
	// // them, otherwise there will be a naturally occuring second face.
	// else if (directionToNode.weight == Weight.D2) {
	// int sharedSides = 0;
	// if (x1 != 0 && x1 == x2) {
	// faces.add(Direction.getDirection(x1, 0, 0));
	// sharedSides++;
	// }
	// if (y1 != 0 && y1 == y2) {
	// faces.add(Direction.getDirection(0, y1, 0));
	// sharedSides++;
	// }
	// if (z1 != 0 && z1 == z2) {
	// faces.add(Direction.getDirection(0, 0, z1));
	// sharedSides++;
	// }
	//
	// // my god this is not obvious at all. if an edge (D2) doesn't share
	// // a side with the parent, then it has two faces: one which is
	// // adjacent to the parent node, and one which is adjacent to the
	// // test node. the side that will be adjacent to the parent will have
	// // a value while the same coord on the test node will be zero, and
	// // the opposite is true for the other face. thus:
	// if (sharedSides > 0) {
	// if (x1 != 0 && x2 == 0) {
	// faces.add(Direction.getDirection(x1, 0, 0));
	// }
	// if (y1 != 0 && y2 == 0) {
	// faces.add(Direction.getDirection(0, y1, 0));
	// }
	// if (z1 != 0 && z2 == 0) {
	// faces.add(Direction.getDirection(0, 0, z1));
	// }
	//
	// if (x2 != 0 && x1 == 0) {
	// faces.add(Direction.getDirection(x2, 0, 0));
	// }
	// if (y2 != 0 && y1 == 0) {
	// faces.add(Direction.getDirection(0, y2, 0));
	// }
	// if (z2 != 0 && z1 == 0) {
	// faces.add(Direction.getDirection(0, 0, z2));
	// }
	// }
	// }
	//
	// return faces;
	// }

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

	// public ArrayList<Direction> edgesAndFacesBetween(
	// Direction directionToParent, Direction directionToNode) {
	// int x1 = directionToParent.x;
	// int y1 = directionToParent.y;
	// int z1 = directionToParent.z;
	// int x2 = directionToNode.x;
	// int y2 = directionToNode.y;
	// int z2 = directionToNode.z;
	//
	// ArrayList<Direction> dirs = facesBetween(directionToParent,
	// directionToNode);
	// // same side case.
	// if (dirs.size() == 1) {
	// // if two nodes share a zero coord, then add the edges adjacent to
	// // the face
	// if (x1 * x2 == x1 + x2) {
	// dirs.add(Direction.getDirection(-1, dirs.get(0).y,
	// dirs.get(0).z));
	// dirs.add(Direction.getDirection(1, dirs.get(0).y, dirs.get(0).z));
	// return dirs;
	// }
	// if (y1 * y2 == y1 + y2) {
	// dirs.add(Direction.getDirection(dirs.get(0).x, -1,
	// dirs.get(0).z));
	// dirs.add(Direction.getDirection(dirs.get(0).x, 1, dirs.get(0).z));
	// return dirs;
	// }
	// if (z1 * z2 == z1 + z2) {
	// dirs.add(Direction.getDirection(dirs.get(0).x, dirs.get(0).y,
	// -1));
	// dirs.add(Direction.getDirection(dirs.get(0).x, dirs.get(0).y,
	// -1));
	// return dirs;
	// }
	//
	// // corner case, add missing edge which shares only one coordinate
	// // with the face
	// if (dirs.get(0).x != 0) {
	// if (z2 == -z1) {
	// dirs.add(Direction.getDirection(dirs.get(0).x, y2, 0));
	// }
	// if (y2 == -y1) {
	// dirs.add(Direction.getDirection(dirs.get(0).x, 0, z2));
	// }
	// }
	// if (dirs.get(0).y != 0) {
	// if (z2 == -z1) {
	// dirs.add(Direction.getDirection(x2, dirs.get(0).y, 0));
	// }
	// if (x2 == -x1) {
	// dirs.add(Direction.getDirection(0, dirs.get(0).y, z2));
	// }
	// }
	// if (dirs.get(0).z != 0) {
	// if (y2 == -y1) {
	// dirs.add(Direction.getDirection(x2, 0, dirs.get(0).z));
	// }
	// if (x2 == -x1) {
	// dirs.add(Direction.getDirection(0, y2, dirs.get(0).z));
	// }
	// }
	// }
	//
	// else if (dirs.size() == 2) {
	// if (dirs.get(0).x != 0) {
	// if (dirs.get(1).y != 0) {
	// dirs.add(Direction.getDirection(dirs.get(0).x,
	// dirs.get(1).y, 0));
	// } else {
	// dirs.add(Direction.getDirection(dirs.get(0).x, 0,
	// dirs.get(1).z));
	// }
	// } else if (dirs.get(0).y != 0) {
	// if (dirs.get(1).x != 0) {
	// dirs.add(Direction.getDirection(dirs.get(1).x,
	// dirs.get(0).y, 0));
	// } else {
	// dirs.add(Direction.getDirection(0, dirs.get(0).y,
	// dirs.get(1).z));
	// }
	// } else if (dirs.get(0).z != 0) {
	// if (dirs.get(1).x != 0) {
	// dirs.add(Direction.getDirection(dirs.get(1).x, 0,
	// dirs.get(0).z));
	// } else {
	// dirs.add(Direction.getDirection(0, dirs.get(1).y,
	// dirs.get(0).z));
	// }
	// }
	// }
	//
	// return dirs;
	// }

	// returns an array of directions to all adjacent nodes.
	private ArrayList<Direction> dumbNeighbourDirections(int nodeX, int nodeY,
			int nodeZ) {
		ArrayList<Direction> neighbours = new ArrayList<Direction>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				for (int k = -1; k <= 1; k++) {
					if (!(i == 0 && j == 0 && k == 0)) {
						neighbours.add(Direction.getDirection(i, j, k));
					}
				}
			}
		}

		return neighbours;
	}

	// checks if a particular block is a suitable position to occupy. should be
	// in static. should have fields indicating height, width, and depth.

	private boolean isViable(int tx, int ty, int tz) {
		checks++;

		String key = tx + " " + ty + " " + tz;

		if (!bounds.intersectsWith(AxisAlignedBB.getBoundingBox(tx, ty, tz,
				tx + 1, ty + 1, tz + 1))) {
			return false;
		}

		int width = 1;
		int height = 2;

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
