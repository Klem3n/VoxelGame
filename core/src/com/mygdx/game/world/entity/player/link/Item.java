package com.mygdx.game.world.entity.player.link;

import com.mygdx.game.block.Block;
import com.mygdx.game.block.BlockManager;

public class Item {
    private int id;
    private int amount;

    public Item(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Block getBlock() {
        return BlockManager.getById(id);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", amount=" + amount +
                '}';
    }
}
