package com.mygdx.game.block;

public enum BlockType {
    AIR(0, -1, -1, -1, false),
    GRASS(1, 0, 3, 2, true),
    DIRT(2, 2, 2, 2, true),
    STONE(3, 6, 6, 6, true),
    COBBLESTONE(4, 1, 1, 1, true),
    OAK_WOOD(5, 22, 21, 22, true),
    OAK_LEAVES(6, 53, 53, 53, true),
    WATER(7, 205, 206, 206, false, 0.6f),
    ;

    private final boolean solid;

    private final int id, topTexture, sideTexture, bottomTexture;

    private final float alpha;

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid) {
        this(id, topTexture, sideTexture, bottomTexture, solid, 1f);
    }

    BlockType(int id, int topTexture, int sideTexture, int bottomTexture, boolean solid, float alpha) {
        this.solid = solid;
        this.id = id;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
        this.alpha = alpha;
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

    public static BlockType getById(int id){
        for (BlockType b : values()) {
            if(b.id == id){
                return b;
            }
        }

        return BlockType.AIR;
    }
}
