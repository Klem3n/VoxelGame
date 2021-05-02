package com.mygdx.game.block.impl;

import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.renderer.DefaultBlockRenderer;
import com.mygdx.game.world.Chunk;

public class BlockStone extends Block {
    public BlockStone() {
        super(BlockID.STONE, "Stone block", true, 1f);

        setDefaultBounds();
    }

    @Override
    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return DefaultBlockRenderer.render(verticies, vertexOffset, chunk, 6, 6, 6, getAlpha(), x, y, z, faceMask);
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return DefaultBlockRenderer.calculateFaceMasks(this, chunk, x, y, z);
    }

    @Override
    public int getInventoryTexture() {
        return 6;
    }
}
