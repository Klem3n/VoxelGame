package com.mygdx.game.world.generator.impl;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.FastNoiseLite;
import com.mygdx.game.utils.WhittakerBiomes;
import com.mygdx.game.world.World;
import com.mygdx.game.world.biome.Biome;
import com.mygdx.game.world.biome.BiomeManager;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.generator.WorldGenerator;

import static com.mygdx.game.utils.Constants.SEALEVEL;

public class DefaultWorldGenerator extends WorldGenerator {
    private static final int RANDOM_NOISE_AMOUNT = 8;

    private final World world;

    private final FastNoiseLite sumDolin, sumHribov, sumGorInOceanov, swampNoise, caveNoise, riverNoise, riverDepthNoise, sumTemperature, humidityNoise, soilNoise;

    //random noises with high octaves used to generate foliage, soil deposits, caves
    private FastNoiseLite[] randomNoises;

    private Vector3 spawnPoint;

    public DefaultWorldGenerator(World world) {
        this.world = world;

        sumDolin = new FastNoiseLite(world.getWorldSeed());
        sumHribov = new FastNoiseLite(world.getWorldSeed() + 1);
        sumGorInOceanov = new FastNoiseLite(world.getWorldSeed() + 2);
        swampNoise = new FastNoiseLite(world.getWorldSeed() + 3);
        caveNoise = new FastNoiseLite(world.getWorldSeed() + 4);
        riverNoise = new FastNoiseLite(world.getWorldSeed() + 5);
        riverDepthNoise = new FastNoiseLite(world.getWorldSeed() + 6);
        sumTemperature = new FastNoiseLite(world.getWorldSeed() + 7);
        humidityNoise = new FastNoiseLite(world.getWorldSeed() + 8);
        soilNoise = new FastNoiseLite(world.getWorldSeed() + 9);

        sumDolin.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        sumHribov.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        sumGorInOceanov.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        swampNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        caveNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        riverNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        riverDepthNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        sumTemperature.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        humidityNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
        soilNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);

        sumDolin.SetFractalOctaves(5);
        sumDolin.SetFrequency(0.02f);

        sumHribov.SetFractalOctaves(5);
        sumHribov.SetFrequency(0.05f);

        sumGorInOceanov.SetFractalOctaves(3);
        sumGorInOceanov.SetFrequency(0.003f);

        swampNoise.SetFractalOctaves(2);
        swampNoise.SetFrequency(0.15f);

        caveNoise.SetFractalOctaves(2);
        caveNoise.SetFrequency(0.15f);

        riverNoise.SetFractalOctaves(3);
        riverNoise.SetFrequency(0.03f);

        riverDepthNoise.SetFractalOctaves(1);
        riverDepthNoise.SetFrequency(0.005f);

        sumTemperature.SetFractalOctaves(3);
        sumTemperature.SetFrequency(0.005f);

        humidityNoise.SetFractalOctaves(3);
        humidityNoise.SetFrequency(0.005f);

        soilNoise.SetFractalOctaves(3);
        soilNoise.SetDomainWarpAmp(0.0f);
        soilNoise.SetFrequency(0.01f);

        randomNoises = new FastNoiseLite[RANDOM_NOISE_AMOUNT];

