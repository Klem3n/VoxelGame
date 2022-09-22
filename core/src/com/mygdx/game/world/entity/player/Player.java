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
 * Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 *
 * @author badlogic
 */
public class Player extends Entity {
    private final Camera camera;

    private final CollisionRay blockDetectionRay;
    private WorldBlock selectedBlock;

    private Inventory inventory;

    public Player(Camera camera, Vector3 position) {
        super(position, new Vector3(0.6f, 1.6f, 0.6f));
        this.camera = camera;
        blockDetectionRay = new CollisionRay(this.camera.position, this.camera.direction, 4f);
        this.camera.position.set(this.position).add(0f, 1.5f, 0f);

        inventory = new Inventory();
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
