package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.collision.ray.RayHit;

/**
 * Wrold block is a {@link Block} object currently located in the world
 */
public class WorldBlock {
    /**
     * The world position of the block
     */
    private Vector3 position;
    /**
     * The block
     */
    private Block block;
    /**
     * The ray cast that the block interacted with
     */
    private RayHit rayHit;

    /**
     * creates a new {@link WorldBlock} object
     */
    public WorldBlock(Vector3 position, Block block, RayHit rayHit) {
        this.position = position;
        this.block = block;
        this.rayHit = rayHit;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public RayHit getRayHit() {
        return rayHit;
    }

    public void setRayHit(RayHit rayHit) {
        this.rayHit = rayHit;
    }

    @Override
    public String toString() {
        return "WorldBlock{" +
                "position=" + position +
                ", blockType=" + block +
                ", rayHit=" + rayHit +
                '}';
    }
}
