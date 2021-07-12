package com.mygdx.game.world.generator.impl;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.FastNoiseLite;
import com.mygdx.game.world.Biome;
import com.mygdx.game.world.Chunk;
import com.mygdx.game.world.World;
import com.mygdx.game.world.generator.WorldGenerator;

import static com.mygdx.game.world.Biome.*;

public class DefaultWorldGenerator extends WorldGenerator {

    private final World world;

    private final FastNoiseLite defaultNoise, hillNoise, extremeEnvironmentNoise, swampNoise, caveNoise, riverNoise, riverDepthNoise, tempNoise, humidityNoise;

    private Vector3 spawnPoint;

    public DefaultWorldGenerator(World world) {
        this.world = world;

        defaultNoise = new FastNoiseLite(world.getWorldSeed());
        hillNoise = new FastNoiseLite(world.getWorldSeed() + 1);
        extremeEnvironmentNoise = new FastNoiseLite(world.getWorldSeed() + 2);
        swampNoise = new FastNoiseLite(world.getWorldSeed() + 3);
        caveNoise = new FastNoiseLite(world.getWorldSeed() + 4);
        riverNoise = new FastNoiseLite(world.getWorldSeed() + 5);
        riverDepthNoise = new FastNoiseLite(world.getWorldSeed() + 6);
        tempNoise = new FastNoiseLite(world.getWorldSeed() + 7);
        humidityNoise = new FastNoiseLite(world.getWorldSeed() + 8);

        defaultNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        hillNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        extremeEnvironmentNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        swampNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        caveNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        riverNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        riverDepthNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        tempNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        humidityNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);

        tempNoise.SetFractalOctaves(1);
        humidityNoise.SetFractalOctaves(1);
    }

    @Override
    public float getHeight(int x, int z) {
        float level1 = 20.0f * defaultNoise.GetNoise(x * 1f, z * 1f);
        float level2 = 25.0f * hillNoise.GetNoise(x * 4f, z * 4f) * extremeEnvironmentNoise.GetNoise(x * 0.2f, z * 0.2f);
        float level3 = 4.0f * defaultNoise.GetNoise(x * 8f, z * 8f);

        return 70 + level1 + level2 + level3;
    }

    @Override
    public float getTemperature(int x, int z) {
        //temperatures from -30 to 50
        return tempNoise.GetNoise(x * 0.5f, z * 0.5f) * 40.0f + 10.0f;
    }

    @Override
    public float getHumidity(int x, int z) {
        //humidity 0% - 100%
        return humidityNoise.GetNoise(x * 0.5f, z * 0.5f) * 50.0f + 50.0f;
    }

    @Override
    public Biome getBiome(int x, int z) {
        float temp = getTemperature(x, z);
        float humidity = getHumidity(x, z);

        Biome biome = FOREST;

        if (temp < -15.0) {
            biome = TUNDRA;
        } else if (temp < 0.0) {
            biome = TAIGA;
        } else {
            if (humidity > 66.0) {
                if (temp > 30.0) {
                    biome = JUNGLE;
                } else if (temp > 20.0) {
                    biome = SWAMP;
                } else {
                    biome = DARK_FOREST;
                }
            } else if (humidity < 33.0) {
                biome = temp < 35.0 ? PLAINS : DESERT;
            }
        }

        return biome;
    }

    @Override
    public void generateChunk(Chunk chunk) {
        Vector3 position = chunk.getPosition();

        for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                float height = getHeight(x + (int) position.x, z + (int) position.z);

                for (int y = 0; y < Constants.CHUNK_SIZE_Y; y++) {
                    int realY = y + (int) position.y;

                    if (realY < 0) {
                        break;
                    }

                    if (realY > height && realY < 62) {
                        chunk.setFast(x, y, z, BlockID.WATER);
                        continue;
                    } else if (realY > height) {
                        break;
                    }

                    float depth = height - realY;

                    if (depth == 0.0f) {
                        chunk.setFast(x, y, z, BlockID.GRASS);
                    } else if (depth < 5.0f) {
                        chunk.setFast(x, y, z, BlockID.DIRT);
                    } else if (realY > 0.0f) {
                        chunk.setFast(x, y, z, BlockID.STONE);
                    } else {
                        chunk.setFast(x, y, z, BlockID.BEDROCK);
                    }
                }
            }
        }

        chunk.updateNeighborChunks();
    }

    @Override
    public Vector3 getSpawnPoint() {
        if (spawnPoint == null) {
            spawnPoint = generateSpawnPoint();
        }
        return spawnPoint;
    }

    private Vector3 generateSpawnPoint() {
        return null;
    }
}
