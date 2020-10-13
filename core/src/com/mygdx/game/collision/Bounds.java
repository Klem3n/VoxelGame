package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;

public abstract class Bounds {
    public abstract void checkCollision(Vector3 velocity);
}
