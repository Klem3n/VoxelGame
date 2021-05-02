package com.mygdx.game.ui.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.VoxelGame;
import com.mygdx.game.ui.ScreenType;

public class LoadScreen extends ScreenAdapter {
    private final VoxelGame game;
    private final AssetManager assets;
    private float duration;

    public LoadScreen(VoxelGame game) {
        this.game = game;
        this.assets = game.getAssets();
    }

    @Override
    public void render(float delta) {
        duration += delta;

        assets.update();
        if (assets.isFinished()) {
            //TODO code main menu
            game.gotoScreen(ScreenType.GAME);
        }
    }
}
