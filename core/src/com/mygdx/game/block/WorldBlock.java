package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;

public class WorldBlock {
    Vector3 position;
    Block block;

    public WorldBlock(Vector3 position, Block block) {
        this.position = position;
        this.block = block;
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

    @Override
    public String toString() {
        return "WorldBlock{" +
                "position= (" + (int)position.x + ", " + (int)position.y + ", " +(int)position.z +
                "), block=" + block +
                '}';
    }
}
