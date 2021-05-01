package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;

public class Bounds {
    /**
     * The position offset of bounds
     */
    private final Vector3 offset;
    private final Vector3 dimensions;

    public Bounds(float width, float height, float depth) {
        this(Vector3.Zero, new Vector3(width, height, depth));
    }

    public Bounds(Vector3 dimensions) {
        this(Vector3.Zero, dimensions);
    }

    public Bounds(float offsetX, float offsetY, float offsetZ, float width, float height, float depth) {
        this(new Vector3(offsetX, offsetY, offsetZ), new Vector3(width, height, depth));
    }

    public Bounds(Vector3 offset, float width, float height, float depth) {
        this(offset, new Vector3(width, height, depth));
    }

    public Bounds(Vector3 offset, Vector3 dimensions) {
        this.dimensions = dimensions;
        this.offset = offset;
    }

    public float getWidth() {
        return dimensions.x;
    }

    public float getHeight() {
        return dimensions.y;
    }

    public float getDepth() {
        return dimensions.z;
    }

    public float getOffsetX(){
        return offset.x;
    }

    public float getOffsetY(){
        return offset.y;
    }

    public float getOffsetZ(){
        return offset.z;
    }

    public Vector3 getOffset(){
        return offset;
    }

    public Vector3 getDimensions() {
        return dimensions;
    }
}
