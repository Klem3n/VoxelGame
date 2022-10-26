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
import com.mygdx.game.utils.SaveUtils;
import com.mygdx.game.world.World;
import com.mygdx.game.world.biome.BiomeManager;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.entity.Entity;

import static com.mygdx.game.utils.Constants.*;

public class GameScreen extends ScreenAdapter {
    /**
     * The game object reference
     */
    private final VoxelGame game;
    /**
     * The asset manager
     */
    private final AssetManager assets;

    /**
     * Batch of sprites used for rendering the HUD
     */
    private SpriteBatch spriteBatch;
    /**
     * Font used to render the text on the screen
     */
    private BitmapFont font;
    /**
     * Batch of models to be rendered (chunk meshes)
     */
    private ModelBatch modelBatch;
    /**
     * Player camera reference
     */
    private PerspectiveCamera camera;
    /**
     * Enviorment variable used to create realistic lightning in the game
     */
    private Environment environment;
    /**
     * Player controller reference
     */
    private PlayerController playerController;
    /**
     * World reference
     */
    private World world;
    /**
     * Player HUD reference
     */
    private Hud hud;

    /**
     * Array of all active entities in the world
     */
    private final Array<Entity> entities = new Array<>();

    /**
     * Creates a new {@link GameScreen} object, that is responsible for rendering the game world
     *
     * @param game The game reference itself
     */
    public GameScreen(VoxelGame game) {
        this.game = game;
        this.assets = game.getAssets();
        this.spriteBatch = game.getBatch();

        create();
    }

    /**
     * Creates and initializes all the needed variables
     * <p>
     * Creates the player, loads the world, sets the environment and instantiates the material
     */
    public void create() {
        Texture texture = assets.get(AssetDescriptors.TILES);

        hud = new Hud(assets, spriteBatch);

        font = new BitmapFont();
        modelBatch = new ModelBatch();
        DefaultShader.defaultCullFace = GL20.GL_FRONT;
        camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;

        int worldSeed = 1234;
        Vector3 playerPosition = new Vector3(.3f, 80.0f, .3f);

        if (SaveUtils.PLAYER_POSITION != null) {
            worldSeed = SaveUtils.WORLD_SEED;
            playerPosition.set(SaveUtils.PLAYER_POSITION);
        }

        playerController = new PlayerController(camera, playerPosition);
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

        world = new World(playerController, worldSeed);

        Gdx.input.setCursorCatched(true);
    }

    /**
     * Updates the world
     *
     * @param deltaTime The time (in seconds) passed since the last frame render
     */
    private void update(float deltaTime) {
        if (!World.INSTANCE.loaded()) {
            return;
        }

        playerController.update();

        //Update all entities after player controller
        entities.forEach(Entity::update);

        hud.update(deltaTime);
    }

    /**
     * Renders the world and the HUD
     *
     * @param deltaTime The time (in seconds) passed since the last frame render
     */
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

                font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 40);

                font.draw(spriteBatch, "Biome: " + BiomeManager.getById(biome).getName(), 10, 400);
                font.draw(spriteBatch, "Temp: " + temp, 10, 385);
                font.draw(spriteBatch, "Hum: " + hum, 10, 370);

                font.draw(spriteBatch, "Position: " + playerController.getPosition(), 10, 415);
            }
        }

        spriteBatch.end();
    }

    /**
     * Resizes the game screen window
     *
     * @param width  The width
     * @param height The heigt
     */
    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        hud.resize(width, height);
    }

    /**
     * Disposes of the game screen and the world
     */
    @Override
    public void dispose() {
        font.dispose();
        modelBatch.dispose();
        world.dispose();
    }
}
