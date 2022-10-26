package com.mygdx.game.world.entity.player;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.collision.ray.CollisionRay;
import com.mygdx.game.world.World;
import com.mygdx.game.world.entity.Entity;
import com.mygdx.game.world.entity.player.link.Inventory;

/**
 * An entity that takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 */
public class Player extends Entity {
    /**
     * Players camera component
     */
    private final Camera camera;

    /**
     * Collision ray used for detecting interacting blocks
     */
    private final CollisionRay blockDetectionRay;
    /**
     * Which block the player is currently looking at
     */
    private WorldBlock selectedBlock;
    /**
     * Players inventory component
     */
    private Inventory inventory;

    /**
     * Creates a new {@link Player} object
     *
     * @param camera   The players camera component
     * @param position The players position
     */
    public Player(Camera camera, Vector3 position) {
        super(position, new Vector3(0.6f, 1.6f, 0.6f));
        this.camera = camera;
        blockDetectionRay = new CollisionRay(this.camera.position, this.camera.direction, 4f);
        this.camera.position.set(this.position).add(0f, 1.5f, 0f);

        inventory = new Inventory();
    }

    /**
     * Updates the player entity every frame
     */
    @Override
    public void update() {
        super.update();

        selectedBlock = blockDetectionRay.trace();
    }

    /**
     * Updates the player entity every frame
     *
     * @param deltaTime The time (in seconds) passed since the last frame
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        camera.position.set(position.cpy().add(0.3f, 1.5f, 0.3f));
        camera.update(true);
    }

    /**
     * Executed when the mouse button is released
     * <p>
     * Handles the placing and removing of the blocks
     */
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        /*
         Block destroy and place
         */
        if (selectedBlock != null && selectedBlock.getPosition() != null && selectedBlock.getRayHit().isHit()) {
            if (button == 0) {
                Block block = getWorld().get(selectedBlock.getPosition(), false, false);

                if (block != null && block.getId() != BlockID.BEDROCK) {
                    getWorld().set(selectedBlock.getPosition(), Block.AIR, false, false);
                }
            } else if (button == 1) {
                Vector3 placePosition = selectedBlock.getPosition().cpy().add(selectedBlock.getRayHit().getHitDirection());

                Block type = getWorld().get(placePosition, false, false);

                Block toPlace = getInventory().getSelectedBlock();

                if ((type == null || !type.isSolid()) && toPlace != Block.AIR) {
                    getWorld().set(placePosition, toPlace, false, false);
                }
            }
        }

        return false;
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector3 position) {
        super.setPosition(position);

        this.camera.position.set(position).add(0.3f, 1.0f, 0.3f);
        this.camera.update();
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

    public Inventory getInventory() {
        return inventory;
    }
}
