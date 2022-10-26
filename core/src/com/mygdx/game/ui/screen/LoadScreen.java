package com.mygdx.game.ui.screen;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.VoxelGame;
import com.mygdx.game.ui.ScreenType;

/**
 * Screen that handles the loading of the game
 */
public class LoadScreen extends ScreenAdapter {
    /**
     * The game reference
     */
    private final VoxelGame game;
    /**
     * Asset manager
     */
    private final AssetManager assets;
    /**
     * Loading duration
     */
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
