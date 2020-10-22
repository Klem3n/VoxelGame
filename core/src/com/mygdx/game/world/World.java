package com.mygdx.game.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.Player;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.impl.SelectedBlockRenderer;
import com.mygdx.game.thread.BackgroundWorker;

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

    private final Player player;

    private Vector3 lastUpdatePosition = null;

    private boolean isRunning;

    private final BackgroundWorker backgroundWorker;

    public World(TextureRegion[][] tiles, Player player) {
        INSTANCE = this;
        TEXTURE_TILES = tiles;
        this.player = player;
        isRunning = true;

        backgroundWorker = new BackgroundWorker(this);
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
        if(!getChunkPosition(player.getPosition()).equals(lastUpdatePosition)){
            for (int y = -RENDER_DISTANCE; y < RENDER_DISTANCE; y++) {
                for (int z = -RENDER_DISTANCE; z < RENDER_DISTANCE; z++) {
                    for (int x = -RENDER_DISTANCE; x < RENDER_DISTANCE; x++) {
                        Vector3 position = getChunkPosition(player.getPosition()).add(x, y, z);
                        Vector3 offset = new Vector3(position.x * CHUNK_SIZE_X, position.y * CHUNK_SIZE_Y, position.z * CHUNK_SIZE_Z);

                        if(chunks.containsKey(position) || Math.abs(getChunkPosition(player.getPosition()).dst(getChunkPosition(offset))) > RENDER_DISTANCE){
                            continue;
                        }

                        Chunk chunk = new Chunk(position, position.x * CHUNK_SIZE_X, position.y * CHUNK_SIZE_Y, position.z * CHUNK_SIZE_Z);

                        chunks.put(position, chunk);
                    }
                }
            }

            lastUpdatePosition = getChunkPosition(player.getPosition());
        }
    }

    private RenderState renderState = RenderState.READY;
    private Chunk chunkToRender = null;
    @Override
    public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
        RENDERED_CHUNKS = 0;

        updateChunks();

        List<Vector3> toRemove = new ArrayList<>();

        for (Map.Entry<Vector3, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();

            if(!chunk.isVisible(getChunkPosition(player.getPosition()))){
                chunk.dispose();
                toRemove.add(entry.getKey());
                continue;
            }

            /**
             * render chunk
             */
            chunk.getRenderables(renderables, pool);
        }

        /**
         * Renders a box around the block we're aiming at
         */
        SelectedBlockRenderer.render(player.getSelectedBlock(), renderables, pool);

        toRemove.forEach(chunks::remove);
    }

    @Override
    public void dispose() {
        isRunning = false;
        chunks.forEach((key, entry)->{
            entry.dispose();
        });
    }

    public boolean isRunning() {
        return isRunning;
    }

    public BackgroundWorker getBackgroundWorker() {
        return backgroundWorker;
    }

    public RenderState getRenderState() {
        return renderState;
    }

    public void setRenderState(RenderState renderState) {
        this.renderState = renderState;
    }

    public Chunk getChunkToRender() {
        return chunkToRender;
    }

    public void setChunkToRender(Chunk chunkToRender) {
        this.chunkToRender = chunkToRender;
    }

    public enum RenderState{
        READY,
        RENDERING,
        DONE
    }
}