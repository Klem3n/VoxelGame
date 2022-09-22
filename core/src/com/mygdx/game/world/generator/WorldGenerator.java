package com.mygdx.game.world.generator;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.world.chunk.Chunk;

public abstract class WorldGenerator {
    public abstract float getTemperature(int x, int z);

    public abstract float getHumidity(int x, int z);

    public abstract int getBiome(int x, int z);

    public abstract Vector3 getSpawnPoint();

    public abstract void generateChunk(Chunk chunk);
}
