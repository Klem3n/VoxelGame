package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.ui.ScreenType;
import com.mygdx.game.ui.screen.GameScreen;
import com.mygdx.game.ui.screen.LoadScreen;

public class VoxelGame extends Game {
	private final AssetManager assets = new AssetManager();

	public static boolean DEBUG = false;

	public void gotoScreen(ScreenType screenType) {
		switch (screenType) {
			case LOAD_SCREEN:
				super.setScreen(new LoadScreen(this));
				break;
			case MAIN_MENU:
				break;
			case GAME:
				super.setScreen(new GameScreen(this));
				break;
		}
		System.gc();
	}

	public AssetManager getAssets() {
		return assets;
	}

	@Override
	public void create() {
		assets.load(AssetDescriptors.TILES);
		assets.load(AssetDescriptors.CROSSHAIR);

		gotoScreen(ScreenType.LOAD_SCREEN);
	}
}
