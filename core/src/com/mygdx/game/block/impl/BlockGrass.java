package com.mygdx.game.block.impl;

import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.block.renderer.DefaultBlockRenderer;
import com.mygdx.game.world.biome.BiomeID;
import com.mygdx.game.world.chunk.Chunk;

public class BlockGrass extends Block {
    public BlockGrass() {
        super(BlockID.GRASS, "Grass block", true, 1f);

        setDefaultBounds();
    }

    @Override
    public int render(float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return DefaultBlockRenderer.render(verticies, vertexOffset, chunk, getTopTexture(chunk, x, z), getSideTexture(chunk, x, z), 2, getAlpha(), x, y, z, faceMask, true);
    }

    @Override
    public byte calculateFaceMasks(Chunk chunk, int x, int y, int z) {
        return DefaultBlockRenderer.calculateFaceMasks(this, chunk, x, y, z);
    }

    @Override
    public int getInventoryTexture() {
        return 3;
    }

    private int getSideTexture(Chunk chunk, int x, int z) {
        int biome = chunk.getBiome(x, z);

        if (biome == BiomeID.SAVANNA) {
            return 14 * 16 + 2;
        } else if (biome == BiomeID.RAIN_FOREST || biome == BiomeID.TROPICAL_RAIN_FOREST) {
            return 14 * 16 + 4;
        } else if (biome == BiomeID.TUNDRA || biome == BiomeID.COLD_DESERT) {
            return 14 * 16 + 3;
        }

        return 3;
    }

    private int getTopTexture(Chunk chunk, int x, int z) {
        int biome = chunk.getBiome(x, z);

        if (biome == BiomeID.SAVANNA) {
            return 13 * 16 + 2;
        } else if (biome == BiomeID.RAIN_FOREST || biome == BiomeID.TROPICAL_RAIN_FOREST) {
            return 13 * 16 + 4;
        } else if (biome == BiomeID.TUNDRA || biome == BiomeID.COLD_DESERT) {
            return 13 * 16 + 3;
        }

        return 0;
    }
}
