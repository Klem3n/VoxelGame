package com.mygdx.game.world.biome.impl;

import com.mygdx.game.block.BlockID;
import com.mygdx.game.world.biome.Biome;
import com.mygdx.game.world.biome.BiomeID;
import com.mygdx.game.world.tree.TreeID;

public class BiomeBorealForest extends Biome {
    public BiomeBorealForest() {
        super(BiomeID.BOREAL_FOREST, "Boreal forest");


        folliageOdds = 10;
        treeOdds = 150;

        getFolliage().addAll(BlockID.TALL_GRASS);
        getTrees().add(TreeID.SPRUCE);
    }
}
