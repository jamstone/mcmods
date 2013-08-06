package mods.jamstone.aurus.common;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenMobNest implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (!world.isRemote && chunkX % 5 == 0 && chunkZ % 5 == 0) {
			// world.setBlock(chunkX * 16 + random.nextInt(16), 100, chunkZ * 16
			// + random.nextInt(16), Block.waterMoving.blockID);
			BiomeGenBase b = world.getBiomeGenForCoords(chunkX, chunkZ);
			int bID = b.biomeID;
			if (bID != 6) {
				// Do nothing, these biomes are inappropriate
			} else {
				int seed = Math.abs((int) world.getSeed());
				int chunksApart = 10;
				int SpawnableXOffset = ((seed + chunksApart / 4) % chunksApart);
				int SpawnableZOffset = ((seed + chunksApart / 3) % chunksApart);

				if (true) {

					int x = chunkX * 16 + random.nextInt(16);
					int z = chunkZ * 16 + random.nextInt(16);

					Chunk c = world.getChunkFromBlockCoords(x, z);

					int y = c.getHeightValue(x - c.xPosition * 16, z
							- c.zPosition * 16) + 2;

					int block = Aurus.nestID;

					int[][] indices = { { -1, 0, -1 }, { -1, 0, 0 },
							{ -1, 0, 1 }, { 0, 0, -1 }, { 0, 0, 0 },
							{ 0, 0, 1 }, { 1, 0, -1 }, { 1, 0, 0 },
							{ 1, 0, 1 },

							{ -2, 1, -2 }, { -2, 1, -1 }, { -2, 1, 0 },
							{ -2, 1, 1 }, { -2, 1, 2 }, { -1, 1, -2 },
							{ -1, 1, -1 }, { -1, 1, 1 }, { -1, 1, 2 },
							{ 0, 1, -2 }, { 0, 1, 2 }, { 1, 1, -2 },
							{ 1, 1, -1 }, { 1, 1, 1 }, { 1, 1, 2 },
							{ 2, 1, -2 }, { 2, 1, -1 }, { 2, 1, 0 },
							{ 2, 1, 1 }, { 2, 1, 2 },

							{ -3, 2, -2 }, { -3, 2, -1 }, { -3, 2, 0 },
							{ -3, 2, 1 }, { -3, 2, 2 }, { -2, 2, -3 },
							{ -2, 2, -2 }, { -2, 2, 2 }, { -2, 2, 3 },
							{ -1, 2, -3 }, { -1, 2, 3 }, { 0, 2, -3 },
							{ 0, 2, 3 }, { 1, 2, -3 }, { 1, 2, 3 },
							{ 2, 2, -3 }, { 2, 2, -2 }, { 2, 2, 2 },
							{ 2, 2, 3 }, { 3, 2, -2 }, { 3, 2, -1 },
							{ 3, 2, 0 }, { 3, 2, 1 }, { 3, 2, 2 },

							{ -3, 3, -2 }, { -3, 3, -1 }, { -3, 3, 0 },
							{ -3, 3, 1 }, { -3, 3, 2 }, { -2, 3, -3 },
							{ -2, 3, 3 }, { -1, 3, -3 }, { -1, 3, 3 },
							{ 0, 3, -3 }, { 0, 3, 3 }, { 1, 3, -3 },
							{ 1, 3, 3 }, { 2, 3, -3 }, { 2, 3, 3 },
							{ 3, 3, -2 }, { 3, 3, -1 }, { 3, 3, 0 },
							{ 3, 3, 1 }, { 3, 3, 2 },

							{ -3, 4, -2 }, { -3, 4, -1 }, { -3, 4, 0 },
							{ -3, 4, 1 }, { -3, 4, 2 }, { -2, 4, -3 },
							{ -2, 4, 3 }, { -1, 4, -3 }, { -1, 4, 3 },
							{ 0, 4, -3 }, { 0, 4, 3 }, { 1, 4, -3 },
							{ 1, 4, 3 }, { 2, 4, -3 }, { 2, 4, 3 },
							{ 3, 4, -2 }, { 3, 4, -1 }, { 3, 4, 0 },
							{ 3, 4, 1 }, { 3, 4, 2 },

							{ -3, 5, -2 }, { -3, 5, -1 }, { -3, 5, 0 },
							{ -3, 5, 1 }, { -3, 5, 2 }, { -2, 5, -3 },
							{ -2, 5, 3 }, { -1, 5, -3 }, { -1, 5, 3 },
							{ 0, 5, -3 }, { 0, 5, 3 }, { 1, 5, -3 },
							{ 1, 5, 3 }, { 2, 5, -3 }, { 2, 5, 3 },
							{ 3, 5, -2 }, { 3, 5, -1 }, { 3, 5, 0 },
							{ 3, 5, 1 }, { 3, 5, 2 },

							{ -3, 6, -2 }, { -3, 6, -1 }, { -3, 6, 0 },
							{ -3, 6, 1 }, { -3, 6, 2 }, { -2, 6, -3 },
							{ -2, 6, 3 }, { -1, 6, -3 }, { -1, 6, 3 },
							{ 0, 6, -3 }, { 0, 6, 3 }, { 1, 6, -3 },
							{ 1, 6, 3 }, { 2, 6, -3 }, { 2, 6, 3 },
							{ 3, 6, -2 }, { 3, 6, -1 }, { 3, 6, 0 },
							{ 3, 6, 1 }, { 3, 6, 2 },

							{ -3, 7, -2 }, { -3, 7, -1 }, { -3, 7, 0 },
							{ -3, 7, 1 }, { -3, 7, 2 }, { -2, 7, -3 },
							{ -2, 7, -2 }, { -2, 7, -1 }, { -2, 7, 0 },
							{ -2, 7, 1 }, { -2, 7, 2 }, { -2, 7, 3 },
							{ -1, 7, -3 }, { -1, 7, -2 }, { -1, 7, -1 },
							{ -1, 7, 1 }, { -1, 7, 2 }, { -1, 7, 3 },
							{ 0, 7, -3 }, { 0, 7, -2 }, { 0, 7, 2 },
							{ 0, 7, 3 }, { 1, 7, -3 }, { 1, 7, -2 },
							{ 1, 7, -1 }, { 1, 7, 1 }, { 1, 7, 2 },
							{ 1, 7, 3 }, { 2, 7, -3 }, { 2, 7, -2 },
							{ 2, 7, -1 }, { 2, 7, 0 }, { 2, 7, 1 },
							{ 2, 7, 2 }, { 2, 7, 3 }, { 3, 7, -2 },
							{ 3, 7, -1 }, { 3, 7, 0 }, { 3, 7, 1 },
							{ 3, 7, 2 },

							{ -1, 8, -1 }, { -1, 8, 0 }, { -1, 8, 1 },
							{ 0, 8, -1 }, { 0, 8, 0 }, { 0, 8, 1 },
							{ 1, 8, -1 }, { 1, 8, 0 }, { 1, 8, 1 },

							{ 0, 9, 0 }

					};

					for (int i = 0; i < indices.length; i++) {
						world.setBlock(x + indices[i][0], y
								+ indices[i][1], z + indices[i][2], block);
					}

					world.setBlock(x, y + 5, z,
							Block.oreDiamond.blockID);
					world.setBlock(x, y + 4, z,
							Block.mobSpawner.blockID);
					TileEntityMobSpawner spawner = (TileEntityMobSpawner) world
							.getBlockTileEntity(x, y + 4, z);
					if (spawner != null) {
						spawner.func_98049_a().setMobID("fledgelingBog");
					}
				}
			}
		}
	}

}
