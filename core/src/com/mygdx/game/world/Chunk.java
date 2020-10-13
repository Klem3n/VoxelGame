package com.mygdx.game.world;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.block.BlockType;

import static com.mygdx.game.utils.Constants.*;

public class Chunk implements Disposable {
    public final byte[] voxels;
    public final int width;
    public final int height;
    public final int depth;
    public final Vector3 offset = new Vector3();
    public final Vector3 chunkPosition = new Vector3();
    private final int widthTimesHeight;
    private final int topOffset;
    private final int bottomOffset;
    private final int leftOffset;
    private final int rightOffset;
    private final int frontOffset;
    private final int backOffset;

    private int numVertices;
    private Mesh mesh;
    private boolean dirty;

    private boolean generated;

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
            * depth * 36 / 3,
            VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.ColorUnpacked());

        int len = width * height * depth * 6 * 6 / 3;
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
        this.numVertices = 0;
    }

    public void generateHeightMap(){
        for(int x = 0; x < width; x++){
            for(int z = 0; z < depth; z++){
                int heightMap = (int) (fastNoiseLite.GetNoise(offset.x + x, offset.z + z) * 32);

                for(int y = 0; y < height; y++){
                    float actualHeight = offset.y + y;

                    if(actualHeight > heightMap){
                        if(actualHeight < 0){
                            setFast(x, y, z, BlockType.WATER);
                        }
                    } else {
                        if(heightMap - actualHeight == 0){
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
    public int calculateVertices(float[] vertices, TextureRegion[][] tiles) {
        int i = 0;
        int vertexOffset = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++, i++) {
                    byte voxel = voxels[i];

                    BlockType blockType = BlockType.getById(voxel);

                    if (blockType == null || blockType.equals(BlockType.AIR)) continue;

                    TextureRegion topTexture = tiles[blockType.getTopTexture()/tiles.length][blockType.getTopTexture()%tiles[0].length];
                    TextureRegion bottomTexture = tiles[blockType.getBottomTexture()/tiles.length][blockType.getBottomTexture()%tiles[0].length];
                    TextureRegion sideTexture = tiles[blockType.getBottomTexture()/tiles.length][blockType.getSideTexture()%tiles[0].length];

                    if(renderTop(i, x, z, y)){
                        vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, topTexture, blockType);
                    }
                    if(renderBottom(i, x, z, y)){
                        vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, bottomTexture, blockType);
                    }
                    if(renderLeft(i, x, z, y)){
                        vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, sideTexture, blockType);
                    }
                    if(renderRight(i, x, z, y)){
                        vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, sideTexture, blockType);
                    }
                    if(renderFront(i, x, z, y)){
                        vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, sideTexture, blockType);
                    }
                    if(renderBack(i, x, z, y)){
                        vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, sideTexture, blockType);
                    }
                }
            }
        }

        return vertexOffset / VERTEX_SIZE;
    }

    private boolean renderTop(int index, int x, int z, int y) {
        BlockType render = BlockType.getById(voxels[index]);
        BlockType blockType;

        if(y == height-1) {
            blockType = World.INSTANCE.get(offset.x + x, offset.y + y + 1, offset.z + z);
        } else {
            blockType = BlockType.getById(voxels[index + topOffset]);
        }

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderBottom(int index, int x, int z, int y) {
        BlockType render = BlockType.getById(voxels[index]);
        BlockType blockType;

        if(y == 0) {
            blockType = World.INSTANCE.get(offset.x + x, offset.y + y - 1, offset.z + z);
        } else {
            blockType = BlockType.getById(voxels[index + bottomOffset]);
        }

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderLeft(int index, int x, int z, int y) {
        BlockType render = BlockType.getById(voxels[index]);
        BlockType blockType;

        if(x == 0) {
            blockType = World.INSTANCE.get(offset.x + x - 1, offset.y + y, offset.z + z);
        } else {
            blockType = BlockType.getById(voxels[index + leftOffset]);
        }

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderRight(int index, int x, int z, int y) {
        BlockType render = BlockType.getById(voxels[index]);
        BlockType blockType;

        if(x == width-1) {
            blockType = World.INSTANCE.get(offset.x + x + 1, offset.y + y, offset.z + z);
        } else {
            blockType = BlockType.getById(voxels[index + rightOffset]);
        }

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderFront(int index, int x, int z, int y) {
        BlockType render = BlockType.getById(voxels[index]);
        BlockType blockType;

        if(z == 0) {
            blockType = World.INSTANCE.get(offset.x + x, offset.y + y, offset.z + z - 1);
        } else {
            blockType = BlockType.getById(voxels[index + frontOffset]);
        }

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderBack(int index, int x, int z, int y) {
        BlockType render = BlockType.getById(voxels[index]);
        BlockType blockType;

        if(z == depth-1) {
            blockType = World.INSTANCE.get(offset.x + x, offset.y + y, offset.z + z + 1);
        } else {
            blockType = BlockType.getById(voxels[index + backOffset]);
        }

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    public static int createTop (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public static int createBottom (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public static int createLeft (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public static int createRight (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public static int createFront (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public static int createBack (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

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

    public int getNumVertices() {
        return numVertices;
    }

    public void setNumVertices(int numVertices) {
        this.numVertices = numVertices;
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
}