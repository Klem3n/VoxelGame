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

public class Hud implements Disposable {
    private final AssetManager assets;
    private final Stage stage;
    private final Viewport viewport;

    private Image crosshair;

    private HotbarActor hotbarActor;

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

    public void update(float dt) {
        Player player = World.INSTANCE.getPlayer();

        if (player.getInventory().isDirty()) {
            hotbarActor.update(player.getInventory());

            player.getInventory().setDirty(false);
        }
    }

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
