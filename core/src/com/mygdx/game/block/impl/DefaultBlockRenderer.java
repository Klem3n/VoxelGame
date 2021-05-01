package com.mygdx.game.block.impl;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.BlockRenderer;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.world.Chunk;
import com.mygdx.game.world.World;

import static com.mygdx.game.utils.Constants.*;

public class DefaultBlockRenderer extends BlockRenderer {
    public static final DefaultBlockRenderer INSTANCE = new DefaultBlockRenderer();

    @Override
    public int render(BlockType type, float[] vertices, int vertexOffset, Chunk chunk, int x, int y, int z, byte faceMask) {
        if ((faceMask & 0x1) != 0) {
            vertexOffset = createTop(chunk.offset, x, y, z, vertices, vertexOffset, getTopTexture(type), type);
        }
        if ((faceMask & 0x2) != 0) {
            vertexOffset = createBottom(chunk.offset, x, y, z, vertices, vertexOffset, getBottomTexture(type), type);
        }
        if ((faceMask & 0x4) != 0) {
            vertexOffset = createLeft(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }
        if ((faceMask & 0x8) != 0) {
            vertexOffset = createRight(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }
        if ((faceMask & 0x10) != 0) {
            vertexOffset = createFront(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }
        if ((faceMask & 0x20) != 0) {
            vertexOffset = createBack(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }


        return vertexOffset;
    }

    @Override
    public boolean collides(BlockType blockType, Vector3 position) {
        if(blockType == BlockType.AIR || blockType == BlockType.WATER){
            return false;
        }

        return true;
    }

    @Override
    public byte calculateFaceMasks(BlockType blockType, Chunk chunk, int x, int y, int z) {
        byte mask = 0;

        if (renderTop(blockType, chunk, x, y, z)) {
            mask |= 0x1;
        }
        if (renderBottom(blockType, chunk, x, y, z)) {
            mask |= 0x2;
        }
        if (renderLeft(blockType, chunk, x, y, z)) {
            mask |= 0x4;
        }
        if (renderRight(blockType, chunk, x, y, z)) {
            mask |= 0x8;
        }
        if (renderFront(blockType, chunk, x, y, z)) {
            mask |= 0x10;
        }
        if (renderBack(blockType, chunk, x, y, z)) {
            mask |= 0x20;
        }

        return mask;
    }

    private boolean renderTop(BlockType render, Chunk chunk, int x, int y, int z) {
        BlockType blockType = chunk.getBlock(x, y + 1, z);

        if (blockType == null)
            return false;

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private boolean renderBottom(BlockType render, Chunk chunk, int x, int y, int z) {
        BlockType blockType = chunk.getBlock(x, y - 1, z);

        if (blockType == null)
            return false;

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private boolean renderLeft(BlockType render, Chunk chunk, int x, int y, int z) {
        BlockType blockType = chunk.getBlock(x - 1, y, z);

        if (blockType == null)
            return false;

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private boolean renderRight(BlockType render, Chunk chunk, int x, int y, int z) {
        BlockType blockType = chunk.getBlock(x + 1, y, z);

        if (blockType == null)
            return false;

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private boolean renderFront(BlockType render, Chunk chunk, int x, int y, int z) {
        BlockType blockType = chunk.getBlock(x, y, z - 1);

        if (blockType == null)
            return false;

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private boolean renderBack(BlockType render, Chunk chunk, int x, int y, int z) {
        BlockType blockType = chunk.getBlock(x, y, z + 1);

        if (blockType == null)
            return false;

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    public int createTop(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + sizeY;
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

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y + sizeY;
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

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public int createBottom(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public int createLeft(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public int createRight(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public int createFront(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }

    public int createBack(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y + sizeY;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        vertices[vertexOffset++] = offset.x + x + sizeX;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = blockType.getAlpha();

        return vertexOffset;
    }
}
