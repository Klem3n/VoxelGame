package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.Player;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.impl.SelectedBlockRenderer;
import com.mygdx.game.controller.PlayerController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mygdx.game.utils.Constants.*;

public class World implements RenderableProvider, Disposable {
    public static World INSTANCE;


    public final Map<Vector3, Chunk> chunks = new ConcurrentHashMap<>();
    public static int RENDERED_CHUNKS;
    public static TextureRegion[][] TEXTURE_TILES;

    private final PlayerController playerController;

    private Vector3 lastUpdatePosition = null;

    private boolean isRunning;

    public World(TextureRegion[][] tiles, PlayerController playerController) {
        INSTANCE = this;
        TEXTURE_TILES = tiles;
        this.playerController = playerController;
        isRunning = true;
    }

    public void set(Vector3 position, BlockType blockType) {
        set(position.x, position.y, position.z, blockType);
    }

    public void set (float x, float y, float z, BlockType blockType) {
        int ix = floor(x);
        int iy = floor(y);
        int iz = floor(z);
        int chunkX = floor(x / CHUNK_SIZE_X);
        int chunkY = floor(y / CHUNK_SIZE_Y);
        int chunkZ = floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        if((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null){
            return;
        }

        chunk.set(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z), blockType);
    }

    public BlockType get (Vector3 position) {
        return get(position.x, position.y, position.z);
    }

    public BlockType get (float x, float y, float z) {
        int ix = floor(x);
        int iy = floor(y);
        int iz = floor(z);
        int chunkX = floor(x / CHUNK_SIZE_X);
        int chunkY = floor(y / CHUNK_SIZE_Y);
        int chunkZ = floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        if((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null){
            return null;
        }

        return chunk.get(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z));
    }

    public float getHighest (float x, float z) {
        int ix = (int) x;
        int iz = (int) z;

        for (int y = RENDER_DISTANCE * CHUNK_SIZE_Y - 1; y > 0; y--) {
            BlockType blockType = get(ix, y, iz);
            if (blockType != null && blockType != BlockType.AIR) {
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

            if(!chunk.isVisible(getChunkPosition(playerController.getPosition()))){
                chunk.dispose();
                toRemove.add(entry.getKey());
                continue;
            }

            /**
             * render chunk
             */
            chunk.render(renderables, pool, false);
        }

        toRemove.forEach(chunks::remove);

        for (Map.Entry<Vector3, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();
            chunk.render(renderables, pool, true);
        }

        /**
         * Renders a box around the block we're aiming at
         */
        SelectedBlockRenderer.render(playerController.getPlayer().getSelectedBlock(), renderables, pool);
    }

    @Override
    public void dispose() {
        isRunning = false;
        chunks.values().forEach(Chunk::dispose);

        Chunk.MESH_POOL.dispose();
    }

    public boolean isRunning() {
        return isRunning;
    }
}