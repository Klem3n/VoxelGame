package com.mygdx.game.world.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.collision.EntityBounds;

public class Entity {
    /**
     * Static variable for the Earths gravity
     */
    private static final float GRAVITY = 9.8f;
    /**
     * The falling speed cap static variable
     */
    private static final float MAX_FALLING_SPEED = -10f;

    /**
     * Entities position
     */
    protected final Vector3 position;
    /**
     * Collision bounds of the entity
     */
    private final EntityBounds bounds;

    /**
     * The velocity of the entity
     */
    protected final Vector3 velocity = new Vector3();

    /**
     * If entity is on ground
     */
    private boolean onGround = false;

    /**
     * Creates a new {@link Entity} object
     *
     * @param position   The entities real world position
     * @param dimensions The collision dimensions of the entity
     */
    public Entity(Vector3 position, Vector3 dimensions) {
        this.position = position;
        this.bounds = new EntityBounds(this, dimensions);
    }

    /**
     * Updates the entity every frame
     */
    public void update() {
        update(Gdx.graphics.getDeltaTime());

        velocity.x *= 0.9f;
        velocity.z *= 0.9f;
    }

    /**
     * Updates the entity every frame
     *
     * @param deltaTime time (in seconds) passed since the last frame
     */
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

    /**
     * Handles the collision detection of the entity
     *
     * @param velocity Current entity velocity
     */
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

    public void setPosition(Vector3 position) {
        this.position.set(position);
    }
}
