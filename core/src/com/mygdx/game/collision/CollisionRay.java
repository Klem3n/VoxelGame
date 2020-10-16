package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.world.World;

import static com.mygdx.game.utils.Constants.*;

public class CollisionRay {

    final Vector3 origin;
    final Vector3 direction;

    final float distance;

    public CollisionRay(final Vector3 origin, final Vector3 direction, final float distance){
        this.origin = origin;
        this.direction = direction;
        this.distance = distance;
    }

    public WorldBlock trace(){
        float tMaxX, tMaxY, tMaxZ, tDeltaX, tDeltaY, tDeltaZ;

        Vector3 start = origin.cpy();
        Vector3 voxel = floor(start);
        Vector3 end = direction.cpy().scl(distance).add(origin);

        int dx = getSign(end.x - start.x);
        if (dx != 0) {
            tDeltaX = Math.min(dx / (end.x - start.x), Float.MAX_VALUE);
        } else {
            tDeltaX = Float.MAX_VALUE;
        }
        if (dx > 0) {
            tMaxX = tDeltaX * frac1(start.x) - 0.5f;
        } else {
            tMaxX = tDeltaX * frac0(start.x) - 0.5f;
        }

        int dy = getSign(end.y - start.y);
        if (dy != 0) {
            tDeltaY = Math.min(dy / (end.y - start.y), Float.MAX_VALUE);
        } else {
            tDeltaY = Float.MAX_VALUE;
        }
        if (dy > 0) {
            tMaxY = tDeltaY * frac1(start.y) - 0.5f;
        } else {
            tMaxY = tDeltaY * frac0(start.y) - 0.5f;
        }

        int dz = getSign(end.z - start.z);
        if (dz != 0) {
            tDeltaZ = Math.min(dz / (end.z - start.z), Float.MAX_VALUE);
        } else {
            tDeltaZ = Float.MAX_VALUE;
        }
        if (dz > 0) {
            tMaxZ = tDeltaZ * frac1(start.z) - 0.5f;
        } else {
            tMaxZ = tDeltaZ * frac0(start.z) - 0.5f;
        }

        Array<BlockType> passed = new Array<>();

        while (true) {
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    voxel.x += dx;
                    tMaxX += tDeltaX;
                } else {
                    voxel.z += dz;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    voxel.y += dy;
                    tMaxY += tDeltaY;
                } else {
                    voxel.z += dz;
                    tMaxZ += tDeltaZ;
                }
            }
            if (tMaxX > 1 && tMaxY > 1 && tMaxZ > 1) break;

            passed.add(World.INSTANCE.get(voxel));

            if (World.INSTANCE.get(voxel) != null && World.INSTANCE.get(voxel).collides(voxel)) {
                return new WorldBlock(voxel, World.INSTANCE.get(voxel));
            }
        }

        return new WorldBlock(null, BlockType.AIR);
    }

    private int getSign(float val){
        return val > 0 ? 1 : (val < 0 ? -1 : 0);
    }

    private float frac0(float val){
        return (float) (val - Math.floor(val));
    }

    private float frac1(float val){
        return (float) (1 - val + Math.floor(val));
    }
}
