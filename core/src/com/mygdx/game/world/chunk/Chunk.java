package com.mygdx.game.world.chunk;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockManager;
import com.mygdx.game.utils.ChunkMeshPool;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.world.World;

import static com.mygdx.game.utils.Constants.*;

public class Chunk implements Disposable {
    public static final ChunkMeshPool MESH_POOL = new ChunkMeshPool();

    private boolean active;

    private final byte[] faceMasks;
    private final int width;
    private final int height;
    private final int depth;
    public final Vector3 position = new Vector3();
    private final int widthTimesHeight;

    private Mesh mesh;
    private Mesh meshTransparent;
    private boolean dirty;

    private boolean generated;

    private final ChunkData chunkData;

    /**
     * Amount of vertices generated for this chunk
     */
    private int vertAmount = 0;
    private int vertAmountTransparent = 0;

    public static final float[] VERTICES = new float[VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];

    private final int maxVertices, maxIndices;

    public Chunk(Vector3 chunkPosition, float x, float y, float z) {
        this.width = CHUNK_SIZE_X;
        this.height = CHUNK_SIZE_Y;
        this.depth = CHUNK_SIZE_Z;

        chunkData = ChunkData.loadChunkData(this, chunkPosition);

        this.faceMasks = new byte[width * height * depth];

        this.widthTimesHeight = width * height;

        this.maxVertices = width * height * depth * VERTEX_SIZE * 6;
        this.maxIndices = width * height
                * depth * 36;

        this.position.set(x, y, z);
    }

    public void generateMesh(){
        generated = true;

        this.mesh = MESH_POOL.obtain(new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.ColorUnpacked()),
                maxVertices, maxIndices);

        this.meshTransparent = MESH_POOL.obtain(new VertexAttributes(VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.ColorUnpacked()),
                maxVertices, maxIndices);

