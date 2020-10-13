package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Player;
import com.mygdx.game.world.World;

public class PlayerBounds extends Bounds {
    private final Player player;
    private final Vector3 position;

    public PlayerBounds(Player player, Vector3 position){
        this.position = position;
        this.player = player;
    }

    @Override
    public void checkCollision(Vector3 velocity) {
        World world = World.INSTANCE;

        float x = velocity.x;
        float y = velocity.y;
        float z = velocity.z;

        Vector3[] blocks = new Vector3[]{position.cpy(), position.cpy().add(0f, -1.5f, 0f)};

        for (Vector3 block : blocks) {
            if(world.get(block.cpy().add(x, 0, 0)).isSolid()){
                x = 0;
            }
            if(world.get(block.cpy().add(0, y, 0)).isSolid()){
                y = 0;
            }
            if(world.get(block.cpy().add(0, 0, z)).isSolid()){
                z = 0;
            }
        }

        velocity.set(x, y, z);
    }
}
