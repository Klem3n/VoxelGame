package com.mygdx.game.collision.ray;

import com.badlogic.gdx.math.Vector3;

/**
 * Ray hit object that returns the hit direction of ray cast
 */
public class RayHit {
    /**
     * The ray that was cast
     */
    private final CollisionRay ray;
    /**
     * If anything was hit
     */
    private final boolean hit;
    /**
     * Minimum hit distance
     */
    private final float minDistance;
    /**
     * Maximum hit distance
     */
    private final float maxDistance;
    /**
     * The hit direction of the ray
     */
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
