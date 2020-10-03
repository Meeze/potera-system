package de.potera.rysefoxx.menubuilder.interfaces;

import org.bukkit.event.inventory.InventoryClickEvent;

public interface InventoryEventHandler {


    /**
     * Called when a {@link InventoryClickEvent} is called on this inventory
     *
     * @param event the {@link InventoryClickEvent}
     */
    void handle(InventoryClickEvent event);


}
