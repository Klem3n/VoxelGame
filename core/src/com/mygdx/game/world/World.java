package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.renderer.SelectedBlockRenderer;
import com.mygdx.game.controller.PlayerController;
import com.mygdx.game.utils.ThreadUtil;
import com.mygdx.game.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mygdx.game.utils.Constants.*;

public class World implements RenderableProvider, Disposable {
    public static World INSTANCE;

    /**
     * The ExecutorService.
     */
    private final ExecutorService chunkExecutor;

    public final Map<Vector3, Chunk> chunks = new ConcurrentHashMap<>();
    public static int RENDERED_CHUNKS;
    public static TextureRegion[][] TEXTURE_TILES;

    private final PlayerController playerController;

    private Vector3 lastUpdatePosition = null;

    private boolean isRunning;

    private final Material material;

    public World(TextureRegion[][] tiles, PlayerController playerController, Material material) {
        INSTANCE = this;
        TEXTURE_TILES = tiles;
        this.playerController = playerController;
        this.material = material;
        isRunning = true;

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

                        if(chunks.containsKey(position) || Math.abs(getChunkPosition(playerController.getPosition()).dst(getChunkPosition(offset))) > RENDER_DISTANCE){
                            continue;
                        }

                        Chunk chunk = new Chunk(position, position.x * CHUNK_SIZE_X, position.y * CHUNK_SIZE_Y, position.z * CHUNK_SIZE_Z);

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

        for (Map.Entry<Vector3, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();

            if (!chunk.isVisible(getChunkPosition(playerController.getPosition()))) {
                chunk.dispose();
                toRemove.add(entry.getKey());
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

        toRemove.forEach(chunks::remove);

        for (Map.Entry<Vector3, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            chunk.render(renderables, pool, true);
        }
    }

    @Override
    public void dispose() {
        isRunning = false;
        chunks.values().forEach(Chunk::dispose);

        Chunk.MESH_POOL.dispose();
        chunkExecutor.shutdown();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public ExecutorService getChunkExecutor() {
        return chunkExecutor;
    }

    public Material getMaterial() {
        return material;
    }

    public Player getPlayer() {
        return playerController.getPlayer();
    }
}