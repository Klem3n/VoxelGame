package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.impl.DefaultBlockRenderer;
import com.mygdx.game.block.impl.FolliageBlockRenderer;
import com.mygdx.game.world.Chunk;

import java.lang.reflect.InvocationTargetException;

public enum BlockType {
    AIR(0, -1, -1, -1, false, null),
    GRASS(1, 0, 3, 2, true, DefaultBlockRenderer.INSTANCE),
    DIRT(2, 2, 2, 2, true, DefaultBlockRenderer.INSTANCE),
    STONE(3, 6, 6, 6, true, DefaultBlockRenderer.INSTANCE),
    COBBLESTONE(4, 1, 1, 1, true, DefaultBlockRenderer.INSTANCE),
    OAK_WOOD(5, 22, 21, 22, true, DefaultBlockRenderer.INSTANCE),
    OAK_LEAVES(6, 53, 53, 53, true, DefaultBlockRenderer.INSTANCE),
    WATER(7, 205, 206, 206, false, 0.6f, 1f, 0.9f, 1f, DefaultBlockRenderer.INSTANCE),
    ROSE(8, 12, 12, 12, false, 0.5f, FolliageBlockRenderer.INSTANCE),
    DANDELION(9, 13, 13, 13, false, 0.5f, FolliageBlockRenderer.INSTANCE),
    ;

    private final boolean solid;

    private final int id, topTexture, sideTexture, bottomTexture;

    private final float alpha, sizeX, sizeY, sizeZ;

    private BlockRenderer blockRenderer;

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid, BlockRenderer blockRenderer) {
        this(id, topTexture, sideTexture, bottomTexture, solid, 1f, blockRenderer);
    }

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid, float size, BlockRenderer blockRenderer) {
        this(id, topTexture, sideTexture, bottomTexture, solid, 1f, size, size, size, blockRenderer);
    }

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid, float alpha, float sizeX, float sizeY, float sizeZ, BlockRenderer blockRenderer) {
        this.solid = solid;
        this.id = id;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
        this.alpha = alpha;
        this.blockRenderer = blockRenderer;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public int getId() {
        return id;
    }

    public int getTopTexture() {
        return topTexture;
    }

    public int getSideTexture() {
        return sideTexture;
    }

    public int getBottomTexture() {
        return bottomTexture;
    }

    public boolean isSolid() {
        return solid;
    }

    public float getAlpha() {
        return alpha;
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    public float getSizeZ() {
        return sizeZ;
    }

    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask){
        if(blockRenderer == null)
            return vertexOffset;

        return blockRenderer.render(this, verticies, vertexOffset, chunk, x, y, z, faceMask);
    }

    public static BlockType getById(int id){
        for (BlockType b : values()) {
            if(b.id == id){
                return b;
            }
        }

        return BlockType.AIR;
    }

    public boolean collides(Vector3 toVec) {
        if(blockRenderer == null)
            return false;

        return blockRenderer.collides(this, toVec.cpy());
    }

    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        if(blockRenderer == null)
            return 0;

        return blockRenderer.calculateFaceMasks(this, chunk, x, y, z);
    }
}
