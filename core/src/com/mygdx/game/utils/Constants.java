package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;

/**
 * Utility file that contains static constants and methods
 */
public class Constants {
    /**
     * The default render distance
     */
    public static final int RENDER_DISTANCE = 6;

    /**
     * How big the chunk is (16x16x16)
     */
    public static final int BIT_CHUNK_SIZE = 4;

    /**
     * Chunk size X
     */
    public static final int CHUNK_SIZE_X = 1 << BIT_CHUNK_SIZE;
    /**
     * Chunk size Y
     */
    public static final int CHUNK_SIZE_Y = 1 << BIT_CHUNK_SIZE;
    /**
     * Chunk size Z
     */
    public static final int CHUNK_SIZE_Z = 1 << BIT_CHUNK_SIZE;

    /**
     * Size of the block vertex
     */
    public static final int VERTEX_SIZE = 12;

    /**
     * Sea level height
     */
    public static final float SEALEVEL = 64.0f;

    /**
     * Texture material
     */
    public static Material MATERIAL;
    /**
     * Block texture tiles
     */
    public static TextureRegion[][] TEXTURE_TILES;

    /**
     * Converts the position to chunk map position
     *
     * @param position The world position
     */
    public static Vector3 getChunkPosition(Vector3 position) {
        return new Vector3(floor(position.x / CHUNK_SIZE_X), floor(position.y / CHUNK_SIZE_Y), floor(position.z / CHUNK_SIZE_Z));
    }

    /**
     * Floors a float value
     *
     * @param val The value to floor
     */
    public static int floor(float val) {
        return (int) Math.floor(val);
    }

    /**
     * Floors a vector
     *
     * @param vector The vector to floor
     */
    public static Vector3 floor(Vector3 vector) {
        return new Vector3((int) Math.floor(vector.x), (int) Math.floor(vector.y), (int) Math.floor(vector.z));
    }

    /**
     * Ceils a float and converts to an integer
     *
     * @param i The number to ceil
     */
    public static int ceil(float i) {
        return (int) Math.ceil(i);
    }

    /**
     * Clamps a value between 0.0f and 1.0f
     *
     * @param val The value
     * @param min Min value
     * @param max Max value
     * @return The value clamped between 0.0f and 1.0f
     */
    public static float clamp(float val, float min, float max) {
        if (val <= min) {
            return 0;
        }
        if (val >= max) {
            return 1;
        }
        return (val - min) / (max - min);
    }
}
