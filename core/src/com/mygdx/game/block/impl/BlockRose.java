package com.mygdx.game.block.impl;

import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.renderer.FolliageBlockRenderer;
import com.mygdx.game.collision.Bounds;
import com.mygdx.game.world.Chunk;

public class BlockRose extends Block {
    public BlockRose() {
        super(BlockID.ROSE, "Rose", false, 1f);

        bounds.add(new Bounds(0.25f, 0.0f, 0.25f, 0.5f, 0.5f, 0.5f));
    }

    @Override
    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return FolliageBlockRenderer.render(verticies, vertexOffset, chunk, 12, getAlpha(), 0.5f, x, y, z, faceMask);
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return 0;
    }

    @Override
    public int getInventoryTexture() {
        return 12;
    }
}
