package com.mygdx.game.ui.actor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.assets.AssetDescriptors;
import com.mygdx.game.utils.Constants;
import com.mygdx.game.world.entity.player.link.Item;

/**
 * HUD component for the inventory slot image
 */
public class InventorySlotActor extends Image {

    boolean selected = false;

    private final Texture hotbarTexture;
    private final Texture selectedTexture;

    private Item item;

    private Image itemImage;

    public InventorySlotActor(AssetManager assets) {
        super(assets.get(AssetDescriptors.HOTBAR));

        hotbarTexture = assets.get(AssetDescriptors.HOTBAR);
        selectedTexture = assets.get(AssetDescriptors.HOTBAR_SELECTED);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        if (itemImage != null) {
            itemImage.setBounds(getX() + 6, getY() + 6, getWidth() - 12, getHeight() - 12);
            itemImage.draw(batch, parentAlpha);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        if (selected) {
            setDrawable(new TextureRegionDrawable(new TextureRegion(selectedTexture)));
        } else {
            setDrawable(new TextureRegionDrawable(new TextureRegion(hotbarTexture)));
        }
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;

        if (item != null) {
            int topTexture = item.getBlock().getInventoryTexture();
            TextureRegion[][] tiles = Constants.TEXTURE_TILES;

            if (topTexture >= 0 && topTexture <= tiles.length * tiles[0].length) {
                itemImage = new Image(tiles[topTexture / tiles.length][topTexture % tiles[0].length]);
            }
        } else {
            itemImage = null;
        }
    }
}
