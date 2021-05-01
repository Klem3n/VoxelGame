package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.game.controller.PlayerController;
import com.mygdx.game.utils.ThreadUtil;
import com.mygdx.game.world.World;
import com.mygdx.game.world.entity.Entity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mygdx.game.utils.Constants.floor;

public class VoxelGame extends ApplicationAdapter {
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private ModelBatch modelBatch;
	private PerspectiveCamera camera;
	private Environment environment;
	private PlayerController playerController;
	private World world;
	private ExtendViewport viewport;

	private Image crosshair;

	public static Material MATERIAL;

	public static boolean DEBUG = false;

	private Array<Entity> entities = new Array<>();

	/**
	 * The ExecutorService.
	 */
	public static final ExecutorService CHUNK_EXECUTOR = Executors.newFixedThreadPool(1, ThreadUtil.create("ClientSynchronizer"));

	@Override
	public void create() {
		crosshair = new Image(new Texture("crosshair.png"));
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		modelBatch = new ModelBatch();
		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		camera = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.0001f;
		camera.far = 100f;
		viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		viewport.apply();

		playerController = new PlayerController(camera, new Vector3(0.2f, 1.0f, 0.2f));
		Gdx.input.setInputProcessor(playerController);

		entities.add(playerController.getPlayer());

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		environment.set(new ColorAttribute(ColorAttribute.Fog, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));

		Texture texture = new Texture("tiles.png");

		MATERIAL = new Material(TextureAttribute.createDiffuse(texture),
				IntAttribute.createCullFace(GL20.GL_FRONT),
				new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
				FloatAttribute.createAlphaTest(0.5f));

		TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);

		world = new World(tiles, playerController);

		Gdx.input.setCursorCatched(true);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(camera);
		modelBatch.render(world, environment);
		modelBatch.end();
		playerController.update();

		//Update all entities after player controller

		entities.forEach(Entity::update);

		spriteBatch.begin();

		if (DEBUG) {
			font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + " Rendered chunks: " + World.RENDERED_CHUNKS +
					"            Position: " + floor(camera.position.x) + ", " + floor(camera.position.y) + ", " + floor(camera.position.z), 0, 20);

			font.draw(spriteBatch, "Memory usage: " + Gdx.app.getJavaHeap() / 1048576 + " MB", 0, 40);
		}

		crosshair.setPosition(Gdx.graphics.getWidth()/2f - crosshair.getWidth()/2, Gdx.graphics.getHeight()/2f - crosshair.getHeight()/2);
		crosshair.draw(spriteBatch, 1f);

		spriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		viewport.update(width, height);
	}

	@Override
	public void dispose () {
		spriteBatch.dispose();
		font.dispose();
		modelBatch.dispose();
		world.dispose();
	}
}
