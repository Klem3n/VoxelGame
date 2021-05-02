package com.mygdx.game.world.entity.player.link;

import com.mygdx.game.block.BlockType;

public class Inventory {
    public static final int HOTBAR_SIZE = 10;

    private int selectedIndex = 0;
    private boolean dirty = true;

    private Item[] hotbar = new Item[HOTBAR_SIZE];

    public Inventory() {
        for (int i = 0; i < hotbar.length; i++) {
            hotbar[i] = new Item(i + 1, 1);
        }
    }

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

    public BlockType getSelectedBlock() {
        Item item = hotbar[selectedIndex];

        if (item != null) {
            return item.getBlockType();
        }

        return BlockType.AIR;
    }
}
