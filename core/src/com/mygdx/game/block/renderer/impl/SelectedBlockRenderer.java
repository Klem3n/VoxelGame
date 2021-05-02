package com.mygdx.game.block.renderer.impl;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.block.BlockType;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.world.World;

public class SelectedBlockRenderer {
    private static float[] VERTICES = new float[4*12*6];

    private static Mesh mesh;

    public static void render(WorldBlock selectedBlock, Array<Renderable> renderables, Pool<Renderable> pool){
        if(selectedBlock == null || selectedBlock.getPosition() == null || selectedBlock.getBlockType() == BlockType.AIR || selectedBlock.getBlockType() == BlockType.WATER) {
            return;
        }

        if(mesh == null){
            mesh = new Mesh(true, 4 * 12 * 6, 36,
                    VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.ColorUnpacked());

            int len = 36;
            short[] indices = new short[len];
            short j = 0;
            for (int i = 0; i < len; i += 6, j += 4) {
                indices[i + 0] = (short)(j + 0);
                indices[i + 1] = (short)(j + 1);
                indices[i + 2] = (short)(j + 2);
                indices[i + 3] = (short)(j + 2);
                indices[i + 4] = (short)(j + 3);
                indices[i + 5] = (short)(j + 0);
            }

            mesh.setIndices(indices);
        }

        int vertexOffset = 0;

        Vector3 position = new Vector3((float) Math.floor(selectedBlock.getPosition().x), (float) Math.floor(selectedBlock.getPosition().y), (float) Math.floor(selectedBlock.getPosition().z));

        position.x += (1-selectedBlock.getBlockType().getSizeX())/2;
        position.z += (1-selectedBlock.getBlockType().getSizeZ())/2;

        TextureRegion textureRegion = World.TEXTURE_TILES[15][10];

        vertexOffset = createTop(position, VERTICES, vertexOffset, textureRegion, selectedBlock.getBlockType());
        vertexOffset = createBottom(position, VERTICES, vertexOffset, textureRegion, selectedBlock.getBlockType());
        vertexOffset = createBack(position, VERTICES, vertexOffset, textureRegion, selectedBlock.getBlockType());
        vertexOffset = createFront(position, VERTICES, vertexOffset, textureRegion, selectedBlock.getBlockType());
        vertexOffset = createLeft(position, VERTICES, vertexOffset, textureRegion, selectedBlock.getBlockType());
        vertexOffset = createRight(position, VERTICES, vertexOffset, textureRegion, selectedBlock.getBlockType());

        mesh.setVertices(VERTICES, 0, vertexOffset);
        
        Renderable renderable = pool.obtain();
        renderable.material = World.INSTANCE.getMaterial();
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = vertexOffset / 8;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderables.add(renderable);
    }

    private static int createTop(Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        return vertexOffset;
    }

    private static int createBottom (Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        return vertexOffset;
    }

    private static int createLeft (Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        return vertexOffset;
    }

    private static int createRight (Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        return vertexOffset;
    }

    private static int createFront (Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        return vertexOffset;
    }

    private static int createBack (Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, BlockType blockType) {
        float sizeX = blockType.getSizeX();
        float sizeY = blockType.getSizeY();
        float sizeZ = blockType.getSizeZ();

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y + sizeY;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + sizeX;
        vertices[vertexOffset++] = position.y;
        vertices[vertexOffset++] = position.z + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        return vertexOffset;
    }
}
