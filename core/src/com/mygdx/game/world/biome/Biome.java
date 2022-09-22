package com.mygdx.game.world.biome;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.FastNoiseLite;
import com.mygdx.game.world.World;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.tree.TreeManager;

import java.util.Random;

import static com.mygdx.game.utils.Constants.CHUNK_SIZE_Y;
import static com.mygdx.game.utils.Constants.SEALEVEL;

public abstract class Biome {

    private final int id;
    private final String name;

    //The odds of trees, flowers and animals spawning in the biome (50 - 1 in 50)
    protected int treeOdds = Integer.MAX_VALUE, folliageOdds = Integer.MAX_VALUE, animalOdds = Integer.MAX_VALUE;

    protected int topSoil = BlockID.GRASS;
    protected int soilBlock = BlockID.DIRT;

    private final Array<Integer> folliage = new Array<>();
    private final Array<Integer> trees = new Array<>();

    public Biome(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Fills chunk with stone and bedrock
     */
    public void fillChunk(Chunk chunk, int x, int y, int z, World instance, FastNoiseLite[] randomNoises) {
        Vector3 position = chunk.getPosition().cpy().add(x, y, z);
        int height = (int) chunk.getHeight(x, z);
        float temp = chunk.getTemp(x, z);

        int randomSeed = new Vector3(position.x, 0, position.z).hashCode();

        Random random = new Random(randomSeed);

        if (position.y <= height) {
            int bedRockHeight = (int) ((random.nextDouble() * 3) + 3);

            if (position.y <= bedRockHeight) {
                chunk.setFast(x, y, z, BlockID.BEDROCK);
            } else {
                chunk.setFast(x, y, z, BlockID.STONE);
            }
        } else if (position.y <= SEALEVEL) {
            if (temp <= 0f && position.y == SEALEVEL) {
                chunk.setFast(x, y, z, BlockID.ICE);
            } else {
                chunk.setFast(x, y, z, BlockID.WATER);
            }
        }
    }

    /**
     * Fills chunks top soil
     */
    public void fillTopSoil(Chunk chunk, int x, int z, World instance, FastNoiseLite soilNoise, FastNoiseLite[] randomNoises) {
        int height = (int) chunk.getHeight(x, z);
        int realX = (int) (chunk.getPosition().x + x);
        int realZ = (int) (chunk.getPosition().z + z);

        int soilDepth = soilNoise.GetIntNoise(5, realX, realZ);

        for (int y = CHUNK_SIZE_Y - 1; y >= 0; y--) {
            int realY = (int) (chunk.getPosition().y + y);
            int depth = height - realY;

            if (depth < 0 || depth > soilDepth) {
                continue;
            }

            if (depth == 0 && realY >= SEALEVEL) {
                chunk.setFast(x, y, z, topSoil);
            } else {
                chunk.setFast(x, y, z, soilBlock);
            }
        }
    }

    public void addBiomeFeatures(Chunk chunk, int x, int z, World world, FastNoiseLite[] randomNoises) {
        int height = (int) chunk.getHeight(x, z) + 1;

        if (chunk.getChunkPosition().y != (height >> Constants.BIT_CHUNK_SIZE) || height <= Constants.SEALEVEL) {
            return;
        }

        FastNoiseLite folliageNoise = randomNoises[0];
        FastNoiseLite flowerTypeNoise = randomNoises[1];
        FastNoiseLite treeNoise = randomNoises[2];
        FastNoiseLite treeTypeNoise = randomNoises[3];

        int realX = (int) chunk.position.x + x;
        int realY = height % CHUNK_SIZE_Y;
        int realZ = (int) chunk.position.z + z;

        if (!folliage.isEmpty()) {
            if (folliageNoise.GetIntNoise(Integer.MAX_VALUE, realX, realZ) % folliageOdds == 0) {
                int index = flowerTypeNoise.GetIntNoise(folliage.size, realX, realZ);

                chunk.setFast(x, height % CHUNK_SIZE_Y, z, folliage.get(index));
            }
        }

        if (!trees.isEmpty()) {

            if (treeNoise.GetIntNoise(Integer.MAX_VALUE, realX, realZ) % treeOdds == 0) {
                int index = flowerTypeNoise.GetIntNoise(trees.size, x, realZ);

                TreeManager.getById(trees.get(index)).buildTree(chunk, x, height % CHUNK_SIZE_Y, z, world, randomNoises);
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTreeOdds() {
        return treeOdds;
    }

    public int getFolliageOdds() {
        return folliageOdds;
    }

    public int getAnimalOdds() {
        return animalOdds;
    }

    public int getTopSoil() {
        return topSoil;
    }

    public int getSoilBlock() {
        return soilBlock;
    }

    public Array<Integer> getTrees() {
        return trees;
    }

    public Array<Integer> getFolliage() {
        return folliage;
    }
}
