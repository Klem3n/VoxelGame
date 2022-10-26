package com.mygdx.game.world.tree;

import com.mygdx.game.utils.FastNoiseLite;
import com.mygdx.game.world.World;
import com.mygdx.game.world.chunk.Chunk;

/**
 * Abstract class of a tree in the world
 */
public abstract class Tree {

    private final int id;

    public Tree(int id) {
        this.id = id;
    }

    /**
     * Method that generates the tree in the chunk
     */
    public abstract void buildTree(Chunk chunk, int x, int y, int z, World world, FastNoiseLite[] randomNoises);

    public int getId() {
        return id;
    }
}
