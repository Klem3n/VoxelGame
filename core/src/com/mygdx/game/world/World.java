package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.renderer.SelectedBlockRenderer;
import com.mygdx.game.controller.PlayerController;
import com.mygdx.game.utils.ThreadUtil;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.entity.player.Player;
import com.mygdx.game.world.generator.WorldGenerator;
import com.mygdx.game.world.generator.impl.DefaultWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mygdx.game.utils.Constants.*;

public class World implements RenderableProvider, Disposable {
    /**
     * Static instance of the current world being played
     */
    public static World INSTANCE;
    /**
     * Static variable representing how many chunks are currently being rendered in the current frame
     */
    public static int RENDERED_CHUNKS;

    /**
     * The ExecutorService responsible for generating chunks.
     */
    private final ExecutorService chunkExecutor;

    /**
     * Array of all chunks currently in the world
     */
    private final ArrayMap<Vector3, Chunk> chunks = new ArrayMap<>();
    /**
     * Locks access to the chunk cache
     */
    private final Object lock = new Object();

    /**
     * Player controller reference
     */
    private final PlayerController playerController;
    /**
     * Last player position where the chunks were updated
     */
    private Vector3 lastUpdatePosition = null;
    /**
     * The generator object used for generating the world and its terrain
     */
    private final WorldGenerator worldGenerator;
    /**
     * The seed of the world
     */
    private final int seed;
    /**
     * If the world has finished loading
     */
    private boolean loaded = false;

    /**
     * Creates a new {@link World} object
     *
     * @param playerController Reference to the player controller
     * @param seed             World seed
     */
    public World(PlayerController playerController, int seed) {
        INSTANCE = this;
        this.playerController = playerController;
        this.worldGenerator = new DefaultWorldGenerator(this);
        this.seed = seed;

        chunkExecutor = Executors.newFixedThreadPool(3, ThreadUtil.create("ClientSynchronizer"));
    }

