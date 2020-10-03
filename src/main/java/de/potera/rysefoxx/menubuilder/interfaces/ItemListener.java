package de.potera.rysefoxx.menubuilder.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public interface ItemListener {

    /**
     * Called when a player clicks the item
     *
     * @param player {@link Player} who clicked the item
     * @param action the {@link ClickType} performed
     * @param item   the clicked {@link ItemStack}
     */
    void onInteract(Player player, ClickType action, ItemStack item);

}
