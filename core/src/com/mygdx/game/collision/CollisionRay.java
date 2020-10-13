package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.world.World;

public class CollisionRay {

    final Vector3 origin;
    final Vector3 direction;

    final float distance;
    final float step;

    public CollisionRay(final Vector3 origin, final Vector3 direction, final float distance, final float step){
        this.origin = origin;
        this.direction = direction;
        this.distance = distance;
        this.step = step;
    }

    public CollisionRay(final Vector3 origin, final Vector3 direction, final float distance){
        this(origin, direction, distance, 0.1f);
    }

    public WorldBlock trace(){
        final World world = World.INSTANCE;

        BlockType blockType = BlockType.AIR;

        Vector3 toVec = null;

        for(float i = 0f; i < distance; i += step){
            toVec = direction.cpy();
            toVec.scl(i).add(origin);

            blockType = world.get(toVec);

            if(blockType != BlockType.AIR) {
                break;
            }
        }

        return new WorldBlock(toVec, blockType);
    }
}
