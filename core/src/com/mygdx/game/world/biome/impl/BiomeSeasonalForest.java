package com.mygdx.game.world.biome.impl;

import com.mygdx.game.block.BlockID;
import com.mygdx.game.world.biome.Biome;
import com.mygdx.game.world.biome.BiomeID;
import com.mygdx.game.world.tree.TreeID;

public class BiomeSeasonalForest extends Biome {
    public BiomeSeasonalForest() {
        super(BiomeID.TEMPERATURE_SEASONAL_FOREST, "Seasonal forest");

        folliageOdds = 10;
        treeOdds = 100;

        getFolliage().addAll(BlockID.DANDELION, BlockID.ROSE, BlockID.BUSH, BlockID.BUSH, BlockID.TALL_GRASS, BlockID.TALL_GRASS, BlockID.TALL_GRASS, BlockID.TALL_GRASS);
        getTrees().add(TreeID.BIRCH, TreeID.OAK);
    }
}
