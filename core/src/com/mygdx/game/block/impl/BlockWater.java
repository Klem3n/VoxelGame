package com.mygdx.game.block.impl;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.renderer.DefaultBlockRenderer;
import com.mygdx.game.collision.ray.CollisionRay;
import com.mygdx.game.collision.ray.RayHit;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.entity.Entity;

public class BlockWater extends Block {
    public BlockWater() {
        super(BlockID.WATER, "Water block", false, 0.6f);

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
        return DefaultBlockRenderer.render(verticies, vertexOffset, chunk, 205, 206, 206, getAlpha(), x, y, z, faceMask, true);
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return DefaultBlockRenderer.calculateFaceMasks(this, chunk, x, y, z);
    }

    @Override
    public int getInventoryTexture() {
        return 206;
    }
}
