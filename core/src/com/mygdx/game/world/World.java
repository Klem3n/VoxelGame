package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.mygdx.game.Player;
import com.mygdx.game.VoxelGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mygdx.game.utils.Constants.*;

public class World implements RenderableProvider, Disposable {
    public static World INSTANCE;


    public final ArrayMap<Vector3, Chunk> chunks = new ArrayMap<>();
    public float[] vertices;
    public int renderedChunks;
    private final TextureRegion[] tiles;

    private final Player player;

    private Vector3 lastUpdatePosition = null;

    public World(TextureRegion[] tiles, Player player) {
        INSTANCE = this;

        this.tiles = tiles;

        this.vertices = new float[VERTEX_SIZE * VERTEX_SIZE * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];

        this.player = player;
    }

    public void set(Vector3 position, byte voxel) {
        set(position.x, position.y, position.z, voxel);
    }

    public void set (float x, float y, float z, byte voxel) {
        int ix = (int)Math.floor(x);
        int iy = (int)Math.floor(y);
        int iz = (int)Math.floor(z);
        int chunkX = (int) Math.floor(x / CHUNK_SIZE_X);
        int chunkY = (int) Math.floor(y / CHUNK_SIZE_Y);
        int chunkZ = (int) Math.floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        if((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null){
            return;
        }

        chunk.set(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z), voxel);
    }

    public byte get (Vector3 position) {
        return get(position.x, position.y, position.z);
    }

    public byte get (float x, float y, float z) {
        int ix = (int)Math.floor(x);
        int iy = (int)Math.floor(y);
        int iz = (int)Math.floor(z);
        int chunkX = (int) Math.floor(x / CHUNK_SIZE_X);
        int chunkY = (int) Math.floor(y / CHUNK_SIZE_Y);
        int chunkZ = (int) Math.floor(z / CHUNK_SIZE_Z);

        Chunk chunk;

        if((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null){
            return 0;
        }

        return chunk.get(Math.floorMod(ix, CHUNK_SIZE_X), Math.floorMod(iy, CHUNK_SIZE_Y), Math.floorMod(iz, CHUNK_SIZE_Z));
    }

    public float getHighest (float x, float z) {
        int ix = (int) x;
        int iz = (int) z;

        // FIXME optimize
        for (int y = RENDER_DISTANCE * CHUNK_SIZE_Y - 1; y > 0; y--) {
            if (get(ix, y, iz) > 0) return y + 1;
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

    @Override
    public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
        renderedChunks = 0;

        updateChunks();

        List<Vector3> toRemove = new ArrayList<>();

        for (ObjectMap.Entry<Vector3, Chunk> entry : chunks.entries()) {
            Chunk chunk = entry.value;

            if(!chunk.isVisible(getChunkPosition(player.getPosition()))){
                chunk.dispose();
                toRemove.add(entry.key);
                continue;
            }

            Mesh mesh = chunk.getMesh();

            if(mesh == null){
                continue;
            }

            if (chunk.isDirty()) {
                int numVerts = chunk.calculateVertices(vertices, tiles);
                chunk.setNumVertices(numVerts / 4 * VERTEX_SIZE);
                mesh.setVertices(vertices, 0, numVerts * VERTEX_SIZE);
                chunk.setDirty(false);
            }
            if (chunk.getNumVertices() == 0) continue;
            Renderable renderable = pool.obtain();
            renderable.material = VoxelGame.MATERIAL;
            renderable.meshPart.mesh = mesh;
            renderable.meshPart.offset = 0;
            renderable.meshPart.size = chunk.getNumVertices();
            renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
            renderables.add(renderable);
            renderedChunks++;
        }

        toRemove.forEach(chunks::removeKey);
    }

    @Override
    public void dispose() {
        chunks.forEach((entry)->{
            entry.value.dispose();
        });
    }
}