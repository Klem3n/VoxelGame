package com.mygdx.game.world.chunk;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import static com.mygdx.game.utils.Constants.*;

/**
 * Object that represents all the important information of the chunk
 */
public class ChunkData {
    /**
     * The cache of all generated chunk data
     */
    public static final ArrayMap<Vector3, ChunkData> CHUNK_CACHE = new ArrayMap<>();

    /**
     * The chunks position in the chunk map
     */
    private final Vector3 chunkPosition;
    /**
     * The blocks in the chunk
     */
    private final byte[] voxels;
    /**
     * The biomes in the chunk
     */
    private final int[] biomes;
    /**
     * Height map
     */
    private final float[] heights;
    /**
     * Temperature map
     */
    private final float[] temp;
    /**
     * Humidity map
     */
    private final float[] humidity;
    /**
     * All manually modified blocks in the chunk
     */
    private final Array<Integer> changedVoxelIndexes;
    /**
     * Array of all the changed block IDS in the chunk
     */
    private final Array<Byte> changedVoxels;
    /**
     * If the chunk was fully loaded
     */
    private boolean loaded = false;

    public ChunkData(Vector3 chunkPosition, byte[] voxels, int[] biomes, float[] heights, float[] temp, float[] humidity) {
        this.chunkPosition = chunkPosition;
        this.voxels = voxels;
        this.biomes = biomes;
        this.heights = heights;
        this.temp = temp;
        this.humidity = humidity;
        this.changedVoxelIndexes = new Array<>();
        this.changedVoxels = new Array<>();

        CHUNK_CACHE.put(chunkPosition, this);
    }

    public ChunkData(Vector3 chunkPosition) {
        this(chunkPosition, new byte[CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z], new int[CHUNK_SIZE_X * CHUNK_SIZE_Z], new float[CHUNK_SIZE_X * CHUNK_SIZE_Z], new float[CHUNK_SIZE_X * CHUNK_SIZE_Z], new float[CHUNK_SIZE_X * CHUNK_SIZE_Z]);
    }

    public Vector3 getChunkPosition() {
        return chunkPosition;
    }

    public byte[] getVoxels() {
        return voxels;
    }

    public int[] getBiomes() {
        return biomes;
    }

    public float[] getHeights() {
        return heights;
    }

    public float[] getTemp() {
        return temp;
    }

    public float[] getHumidity() {
        return humidity;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public Array<Integer> getChangedVoxelIndexes() {
        return changedVoxelIndexes;
    }

    public Array<Byte> getChangedVoxels() {
        return changedVoxels;
    }

    public static ChunkData loadChunkData(Chunk chunk, Vector3 chunkPosition) {
        if (CHUNK_CACHE.containsKey(chunkPosition)) {
            chunk.setDirty(true);
            return CHUNK_CACHE.get(chunkPosition);
        } else {
            return new ChunkData(chunkPosition);
        }
    }
}
