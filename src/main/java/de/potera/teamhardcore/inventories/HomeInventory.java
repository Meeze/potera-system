package de.potera.teamhardcore.inventories;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Home;
import de.potera.teamhardcore.users.UserHomes;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.ItemDefaults;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HomeInventory {

    private static Integer[] slots = {28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

    public static void openHomeInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 6, StringDefaults.INVENTORY_PREFIX + "Homes");

        UserHomes userHomes = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserHomes();
        List<Home> homes = new ArrayList<>(userHomes.getHomes().values());

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        inventory.setItem(13, new ItemBuilder(Material.WORKBENCH).setDisplayName("§7Übersicht deiner §cHomes").build());


        for (int i = 0; i < slots.length; i++) {
            int slot = slots[i];
            if (homes.isEmpty()) {
                inventory.setItem(slot,
                        new ItemBuilder(Material.STAINED_CLAY).setDisplayName("§7Kein Home gesetzt").setDurability(
                                14).build());
                continue;
            }

            ItemStack displayItem = (i < homes.size()) ? new ItemBuilder(Material.STAINED_CLAY).setDurability(5)
                    .setDisplayName("§7Home: §c" + homes.get(i).getName())
                    .setLore("", "§7[Linksklick] : §eTeleportation starten").build()
                    : new ItemBuilder(Material.STAINED_CLAY).setDisplayName("§7Kein Home gesetzt")
                    .setDurability(14).build();

            inventory.setItem(slot, displayItem);
        }
        player.openInventory(inventory);
    }

    public static int getHomeIndexBySlot(int clicked) {
        if (clicked >= 28 && clicked <= 34)
            return clicked - 28;
        else if (clicked >= 37 && clicked <= 43)
            return clicked - 30;
        return -1;
    }

}
