package com.mygdx.game.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.utils.Array;

/**
 * A pool of mesh objects
 */
public class ChunkMeshPool implements ModelCache.MeshPool {
    private Array<Mesh> freeMeshes = new Array<Mesh>();
    private Array<Mesh> usedMeshes = new Array<Mesh>();

    /**
     * Obtains a mesh from the pool or generates a new one if none are available
     *
     * @param vertexAttributes Vertex attributes of the mesh
     * @param vertexCount      Amount of verticies of the mesh
     * @param indexCount       Amount of indicies of the mesh
     * @return A mesh from the pool or creates a new one if it doesnt exist
     */
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

    /**
     * adds all used meshes to the free meshes pool
     */
    @Override
    public void flush() {
        freeMeshes.addAll(usedMeshes);
        usedMeshes.clear();
    }

    /**
     * Flushes a mesh and adds it to the pool
     */
    public void flush(Mesh mesh) {
        usedMeshes.removeValue(mesh, true);

        if (!freeMeshes.contains(mesh, true)) {
            freeMeshes.add(mesh);
        }
    }

    /**
     * Clears the mesh pool
     */
    public void clear() {
        freeMeshes.clear();
        usedMeshes.clear();
    }

    /**
     * Disposes of the mesh pool and the meshes
     */
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
