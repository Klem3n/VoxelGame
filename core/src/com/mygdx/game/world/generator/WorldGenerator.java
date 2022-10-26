package com.mygdx.game.world.generator;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.world.chunk.Chunk;

/**
 * An abstract class that represents the world generator that is used to generate the world
 */
public abstract class WorldGenerator {
    /**
     * Gets the temperature in the world on a given location
     */
    public abstract float getTemperature(int x, int z);

    /**
     * Gets the humidity in the world on a given location
     */
    public abstract float getHumidity(int x, int z);

    /**
     * Gets the biome in the world on a given location
     */
    public abstract int getBiome(int x, int z);

    public abstract Vector3 getSpawnPoint();

    /**
     * Method that generates the chunk in the world
     *
     * @param chunk The chunk to generate
     */
    public abstract void generateChunk(Chunk chunk);
}
