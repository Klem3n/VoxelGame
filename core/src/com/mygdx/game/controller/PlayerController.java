package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.mygdx.game.world.entity.player.Player;

/**
 * Represents the controls player can use to traverse and interact with the world
 */
public class PlayerController extends InputAdapter {
    /**
     * Static variable for players speed
     */
    private static final float SPEED = 0.6f;
    /**
     * Static variable for players jump force
     */
    private static final float JUMP_FORCE = 5f;
    /**
     * Static variable for players rotation speed
     */
    private static final float ROTATION_SPEED = 0.4f;

    /**
     * Maps the controlls to static variables
     */
    private static final int STRAFE_LEFT = Keys.A;
    private static final int STRAFE_RIGHT = Keys.D;
    private static final int FORWARD = Keys.W;
    private static final int BACKWARD = Keys.S;
    private static final int UP = Keys.Q;
    private static final int DOWN = Keys.E;
    private static final int JUMP = Keys.SPACE;
    private static final int ESCAPE = Keys.ESCAPE;

    /**
     * Players camera
     */
    private final Camera camera;
    /**
     * Player reference
     */
    private final Player player;

    /**
     * Control map
     */
    private final IntIntMap keys = new IntIntMap();

    /**
     * Current mouse X position
     */
    private int mouseX = 0;
    /**
     * Current mouse Y position
     */
    private int mouseY = 0;

    /**
     * Creates a new {@link PlayerController} object and {@link Player} object
     *
     * @param camera   The players camera
     * @param position The players position
     */
    public PlayerController(Camera camera, Vector3 position) {
        this.camera = camera;
        this.player = new Player(camera, position);
    }

    /**
     * Executed when a key is pressed down
     *
     * @param keycode The key code
     */
    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    /**
     * Executed when a key is released
     *
     * @param keycode The key code
     */
    @Override
    public boolean keyUp(int keycode) {
        keys.remove(keycode, 0);

        if (keycode >= Keys.NUM_0 && keycode <= Keys.NUM_9) {
            if (keycode != Keys.NUM_0) {
                player.getInventory().setSelectedIndex(keycode - 8);
            } else {
                player.getInventory().setSelectedIndex(9);
            }
        }

        if (keycode >= Keys.NUMPAD_0 && keycode <= Keys.NUMPAD_9) {
            if (keycode != Keys.NUMPAD_0) {
                player.getInventory().setSelectedIndex(keycode - 145);
            } else {
                player.getInventory().setSelectedIndex(9);
            }
        }

        return true;
    }

    /**
     * Exectued when the player scrolls on the screen
     *
     * @param amount The scroll amount
     */
    @Override
    public boolean scrolled(int amount) {
        int index = Math.floorMod(player.getInventory().getSelectedIndex() + amount, 10);

        player.getInventory().setSelectedIndex(index);

        return false;
    }

    /**
     * Executes when the player drags on the screen
     */
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        lookAround(screenX, screenY);
        return false;
    }

    /**
     * Executes when the player moves the mouse
     */
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        lookAround(screenX, screenY);
        return false;
    }

    /**
     * Handles the player's camera movement
     *
     * @param screenX X coordinate movement
     * @param screenY Y coordinate movement
     */
    private void lookAround(int screenX, int screenY) {
        int magX = Math.abs(mouseX - screenX);
        int magY = Math.abs(mouseY - screenY);

        if (mouseX > screenX) {
            camera.direction.rotate(Vector3.Y, 1 * magX * ROTATION_SPEED);
        }

        if (mouseX < screenX) {
            camera.direction.rotate(Vector3.Y, -1 * magX * ROTATION_SPEED);
        }

        if (mouseY < screenY) {
            camera.direction.rotate(camera.direction.cpy().crs(Vector3.Y), -1 * magY * ROTATION_SPEED);
        }

        if (mouseY > screenY) {
            camera.direction.rotate(camera.direction.cpy().crs(Vector3.Y), 1 * magY * ROTATION_SPEED);
        }

        camera.update();

        mouseX = screenX;
        mouseY = screenY;
    }

    /**
     * Executes when the mouse button is released
     */
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        player.touchUp(screenX, screenY, pointer, button);
        return false;
    }

    /**
     * Updates the player every frame
     */
    public void update() {
        float speed = SPEED;

        if (keys.containsKey(Keys.SHIFT_LEFT)) {
            speed *= 2;
        }

        Vector3 transform = new Vector3();

        if (keys.containsKey(FORWARD)) {
            transform = camera.direction.cpy();
            transform.y = 0f;
            transform.nor().scl(speed);
        }
        if (keys.containsKey(BACKWARD)) {
            transform = camera.direction.cpy();
            transform.y = 0f;
            transform.nor().scl(-speed);
        }
        if (keys.containsKey(STRAFE_LEFT)) {
            transform = camera.direction.cpy();
            transform.crs(camera.up);
            transform.nor().scl(-speed);
        }
        if (keys.containsKey(STRAFE_RIGHT)) {
            transform = camera.direction.cpy();
            transform.crs(camera.up);
            transform.nor().scl(speed);
        }

        if (keys.containsKey(ESCAPE)) {
            keys.remove(ESCAPE, 0);

            Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
        }

        transform.y = 0;

        Vector3 velocity = player.getVelocity();

        velocity.add(transform);

        if (keys.containsKey(JUMP) && player.isOnGround()) {
            velocity.y = JUMP_FORCE;
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Vector3 getPosition() {
        return player.getPosition();
    }
}
