package com.mygdx.game;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.collision.CollisionRay;
import com.mygdx.game.world.World;
import com.mygdx.game.world.entity.Entity;

/**
 * Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 *
 * @author badlogic
 */
public class Player extends Entity {
    private final Camera camera;

    private final CollisionRay blockDetectionRay;
    private WorldBlock selectedBlock;

    public Player(Camera camera, Vector3 position) {
        super(position, new Vector3(0.6f, 1.6f, 0.6f));
        this.camera = camera;
        blockDetectionRay = new CollisionRay(this.camera.position, this.camera.direction, 3);
        this.camera.position.set(this.position).add(0f, 1.5f, 0f);
    }

    @Override
    public void update() {
        super.update();

        selectedBlock = blockDetectionRay.trace();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        camera.position.set(position.cpy().add(0.3f, 1.5f, 0.3f));
        camera.update(true);
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        /*
         Block destroy
         */
        if (button == 0) {
            if (selectedBlock != null && selectedBlock.getPosition() != null && selectedBlock.getBlockType() != BlockType.AIR && selectedBlock.getBlockType() != BlockType.WATER) {
                getWorld().set(selectedBlock.getPosition(), BlockType.AIR);
            }
        }

        return false;
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }

    public WorldBlock getSelectedBlock() {
        return selectedBlock;
    }

    public World getWorld() {
        return World.INSTANCE;
    }

    public Vector3 getVelocity() {
        return velocity;
    }
}
