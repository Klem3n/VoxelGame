package com.mygdx.game.block.impl;

import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.renderer.DefaultBlockRenderer;
import com.mygdx.game.world.chunk.Chunk;

public class BlockJungleWood extends Block {
    public BlockJungleWood() {
        super(BlockID.JUNGLE_WOOD, "Jungle wood block", true, 1f);

        setDefaultBounds();
    }

    @Override
    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return DefaultBlockRenderer.render(verticies, vertexOffset, chunk, 16 * 12 + 3, 16 * 12 + 2, 16 * 12 + 3, getAlpha(), x, y, z, faceMask, true);
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return DefaultBlockRenderer.calculateFaceMasks(this, chunk, x, y, z);
    }

    @Override
    public int getInventoryTexture() {
        return 16 * 13 + 2;
    }
}
