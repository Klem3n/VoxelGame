package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.block.impl.DefaultBlockRenderer;
import com.mygdx.game.block.impl.FolliageBlockRenderer;
import com.mygdx.game.collision.Bounds;
import com.mygdx.game.world.Chunk;
import com.mygdx.game.world.entity.Entity;

public enum BlockType {
    AIR(0, -1, -1, -1, false, null),
    GRASS(1, 0, 3, 2, true, DefaultBlockRenderer.INSTANCE),
    DIRT(2, 2, 2, 2, true, DefaultBlockRenderer.INSTANCE),
    STONE(3, 6, 6, 6, true, DefaultBlockRenderer.INSTANCE),
    COBBLESTONE(4, 1, 1, 1, true, DefaultBlockRenderer.INSTANCE),
    OAK_WOOD(5, 22, 21, 22, true, DefaultBlockRenderer.INSTANCE),
    OAK_LEAVES(6, 53, 53, 53, true, DefaultBlockRenderer.INSTANCE),
    WATER(7, 205, 206, 206, false, 0.6f, 1f, 1f, 1f, DefaultBlockRenderer.INSTANCE),
    ROSE(8, 12, 12, 12, false, 0.5f, FolliageBlockRenderer.INSTANCE),
    DANDELION(9, 13, 13, 13, false, 0.5f, FolliageBlockRenderer.INSTANCE),
    ;

    private final boolean solid;

    private final int id, topTexture, sideTexture, bottomTexture;

    private final float alpha, sizeX, sizeY, sizeZ, offsetX, offsetY, offsetZ;

    private final Array<Bounds> bounds = new Array<>();

    private BlockRenderer blockRenderer;

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid, BlockRenderer blockRenderer, Bounds... bounds) {
        this(id, topTexture, sideTexture, bottomTexture, solid, 1f, blockRenderer, bounds);
    }

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid, float size, BlockRenderer blockRenderer, Bounds... bounds) {
        this(id, topTexture, sideTexture, bottomTexture, solid, 1f, size, size, size, blockRenderer, bounds);
    }

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid, float alpha, float sizeX, float sizeY, float sizeZ, BlockRenderer blockRenderer, Bounds... bounds) {
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
        this.offsetX = 0;
        this.offsetY = 0;
        this.offsetZ = 0;

        if (bounds.length == 0) {
            createBox();
        } else {
            this.bounds.addAll(bounds);
        }
    }

    private void createBox() {
        Bounds box = new Bounds(offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ);

        this.bounds.add(box);
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

    public boolean isTransparent(){
        return alpha < 1f;
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

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask){
        if (blockRenderer == null) {
            return vertexOffset;
        }

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
        if (blockRenderer == null) {
            return false;
        }

        return blockRenderer.collides(this, toVec.cpy());
    }

    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        if (blockRenderer == null) {
            return 0;
        }

        return blockRenderer.calculateFaceMasks(this, chunk, x, y, z);
    }

    public boolean collides(Entity entity, Vector3 entityPosition, Vector3 blockPosition) {
        if (!solid) {
            return false;
        }

        final float min_x1 = entityPosition.x;
        final float min_y1 = entityPosition.y;
        final float min_z1 = entityPosition.z;

        final float max_x1 = entityPosition.x + entity.getBounds().getWidth();
        final float max_y1 = entityPosition.y + entity.getBounds().getHeight();
        final float max_z1 = entityPosition.z + entity.getBounds().getDepth();

        for (Bounds box : bounds) {
            final float min_x2 = blockPosition.x + box.getOffsetX();
            final float min_y2 = blockPosition.y + box.getOffsetY();
            final float min_z2 = blockPosition.z + box.getOffsetZ();

            final float max_x2 = blockPosition.x + box.getOffsetX() + box.getWidth();
            final float max_y2 = blockPosition.y + box.getOffsetY() + box.getHeight();
            final float max_z2 = blockPosition.z + box.getOffsetZ() + box.getDepth();

            if (((min_x1 <= min_x2 && min_x2 <= max_x1) || (min_x2 <= min_x1 && min_x1 <= max_x2)) &&
                    ((min_y1 <= min_y2 && min_y2 <= max_y1) || (min_y2 <= min_y1 && min_y1 <= max_y2)) &&
                    ((min_z1 <= min_z2 && min_z2 <= max_z1) || (min_z2 <= min_z1 && min_z1 <= max_z2))) {
                return true;
            }
        }

        return false;
    }
}
