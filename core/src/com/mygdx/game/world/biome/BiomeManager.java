package com.mygdx.game.world.biome;

import com.google.common.reflect.ClassPath;
import com.mygdx.game.world.tree.TreeManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps biome IDs to the corresponding biome object
 */
public class BiomeManager {
    /**
     * Biome map, mapped by their ids
     */
    private static final Map<Integer, Biome> biomeMap = new HashMap<>();

    /**
     * Gets a biome by their ID
     *
     * @param id The ID of the biome
     */
    public static Biome getById(int id) {
        return biomeMap.get(id);
    }

    /**
     * Registers a new biome, checks if it already exists and adds it to the map
     *
     * @param biome The biome to register
     */
    private static void registerBiome(Biome biome) {
        if (biomeMap.containsKey(biome.getId())) {
            System.err.println("Duplicate biome id: " + biome.getId());
            return;
        }

        biomeMap.put(biome.getId(), biome);
    }

    /**
     * Initializes the biome manager and dynamically loads all biomes from {@link com.mygdx.game.world.biome.impl} package
     */
    public static void init() {
        try {
            ClassPath classPath = ClassPath.from(TreeManager.class.getClassLoader());

            for (ClassPath.ClassInfo topLevelClass : classPath.getTopLevelClasses("com.mygdx.game.world.biome.impl")) {
                Class<?> clazz = topLevelClass.load();

                if (Biome.class.isAssignableFrom(clazz)) {
                    registerBiome((Biome) clazz.getConstructors()[0].newInstance());
                }
            }

            System.out.println("INFO: loaded " + biomeMap.size() + " biomes.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
