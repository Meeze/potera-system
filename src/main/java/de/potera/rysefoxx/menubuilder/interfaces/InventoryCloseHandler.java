package de.potera.rysefoxx.menubuilder.interfaces;

import org.bukkit.event.inventory.InventoryCloseEvent;

public interface InventoryCloseHandler {
    /**
     * Called when a {@link InventoryCloseEvent} is called on this inventory
     *
     * @param event the {@link InventoryCloseEvent}
     */
    void handle(InventoryCloseEvent event);


}
