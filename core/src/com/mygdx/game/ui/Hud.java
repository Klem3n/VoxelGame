package com.mygdx.game.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.ui.actor.HotbarActor;
import com.mygdx.game.world.World;
import com.mygdx.game.world.entity.player.Player;

/**
 * Handles the players HUD
 */
public class Hud implements Disposable {
    /**
     * The asset manager
     */
    private final AssetManager assets;
    /**
     * The stage
     */
    private final Stage stage;
    /**
     * HUD viewport
     */
    private final Viewport viewport;
    /**
     * Image of the crosshair
     */
    private Image crosshair;
    /**
     * Inventory hotbar actor component
     */
    private HotbarActor hotbarActor;

    /**
     * Creates a new {@link Hud} object
     *
     * @param assets The asset manager
     * @param sb     Sprite batch component
     */
    public Hud(AssetManager assets, SpriteBatch sb) {
        this.assets = assets;

        crosshair = new Image(assets.get(AssetDescriptors.CROSSHAIR));

        viewport = new ScreenViewport(new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.setFillParent(true);

        table.add(crosshair).expand();

        hotbarActor = new HotbarActor(assets);

        stage.addActor(table);
        stage.addActor(hotbarActor);
    }

    /**
     * Updates the HUD every frame
     *
     * @param dt time passed since the last frame (in seconds)
     */
    public void update(float dt) {
        Player player = World.INSTANCE.getPlayer();

        if (player.getInventory().isDirty()) {
            hotbarActor.update(player.getInventory());

            player.getInventory().setDirty(false);
        }
    }

    /**
     * Resizes the HUD when window size is changed
     *
     * @param width  The window width
     * @param height The window height
     */
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public Stage getStage() {
        return stage;
    }
}
