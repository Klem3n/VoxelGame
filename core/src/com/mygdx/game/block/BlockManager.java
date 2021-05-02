package com.mygdx.game.block;

import com.google.common.reflect.ClassPath;

import java.util.HashMap;
import java.util.Map;

public class BlockManager {
    private static final Map<Integer, Block> blockMap = new HashMap<>();

    public static Block getById(int id) {
        return blockMap.getOrDefault(id, Block.AIR);
    }

    private static void registerBlock(Block block) {
        if (blockMap.containsKey(block.getId())) {
            System.err.println("Duplicate block id: " + block.getId());
            return;
        }

        if (block.getId() == 0) {
            Block.AIR = block;
        }

        blockMap.put(block.getId(), block);
    }

    static {
        try {
            ClassPath classPath = ClassPath.from(BlockManager.class.getClassLoader());

            for (ClassPath.ClassInfo topLevelClass : classPath.getTopLevelClasses("com.mygdx.game.block.impl")) {
                Class<?> clazz = topLevelClass.load();

                if (Block.class.isAssignableFrom(clazz)) {
                    registerBlock((Block) clazz.getConstructors()[0].newInstance());
                }
            }

            System.out.println("INFO: loaded " + blockMap.size() + " blocks.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
