package de.potera.teamhardcore.others.crates;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ContentPiece {

    private final int chanceWeight;
    private final ItemStack displayItem;

    public ContentPiece(int chanceWeight, ItemStack displayItem) {
        this.chanceWeight = chanceWeight;
        this.displayItem = displayItem;
    }

    public int getChanceWeight() {
        return chanceWeight;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public abstract void onWin(Player player);

}
