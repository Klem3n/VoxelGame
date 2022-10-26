package com.mygdx.game.block;

import com.google.common.reflect.ClassPath;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the blocks and maps them to {@link Block} objects by ids
 */
public class BlockManager {
    /**
     * Static map of block objects mapped by their ids
     */
    private static final Map<Integer, Block> blockMap = new HashMap<>();

    /**
     * Gets a Block by their integer ID
     *
     * @param id The ID of the block
     */
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

    /**
     * Initializes the block manager and dynamically loads all blocks from {@link com.mygdx.game.block.impl} package
     */
    public static void init() {
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
