package com.mygdx.game.block.impl;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.BlockRenderer;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.world.Chunk;

public class FolliageBlockRenderer extends BlockRenderer {
    public static final FolliageBlockRenderer INSTANCE = new FolliageBlockRenderer();

    @Override
    public int render(BlockType type, float[] verticies, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        return renderX(chunk.offset, x, y, z, verticies, vertexOffset, type);
    }

    @Override
    public byte calculateFaceMasks(BlockType blockType, Chunk chunk, int x, int y, int z) {
        return 0;
    }

    public int renderX(Vector3 offset, float x, float y, float z, float[] vertices, int vertexOffset, BlockType blockType) {
        TextureRegion textureRegion = getSideTexture(blockType);

        float size = blockType.getSizeX();

        x += (1 - size)/2;
        z += (1 - size)/2;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        //SECOND FACE
        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        //THIRD FACE
        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        //FOURTH FACE
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z + size;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y + size;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + size;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();
        return vertexOffset;
    }
}
