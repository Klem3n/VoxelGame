package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.world.World;

public class CollisionRay extends Ray {

    final float distance;

    public CollisionRay(final Vector3 origin, final Vector3 direction, final float distance){
        super(origin, direction);

        this.distance = distance;
    }

    public WorldBlock trace(){
        final World world = World.INSTANCE;

        Block block = Block.AIR;

        final Vector3 toVec = new Vector3();

        for(float i = 0f; i < distance; i += 0.1f){
            toVec.set(direction).scl(i).add(origin);

            byte b = world.get(toVec);

            if(b != 0){
                block = Block.getById(b);
                break;
            }
        }

        if(block == Block.AIR)
            return null;

        return new WorldBlock(toVec, block);
    }
}
