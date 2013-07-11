package mods.ifw.aurus.common;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenFireDragonNest implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		// world.setBlock(chunkX * 16 + random.nextInt(16), 100, chunkZ * 16
		// + random.nextInt(16), Block.waterMoving.blockID);
		BiomeGenBase b = world.getBiomeGenForCoords(chunkX, chunkZ);
		int bID = b.biomeID;
		if (bID < 1 || (bID > 7 && bID < 11)) {
			// Do nothing, these biomes are inappropriate
		} else {
			int seed = Math.abs((int) world.getSeed());
			int chunksApart = 20;
			int SpawnableXOffset = ((seed + chunksApart / 3) % chunksApart);
			int SpawnableZOffset = ((seed + chunksApart / 2) % chunksApart);

			if (Math.abs(chunkX % chunksApart) == SpawnableXOffset
					&& Math.abs(chunkZ % chunksApart) == SpawnableZOffset) {

				int x = chunkX * 16 + random.nextInt(16);
				int y = random.nextInt(5) + 2;
				int z = chunkZ * 16 + random.nextInt(16);

				System.out.println("Carved out a fire dragon nest at: " + x
						+ ", " + y + ", " + z);

				int lava = Block.lavaMoving.blockID;
				int stone = Block.stone.blockID;
				int stoneBrick = Block.stoneBrick.blockID;
				int halfStone = Block.stoneSingleSlab.blockID;
				int gravel = Block.gravel.blockID;

				int deviation = 2;

				int radius = 40;
				int rad2 = radius * radius;

				int devX = 1;// random.nextInt(deviation) + 1;
				int devY = 2;// random.nextInt(deviation) + 1;
				int devZ = 1;// random.nextInt(deviation) + 1;

				ArrayList<int[]> stalactites = new ArrayList<int[]>();
				ArrayList<int[]> lavacolumns = new ArrayList<int[]>();

				for (int i = -radius; i < radius; i++) {
					for (int j = 0; j < radius; j++) {
						for (int k = -radius; k < radius; k++) {
							int hyp2 = (i * devX) * (i * devX) + (j * devY)
									* (j * devY) + (k * devZ) * (k * devZ);
							if (hyp2 > rad2) {
								continue;
							} else if (hyp2 >= (radius - deviation)
									* (radius - deviation)
									&& hyp2 <= (radius + deviation)
											* (radius + deviation)) {
								if (random.nextInt(200) == 0) {
									lavacolumns.add(new int[] { x + i, y + j,
											z + k });
								} else {
									stalactites.add(new int[] { x + i, y + j,
											z + k });
									world.setBlockAndMetadataWithNotify(x + i,
											y + j, z + k, stoneBrick,
											random.nextInt(3));
								}
							} else if (j == 0) {
								if ((i * devX) * (i * devX) + (k * devZ)
										* (k * devZ) >= (radius * 0.8f * devX)
										* (radius * 0.8f * devZ)) {
									world.setBlockWithNotify(x + i, y + j, z
											+ k, 0);
								} else if ((i * devX) * (i * devX) + (k * devZ)
										* (k * devZ) >= (radius * 0.8f * devX - 1)
										* (radius * 0.8f * devZ - 1)) {
									world.setBlockAndMetadataWithNotify(x + i,
											y + j, z + k, halfStone, 0x5);
								} else {
									world.setBlockAndMetadataWithNotify(x + i,
											y + j, z + k, stoneBrick,
											random.nextInt(3));

								}
								world.setBlockWithNotify(x + i, y + j - 1, z
										+ k, lava);
							} else if (j == 1
									&& (i * devX) * (i * devX) + (k * devZ)
											* (k * devZ) < (radius * 0.7f * devX)
											* (radius * 0.7f * devZ)) {
								if ((i * devX) * (i * devX) + (k * devZ)
										* (k * devZ) >= (radius * 0.5f * devX)
										* (radius * 0.5f * devZ)) {
									world.setBlockAndMetadataWithNotify(x + i,
											y + j, z + k, halfStone, 0x5);
									world.setBlockAndMetadataWithNotify(x + i,
											y + j, z + k, stoneBrick,
											random.nextInt(3));
								} else {
									world.setBlockAndMetadataWithNotify(x + i,
											y + j, z + k, stoneBrick,
											random.nextInt(3));
								}
							} else {
								world.setBlockWithNotify(x + i, y + j, z + k, 0);
							}
						}
					}
				}

				for (int l = 0; l < lavacolumns.size(); l++) {

					// TODO: this is messy, tons of unnecessary
					// addition/subtraction.

					int i = lavacolumns.get(l)[0] - x;
					int j = lavacolumns.get(l)[1] - y;
					int k = lavacolumns.get(l)[2] - z;

					if (y + j - 3 < 2) {
						continue;
					}

					world.setBlockWithNotify(x + i, y + j, z + k, lava);
					world.setBlockWithNotify(x + i, y + j - 1, z + k, lava);
					world.setBlockWithNotify(x + i, y + j - 2, z + k, lava);
					world.setBlockWithNotify(x + i, y + j - 3, z + k, lava);
					if ((i * devX) * (i * devX) + (k * devZ) * (k * devZ) >= (radius * 0.8f * devX)
							* (radius * 0.8f * devZ)) {
						world.setBlockWithNotify(x + i + 0, y - 1, z + k + 0,
								lava);
					} else {
						world.setBlockWithNotify(x + i + 0, y, z + k + 0, lava);
						world.setBlockWithNotify(x + i + 0, y + 1, z + k + 0, 0);

						if (random.nextInt(7) == 0) {
							world.setBlockWithNotify(x + i + 0, y + 3, z + k
									+ 0, Block.mobSpawner.blockID);
							TileEntityMobSpawner spawner = (TileEntityMobSpawner) world
									.getBlockTileEntity(x + i + 0, y + 3, z + k
											+ 0);
							if (spawner != null) {
								spawner.func_98049_a().setMobID("Fledgeling");
							}
						}
					}
				}

				for (int i = 0; i < stalactites.size(); i++) {
					if (((x - stalactites.get(i)[0]) * devX)
							* ((x - stalactites.get(i)[0]) * devX)
							+ ((z - stalactites.get(i)[2]) * devZ)
							* ((z - stalactites.get(i)[2]) * devZ) < (radius * 0.8f * devX)
							* (radius * 0.8f * devZ)
							&& world.getBlockId(stalactites.get(i)[0], y,
									stalactites.get(i)[2]) == lava) {// don't
						// make a
						// stalactite
						// if
						// there's
						// supposed
						// to be a
						// lava
						// column
						continue;
					}
					int h = random.nextInt((int) (8 / devY));
					if (h == (int) (8 / devY) - 1) {
						h += 1;
					}
					for (int l = h; l > 0; l--) {
						if (stalactites.get(i)[1] - l < 2) {
							continue;
						}
						world.setBlockAndMetadataWithNotify(
								stalactites.get(i)[0], stalactites.get(i)[1]
										- l, stalactites.get(i)[2], stone,
								random.nextInt(3));
					}
					int r = random.nextInt(1000);

					if (stalactites.get(i)[1] - h - 1 < 2) {

					} else if (r < 5) {
						world.setBlockWithNotify(stalactites.get(i)[0],
								stalactites.get(i)[1] - h - 1,
								stalactites.get(i)[2], Block.oreDiamond.blockID);
					} else if (r < 20) {
						world.setBlockWithNotify(stalactites.get(i)[0],
								stalactites.get(i)[1] - h - 1,
								stalactites.get(i)[2], Block.oreGold.blockID);
					} else if (r < 100) {
						world.setBlockWithNotify(stalactites.get(i)[0],
								stalactites.get(i)[1] - h - 1,
								stalactites.get(i)[2], Block.oreIron.blockID);
					} else if (r < 150) {
						world.setBlockWithNotify(stalactites.get(i)[0],
								stalactites.get(i)[1] - h - 1,
								stalactites.get(i)[2], gravel);
					} else if (r < 160) {
						world.setBlockWithNotify(stalactites.get(i)[0],
								stalactites.get(i)[1] - h - 1,
								stalactites.get(i)[2], gravel);
						world.setBlockWithNotify(stalactites.get(i)[0],
								stalactites.get(i)[1] - h - 2,
								stalactites.get(i)[2], gravel);
					}
				}

				// TODO: Add dragon, diversity of resources in this pile, a
				// chest full of goodies?, and if dragon does not spawn, don't
				// spawn resource pile either.
				// TODO: Dragon: create a spawning tile/tileentity that checks
				// for the nearest Fire Dragon. If the nearest Fire Dragon is
				// too far away, a new sleeping dragon spawns.

				world.setBlockWithNotify(x, y, z, Block.oreDiamond.blockID);

				world.setBlockWithNotify(x - 1, y, z, Block.oreDiamond.blockID);
				world.setBlockWithNotify(x, y, z - 1, Block.oreDiamond.blockID);
				world.setBlockWithNotify(x, y, z + 1, Block.oreDiamond.blockID);
				world.setBlockWithNotify(x + 1, y, z, Block.oreDiamond.blockID);
				world.setBlockWithNotify(x, y + 1, z, Block.oreDiamond.blockID);

			}
		}
	}
}
