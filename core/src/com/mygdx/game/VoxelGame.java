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
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.utils.PerlinNoiseGenerator;
import com.mygdx.game.voxel.VoxelWorld;

public class VoxelGame extends ApplicationAdapter {
	SpriteBatch spriteBatch;
	BitmapFont font;
	ModelBatch modelBatch;
	PerspectiveCamera camera;
	Environment lights;
	FirstPersonCameraController controller;
	VoxelWorld voxelWorld;

	public static Material MATERIAL;

	@Override
	public void create () {
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		modelBatch = new ModelBatch();
		DefaultShader.defaultCullFace = GL20.GL_FRONT;
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.near = 0.5f;
		camera.far = 1000;
		controller = new FirstPersonCameraController(camera);
		Gdx.input.setInputProcessor(controller);

		lights = new Environment();
		lights.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
		lights.add(new DirectionalLight().set(1, 1, 1, 0, -1, 0));

		Texture texture = new Texture("tiles.png");

		MATERIAL = new Material(TextureAttribute.createDiffuse(texture),
				IntAttribute.createCullFace(GL20.GL_FRONT),
				new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA),
				FloatAttribute.createAlphaTest(0.5f));

		TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);

		MathUtils.random.setSeed(0);
		voxelWorld = new VoxelWorld(tiles[0], 20, 4, 20);
		PerlinNoiseGenerator.generateVoxels(voxelWorld, 0, 63, 10);
		float camX = voxelWorld.voxelsX / 2f;
		float camZ = voxelWorld.voxelsZ / 2f;
		float camY = voxelWorld.getHighest(camX, camZ) + 1.5f;
		camera.position.set(camX, camY, camZ);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(camera);
		modelBatch.render(voxelWorld, lights);
		modelBatch.end();
		controller.update();

		spriteBatch.begin();
		font.draw(spriteBatch, "fps: " + Gdx.graphics.getFramesPerSecond() + ", #visible chunks: " + voxelWorld.renderedChunks
				+ "/" + voxelWorld.numChunks, 0, 20);
		spriteBatch.end();
	}
	
	@Override
	public void dispose () {
		spriteBatch.dispose();
		font.dispose();
		modelBatch.dispose();
	}
}
