package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;

public class Box {
    private Vector3 position;

    private final float width, height, depth;

    public Box(Vector3 position, float width, float height, float depth) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Box setPosition(Vector3 position) {
        this.position = position;

        return this;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getDepth() {
        return depth;
    }

    public boolean intersects(final Box box) {
        final float min_x1 = position.x;
        final float min_x2 = box.position.x;

        final float min_y1 = position.y;
        final float min_y2 = box.position.y;

        final float min_z1 = position.z;
        final float min_z2 = box.position.z;

        final float max_x1 = position.x + width;
        final float max_x2 = box.position.x + box.width;

        final float max_y1 = position.y + height;
        final float max_y2 = box.position.y + box.height;

        final float max_z1 = position.z + depth;
        final float max_z2 = box.position.z + box.depth;

        return ((min_x1 <= min_x2 && min_x2 <= max_x1) || (min_x2 <= min_x1 && min_x1 <= max_x2)) &&
                ((min_y1 <= min_y2 && min_y2 <= max_y1) || (min_y2 <= min_y1 && min_y1 <= max_y2)) &&
                ((min_z1 <= min_z2 && min_z2 <= max_z1) || (min_z2 <= min_z1 && min_z1 <= max_z2));
    }
}
