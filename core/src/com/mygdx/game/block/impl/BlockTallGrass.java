package com.mygdx.game.block.impl;

import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.renderer.FolliageBlockRenderer;
import com.mygdx.game.collision.Bounds;
import com.mygdx.game.world.chunk.Chunk;

public class BlockTallGrass extends Block {
    public BlockTallGrass() {
        super(BlockID.TALL_GRASS, "Tall grass", false, 1f);

        bounds.add(new Bounds(0f, 0.0f, 0f, 1f, 0.5f, 1f));
    }

    @Override
    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return FolliageBlockRenderer.render(verticies, vertexOffset, chunk, 39, getAlpha(), 1f, x, y, z, faceMask);
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return 0;
    }

    @Override
    public int getInventoryTexture() {
        return 39;
    }

    @Override
    public boolean renderBehind() {
        return true;
    }
}
