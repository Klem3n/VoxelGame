package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.Player;
import com.mygdx.game.VoxelGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mygdx.game.utils.Constants.*;

public class World implements RenderableProvider, Disposable {
    public static World INSTANCE;


    public final Map<Vector3, Chunk> chunks = new HashMap<>();
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

    public void set (float x, float y, float z, byte voxel) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int chunkX = ix / CHUNK_SIZE_X;
        int chunkY = iy / CHUNK_SIZE_Y;
        int chunkZ = iz / CHUNK_SIZE_Z;

        Chunk chunk;

        if((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null){
            return;
        }

        chunk.set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z,
                voxel);
    }

    public byte get (Vector3 position) {
        return get(position.x, position.y, position.z);
    }

    public byte get (float x, float y, float z) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int chunkX = ix / CHUNK_SIZE_X;
        int chunkY = iy / CHUNK_SIZE_Y;
        int chunkZ = iz / CHUNK_SIZE_Z;

        Chunk chunk;

        if((chunk = chunks.get(new Vector3(chunkX, chunkY, chunkZ))) == null){
            return 0;
        }

        return chunk.get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz
                % CHUNK_SIZE_Z);
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

                        Chunk chunk = new Chunk(position.x * CHUNK_SIZE_X, position.y * CHUNK_SIZE_Y, position.z * CHUNK_SIZE_Z);

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

        for (Map.Entry<Vector3, Chunk> entry : chunks.entrySet()) {
            Chunk chunk = entry.getValue();

            if(!chunk.isVisible(getChunkPosition(player.getPosition()))){
                chunk.dispose();
                toRemove.add(entry.getKey());
                continue;
            }

            if(!chunk.isGenerated()) {
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

        toRemove.forEach(chunks::remove);
    }

    @Override
    public void dispose() {
        chunks.forEach((key, chunk)->{
            chunk.dispose();
        });
    }
}