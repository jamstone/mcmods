package mods.ifw.aurus.common;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenTrees;
import cpw.mods.fml.common.IWorldGenerator;

public class WorldGenBogDragonNest implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
	    IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
	if (world.isRemote)
	    return;
	// world.setBlock(chunkX * 16 + random.nextInt(16), 100, chunkZ * 16
	// + random.nextInt(16), Block.waterMoving.blockID);
	BiomeGenBase b = world.getBiomeGenForCoords(chunkX, chunkZ);
	int bID = b.biomeID;
	if (false && bID != 6) {
	    // Do nothing, these biomes are inappropriate
	} else {
	    int seed = Math.abs((int) world.getSeed());
	    int chunksApart = 20;
	    int SpawnableXOffset = ((seed + chunksApart / 4) % chunksApart);
	    int SpawnableZOffset = ((seed + chunksApart / 3) % chunksApart);

		if ((chunkX + 2 * chunkZ) % 500 == 0) {

//	    if (((chunkX % chunksApart) + 2 * chunksApart) % chunksApart == SpawnableXOffset
//		    && ((chunkZ % chunksApart) + 2 * chunksApart) % chunksApart == SpawnableZOffset) {

		int x = chunkX * 16 + random.nextInt(16);
		int z = chunkZ * 16 + random.nextInt(16);
		int radius = random.nextInt(40) + 40;
		// int rad2 = radius * radius;

		Chunk c = world.getChunkFromBlockCoords(x, z);

		// check if there are enough trees in this chunk to bother with
		// placing
		// a bog here. also check that we aren't touching any real
		// water.

		int y = c.getHeightValue(x - c.xPosition * 16, z - c.zPosition
			* 16) - 1;

		if (world.getBlockMaterial(x, y, z) != Material.water) {
		    return;
		}
		// int offY = (radius * 3) / 4;
		// y += offY;

		System.out.println("Carved out a bog dragon nest at: " + x
			+ ", " + y + ", " + z);

		carveBogPit(world, random, x, y, z, radius);

		world.setBlock(x, y - radius, z,
			Block.oreDiamond.blockID);

	    }
	}
    }

    protected void carveBogPit(World world, Random random, int x, int y, int z,
	    int radius) {
	int rad2 = radius * radius;

	int offY = (int) (radius * 3.0 / 4);
	y += offY;

	int bogWater = Aurus.bogWaterFlowingID;
	int dirt = Block.dirt.blockID;
	int wood = Block.wood.blockID;
	int leaves = Block.leaves.blockID;
	int lilypad = Block.waterlily.blockID;

	ArrayList<int[]> deadtrees = new ArrayList<int[]>();
	// scan for wood at a layer 3 blocks above the water level
	// if a wood block is found, add its (x, y) coordinates to deadtrees
	// after making everything, spawn some dead trees at these coordinates.

	for (int i = -radius; i < radius; i++) {
	    for (int j = -radius; j < radius; j++) {
		for (int k = -radius; k < radius; k++) {
		    int hyp2 = i * i + j * j + k * k;
		    if (hyp2 > rad2) {
			continue;
		    } else if (hyp2 >= (radius - 1) * (radius - 1)
			    && hyp2 <= (radius + 1) * (radius + 1)) {
			if (j <= -offY) {
			    if (world.getBlockId(x + i, y + j, z + k) != bogWater
				    && world.getBlockId(x + i, y + j, z + k) != wood) {
				world.setBlock(x + i, y + j, z + k,
					dirt);
			    }
			}
		    } else if (j == -offY - 2) {
			if (world.getBlockId(x + i, y + j, z + k) != bogWater
				&& world.getBlockId(x + i, y + j, z + k) != wood) {
			    world.setBlock(x + i, y + j, z + k,
				    dirtOrCoal(random));
			}
		    } else if (j <= -offY) {
			if (j < -offY - 2) {
			    if (world.getBlockId(x + i, y + j, z + k) != bogWater
				    && world.getBlockId(x + i, y + j, z + k) != wood) {
				world.setBlock(x + i, y + j, z + k,
					dirtOrCoal(random));
			    }
			} else {
			    if (j == -offY
				    && world.getBlockId(x + i, y - offY + 1, z
					    + k) != wood) {
				if (random.nextInt(120) == 0) {
				    deadtrees.add(new int[] { x + i, z + k });
				}
			    } else if (j == -offY) {
				deadtrees.add(new int[] { x + i, z + k });
			    }
			    world.setBlock(x + i, y + j, z + k, 0);
			}
		    }
		}
	    }
	}

	for (int l = 0; l < deadtrees.size(); l++) {

	    int m = deadtrees.get(l)[0];
	    int n = deadtrees.get(l)[1];

	    world.setBlock(m, y - offY - 2, n, dirt);
	    // world.setBlock(m, y - offY - 1, n, 0);
	    // world.setBlock(m, y - offY - 0, n, 0);
	    // world.setBlock(m, y - offY + 1, n, 0);
	    // world.setBlock(m, y - offY + 2, n, 0);
	    // world.setBlock(m, y - offY + 3, n, 0);
	    makeDeadTree(world, random, m, y - offY - 1, n, (radius / 16));
	}

	for (int i = -radius; i < radius; i++) {
	    for (int j = -radius; j < 0; j++) {
		for (int k = -radius; k < radius; k++) {
		    int hyp2 = i * i + j * j + k * k;
		    if (hyp2 > rad2 || j < -offY - 2) {
			continue;
		    } else if (j <= -offY) {
			if (j <= -offY - 2) {
			} else {
			    if (world.getBlockId(x + i, y + j, z + k) != wood) {
				world.setBlock(x + i, y + j, z + k,
					bogWater);
			    }
			}

		    } else if (j == -offY - 2 && i * i + k * k >= 49
			    && i * i + k * k <= 81) {
			for (int l = random.nextInt(4) + 6; l >= 0; l--) {
			    world.setBlock(x + i, y + j + l, z + k,
				    wood);
			}

		    }

		    else if (world.getBlockId(x + i, y + j, z + k) != wood) {
			if (world.getBlockId(x + i, y + j, z + k) == Block.leaves.blockID
				&& random.nextInt(100) < 3) {
			} else {
			    world.setBlock(x + i, y + j, z + k, 0);
			}
		    }
		}
	    }
	}
	y -= offY - 2;

	int block = Aurus.nestID;

	int[][] indices = { { -1, 0, -1 }, { -1, 0, 0 }, { -1, 0, 1 },
		{ 0, 0, -1 }, { 0, 0, 0 }, { 0, 0, 1 }, { 1, 0, -1 },
		{ 1, 0, 0 }, { 1, 0, 1 },

		{ -2, 1, -2 }, { -2, 1, -1 }, { -2, 1, 0 }, { -2, 1, 1 },
		{ -2, 1, 2 }, { -1, 1, -2 }, { -1, 1, -1 }, { -1, 1, 1 },
		{ -1, 1, 2 }, { 0, 1, -2 }, { 0, 1, 2 }, { 1, 1, -2 },
		{ 1, 1, -1 }, { 1, 1, 1 }, { 1, 1, 2 }, { 2, 1, -2 },
		{ 2, 1, -1 }, { 2, 1, 0 }, { 2, 1, 1 }, { 2, 1, 2 },

		{ -3, 2, -2 }, { -3, 2, -1 }, { -3, 2, 0 }, { -3, 2, 1 },
		{ -3, 2, 2 }, { -2, 2, -3 }, { -2, 2, -2 }, { -2, 2, 2 },
		{ -2, 2, 3 }, { -1, 2, -3 }, { -1, 2, 3 }, { 0, 2, -3 },
		{ 0, 2, 3 }, { 1, 2, -3 }, { 1, 2, 3 }, { 2, 2, -3 },
		{ 2, 2, -2 }, { 2, 2, 2 }, { 2, 2, 3 }, { 3, 2, -2 },
		{ 3, 2, -1 }, { 3, 2, 0 }, { 3, 2, 1 }, { 3, 2, 2 },

		{ -3, 3, -2 }, { -3, 3, -1 }, { -3, 3, 0 }, { -3, 3, 1 },
		{ -3, 3, 2 }, { -2, 3, -3 }, { -2, 3, 3 }, { -1, 3, -3 },
		{ -1, 3, 3 }, { 0, 3, -3 }, { 0, 3, 3 }, { 1, 3, -3 },
		{ 1, 3, 3 }, { 2, 3, -3 }, { 2, 3, 3 }, { 3, 3, -2 },
		{ 3, 3, -1 }, { 3, 3, 0 }, { 3, 3, 1 }, { 3, 3, 2 },

		{ -3, 4, -2 }, { -3, 4, -1 }, { -3, 4, 0 }, { -3, 4, 1 },
		{ -3, 4, 2 }, { -2, 4, -3 }, { -2, 4, 3 }, { -1, 4, -3 },
		{ -1, 4, 3 }, { 0, 4, -3 }, { 0, 4, 3 }, { 1, 4, -3 },
		{ 1, 4, 3 }, { 2, 4, -3 }, { 2, 4, 3 }, { 3, 4, -2 },
		{ 3, 4, -1 }, { 3, 4, 0 }, { 3, 4, 1 }, { 3, 4, 2 },

		{ -3, 5, -2 }, { -3, 5, -1 }, { -3, 5, 0 }, { -3, 5, 1 },
		{ -3, 5, 2 }, { -2, 5, -3 }, { -2, 5, 3 }, { -1, 5, -3 },
		{ -1, 5, 3 }, { 0, 5, -3 }, { 0, 5, 3 }, { 1, 5, -3 },
		{ 1, 5, 3 }, { 2, 5, -3 }, { 2, 5, 3 }, { 3, 5, -2 },
		{ 3, 5, -1 }, { 3, 5, 0 }, { 3, 5, 1 }, { 3, 5, 2 },

		{ -3, 6, -2 }, { -3, 6, -1 }, { -3, 6, 0 }, { -3, 6, 1 },
		{ -3, 6, 2 }, { -2, 6, -3 }, { -2, 6, 3 }, { -1, 6, -3 },
		{ -1, 6, 3 }, { 0, 6, -3 }, { 0, 6, 3 }, { 1, 6, -3 },
		{ 1, 6, 3 }, { 2, 6, -3 }, { 2, 6, 3 }, { 3, 6, -2 },
		{ 3, 6, -1 }, { 3, 6, 0 }, { 3, 6, 1 }, { 3, 6, 2 },

		{ -3, 7, -2 }, { -3, 7, -1 }, { -3, 7, 0 }, { -3, 7, 1 },
		{ -3, 7, 2 }, { -2, 7, -3 }, { -2, 7, -2 }, { -2, 7, -1 },
		{ -2, 7, 0 }, { -2, 7, 1 }, { -2, 7, 2 }, { -2, 7, 3 },
		{ -1, 7, -3 }, { -1, 7, -2 }, { -1, 7, -1 }, { -1, 7, 1 },
		{ -1, 7, 2 }, { -1, 7, 3 }, { 0, 7, -3 }, { 0, 7, -2 },
		{ 0, 7, 2 }, { 0, 7, 3 }, { 1, 7, -3 }, { 1, 7, -2 },
		{ 1, 7, -1 }, { 1, 7, 1 }, { 1, 7, 2 }, { 1, 7, 3 },
		{ 2, 7, -3 }, { 2, 7, -2 }, { 2, 7, -1 }, { 2, 7, 0 },
		{ 2, 7, 1 }, { 2, 7, 2 }, { 2, 7, 3 }, { 3, 7, -2 },
		{ 3, 7, -1 }, { 3, 7, 0 }, { 3, 7, 1 }, { 3, 7, 2 },

		{ -1, 8, -1 }, { -1, 8, 0 }, { -1, 8, 1 }, { 0, 8, -1 },
		{ 0, 8, 0 }, { 0, 8, 1 }, { 1, 8, -1 }, { 1, 8, 0 },
		{ 1, 8, 1 },

		{ 0, 9, 0 }

	};

	for (int i = 0; i < indices.length; i++) {
	    world.setBlock(x + indices[i][0], y + indices[i][1], z
		    + indices[i][2], block);
	}

	world.setBlock(x, y + 5, z, Block.oreDiamond.blockID);
	world.setBlock(x, y + 4, z, Block.mobSpawner.blockID);
	TileEntityMobSpawner spawner = (TileEntityMobSpawner) world
		.getBlockTileEntity(x, y + 4, z);
	if (spawner != null) {
	   spawner.func_98049_a().setMobID("auruBog");
	}

    }

    public int dirtOrCoal(Random random) {
	int r = random.nextInt(100);
	if (r < 5) {
	    return Block.oreCoal.blockID;
	} else {
	    return Block.dirt.blockID;
	}
    }

    public boolean makeDeadTree(World world, Random random, int x, int y,
	    int z, int minHeight) {
	int r = random.nextInt(100);
	if (r < 10) {
	    WorldGenBigTree2 wg = new WorldGenBigTree2(true, 1);
	    wg.setScale(0.8D, 1.0D, 2.0D);
	    return wg.generate(world, random, x, y, z);
	} else {
	    return (new WorldGenTrees(true, minHeight + random.nextInt(8), 1,
		    1, false)).generate(world, random, x, y, z);
	}
    }
}
