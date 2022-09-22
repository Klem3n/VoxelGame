package com.mygdx.game.world.tree.impl;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.block.BlockID;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.utils.FastNoiseLite;
import com.mygdx.game.world.World;
import com.mygdx.game.world.chunk.Chunk;
import com.mygdx.game.world.tree.Tree;
import com.mygdx.game.world.tree.TreeID;

import static com.mygdx.game.utils.Constants.*;

public class SpruceTree extends Tree {
    public SpruceTree() {
        super(TreeID.SPRUCE);
    }

    @Override
    public void buildTree(Chunk chunk, int x, int y, int z, World world, FastNoiseLite[] randomNoises) {
        /*FastNoiseLite heightNoise = randomNoises[3];
        FastNoiseLite crownNoise = randomNoises[4];

        int height = heightNoise.GetIntNoise(12, x, z) + 5;
        
        for (int i = 0; i < height; i++) {
            setBlock(chunk, BlockID.SPRUCE_WOOD, x, y + i, z, world);
        }

        int ringSize = 1;

        for (int i = height; i >= 4; i--) {
            for (int a = 0; a < 360; a += 1) {
                for (int j = 0; j < ringSize; j++) {
                    int leafX = (int) (j * Math.cos(a * Math.PI / 180));
                    int leafZ = (int) (j * Math.sin(a * Math.PI / 180));

                    setBlock(chunk, BlockID.SPRUCE_LEAVES, x + leafX, y + i, z + leafZ, world);
                }
            }

            if (i % 3 == 0) {
                ringSize++;
            }

            setBlock(chunk, BlockID.SPRUCE_LEAVES, x, y + i, z, world);
        }*/

        FastNoiseLite heightNoise = randomNoises[3];
        FastNoiseLite crownNoise = randomNoises[4];

        int height = heightNoise.GetIntNoise(10, x, z) + 4;

        for (int d = 0; d <= height + 2; d++) {
            float radius = 4f * (1 - ((float) d) / (height)) + 1.5f;

            if (d <= height) {
                setBlock(chunk, BlockID.SPRUCE_WOOD, x, y + d, z, world);
            } else {
                setBlock(chunk, BlockID.SPRUCE_LEAVES, x, y + d, z, world);
            }

            if (d > 3) {
                for (int i = -Constants.ceil(radius); i <= Constants.ceil(radius); i++) {
                    for (int j = -Constants.ceil(radius); j <= Constants.ceil(radius); j++) {
                        if (i * i + j * j <= radius * radius && crownNoise.GetIntNoise(3, x + i, y + j) < 2) {
                            setBlock(chunk, BlockID.SPRUCE_LEAVES, x + i, y + d, z + j, world);
                        }
                    }
                }
            }
        }
    }

    private void setBlock(Chunk chunk, int blockId, int x, int y, int z, World world) {
        if (y < CHUNK_SIZE_Y && x < CHUNK_SIZE_X && z < CHUNK_SIZE_Z) {
            chunk.setFast(x, y, z, blockId);
        } else {
            Vector3 position = chunk.getPosition().cpy().add(x, y, z);

            world.setFast(position, blockId, false, true);
        }
    }
}
