package com.mygdx.game.block;

public enum Block {
    AIR(0, -1, -1, -1),
    GRASS(1, 0, 3, 2),
    DIRT(2, 2, 2, 2),
    STONE(3, 1, 1, 1),

    ;

    private final int id, topTexture, sideTexture, bottomTexture;

    Block(int id, int topTexture, int sideTexture, int bottomTexture) {
        this.id = id;
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
        this.bottomTexture = bottomTexture;
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

    public static Block getById(int id){
        for (Block b : values()) {
            if(b.id == id){
                return b;
            }
        }

        return null;
    }
}
