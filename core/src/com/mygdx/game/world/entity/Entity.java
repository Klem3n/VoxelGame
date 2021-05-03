package com.mygdx.game.world.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.collision.EntityBounds;

public class Entity {
    private static final float GRAVITY = 9.8f;
    private static final float MAX_FALLING_SPEED = -10f;

    protected final Vector3 position;
    private final EntityBounds bounds;

    /**
     * The velocity of the entity
     */
    protected final Vector3 velocity = new Vector3();

    /**
     * If entity is on ground
     */
    private boolean onGround = false;

    public Entity(Vector3 position, Vector3 dimensions) {
        this.position = position;
        this.bounds = new EntityBounds(this, dimensions);
    }

    public void update() {
        update(Gdx.graphics.getDeltaTime());

        velocity.x *= 0.9f;
        velocity.z *= 0.9f;
    }

    public void update(float deltaTime) {
        if (!onGround) {
            velocity.y -= GRAVITY * deltaTime;
        }

        if (velocity.y < MAX_FALLING_SPEED) {
            velocity.y = MAX_FALLING_SPEED;
        }

        Vector3 velocity = this.velocity.cpy().scl(deltaTime);

        collisionDetection(velocity);

        if (velocity.y == 0) {
            onGround = true;
            velocity.y = 0;
        } else {
            onGround = false;
        }

        position.add(velocity);
    }

    private void collisionDetection(Vector3 velocity) {
        bounds.checkCollision(velocity);
    }

    public Vector3 getPosition() {
        return position;
    }

    public EntityBounds getBounds() {
        return bounds;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
