package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;

public class WorldBlock {
    Vector3 position;
    BlockType blockType;

    public WorldBlock(Vector3 position, BlockType blockType) {
        this.position = position;
        this.blockType = blockType;
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

    @Override
    public String toString() {
        return "WorldBlock{" +
                "position= (" + (int)position.x + ", " + (int)position.y + ", " +(int)position.z +
                "), block=" + blockType +
                '}';
    }
}
