package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.mygdx.game.world.World;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.entity.player.Player;

public class PlayerController extends InputAdapter {
    private static final float SPEED = 0.6f;
    private static final float JUMP_FORCE = 5f;
    private static final float ROTATION_SPEED = 0.4f;

    private static final int STRAFE_LEFT = Keys.A;
    private static final int STRAFE_RIGHT = Keys.D;
    private static final int FORWARD = Keys.W;
    private static final int BACKWARD = Keys.S;
    private static final int UP = Keys.Q;
    private static final int DOWN = Keys.E;
    private static final int JUMP = Keys.SPACE;
    private static final int ESCAPE = Keys.ESCAPE;

    private final Camera camera;
    private final Player player;

    private final IntIntMap keys = new IntIntMap();

    private int mouseX = 0;
    private int mouseY = 0;

    public PlayerController(Camera camera, Vector3 position) {
        this.camera = camera;
        this.player = new Player(camera, position);
    }

    @Override
    public boolean keyDown(int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

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

        if (keycode == Keys.X) {
            Chunk.MESH_POOL.clear();
            World.INSTANCE.getChunks().forEach(e -> {
                e.value.rerender();
            });
        }

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        int index = Math.floorMod(player.getInventory().getSelectedIndex() + amount, 10);

        player.getInventory().setSelectedIndex(index);

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        lookAround(screenX, screenY);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        lookAround(screenX, screenY);
        return false;
    }

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

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        player.touchUp(screenX, screenY, pointer, button);
        return false;
    }

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
