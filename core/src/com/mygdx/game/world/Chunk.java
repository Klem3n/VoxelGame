package com.mygdx.game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.VoxelGame;
import com.mygdx.game.block.BlockType;

import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.utils.Constants.*;

public class Chunk implements Disposable, RenderableProvider {
    public final byte[] voxels;
    public final int width;
    public final int height;
    public final int depth;
    public final Vector3 offset = new Vector3();
    public final Vector3 chunkPosition = new Vector3();
    private final int widthTimesHeight;
    public final int topOffset;
    public final int bottomOffset;
    public final int leftOffset;
    public final int rightOffset;
    public final int frontOffset;
    public final int backOffset;

    private Mesh mesh;
    private boolean dirty;

    private boolean generated;

    /**
     * Amount of vertices generated for this chunk
     */
    private int vertAmount = 0;

    public static float[] VERTICES = new float[VERTEX_SIZE*6*CHUNK_SIZE_X*CHUNK_SIZE_Y*CHUNK_SIZE_Z];

    public Chunk(Vector3 chunkPosition, float x, float y, float z) {
        this.width = CHUNK_SIZE_X;
        this.height = CHUNK_SIZE_Y;
        this.depth = CHUNK_SIZE_Z;

        this.voxels = new byte[width * height * depth];

        this.topOffset = width * depth;
        this.bottomOffset = -width * depth;
        this.leftOffset = -1;
        this.rightOffset = 1;
        this.frontOffset = -width;
        this.backOffset = width;
        this.widthTimesHeight = width * height;

        this.offset.set(x, y, z);
        this.chunkPosition.set(chunkPosition);

        generateMesh();
        generateHeightMap();
    }

    public void generateMesh(){
        this.mesh = new Mesh(true, width * height * depth * VERTEX_SIZE * 4, width * height
            * depth * 36,
            VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.ColorUnpacked());

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
    }

    public void generateHeightMap(){
        for(int x = 0; x < width; x++){
            for(int z = 0; z < depth; z++){
                int heightMap = (int) (fastNoiseLite.GetNoise(offset.x + x, offset.z + z) * 32);

                for(int y = 0; y < height; y++){
                    float actualHeight = offset.y + y;

                    if(actualHeight > heightMap){
                        if(actualHeight <=0){
                            setFast(x, y, z, BlockType.WATER);
                        }
                    } else {
                        if(heightMap - actualHeight == 0){
                            setFast(x, y, z, BlockType.GRASS);
                        } else if(heightMap - actualHeight == 1){
                            setFast(x, y, z, BlockType.GRASS);
                        } else if(heightMap - actualHeight < 6){
                            setFast(x, y, z, BlockType.DIRT);
                        } else {
                            setFast(x, y, z, BlockType.STONE);
                        }
                    }
                }
            }
        }

        this.generated = true;
        this.dirty = true;
    }

    public BlockType get (int x, int y, int z) {
        if (x < 0 || x >= width) return BlockType.AIR;
        if (y < 0 || y >= height) return BlockType.AIR;
        if (z < 0 || z >= depth) return BlockType.AIR;
        return getFast(x, y, z);
    }

    public BlockType getFast (int x, int y, int z) {
        return BlockType.getById(voxels[x + z * width + y * widthTimesHeight]);
    }

    public void set (int x, int y, int z, BlockType blockType) {
        if (x < 0 || x >= width) return;
        if (y < 0 || y >= height) return;
        if (z < 0 || z >= depth) return;
        setFast(x, y, z, blockType);

        float xDiff = 0;
        float yDiff = 0;
        float zDiff = 0;

        if(x == 0){
            xDiff--;
        } else if(x == width-1){
            xDiff++;
        }

        if(y == 0){
            yDiff--;
        } else if(y == height-1){
            yDiff++;
        }

        if(z == 0){
            zDiff--;
        } else if(z == depth-1){
            zDiff++;
        }

        if(xDiff == 0 && yDiff == 0 && zDiff == 0){
            return;
        }

        Array<Vector3> neighbors = new Array<>();

        neighbors.add(new Vector3(xDiff, yDiff, zDiff));
        neighbors.add(new Vector3(xDiff, yDiff, 0));
        neighbors.add(new Vector3(xDiff, 0, zDiff));
        neighbors.add(new Vector3(xDiff, 0, 0));
        neighbors.add(new Vector3(0, yDiff, zDiff));
        neighbors.add(new Vector3(0, yDiff, 0));
        neighbors.add(new Vector3(0, 0, zDiff));

        neighbors.forEach(pos -> {
            Chunk neighbor = World.INSTANCE.chunks.get(pos.add(chunkPosition));

            if(neighbor != null){
                neighbor.dirty = true;
            }
        });
    }

    public void setFast (int x, int y, int z, BlockType blockType) {
        voxels[x + z * width + y * widthTimesHeight] = (byte) blockType.getId();
        dirty = true;
    }

    /** Creates a mesh out of the chunk, returning the number of indices produced
     * @return the number of vertices produced */
    public int calculateVertices(float[] vertices) {
        int i = 0;
        int vertexOffset = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++, i++) {
                    byte voxel = voxels[i];

                    BlockType blockType = BlockType.getById(voxel);

                    if (blockType == null || blockType.equals(BlockType.AIR)) continue;

                    vertexOffset = blockType.render(vertices, vertexOffset, this, x, y, z);
                }
            }
        }

        return vertexOffset;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
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
        if(mesh != null) {
            mesh.dispose();
        }
    }

    public boolean isVisible(Vector3 chunkPosition) {
        return Math.abs(getChunkPosition(offset).dst(chunkPosition)) <= RENDER_DISTANCE;
    }

    public BlockType getBlock(int x, int y, int z) {
        if(x >= 0 && x < width && y >= 0 && y < height && z >= 0 && z < depth){
            return BlockType.getById(voxels[x + z * width + y * widthTimesHeight]);
        }

        return World.INSTANCE.get(offset.cpy().add(x, y, z));
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        if(mesh == null){
            return;
        }

        if (dirty) {
            render();
        }

        if (vertAmount <= 0) return;

        Renderable renderable = pool.obtain();
        renderable.material = VoxelGame.MATERIAL;
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = vertAmount;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderables.add(renderable);
        World.RENDERED_CHUNKS++;
    }

    public void render() {
        int numVerts = calculateVertices(VERTICES);
        mesh.setVertices(VERTICES, 0, numVerts);
        this.vertAmount = numVerts / 4;
        dirty = false;
    }
}