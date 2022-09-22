package com.mygdx.game.ui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.VoxelGame;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.controller.PlayerController;
import com.mygdx.game.ui.Hud;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.world.World;
import com.mygdx.game.world.biome.BiomeManager;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.entity.Entity;

import static com.mygdx.game.utils.Constants.*;

public class GameScreen extends ScreenAdapter {
    private final VoxelGame game;
    private final AssetManager assets;

    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private ModelBatch modelBatch;
    private PerspectiveCamera camera;
    private Environment environment;
    private PlayerController playerController;
    private World world;
    private Hud hud;

    private Array<Entity> entities = new Array<>();

    public GameScreen(VoxelGame game) {
        this.game = game;
        this.assets = game.getAssets();
        this.spriteBatch = game.getBatch();

        create();
    }

    public void create() {
        Texture texture = assets.get(AssetDescriptors.TILES);

        hud = new Hud(assets, spriteBatch);

        font = new BitmapFont();
        modelBatch = new ModelBatch();
        DefaultShader.defaultCullFace = GL20.GL_FRONT;
        camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;

        playerController = new PlayerController(camera, new Vector3(.3f, 100.0f, .3f));
        Gdx.input.setInputProcessor(playerController);

        entities.add(playerController.getPlayer());

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));

        Material material = new Material(TextureAttribute.createDiffuse(texture),
                IntAttribute.createCullFace(GL20.GL_FRONT),
                new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
                FloatAttribute.createAlphaTest(0.5f));

        TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);

        Constants.MATERIAL = material;
        Constants.TEXTURE_TILES = tiles;

        world = new World(playerController);

        Gdx.input.setCursorCatched(true);
    }

    private void update(float deltaTime) {
        playerController.update();

        //Update all entities after player controller
        entities.forEach(Entity::update);

        hud.update(deltaTime);
    }

    @Override
    public void render(float deltaTime) {
        update(deltaTime);

        //Gdx.gl.glDisable(GL20.GL_BLEND);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_FRONT_AND_BACK);

        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(camera);
        modelBatch.render(world, environment);
        modelBatch.end();

        spriteBatch.enableBlending();
        spriteBatch.setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();

        spriteBatch.begin();

        if (World.INSTANCE != null && World.INSTANCE.getPlayer() != null) {
            Chunk chunk = World.INSTANCE.getChunk(getChunkPosition(World.INSTANCE.getPlayer().getPosition()), false, false);

            if (chunk != null) {
                int x = Math.floorMod((int) World.INSTANCE.getPlayer().getPosition().x, CHUNK_SIZE_X);
                int z = Math.floorMod((int) World.INSTANCE.getPlayer().getPosition().z, CHUNK_SIZE_Z);

                int biome = chunk.getBiome(x, z);
                float temp = chunk.getTemp(x, z);
                float hum = chunk.getHumidity(x, z);

                font.draw(spriteBatch, "Biome: " + BiomeManager.getById(biome).getName(), 10, 400);
                font.draw(spriteBatch, "Temp: " + temp, 10, 385);
                font.draw(spriteBatch, "Hum: " + hum, 10, 370);
            }
        }

        spriteBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        font.dispose();
        modelBatch.dispose();
        world.dispose();
    }
}