    /**
     * Sets a block in a chunk
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public void set(Vector3 position, Block block, boolean generate, boolean load) {
        set(position.x, position.y, position.z, block, generate, load);
    }

    /**
     * Sets a block in a chunk
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public void set(Vector3 position, int block, boolean generate, boolean load) {
        set(position.x, position.y, position.z, block, generate, load);
    }

    /**
     * Sets a block in a chunk
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public void set(float x, float y, float z, Block block, boolean generate, boolean load) {
        if (block == null) {
            block = Block.AIR;
        }

        set(x, y, z, block.getId(), generate, load);
    }

    /**
     * Sets a block in a chunk
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public void set(float x, float y, float z, int block, boolean generate, boolean load) {
        int ix = floor(x);
        int iy = floor(y);
        int iz = floor(z);
        int chunkX = floor(x / CHUNK_SIZE_X);
        int chunkY = floor(y / CHUNK_SIZE_Y);
        int chunkZ = floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        synchronized (lock) {
            if ((chunk = getChunk(new Vector3(chunkX, chunkY, chunkZ), generate, load)) == null) {
                return;
            }

            chunk.set(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z), block);
        }
    }

    /**
     * Sets a block in a chunk without checking for the position bounds
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public void setFast(Vector3 position, Block block, boolean generate, boolean load) {
        if (block == null) {
            block = Block.AIR;
        }

        setFast(position.x, position.y, position.z, block.getId(), generate, load);
    }

    /**
     * Sets a block in a chunk without checking for the position bounds
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public void setFast(Vector3 position, int block, boolean generate, boolean load) {
        setFast(position.x, position.y, position.z, block, generate, load);
    }

    /**
     * Sets a block in a chunk without checking for the position bounds
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public void setFast(float x, float y, float z, int block, boolean generate, boolean load) {
        int ix = floor(x);
        int iy = floor(y);
        int iz = floor(z);
        int chunkX = floor(x / CHUNK_SIZE_X);
        int chunkY = floor(y / CHUNK_SIZE_Y);
        int chunkZ = floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        synchronized (lock) {
            if ((chunk = getChunk(new Vector3(chunkX, chunkY, chunkZ), generate, load)) == null) {
                return;
            }

            chunk.setFast(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z), block);
        }
    }

    /**
     * Gets a block in a chunk
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public Block get(Vector3 position, boolean generate, boolean load) {
        return get(position.x, position.y, position.z, generate, load);
    }

    /**
     * Gets a block in a chunk
     *
     * @param generate if the chunk should be generated
     * @param load     if the chunk should be loaded
     */
    public Block get(float x, float y, float z, boolean generate, boolean load) {
        int ix = floor(x);
        int iy = floor(y);
        int iz = floor(z);
        int chunkX = floor(x / CHUNK_SIZE_X);
        int chunkY = floor(y / CHUNK_SIZE_Y);
        int chunkZ = floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        synchronized (lock) {
            if ((chunk = getChunk(new Vector3(chunkX, chunkY, chunkZ), generate, load)) == null) {
                return null;
            }

            return chunk.get(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z));
        }
    }

    /**
     * Gets a chunk in the world
     *
     * @param chunkPosition The chunks position in the map
     * @param generate      If the chunk should be generated
     * @param load          If the chunk should be loaded
     * @return {@link Chunk} if chunk exists in the world
     */
    public Chunk getChunk(Vector3 chunkPosition, boolean generate, boolean load) {
        synchronized (chunks) {
            Chunk chunk = null;

            if (chunks.containsKey(chunkPosition)) {
                chunk = chunks.get(chunkPosition);
            } else if (load || generate) {
                chunk = new Chunk(chunkPosition, chunkPosition.x * CHUNK_SIZE_X, chunkPosition.y * CHUNK_SIZE_Y, chunkPosition.z * CHUNK_SIZE_Z);

                chunks.put(chunkPosition, chunk);
            }

            if (generate && !chunk.isLoaded()) {
                Chunk finalChunk = chunk;
                chunkExecutor.submit(() -> {
                    worldGenerator.generateChunk(finalChunk);
                    finalChunk.setLoaded(true);
                });
            }

            return chunk;
        }
    }

    /**
     * Updates the chunks rendered every frame
     * <p>
     * If player moved out of a chunks range, the chunk is removed and a new one is added
     */
    private void updateChunks() {
        if (!getChunkPosition(playerController.getPosition()).equals(lastUpdatePosition)) {
            for (int y = -2; y < 2; y++) {
                for (int z = -RENDER_DISTANCE; z < RENDER_DISTANCE; z++) {
                    for (int x = -RENDER_DISTANCE; x < RENDER_DISTANCE; x++) {
                        Vector3 position = getChunkPosition(playerController.getPosition()).add(x, y, z);
                        Vector3 offset = new Vector3(position.x * CHUNK_SIZE_X, position.y * CHUNK_SIZE_Y, position.z * CHUNK_SIZE_Z);

                        synchronized (lock) {
                            if (Math.abs(getChunkPosition(playerController.getPosition()).dst(getChunkPosition(offset))) > RENDER_DISTANCE) {
                                continue;
                            }

                            chunks.put(position, getChunk(position, true, true));
                        }
                    }
                }
            }

            lastUpdatePosition = getChunkPosition(playerController.getPosition());
        }
    }

    /**
     * Gets all the renderable objects in the world
     *
     * @param renderables Array of all objects waiting to be rendered
     * @param pool        The pool of renderable objects
     */
    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        RENDERED_CHUNKS = 0;

        updateChunks();

        List<Vector3> toRemove = new ArrayList<>();

        synchronized (lock) {
            for (ObjectMap.Entry<Vector3, Chunk> entry : chunks.entries()) {
                Chunk chunk = entry.value;

                if (!chunk.isVisible(getChunkPosition(playerController.getPosition()))) {
                    chunk.dispose();
                    toRemove.add(entry.key);
                    continue;
                }

                /**
                 * render chunk
                 */
                chunk.render(renderables, pool, false);
            }
        }

        /**
         * Renders a box around the block we're aiming at
         */
        SelectedBlockRenderer.render(playerController.getPlayer().getSelectedBlock(), renderables, pool);

        synchronized (lock) {
            toRemove.forEach(chunks::removeKey);

            for (Chunk chunk : chunks.values()) {
                chunk.render(renderables, pool, true);
            }

            checkWorldLoad();
        }
    }

    /**
     * Disposes of the world and all the chunks
     * <p>
     * also shuts down the chunk executor threads
     */
    @Override
    public void dispose() {
        synchronized (lock) {
            chunks.values().forEach(Chunk::dispose);
        }

        Chunk.MESH_POOL.dispose();
        chunkExecutor.shutdown();
    }

    public ExecutorService getChunkExecutor() {
        return chunkExecutor;
    }

    public Player getPlayer() {
        return playerController.getPlayer();
    }

    public WorldGenerator getWorldGenerator() {
        return worldGenerator;
    }

    public int getWorldSeed() {
        return this.seed;
    }

    public ArrayMap<Vector3, Chunk> getChunks() {
        return chunks;
    }

    public boolean loaded() {
        return loaded;
    }

    /**
     * Checks if the world has fully finished loading
     */
    public void checkWorldLoad() {
        if (loaded) {
            return;
        }

        for (ObjectMap.Entry<Vector3, Chunk> chunk : chunks) {
            if (chunk.value.isLoaded() && chunk.value.isDirty()) {
                return;
            }
        }

        loaded = true;
    }
}