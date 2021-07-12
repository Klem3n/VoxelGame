package com.mygdx.game.block.renderer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.game.block.Block;
import com.mygdx.game.block.WorldBlock;
import com.mygdx.game.collision.Bounds;
import com.mygdx.game.utils.Constants;

import static com.mygdx.game.utils.Constants.MATERIAL;

public class SelectedBlockRenderer {
    private static float[] VERTICES = new float[4 * 12 * 6];

    private static Mesh mesh;

    public static void render(WorldBlock selectedBlock, Array<Renderable> renderables, Pool<Renderable> pool) {
        if (selectedBlock == null || selectedBlock.getPosition() == null || selectedBlock.getBlock() == null || selectedBlock.getBlock() == Block.AIR || !selectedBlock.getRayHit().isHit()) {
            return;
        }

        if (mesh == null) {
            mesh = new Mesh(true, 4 * 12 * 6, 36,
                    VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0), VertexAttribute.ColorUnpacked());

            int len = 36;
            short[] indices = new short[len];
            short j = 0;
            for (int i = 0; i < len; i += 6, j += 4) {
                indices[i + 0] = (short) (j + 0);
                indices[i + 1] = (short) (j + 1);
                indices[i + 2] = (short) (j + 2);
                indices[i + 3] = (short) (j + 2);
                indices[i + 4] = (short) (j + 3);
                indices[i + 5] = (short) (j + 0);
            }

            mesh.setIndices(indices);
        }

        int vertexOffset = 0;

        Vector3 position = new Vector3((float) Math.floor(selectedBlock.getPosition().x), (float) Math.floor(selectedBlock.getPosition().y), (float) Math.floor(selectedBlock.getPosition().z));

        TextureRegion textureRegion = Constants.TEXTURE_TILES[15][10];

        for (Bounds bound : selectedBlock.getBlock().getBounds()) {
            vertexOffset = createTop(position, VERTICES, vertexOffset, textureRegion, bound);
            vertexOffset = createBottom(position, VERTICES, vertexOffset, textureRegion, bound);
            vertexOffset = createBack(position, VERTICES, vertexOffset, textureRegion, bound);
            vertexOffset = createFront(position, VERTICES, vertexOffset, textureRegion, bound);
            vertexOffset = createLeft(position, VERTICES, vertexOffset, textureRegion, bound);
            vertexOffset = createRight(position, VERTICES, vertexOffset, textureRegion, bound);
        }


        mesh.setVertices(VERTICES, 0, vertexOffset);

        Renderable renderable = pool.obtain();
        renderable.material = MATERIAL;
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = vertexOffset / 8;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderables.add(renderable);
    }

    private static int createTop(Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, Bounds bounds) {
        float sizeX = bounds.getWidth();
        float sizeY = bounds.getHeight();
        float sizeZ = bounds.getDepth();

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
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

    private static int createBottom(Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, Bounds bounds) {
        float sizeX = bounds.getWidth();
        float sizeY = bounds.getHeight();
        float sizeZ = bounds.getDepth();

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
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

    private static int createLeft(Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, Bounds bounds) {
        float sizeX = bounds.getWidth();
        float sizeY = bounds.getHeight();
        float sizeZ = bounds.getDepth();

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
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

    private static int createRight(Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, Bounds bounds) {
        float sizeX = bounds.getWidth();
        float sizeY = bounds.getHeight();
        float sizeZ = bounds.getDepth();

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
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

    private static int createFront(Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, Bounds bounds) {
        float sizeX = bounds.getWidth();
        float sizeY = bounds.getHeight();
        float sizeZ = bounds.getDepth();

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ();
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

    private static int createBack(Vector3 position, float[] vertices, int vertexOffset, TextureRegion textureRegion, Bounds bounds) {
        float sizeX = bounds.getWidth();
        float sizeY = bounds.getHeight();
        float sizeZ = bounds.getDepth();

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV2();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX();
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY() + sizeY;
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = textureRegion.getU2();
        vertices[vertexOffset++] = textureRegion.getV();
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0.7f;

        vertices[vertexOffset++] = position.x + bounds.getOffsetX() + sizeX;
        vertices[vertexOffset++] = position.y + bounds.getOffsetY();
        vertices[vertexOffset++] = position.z + bounds.getOffsetZ() + sizeZ;
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