package com.mygdx.game.world.entity.player.link;

import com.mygdx.game.block.Block;

/**
 * Represents the players inventory
 */
public class Inventory {
    /**
     * Static variable that represents the maximum amount of items in the players inventory
     */
    public static final int HOTBAR_SIZE = 10;

    /**
     * The index of the currently selected block in the inventory
     */
    private int selectedIndex = 0;
    /**
     * Variable that determines if the inventory should be redrawn
     */
    private boolean dirty = true;
    /**
     * Array of items in the players inventory
     */
    private Item[] hotbar = new Item[HOTBAR_SIZE];

    public Inventory() {
        for (int i = 0; i < hotbar.length; i++) {
            hotbar[i] = new Item(i + 4, 1);
        }
    }

    /**
     * Gets an item from the players inventory by the slot
     *
     * @param slot The inventory slot
     */
    public Item getHotbarItem(int slot) {
        if (slot < 0 || slot >= HOTBAR_SIZE) {
            return null;
        }

        return hotbar[slot];
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;

        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public Block getSelectedBlock() {
        Item item = hotbar[selectedIndex];

        if (item != null) {
            return item.getBlock();
        }

        return Block.AIR;
    }
}
