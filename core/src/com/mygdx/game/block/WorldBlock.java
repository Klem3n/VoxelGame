package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.collision.ray.RayHit;

public class WorldBlock {
    private Vector3 position;
    private Block block;
    private RayHit rayHit;

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
