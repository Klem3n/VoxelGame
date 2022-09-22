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
    public static World INSTANCE;
    private static final int WORLD_SEED = 1234;
    public static int RENDERED_CHUNKS;

    /**
     * The ExecutorService.
     */
    private final ExecutorService chunkExecutor;

    private final ArrayMap<Vector3, Chunk> chunks = new ArrayMap<>();
    private final Object lock = new Object();  //lock access to cache

    private final PlayerController playerController;
    private Vector3 lastUpdatePosition = null;
    private final WorldGenerator worldGenerator;


    public World(PlayerController playerController) {
        INSTANCE = this;
        this.playerController = playerController;
        this.worldGenerator = new DefaultWorldGenerator(this);

        chunkExecutor = Executors.newFixedThreadPool(1, ThreadUtil.create("ClientSynchronizer"));
    }

    public void set(Vector3 position, Block block, boolean generate, boolean load) {
        set(position.x, position.y, position.z, block, generate, load);
    }

    public void set(Vector3 position, int block, boolean generate, boolean load) {
        set(position.x, position.y, position.z, block, generate, load);
    }

    public void set(float x, float y, float z, Block block, boolean generate, boolean load) {
        if (block == null) {
            block = Block.AIR;
        }

        set(x, y, z, block.getId(), generate, load);
    }

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

    public void setFast(Vector3 position, Block block, boolean generate, boolean load) {
        if (block == null) {
            block = Block.AIR;
        }

        setFast(position.x, position.y, position.z, block.getId(), generate, load);
    }

    public void setFast(Vector3 position, int block, boolean generate, boolean load) {
        setFast(position.x, position.y, position.z, block, generate, load);
    }

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

    public Block get(Vector3 position, boolean generate, boolean load) {
        return get(position.x, position.y, position.z, generate, load);
    }

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

    @Override
    public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
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
        }
    }

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
        return WORLD_SEED;
    }

    public ArrayMap<Vector3, Chunk> getChunks() {
        return chunks;
    }

}