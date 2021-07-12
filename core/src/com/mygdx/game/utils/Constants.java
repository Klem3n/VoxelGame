package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;

public class Constants {
    public static final FastNoiseLite fastNoiseLite = new FastNoiseLite(0);

    public static final int RENDER_DISTANCE = 6;

    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 16;
    public static final int CHUNK_SIZE_Z = 16;

    public static final int VERTEX_SIZE = 12;

    public static Material MATERIAL;
    public static TextureRegion[][] TEXTURE_TILES;

    public static Vector3 getChunkPosition(Vector3 position) {
        return new Vector3(floor(position.x / CHUNK_SIZE_X), floor(position.y / CHUNK_SIZE_Y), floor(position.z / CHUNK_SIZE_Z));
    }

    public static int floor(float val) {
        return (int) Math.floor(val);
    }

    public static Vector3 floor(Vector3 vector) {
        return new Vector3((int) Math.floor(vector.x), (int) Math.floor(vector.y), (int) Math.floor(vector.z));
    }

    public static int ceil(float i){
        return (int) Math.ceil(i);
    }

    static {
        fastNoiseLite.SetNoiseType(FastNoiseLite.NoiseType.Perlin);
    }
}
