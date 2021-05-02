package com.mygdx.game.block.renderer;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.Block;
import com.mygdx.game.world.Chunk;

import static com.mygdx.game.utils.TextureUtils.getBlockTexture;

public class DefaultBlockRenderer {

    public static int render(float[] vertices, int vertexOffset, Chunk chunk, int topTexture, int sideTexture, int bottomTexture, float alpha, int x, int y, int z, byte faceMask) {
        if ((faceMask & 0x1) != 0) {
            vertexOffset = createTop(chunk.offset, x, y, z, vertices, vertexOffset, getBlockTexture(topTexture), alpha);
        }
        if ((faceMask & 0x2) != 0) {
            vertexOffset = createBottom(chunk.offset, x, y, z, vertices, vertexOffset, getBlockTexture(bottomTexture), alpha);
        }
        if ((faceMask & 0x4) != 0) {
            vertexOffset = createLeft(chunk.offset, x, y, z, vertices, vertexOffset, getBlockTexture(sideTexture), alpha);
        }
        if ((faceMask & 0x8) != 0) {
            vertexOffset = createRight(chunk.offset, x, y, z, vertices, vertexOffset, getBlockTexture(sideTexture), alpha);
        }
        if ((faceMask & 0x10) != 0) {
            vertexOffset = createFront(chunk.offset, x, y, z, vertices, vertexOffset, getBlockTexture(sideTexture), alpha);
        }
        if ((faceMask & 0x20) != 0) {
            vertexOffset = createBack(chunk.offset, x, y, z, vertices, vertexOffset, getBlockTexture(sideTexture), alpha);
        }


        return vertexOffset;
    }

    public static byte calculateFaceMasks(Block block, Chunk chunk, int x, int y, int z) {
        byte mask = 0;

        if (renderTop(block, chunk, x, y, z)) {
            mask |= 0x1;
        }
        if (renderBottom(block, chunk, x, y, z)) {
            mask |= 0x2;
        }
        if (renderLeft(block, chunk, x, y, z)) {
            mask |= 0x4;
        }
        if (renderRight(block, chunk, x, y, z)) {
            mask |= 0x8;
        }
        if (renderFront(block, chunk, x, y, z)) {
            mask |= 0x10;
        }
        if (renderBack(block, chunk, x, y, z)) {
            mask |= 0x20;
        }

        return mask;
    }

    private static boolean renderTop(Block render, Chunk chunk, int x, int y, int z) {
        Block blockType = chunk.getBlock(x, y + 1, z);

        if (blockType == null) {
            return false;
        }

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private static boolean renderBottom(Block render, Chunk chunk, int x, int y, int z) {
        Block blockType = chunk.getBlock(x, y - 1, z);

        if (blockType == null) {
            return false;
        }

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private static boolean renderLeft(Block render, Chunk chunk, int x, int y, int z) {
        Block blockType = chunk.getBlock(x - 1, y, z);

        if (blockType == null) {
            return false;
        }

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private static boolean renderRight(Block render, Chunk chunk, int x, int y, int z) {
        Block blockType = chunk.getBlock(x + 1, y, z);

        if (blockType == null) {
            return false;
        }

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private static boolean renderFront(Block render, Chunk chunk, int x, int y, int z) {
        Block blockType = chunk.getBlock(x, y, z - 1);

        if (blockType == null) {
            return false;
        }

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    private static boolean renderBack(Block render, Chunk chunk, int x, int y, int z) {
        Block blockType = chunk.getBlock(x, y, z + 1);

        if (blockType == null) {
            return false;
        }

        if (render == blockType) {
            return false;
        } else {
            return !blockType.isSolid();
        }
    }

    public static int createTop(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, float alpha) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        return vertexOffset;
    }

    public static int createBottom(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, float alpha) {
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
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
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
        vertices[vertexOffset++] = alpha;

        return vertexOffset;
    }

    public static int createLeft(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, float alpha) {
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
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        return vertexOffset;
    }

    public static int createRight(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, float alpha) {
        vertices[vertexOffset++] = offset.x + x + 1;
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
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        return vertexOffset;
    }

    public static int createFront(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, float alpha) {
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
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
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
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        return vertexOffset;
    }

    public static int createBack(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, TextureRegion textureRegion, float alpha) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = alpha;

        return vertexOffset;
    }
}
