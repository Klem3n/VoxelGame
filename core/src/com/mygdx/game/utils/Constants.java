package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector3;

public class Constants {
    public static final FastNoiseLite fastNoiseLite = new FastNoiseLite(0);

    public static final int RENDER_DISTANCE = 6;

    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 16;
    public static final int CHUNK_SIZE_Z = 16;

    public static final int VERTEX_SIZE = 12;

    public static Vector3 getChunkPosition(Vector3 position){
        return new Vector3((int)position.x/CHUNK_SIZE_X, (int)position.y/CHUNK_SIZE_Y, (int)position.z/CHUNK_SIZE_Z);
    }

    static {
        fastNoiseLite.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
    }
}
