package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.renderer.SelectedBlockRenderer;
import com.mygdx.game.controller.PlayerController;
import com.mygdx.game.utils.ThreadUtil;
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
    public final ArrayMap<Vector3, Chunk> chunks = new ArrayMap<>();
    private final PlayerController playerController;
    private Vector3 lastUpdatePosition = null;
    private final WorldGenerator worldGenerator;

    public World(PlayerController playerController) {
        INSTANCE = this;
        this.playerController = playerController;
        this.worldGenerator = new DefaultWorldGenerator(this);

        chunkExecutor = Executors.newFixedThreadPool(1, ThreadUtil.create("ClientSynchronizer"));
    }

    public void set(Vector3 position, Block block) {
        set(position.x, position.y, position.z, block);
    }

    public void set(float x, float y, float z, Block block) {
        int ix = floor(x);
        int iy = floor(y);
        int iz = floor(z);
        int chunkX = floor(x / CHUNK_SIZE_X);
        int chunkY = floor(y / CHUNK_SIZE_Y);
        int chunkZ = floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        if ((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null) {
            return;
        }

        chunk.set(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z), block);
    }

    public Block get(Vector3 position) {
        return get(position.x, position.y, position.z);
    }

    public Block get(float x, float y, float z) {
        int ix = floor(x);
        int iy = floor(y);
        int iz = floor(z);
        int chunkX = floor(x / CHUNK_SIZE_X);
        int chunkY = floor(y / CHUNK_SIZE_Y);
        int chunkZ = floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        if ((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null) {
            return null;
        }

        return chunk.get(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z));
    }

    public float getHighest (float x, float z) {
        int ix = (int) x;
        int iz = (int) z;

        for (int y = RENDER_DISTANCE * CHUNK_SIZE_Y - 1; y > 0; y--) {
            Block block = get(ix, y, iz);
            if (block != null && block != Block.AIR) {
                return y + 3;
            }
        }
        return 0;
    }

    private void updateChunks(){
        if(!getChunkPosition(playerController.getPosition()).equals(lastUpdatePosition)){
            for (int y = -2; y < 2; y++) {
                for (int z = -RENDER_DISTANCE; z < RENDER_DISTANCE; z++) {
                    for (int x = -RENDER_DISTANCE; x < RENDER_DISTANCE; x++) {
                        Vector3 position = getChunkPosition(playerController.getPosition()).add(x, y, z);
                        Vector3 offset = new Vector3(position.x * CHUNK_SIZE_X, position.y * CHUNK_SIZE_Y, position.z * CHUNK_SIZE_Z);

                        if (chunks.containsKey(position) || Math.abs(getChunkPosition(playerController.getPosition()).dst(getChunkPosition(offset))) > RENDER_DISTANCE) {
                            continue;
                        }

                        Chunk chunk = new Chunk(position, position.x * CHUNK_SIZE_X, position.y * CHUNK_SIZE_Y, position.z * CHUNK_SIZE_Z);

                        worldGenerator.generateChunk(chunk);

                        chunks.put(position, chunk);
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

        /**
         * Renders a box around the block we're aiming at
         */
        SelectedBlockRenderer.render(playerController.getPlayer().getSelectedBlock(), renderables, pool);

        synchronized (chunks) {
            toRemove.forEach(chunks::removeKey);
        }

        for (Chunk chunk : chunks.values()) {
            chunk.render(renderables, pool, true);
        }
    }

    @Override
    public void dispose() {
        chunks.values().forEach(Chunk::dispose);

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
}