        for (int i = 0; i < RANDOM_NOISE_AMOUNT; i++) {
            FastNoiseLite randomNoise = randomNoises[i] = new FastNoiseLite(world.getWorldSeed() + 10 + i);

            randomNoise.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
            randomNoise.SetFractalOctaves(1);
            randomNoise.SetFrequency(0.1f);
        }

    }

    @Override
    public float getTemperature(int x, int z) {
        //temperature od -10 do 35
        return sumTemperature.GetNoise(x, z) * 22.5f + 12.5f;
    }

    @Override
    public float getHumidity(int x, int z) {
        //vlažnost 0% - 100%
        return humidityNoise.GetNoise(x, z) * 50.0f + 50.0f;
    }

    @Override
    public int getBiome(int x, int z) {
        float temp = getTemperature(x, z);
        float humidity = getHumidity(x, z);

        return WhittakerBiomes.getBiome(temp, humidity);
    }

    @Override
    public void generateChunk(Chunk chunk) {
        //First generate biomes
        generateBiomes(chunk);

        //Fills chunk with stone and bed rock
        fillChunk(chunk);

        //Fills the top soil of a chunk (grass, sand, snow, ...)
        fillTopSoil(chunk);

        //Adds the biome features (trees, flowers, animals, ...)
        biomeFeatures(chunk);

        chunk.updateNeighborChunks();
    }

    private void generateBiomes(Chunk chunk) {
        Vector3 position = chunk.getPosition();

        for (int chunkX = 0; chunkX < Constants.CHUNK_SIZE_X; chunkX++) {
            for (int chunkZ = 0; chunkZ < Constants.CHUNK_SIZE_Z; chunkZ++) {
                int x = chunkX + (int) position.x;
                int z = chunkZ + (int) position.z;

                int biome = getBiome(x, z);
                float temp = getTemperature(x, z);
                float humidity = getHumidity(x, z);

                // Generira doline sveta
                // Razpon: [-4, 4]
                float doline = sumDolin.GetNoise(x, z) * 4f;

                // Zemljevid hribov. Ustvari majhne hribe v svetu
                // Razpon: [-15, 15]
                float hribi = sumHribov.GetNoise(x, z) * 15.0f;

                // Zemljevid gor in oceanov
                // Razpon: [-75, 75]
                float GoreInOceani = sumGorInOceanov.GetNoise(x, z) * 75.0f;

                float visina = (SEALEVEL + doline);

                if (GoreInOceani <= -25.0f) {
                    GoreInOceani += 25.0f;
                    visina += GoreInOceani;
                } else if (GoreInOceani >= 25.0f) {
                    GoreInOceani -= 25.0f;
                    visina += GoreInOceani;
                }
                if (hribi > 5.0f) {
                    hribi -= 5.0f;
                    visina += hribi;
                }
                chunk.setHeight(chunkX, chunkZ, visina);

                /* When rivers map intersects with extreme map it creates a river.
                 *  The river map does NOT extend into ranges that cause mountains or oceans.
                 *  Range : -20 to 20
                 */
                float rivers = riverNoise.GetNoise(x, z) * 20.0f;

                /*float riverElev = Math.abs(extreme - rivers);
                if (riverElev <= 5f) {
                    riverElev = 5f - riverElev;  //inverse level
                    height -= riverElev * 0.7f;
                    if (riverElev > 2) {
                        //add clay later
                        //chunk.river[p] = true;
                    }
                }*/

                chunk.setBiome(chunkX, chunkZ, biome);
                chunk.setHeight(chunkX, chunkZ, visina);
                chunk.setTemp(chunkX, chunkZ, temp);
                chunk.setHumidity(chunkX, chunkZ, humidity);
            }
        }
    }

    private void fillChunk(Chunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE_Y; y++) {
                for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                    int biomeId = chunk.getBiome(x, z);

                    Biome biome = BiomeManager.getById(biomeId);

                    biome.fillChunk(chunk, x, y, z, World.INSTANCE, randomNoises);
                }
            }
        }
    }

    private void fillTopSoil(Chunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                int biomeId = chunk.getBiome(x, z);

                Biome biome = BiomeManager.getById(biomeId);

                biome.fillTopSoil(chunk, x, z, World.INSTANCE, soilNoise, randomNoises);
            }
        }
    }

    private void biomeFeatures(Chunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE_X; x++) {
            for (int z = 0; z < Constants.CHUNK_SIZE_Z; z++) {
                int biomeId = chunk.getBiome(x, z);

                Biome biome = BiomeManager.getById(biomeId);

                biome.addBiomeFeatures(chunk, x, z, World.INSTANCE, randomNoises);
            }
        }
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
