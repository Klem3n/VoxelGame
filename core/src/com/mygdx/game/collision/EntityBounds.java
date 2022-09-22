package com.mygdx.game.collision;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.Block;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.world.World;
import com.mygdx.game.world.entity.Entity;

public class EntityBounds extends Bounds {
    private final Entity entity;

    public EntityBounds(Entity entity, Vector3 dimensions) {
        super(dimensions);
        this.entity = entity;
    }

    public void checkCollision(Vector3 velocity) {
        float x = velocity.x;
        float y = velocity.y;
        float z = velocity.z;

        if (hitTest(x, 0, 0)) {
            x = 0;
        }

        if (hitTest(0, y, 0)) {
            y = 0;
        }

        if (hitTest(0, 0, z)) {
            z = 0;
        }

        velocity.set(x, y, z);
    }

    private boolean hitTest(float dx, float dy, float dz) {
        World world = World.INSTANCE;

        Vector3 position = entity.getPosition();

        for (int x = 0; x <= Constants.ceil(getWidth()); x++) {
            for (int y = 0; y <= Constants.ceil(getHeight()); y++) {
                for (int z = 0; z <= Constants.ceil(getDepth()); z++) {
                    Vector3 blockPosition = Constants.floor(new Vector3(x + position.x + dx, y + position.y + dy, z + position.z + dz));

                    Block type = world.get(blockPosition, true, true);

                    if (type != null && type.collides(entity, position.cpy().add(dx, dy, dz), blockPosition)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
