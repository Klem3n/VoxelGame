package com.mygdx.game.world.generator;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.world.Biome;
import com.mygdx.game.world.Chunk;

public abstract class WorldGenerator {
    public abstract float getHeight(int x, int z);

    public abstract float getTemperature(int x, int z);

    public abstract float getHumidity(int x, int z);

    public abstract Biome getBiome(int x, int z);

    public abstract Vector3 getSpawnPoint();

    public abstract void generateChunk(Chunk chunk);
}
