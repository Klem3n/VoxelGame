package com.mygdx.game.assets;

import com.badlogic.gdx.graphics.Texture;

public class AssetDescriptors {
    public static final com.badlogic.gdx.assets.AssetDescriptor<Texture> TILES =
            new com.badlogic.gdx.assets.AssetDescriptor<>(AssetPaths.TILES, Texture.class);

    public static final com.badlogic.gdx.assets.AssetDescriptor<Texture> CROSSHAIR =
            new com.badlogic.gdx.assets.AssetDescriptor<>(AssetPaths.CROSSHAIR, Texture.class);
}
