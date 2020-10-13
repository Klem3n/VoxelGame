package com.mygdx.game.block;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.world.Chunk;
import com.mygdx.game.world.World;

public abstract class BlockRenderer {
    public abstract int render(BlockType type, float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z);

    public TextureRegion getTopTexture(BlockType blockType){
        TextureRegion[][] tiles = World.TEXTURE_TILES;

        return tiles[blockType.getTopTexture()/tiles.length][blockType.getTopTexture()%tiles[0].length];
    }

    public TextureRegion getBottomTexture(BlockType blockType){
        TextureRegion[][] tiles = World.TEXTURE_TILES;

        return tiles[blockType.getBottomTexture()/tiles.length][blockType.getBottomTexture()%tiles[0].length];
    }

    public TextureRegion getSideTexture(BlockType blockType){
        TextureRegion[][] tiles = World.TEXTURE_TILES;

        return tiles[blockType.getBottomTexture()/tiles.length][blockType.getSideTexture()%tiles[0].length];
    }
}
