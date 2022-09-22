package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.utils.Array;

public class ChunkMeshPool implements ModelCache.MeshPool {
    private Array<Mesh> freeMeshes = new Array<Mesh>();
    private Array<Mesh> usedMeshes = new Array<Mesh>();

    @Override
    public Mesh obtain(VertexAttributes vertexAttributes, int vertexCount, int indexCount) {
        for (int i = 0, n = freeMeshes.size; i < n; ++i) {
            final Mesh mesh = freeMeshes.get(i);
            if (mesh.getVertexAttributes().equals(vertexAttributes) && mesh.getMaxVertices() == vertexCount
                    && mesh.getMaxIndices() == indexCount) {
                freeMeshes.removeIndex(i);
                usedMeshes.add(mesh);
                return mesh;
            }
        }
        Mesh result = new Mesh(true, vertexCount, indexCount, vertexAttributes);
        usedMeshes.add(result);
        return result;
    }

    @Override
    public void flush() {
        freeMeshes.addAll(usedMeshes);
        usedMeshes.clear();
    }

    public void flush(Mesh mesh) {
        usedMeshes.removeValue(mesh, true);

        if (!freeMeshes.contains(mesh, true)) {
            freeMeshes.add(mesh);
        }
    }

    public void clear() {
        freeMeshes.clear();
        usedMeshes.clear();
    }

    @Override
    public void dispose() {
        for (Mesh m : usedMeshes) {
            m.dispose();
        }
        usedMeshes.clear();
        for (Mesh m : freeMeshes) {
            m.dispose();
        }
        freeMeshes.clear();
    }
}
