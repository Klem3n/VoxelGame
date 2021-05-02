package com.mygdx.game.collision.ray;

import com.badlogic.gdx.math.Vector3;

public class RayHit {
    private final CollisionRay ray;
    private final boolean hit;
    private final float minDistance;
    private final float maxDistance;
    private final Vector3 hitDirection;

    public RayHit(CollisionRay ray, boolean hit) {
        this.ray = ray;
        this.hit = hit;
        this.minDistance = 0;
        this.maxDistance = 0;
        this.hitDirection = Vector3.Zero;
    }

    public RayHit(CollisionRay ray, boolean hit, float minDistance, float maxDistance, Vector3 hitDirection) {
        this.ray = ray;
        this.hit = hit;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.hitDirection = hitDirection;
    }

    public CollisionRay getRay() {
        return ray;
    }

    public boolean isHit() {
        return hit;
    }

    public float getMinDistance() {
        return minDistance;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public Vector3 getHitDirection() {
        return hitDirection;
    }
}
