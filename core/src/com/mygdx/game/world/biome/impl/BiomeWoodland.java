package com.mygdx.game.world.biome.impl;

import com.mygdx.game.block.BlockID;
import com.mygdx.game.world.biome.Biome;
import com.mygdx.game.world.biome.BiomeID;
import com.mygdx.game.world.tree.TreeID;

public class BiomeWoodland extends Biome {
    public BiomeWoodland() {
        super(BiomeID.WOODLAND, "Woodland");

        folliageOdds = 10;
        treeOdds = 100;

        getFolliage().addAll(BlockID.DANDELION, BlockID.ROSE, BlockID.TALL_GRASS, BlockID.TALL_GRASS, BlockID.TALL_GRASS,
                BlockID.TALL_GRASS, BlockID.TALL_GRASS, BlockID.TALL_GRASS, BlockID.TALL_GRASS);
        getTrees().add(TreeID.BIRCH, TreeID.OAK);
    }
}
