package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.collision.CollisionRay;
import com.mygdx.game.world.World;

/** Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * @author badlogic */
public class Player extends InputAdapter {
    private final Camera camera;
    private final IntIntMap keys = new IntIntMap();
    private int STRAFE_LEFT = Keys.A;
    private int STRAFE_RIGHT = Keys.D;
    private int FORWARD = Keys.W;
    private int BACKWARD = Keys.S;
    private int UP = Keys.Q;
    private int DOWN = Keys.E;
    private int JUMP = Keys.SPACE;
    private int ESCAPE = Keys.ESCAPE;
    private float speed = 0.5f;
    private float degreesPerPixel = 0.5f;
    private final Vector3 velocity = new Vector3();

    private float gravity = 0.1f;
    private float maxFallingSpeed = -10f;
    private float jumpForce = 5f;

    private boolean onGround = false;

    private final CollisionRay blockDetectionRay;

    public Player (Camera camera) {
        this.camera = camera;

        blockDetectionRay = new CollisionRay(this.camera.position, this.camera.direction, 3);
    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    /** Sets the speed in units per second for moving forward, backward and strafing left/right.
     * @param speed the speed in units per second */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /** Sets how many degrees to rotate per pixel the mouse moved.
     * @param degreesPerPixel */
    public void setDegreesPerPixel (float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    private int mouseX = 0;
    private int mouseY = 0;
    private float rotSpeed = 0.4f;

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        int magX = Math.abs(mouseX - screenX);
        int magY = Math.abs(mouseY - screenY);

        if (mouseX > screenX) {
            camera.rotate(Vector3.Y, 1 * magX * rotSpeed);
            camera.update();
        }

        if (mouseX < screenX) {
            camera.rotate(Vector3.Y, -1 * magX * rotSpeed);
            camera.update();
        }

        if (mouseY < screenY) {
            if (camera.direction.y > -0.965) {
                camera.rotate(camera.direction.cpy().crs(Vector3.Y), -1 * magY * rotSpeed);
            }

            if (camera.direction.y < -0.965) {
                camera.direction.y = -0.965f;
            }

            camera.update();
        }

        if (mouseY > screenY) {
            if (camera.direction.y < 0.965) {
                camera.rotate(camera.direction.cpy().crs(Vector3.Y), 1 * magY * rotSpeed);
            }

            if (camera.direction.y > 0.965) {
                camera.direction.y = 0.965f;
            }

            camera.update();
        }

        mouseX = screenX;
        mouseY = screenY;

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        /*
         Block destroy
         */
        if(button == 0){
            WorldBlock block = new CollisionRay(this.camera.position, this.camera.direction, 3).trace();

            if(block != null){
                getWorld().set(block.getPosition(), (byte) 0);
            }
        }

        return false;
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());

        velocity.x *= 0.9f;
        velocity.z *= 0.9f;
    }

    public void update (float deltaTime) {
        float speed = this.speed;

        if(keys.containsKey(Keys.SHIFT_LEFT)){
            speed *= 2;
        }

        double yaw = Math.toRadians(camera.direction.x);
        double yaw90 = Math.toRadians(camera.direction.x + 90);

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
        if(keys.containsKey(ESCAPE)){
            keys.remove(ESCAPE, 0);

            Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
        }

        transform.y = 0;

        velocity.add(transform);

        if(keys.containsKey(JUMP) && onGround){
            keys.remove(JUMP, 0);
            velocity.y = jumpForce;
        }

        if(!onGround){
           velocity.y -= gravity;

           if(velocity.y < maxFallingSpeed){
               velocity.y = maxFallingSpeed;
           }
        } else if(velocity.y < 0){
            velocity.y = 0;
        }

        camera.position.add(velocity.cpy().scl(deltaTime));

        collisionDetection();

        camera.update(true);
    }

    private void collisionDetection() {
        byte block = World.INSTANCE.get(getPosition().cpy().add(0f, -1.6f, 0f));

        if(block == 0){
            onGround = false;
        } else {
            onGround = true;
        }


    }

    public Vector3 getPosition(){
        return camera.position;
    }

    public World getWorld(){
        return World.INSTANCE;
    }
}
