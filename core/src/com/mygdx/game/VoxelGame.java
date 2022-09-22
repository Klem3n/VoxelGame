package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.block.BlockManager;
import com.mygdx.game.ui.ScreenType;
import com.mygdx.game.ui.screen.GameScreen;
import com.mygdx.game.ui.screen.LoadScreen;
import com.mygdx.game.world.biome.BiomeManager;
import com.mygdx.game.world.tree.TreeManager;

public class VoxelGame extends Game {
	private final AssetManager assets = new AssetManager();
	private SpriteBatch batch;

	public static boolean DEBUG = false;

	@Override
	public void create() {
		BlockManager.init();
		BiomeManager.init();
		TreeManager.init();

		assets.load(AssetDescriptors.TILES);
		assets.load(AssetDescriptors.CROSSHAIR);
		assets.load(AssetDescriptors.HOTBAR);
		assets.load(AssetDescriptors.HOTBAR_SELECTED);

		batch = new SpriteBatch();

		gotoScreen(ScreenType.LOAD_SCREEN);
	}

	@Override
	public void dispose() {
		assets.dispose();
		batch.dispose();

		super.dispose();
	}

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

	public SpriteBatch getBatch() {
		return batch;
	}
}
