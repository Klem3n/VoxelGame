package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.collision.CollisionRay;
import com.mygdx.game.collision.PlayerBounds;
import com.mygdx.game.utils.Constants;
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
    private float speed = 0.4f;
    private float degreesPerPixel = 0.5f;
    private final Vector3 velocity = new Vector3();

    private float gravity = 0.4f;
    private float maxFallingSpeed = -10f;
    private float jumpForce = 8f;

    private boolean onGround = false;

    private final CollisionRay blockDetectionRay;
    private WorldBlock selectedBlock;

    private final PlayerBounds bounds;

    private final Vector3 position;

    public Player (Camera camera, Vector3 position) {
        this.camera = camera;
        bounds = new PlayerBounds(this, position);
        blockDetectionRay = new CollisionRay(this.camera.position, this.camera.direction, 3);
        this.position = position;
        this.camera.position.set(this.position).add(0f, 1.5f, 0f);
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
            if (camera.direction.y > -0.900) {
                camera.rotate(camera.direction.cpy().crs(Vector3.Y), -1 * magY * rotSpeed);
            }

            if (camera.direction.y < -0.900) {
                camera.direction.y = -0.800f;
            }

            camera.update();
        }

        if (mouseY > screenY) {
            if (camera.direction.y < 0.900) {
                camera.rotate(camera.direction.cpy().crs(Vector3.Y), 1 * magY * rotSpeed);
            }

            if (camera.direction.y > 0.900) {
                camera.direction.y = 0.800f;
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
            if(selectedBlock != null && selectedBlock.getPosition() != null && selectedBlock.getBlockType() != BlockType.AIR && selectedBlock.getBlockType() != BlockType.WATER){
                getWorld().set(selectedBlock.getPosition(), BlockType.AIR);
            }
        }

        return false;
    }

    public void update () {
        selectedBlock = blockDetectionRay.trace();
        update(Gdx.graphics.getDeltaTime());

        velocity.x *= 0.9f;
        velocity.z *= 0.9f;
    }

    public void update (float deltaTime) {
        float speed = this.speed;

        if(keys.containsKey(Keys.SHIFT_LEFT)){
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

        if(keys.containsKey(ESCAPE)){
            keys.remove(ESCAPE, 0);

            Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
        }
        if(keys.containsKey(Keys.F5)){
            keys.remove(Keys.F5, 0);

            VoxelGame.DEBUG = !VoxelGame.DEBUG;

            World.INSTANCE.chunks.forEach((key, c)->{
                c.setDirty(true);
            });
        }

        transform.y = 0;

        velocity.add(transform);

        if(keys.containsKey(JUMP) && onGround){
            velocity.y = jumpForce;
        }

        //if(!onGround){
           velocity.y -= gravity;

           if(velocity.y < maxFallingSpeed){
               velocity.y = maxFallingSpeed;
           }
        /*} else if(velocity.y < 0){
            velocity.y = 0;
        }*/

        Vector3 velocity = this.velocity.cpy().scl(deltaTime);

        collisionDetection(velocity);

        onGround = velocity.y == 0;

        camera.position.add(velocity);
        position.add(velocity);

        camera.update(true);
    }

    private void collisionDetection(Vector3 velocity) {
        boolean inAir = velocity.y > 0;

        bounds.checkCollision(velocity);

        if(inAir && velocity.y == 0f){
            this.velocity.y = 0;
        }
    }

    public Vector3 getPosition(){
        return position;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public WorldBlock getSelectedBlock() {
        return selectedBlock;
    }

    public World getWorld(){
        return World.INSTANCE;
    }
}
