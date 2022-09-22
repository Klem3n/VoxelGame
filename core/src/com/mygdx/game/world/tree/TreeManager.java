package com.mygdx.game.world.tree;

import com.google.common.reflect.ClassPath;

import java.util.HashMap;
import java.util.Map;

public class TreeManager {
    private static final Map<Integer, Tree> treeMap = new HashMap<>();

    public static Tree getById(int id) {
        return treeMap.get(id);
    }

    private static void registerTree(Tree tree) {
        if (treeMap.containsKey(tree.getId())) {
            System.err.println("Duplicate tree id: " + tree.getId());
            return;
        }

        treeMap.put(tree.getId(), tree);
    }

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
