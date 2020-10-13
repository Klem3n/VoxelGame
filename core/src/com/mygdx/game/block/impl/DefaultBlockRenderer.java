package com.mygdx.game.block.impl;

import com.mygdx.game.block.BlockRenderer;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.world.Chunk;
import com.mygdx.game.world.World;

import static com.mygdx.game.utils.Constants.*;

public class DefaultBlockRenderer extends BlockRenderer {
    public static DefaultBlockRenderer INSTANCE = new DefaultBlockRenderer();

    @Override
    public int render(BlockType type, float[] vertices, int vertexOffset, Chunk chunk, int x, int y, int z) {
        if(renderTop(type, chunk, x, z, y)){
            vertexOffset = Chunk.createTop(chunk.offset, x, y, z, vertices, vertexOffset, getTopTexture(type), type);
        }
        if(renderBottom(type, chunk, x, z, y)){
            vertexOffset = Chunk.createBottom(chunk.offset, x, y, z, vertices, vertexOffset, getBottomTexture(type), type);
        }
        if(renderLeft(type, chunk, x, z, y)){
            vertexOffset = Chunk.createLeft(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }
        if(renderRight(type, chunk, x, z, y)){
            vertexOffset = Chunk.createRight(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }
        if(renderFront(type, chunk, x, z, y)){
            vertexOffset = Chunk.createFront(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }
        if(renderBack(type, chunk, x, z, y)){
            vertexOffset = Chunk.createBack(chunk.offset, x, y, z, vertices, vertexOffset, getSideTexture(type), type);
        }
        return vertexOffset;
    }

    private boolean renderTop(BlockType render, Chunk chunk, int x, int z, int y) {
        BlockType blockType = chunk.getBlock(x, y + 1, z);

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderBottom(BlockType render, Chunk chunk, int x, int z, int y) {
        BlockType blockType = chunk.getBlock(x, y - 1, z);

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderLeft(BlockType render, Chunk chunk, int x, int z, int y) {
        BlockType blockType = chunk.getBlock(x - 1, y, z);

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderRight(BlockType render, Chunk chunk, int x, int z, int y) {
        BlockType blockType = chunk.getBlock(x + 1, y, z);

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderFront(BlockType render, Chunk chunk, int x, int z, int y) {
        BlockType blockType = chunk.getBlock(x, y, z - 1);

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
    private boolean renderBack(BlockType render, Chunk chunk, int x, int z, int y) {
        BlockType blockType = chunk.getBlock(x, y, z + 1);

        if(!render.isSolid() && render == blockType){
            return false;
        } else {
            return !blockType.isSolid();
        }
    }
}
