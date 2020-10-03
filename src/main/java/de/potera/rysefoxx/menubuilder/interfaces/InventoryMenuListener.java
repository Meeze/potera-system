package de.potera.rysefoxx.menubuilder.interfaces;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface InventoryMenuListener {

    /**
     * Called when a player clicks the inventory
     *
     * @param player {@link Player} who clicked
     * @param action the {@link ClickType} performed
     * @param slot   the clicked slot
     */
    void interact(Player player, ClickType action, int slot);
}
