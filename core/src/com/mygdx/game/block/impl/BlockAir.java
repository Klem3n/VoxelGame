package com.mygdx.game.block.impl;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.collision.ray.CollisionRay;
import com.mygdx.game.collision.ray.RayHit;
import com.mygdx.game.world.Chunk;
import com.mygdx.game.world.entity.Entity;

public class BlockAir extends Block {

    public BlockAir() {
        super(BlockID.AIR, "Air block", false, 0f);

        setDefaultBounds();
    }

    @Override
    public RayHit collides(Vector3 blockPosition, CollisionRay ray) {
        return new RayHit(ray, false);
    }

    @Override
    public boolean collides(Entity entity, Vector3 entityPosition, Vector3 blockPosition) {
        return false;
    }

    @Override
    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return vertexOffset;
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return 0;
    }

    @Override
    public int getInventoryTexture() {
        return -1;
    }
}
