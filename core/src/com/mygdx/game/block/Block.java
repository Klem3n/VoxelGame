package com.mygdx.game.block;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.collision.Bounds;
import com.mygdx.game.collision.ray.CollisionRay;
import com.mygdx.game.collision.ray.RayHit;
import com.mygdx.game.utils.RaycastUtils;
import com.mygdx.game.world.Chunk;
import com.mygdx.game.world.entity.Entity;

public abstract class Block {
    public static Block AIR;

    private final int id;
    private final String name;
    private final boolean solid;
    private final float alpha;

    protected final Array<Bounds> bounds = new Array<>();

    public Block(int id, String name, boolean solid, float alpha) {
        this.id = id;
        this.name = name;
        this.solid = solid;
        this.alpha = alpha;
    }

    public abstract int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask);

    public abstract byte calculateFaceMasks(Chunk chunk, int x, int y, int z);

    /**
     * Checks if ray is colliding with the block
     *
     * @param blockPosition The block position
     * @param ray           The ray
     * @return {@code True} if ray is colliding with the block
     */
    public RayHit collides(Vector3 blockPosition, CollisionRay ray) {
        RayHit hit = new RayHit(ray, false);

        for (Bounds bound : bounds) {
            if ((hit = RaycastUtils.intersect(blockPosition, bound, ray)).isHit()) {
                return hit;
            }
        }

        return hit;
    }

    /**
     * Checks collision with an entity
     *
     * @param entity         The entity
     * @param entityPosition Entities position
     * @param blockPosition  The blocks position
     * @return {@code True} if entity is colliding with the block
     */
    public boolean collides(Entity entity, Vector3 entityPosition, Vector3 blockPosition) {
        if (!solid) {
            return false;
        }

        final float min_x1 = entityPosition.x;
        final float min_y1 = entityPosition.y;
        final float min_z1 = entityPosition.z;

        final float max_x1 = entityPosition.x + entity.getBounds().getWidth();
        final float max_y1 = entityPosition.y + entity.getBounds().getHeight();
        final float max_z1 = entityPosition.z + entity.getBounds().getDepth();

        for (Bounds box : bounds) {
            final float min_x2 = blockPosition.x + box.getOffsetX();
            final float min_y2 = blockPosition.y + box.getOffsetY();
            final float min_z2 = blockPosition.z + box.getOffsetZ();

            final float max_x2 = blockPosition.x + box.getOffsetX() + box.getWidth();
            final float max_y2 = blockPosition.y + box.getOffsetY() + box.getHeight();
            final float max_z2 = blockPosition.z + box.getOffsetZ() + box.getDepth();

            if (((min_x1 <= min_x2 && min_x2 <= max_x1) || (min_x2 <= min_x1 && min_x1 <= max_x2)) &&
                    ((min_y1 <= min_y2 && min_y2 <= max_y1) || (min_y2 <= min_y1 && min_y1 <= max_y2)) &&
                    ((min_z1 <= min_z2 && min_z2 <= max_z1) || (min_z2 <= min_z1 && min_z1 <= max_z2))) {
                return true;
            }
        }

        return false;
    }

    public float getAlpha() {
        return alpha;
    }

    protected void setDefaultBounds() {
        bounds.add(new Bounds(1, 1, 1));
    }

    public int getId() {
        return id;
    }

    public Array<Bounds> getBounds() {
        return bounds;
    }

    public String getName() {
        return name;
    }

    public boolean isSolid() {
        return solid;
    }

    public boolean isTransparent() {
        return alpha != 1;
    }
    
    public abstract int getInventoryTexture();
}
