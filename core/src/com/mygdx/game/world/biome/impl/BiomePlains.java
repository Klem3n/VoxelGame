package com.mygdx.game.world.biome.impl;

import com.mygdx.game.block.BlockID;
import com.mygdx.game.world.biome.Biome;
import com.mygdx.game.world.biome.BiomeID;

public class BiomePlains extends Biome {
    public BiomePlains() {
        super(BiomeID.PLAINS, "Plains");

        folliageOdds = 30;
        getFolliage().add(BlockID.DANDELION, BlockID.ROSE);
    }
}
