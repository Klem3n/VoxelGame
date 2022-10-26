package com.mygdx.game.world.tree;

import com.google.common.reflect.ClassPath;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the trees and maps them to {@link Tree} objects by ids
 */
public class TreeManager {
    /**
     * Map of all available trees in the world by ids
     */
    private static final Map<Integer, Tree> treeMap = new HashMap<>();

    /**
     * Gets a tree by their integer ID
     *
     * @param id The ID of the tree
     */
    public static Tree getById(int id) {
        return treeMap.get(id);
    }

    /**
     * Registers the tree and adds it to the tree map
     *
     * @param tree The tree to register
     */
    private static void registerTree(Tree tree) {
        if (treeMap.containsKey(tree.getId())) {
            System.err.println("Duplicate tree id: " + tree.getId());
            return;
        }

        treeMap.put(tree.getId(), tree);
    }

    /**
     * Initializes the tree manager and dynamically loads all trees from {@link com.mygdx.game.world.tree.impl} package
     */
    public static void init() {
        try {
            ClassPath classPath = ClassPath.from(TreeManager.class.getClassLoader());

            for (ClassPath.ClassInfo topLevelClass : classPath.getTopLevelClasses("com.mygdx.game.world.tree.impl")) {
                Class<?> clazz = topLevelClass.load();

                if (Tree.class.isAssignableFrom(clazz)) {
                    registerTree((Tree) clazz.getConstructors()[0].newInstance());
                }
            }

            System.out.println("INFO: loaded " + treeMap.size() + " trees.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
