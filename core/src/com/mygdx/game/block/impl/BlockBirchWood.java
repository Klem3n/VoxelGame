package com.mygdx.game.block.impl;

import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.renderer.DefaultBlockRenderer;
import com.mygdx.game.world.chunk.Chunk;

public class BlockBirchWood extends Block {
    public BlockBirchWood() {
        super(BlockID.BIRCH_WOOD, "Birch wood block", true, 1f);

        setDefaultBounds();
    }

    @Override
    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return DefaultBlockRenderer.render(verticies, vertexOffset, chunk, 21, 117, 21, getAlpha(), x, y, z, faceMask, true);
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return DefaultBlockRenderer.calculateFaceMasks(this, chunk, x, y, z);
    }

    @Override
    public int getInventoryTexture() {
        return 117;
    }
}
