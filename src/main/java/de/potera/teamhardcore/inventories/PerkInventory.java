package de.potera.teamhardcore.inventories;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumPerk;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.ItemDefaults;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PerkInventory {

    private static final int[] inventorySlots = new int[]{10, 11, 12, 14, 15, 16};

    public static void openPerkInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "Perks");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        updatePerkInventory(player, inventory);
        player.openInventory(inventory);
    }

    public static void updatePerkInventory(Player player, Inventory inventory) {
        if (!inventory.getTitle().equals(StringDefaults.INVENTORY_PREFIX + "Perks")) return;

        UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();

        for (int i = 0; i < inventorySlots.length; i++) {
            if (i >= EnumPerk.values().length) break;
            EnumPerk perk = EnumPerk.values()[i];
            int slot = inventorySlots[i];

            boolean hasPerk = userData.getOwnedPerks().contains(perk);
            boolean toggledPerk = userData.getToggledPerks().contains(perk);

            List<String> lore = new ArrayList<>(Arrays.asList("§7" + perk.getDescription(), "",
                    "§7Status§8: " + (hasPerk ? toggledPerk ? "§aAktiv" : "§cInaktiv" : "§cNicht freigeschaltet")));

            ItemStack perkItem = new ItemBuilder(perk.getMaterial()).setDurability(perk.getDurability()).setDisplayName(
                    "§a" + perk.getDisplayName()).addItemFlags(ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_POTION_EFFECTS).setLore(lore).build();
            inventory.setItem(slot, perkItem);
        }
    }

}
