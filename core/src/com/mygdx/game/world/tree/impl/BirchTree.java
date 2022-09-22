package com.mygdx.game.world.tree.impl;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.utils.FastNoiseLite;
import com.mygdx.game.world.World;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.tree.Tree;
import com.mygdx.game.world.tree.TreeID;

public class BirchTree extends Tree {
    public BirchTree() {
        super(TreeID.BIRCH);
    }

    @Override
    public void buildTree(Chunk chunk, int x, int y, int z, World world, FastNoiseLite[] randomNoises) {
        FastNoiseLite heightNoise = randomNoises[3];
        FastNoiseLite crownNoise = randomNoises[4];

        int height = heightNoise.GetIntNoise(4, x, z) + 5;
        int crownSize = 2;

        for (int i = -crownSize; i <= crownSize; i++) {
            for (int j = -crownSize; j <= crownSize; j++) {
                if (y + height < 16 && x + i >= 0 && x + i < 16 && z + j >= 0 && z + j < 16) {
                    chunk.setFast(x + i, y + height, z + j, BlockID.OAK_LEAVES);
                } else {
                    Vector3 position = chunk.getPosition().cpy().add(x + i, y + height, z + j);

                    world.setFast(position, BlockID.OAK_LEAVES, false, true);
                }
            }
        }

        for (int i = -crownSize; i <= crownSize; i++) {
            for (int j = -crownSize; j <= crownSize; j++) {
                if (y + height - 1 < 16 && x + i >= 0 && x + i < 16 && z + j >= 0 && z + j < 16) {
                    chunk.setFast(x + i, y + height - 1, z + j, BlockID.OAK_LEAVES);
                } else {
                    Vector3 position = chunk.getPosition().cpy().add(x + i, y + height - 1, z + j);

                    world.setFast(position, BlockID.OAK_LEAVES, false, true);
                }
            }
        }

        for (int i = -crownSize + 1; i <= crownSize - 1; i++) {
            for (int j = -crownSize + 1; j <= crownSize - 1; j++) {
                if (y + height + 1 < 16 && x + i >= 0 && x + i < 16 && z + j >= 0 && z + j < 16) {
                    chunk.setFast(x + i, y + height + 1, z + j, BlockID.OAK_LEAVES);
                } else {
                    Vector3 position = chunk.getPosition().cpy().add(x + i, y + height + 1, z + j);

                    world.setFast(position, BlockID.OAK_LEAVES, false, true);
                }
            }
        }

        for (int i = 0; i < height; i++) {
            if (y + i < 16) {
                chunk.setFast(x, y + i, z, BlockID.BIRCH_WOOD);
            } else {
                Vector3 position = chunk.getPosition().cpy().add(x, y + i, z);

                world.setFast(position, BlockID.BIRCH_WOOD, false, true);
            }
        }
    }
}
