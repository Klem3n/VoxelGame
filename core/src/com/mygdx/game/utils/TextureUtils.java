package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.VoxelGame;

public class TextureUtils {
    public static TextureRegion getBlockTexture(int texture) {
        TextureRegion[][] tiles = Constants.TEXTURE_TILES;

        if (VoxelGame.DEBUG) {
            return tiles[15][10];
        }

        return tiles[texture / tiles.length][texture % tiles[0].length];
    }
}
