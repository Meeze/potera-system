package de.potera.rysefoxx.menubuilder.interfaces;

import org.bukkit.inventory.ItemStack;

/**
 * Callback to register an {@link ItemStack}
 */
public interface ItemCallback {

    /**
     * @return the slot of the Item
     */
    int getSlot();

    /**
     * @return the {@link ItemStack} to set
     */
    ItemStack getItem();

}
