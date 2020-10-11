package com.mygdx.game.voxel;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.VoxelGame;
import com.mygdx.game.voxel.VoxelChunk;

import static com.mygdx.game.voxel.VoxelChunk.VERTEX_SIZE;

public class VoxelWorld implements RenderableProvider {
    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 16;
    public static final int CHUNK_SIZE_Z = 16;

    public final VoxelChunk[] chunks;
    public final int[] numVertices;
    public float[] vertices;
    public final int chunksX;
    public final int chunksY;
    public final int chunksZ;
    public final int voxelsX;
    public final int voxelsY;
    public final int voxelsZ;
    public int renderedChunks;
    public int numChunks;
    private final TextureRegion[] tiles;

    public VoxelWorld (TextureRegion[] tiles, int chunksX, int chunksY, int chunksZ) {
        this.tiles = tiles;
        this.chunks = new VoxelChunk[chunksX * chunksY * chunksZ];
        this.chunksX = chunksX;
        this.chunksY = chunksY;
        this.chunksZ = chunksZ;
        this.numChunks = chunksX * chunksY * chunksZ;
        this.voxelsX = chunksX * CHUNK_SIZE_X;
        this.voxelsY = chunksY * CHUNK_SIZE_Y;
        this.voxelsZ = chunksZ * CHUNK_SIZE_Z;
        int i = 0;
        for (int y = 0; y < chunksY; y++) {
            for (int z = 0; z < chunksZ; z++) {
                for (int x = 0; x < chunksX; x++) {
                    VoxelChunk chunk = new VoxelChunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z);
                    chunk.offset.set(x * CHUNK_SIZE_X, y * CHUNK_SIZE_Y, z * CHUNK_SIZE_Z);
                    chunks[i++] = chunk;
                }
            }
        }

        this.numVertices = new int[chunksX * chunksY * chunksZ];
        for (i = 0; i < numVertices.length; i++)
            numVertices[i] = 0;

        this.vertices = new float[VERTEX_SIZE * VERTEX_SIZE * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
    }

    public void set (float x, float y, float z, byte voxel) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int chunkX = ix / CHUNK_SIZE_X;
        if (chunkX < 0 || chunkX >= chunksX) return;
        int chunkY = iy / CHUNK_SIZE_Y;
        if (chunkY < 0 || chunkY >= chunksY) return;
        int chunkZ = iz / CHUNK_SIZE_Z;
        if (chunkZ < 0 || chunkZ >= chunksZ) return;
        chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z,
                voxel);
    }

    public byte get (float x, float y, float z) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int chunkX = ix / CHUNK_SIZE_X;
        if (chunkX < 0 || chunkX >= chunksX) return 0;
        int chunkY = iy / CHUNK_SIZE_Y;
        if (chunkY < 0 || chunkY >= chunksY) return 0;
        int chunkZ = iz / CHUNK_SIZE_Z;
        if (chunkZ < 0 || chunkZ >= chunksZ) return 0;
        return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz
                % CHUNK_SIZE_Z);
    }

    public float getHighest (float x, float z) {
        int ix = (int)x;
        int iz = (int)z;
        if (ix < 0 || ix >= voxelsX) return 0;
        if (iz < 0 || iz >= voxelsZ) return 0;
        // FIXME optimize
        for (int y = voxelsY - 1; y > 0; y--) {
            if (get(ix, y, iz) > 0) return y + 1;
        }
        return 0;
    }

    public void setColumn (float x, float y, float z, byte voxel) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        if (ix < 0 || ix >= voxelsX) return;
        if (iy < 0 || iy >= voxelsY) return;
        if (iz < 0 || iz >= voxelsZ) return;
        // FIXME optimize
        for (; iy > 0; iy--) {
            set(ix, iy, iz, voxel);
        }
    }

    public void setCube (float x, float y, float z, float width, float height, float depth, byte voxel) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int iwidth = (int)width;
        int iheight = (int)height;
        int idepth = (int)depth;
        int startX = Math.max(ix, 0);
        int endX = Math.min(voxelsX, ix + iwidth);
        int startY = Math.max(iy, 0);
        int endY = Math.min(voxelsY, iy + iheight);
        int startZ = Math.max(iz, 0);
        int endZ = Math.min(voxelsZ, iz + idepth);
        // FIXME optimize
        for (iy = startY; iy < endY; iy++) {
            for (iz = startZ; iz < endZ; iz++) {
                for (ix = startX; ix < endX; ix++) {
                    set(ix, iy, iz, voxel);
                }
            }
        }
    }

    @Override
    public void getRenderables (Array<Renderable> renderables, Pool<Renderable> pool) {
        renderedChunks = 0;
        for (int i = 0; i < chunks.length; i++) {
            VoxelChunk chunk = chunks[i];
            Mesh mesh = chunk.getMesh();
            if (chunk.isDirty()) {
                int numVerts = chunk.calculateVertices(vertices, tiles);
                numVertices[i] = numVerts / 4 * VERTEX_SIZE;
                mesh.setVertices(vertices, 0, numVerts * VERTEX_SIZE);
                chunk.setDirty(false);
            }
            if (numVertices[i] == 0) continue;
            Renderable renderable = pool.obtain();
            renderable.material = VoxelGame.MATERIAL;
            renderable.meshPart.mesh = mesh;
            renderable.meshPart.offset = 0;
            renderable.meshPart.size = numVertices[i];
            renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
            renderables.add(renderable);
            renderedChunks++;
        }
    }
}