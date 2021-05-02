package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.collision.ray.RayHit;

public class WorldBlock {
    private Vector3 position;
    private BlockType blockType;
    private RayHit rayHit;

    public WorldBlock(Vector3 position, BlockType blockType, RayHit rayHit) {
        this.position = position;
        this.blockType = blockType;
        this.rayHit = rayHit;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
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
                ", blockType=" + blockType +
                ", rayHit=" + rayHit +
                '}';
    }
}