        int len = width * height * depth * 6 * 6;
        short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i + 0] = (short)(j + 0);
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = (short)(j + 0);
        }

        this.mesh.setIndices(indices);
        this.meshTransparent.setIndices(indices);
    }

    public Block get(int x, int y, int z) {
        if (x < 0 || x >= width) {
            return Block.AIR;
        }
        if (y < 0 || y >= height) {
            return Block.AIR;
        }
        if (z < 0 || z >= depth) {
            return Block.AIR;
        }
        return getFast(x, y, z);
    }

    public Block getFast(int x, int y, int z) {
        return BlockManager.getById(chunkData.getVoxels()[x + z * width + y * widthTimesHeight]);
    }

    public void set(int x, int y, int z, int block) {
        if (x < 0 || x >= width) {
            return;
        }
        if (y < 0 || y >= height) {
            return;
        }
        if (z < 0 || z >= depth) {
            return;
        }

        setFast(x, y, z, block);
        modifyBlock(x, y, z, block);

        float xDiff = 0;
        float yDiff = 0;
        float zDiff = 0;

        if (x == 0) {
            xDiff--;
        } else if (x == width - 1) {
            xDiff++;
        }

        if(y == 0){
            yDiff--;
        } else if(y == height-1){
            yDiff++;
        }

        if (z == 0) {
            zDiff--;
        } else if (z == depth - 1) {
            zDiff++;
        }

        if (xDiff == 0 && yDiff == 0 && zDiff == 0) {
            return;
        }

        updateNeighborChunks();
    }

    private void modifyBlock(int x, int y, int z, int block) {
        chunkData.getChangedVoxels();
    }

    public void setFast(int x, int y, int z, Block block) {
        chunkData.getVoxels()[x + z * width + y * widthTimesHeight] = (byte) block.getId();
        dirty = true;
    }

    public void setFast(int x, int y, int z, byte id) {
        chunkData.getVoxels()[x + z * width + y * widthTimesHeight] = id;
        dirty = true;
    }

    public void setFast(int x, int y, int z, int id) {
        chunkData.getVoxels()[x + z * width + y * widthTimesHeight] = (byte) id;
        dirty = true;
    }

    /**
     * Marks all neighbor chunks as dirty
     */
    public void updateNeighborChunks() {
        Array<Vector3> neighbors = new Array<>();

        neighbors.add(new Vector3(0, 1, 0));
        neighbors.add(new Vector3(0, -1, 0));
        neighbors.add(new Vector3(1, 0, 0));
        neighbors.add(new Vector3(-1, 0, 0));
        neighbors.add(new Vector3(0, 0, 1));
        neighbors.add(new Vector3(0, 0, -1));

        neighbors.forEach(pos -> {
            Chunk neighbor = World.INSTANCE.getChunk(pos.add(chunkData.getChunkPosition()), false, false);

            if (neighbor != null) {
                neighbor.dirty = true;
            }
        });
    }

    /**
     * Updates face masks in the chunk
     */
    public void update() {
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++, i++) {
                    byte voxel = chunkData.getVoxels()[i];

                    Block block = BlockManager.getById(voxel);

                    if (block == null || block.equals(Block.AIR)) {
                        continue;
                    }

                    faceMasks[i] = block.calculateFaceMasks(this, x, y, z);
                }
            }
        }
    }

    /** Creates a mesh out of the chunk, returning the number of indices produced
     * @return the number of vertices produced */
    public int calculateVertices(float[] vertices, boolean transparent) {
        int i = 0;
        int vertexOffset = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++, i++) {
                    byte voxel = chunkData.getVoxels()[i];
                    byte faceMask = faceMasks[i];

                    Block block = BlockManager.getById(voxel);

                    if (block == null || block.equals(Block.AIR) || block.isTransparent() != transparent) {
                        continue;
                    }

                    vertexOffset = block.render(vertices, vertexOffset, this, x, y, z, faceMask);
                }
            }
        }

        return vertexOffset;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

    @Override
    public void dispose() {
        if (mesh != null) {
            MESH_POOL.flush(mesh);
        }

        if (meshTransparent != null) {
            MESH_POOL.flush(meshTransparent);
        }

        generated = false;
    }

    public boolean isVisible(Vector3 chunkPosition) {
        return Math.abs(Constants.getChunkPosition(position).dst(chunkPosition)) <= RENDER_DISTANCE;
    }

    public Block getBlock(int x, int y, int z) {
        if (x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth) {
            return BlockManager.getById(chunkData.getVoxels()[x + z * width + y * widthTimesHeight]);
        }

        return World.INSTANCE.get(position.cpy().add(x, y, z), false, false);
    }

    public void rerender() {
        generated = false;
        dirty = true;
        setLoaded(false);
    }

    public void render(Array<Renderable> renderables, Pool<Renderable> pool, boolean transparent) {
        if (!generated) {
            generateMesh();
        }

        if (dirty) {
            generate();
            return;
        }

        Renderable renderable;

        if (!transparent) {
            if (vertAmount <= 0) {
                return;
            }

            renderable = pool.obtain();
            renderable.material = MATERIAL;
            renderable.meshPart.mesh = mesh;
            renderable.meshPart.offset = 0;
            renderable.meshPart.size = vertAmount;
            renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
            renderables.add(renderable);
            World.RENDERED_CHUNKS++;
            return;
        }

        if(vertAmountTransparent <= 0){
            return;
        }

        renderable = pool.obtain();
        renderable.material = MATERIAL;
        renderable.meshPart.mesh = meshTransparent;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = vertAmountTransparent;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderables.add(renderable);
    }

    private synchronized void generate() {
        if (!isLoaded()) {
            return;
        }

        update();

        int numVerts = calculateVertices(VERTICES, false);
        mesh.setVertices(VERTICES, 0, numVerts);
        this.vertAmount = numVerts / 4;

        numVerts = calculateVertices(VERTICES, true);
        meshTransparent.setVertices(VERTICES, 0, numVerts);
        this.vertAmountTransparent = numVerts / 4;

        dirty = false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getChunkPosition() {
        return chunkData.getChunkPosition();
    }

    public void setBiome(int x, int z, int biome) {
        this.chunkData.getBiomes()[x + z * depth] = biome;
    }

    public int getBiome(int x, int z) {
        return this.chunkData.getBiomes()[x + z * depth];
    }

    public void setHeight(int x, int z, float height) {
        this.chunkData.getHeights()[x + z * depth] = height;
    }

    public float getHeight(int x, int z) {
        return this.chunkData.getHeights()[x + z * depth];
    }

    public void setTemp(int x, int z, float temp) {
        this.chunkData.getTemp()[x + z * depth] = temp;
    }

    public float getTemp(int x, int z) {
        return this.chunkData.getTemp()[x + z * depth];
    }

    public void setHumidity(int x, int z, float humidity) {
        this.chunkData.getHumidity()[x + z * depth] = humidity;
    }

    public float getHumidity(int x, int z) {
        return this.chunkData.getHumidity()[x + z * depth];
    }

    public boolean isLoaded() {
        return chunkData.isLoaded();
    }

    public void setLoaded(boolean loaded) {
        this.chunkData.setLoaded(loaded);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Chunk chunk = (Chunk) o;

        return chunk.getChunkPosition().hashCode() == this.getChunkPosition().hashCode();
    }

    @Override
    public int hashCode() {
        return getChunkPosition().hashCode();
    }
}