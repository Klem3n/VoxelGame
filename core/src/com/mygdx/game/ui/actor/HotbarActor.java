package com.mygdx.game.ui.actor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.world.entity.player.link.Inventory;

/**
 * HUD component for the inventory hotbar
 */
public class HotbarActor extends Table {
    private final AssetManager assets;

    private InventorySlotActor[] slots;

    public HotbarActor(AssetManager assets) {
        this.assets = assets;

        setFillParent(true);
        center();
        bottom();

        slots = new InventorySlotActor[Inventory.HOTBAR_SIZE];

        for (int i = 0; i < Inventory.HOTBAR_SIZE; i++) {
            add((slots[i] = new InventorySlotActor(assets)));
        }
    }

    public void update(Inventory inventory) {
        int selected = inventory.getSelectedIndex();

        for (int i = 0; i < slots.length; i++) {
            if (slots[i].isSelected()) {
                slots[i].setSelected(false);
            }

            slots[i].setItem(inventory.getHotbarItem(i));
        }

        slots[selected].setSelected(true);
    }
}
