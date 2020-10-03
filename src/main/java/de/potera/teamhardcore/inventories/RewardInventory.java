package de.potera.teamhardcore.inventories;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.ItemDefaults;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class RewardInventory {

    public static void openDailyInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 3,
                StringDefaults.INVENTORY_PREFIX + "Reward");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();
        Material material = (userData.hasDailyReward() ? Material.STORAGE_MINECART : Material.MINECART);

        ItemStack item = new ItemBuilder(material).setDisplayName(
                "§eTägliche Belohnung").setLore("", "§7[Linksklick] : §eBelohnung abholen").build();

        inventory.setItem(13, item);

        player.openInventory(inventory);
    }

    public static void openPvPShopInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 6, StringDefaults.INVENTORY_PREFIX + "PvP Shop");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        inventory.setItem(20, new ItemBuilder(Material.BOOK).setDisplayName("§6Rangupgrade").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(21, new ItemBuilder(Material.PAPER).setDisplayName("§6Perk Gutschein").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(22, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("§6XX Crate").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(23, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("§6XY Crate").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(24, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("§6XZ Crate").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(29, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("§eZeit Gutschein").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(30, new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("§eVerzaubertes Buch").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(31, new ItemBuilder(Material.EXP_BOTTLE).setDisplayName("§aAMS Boost").setLore("",
                "§7Preis§8: §a250 PvP Punkte").build());
        inventory.setItem(32,
                new ItemBuilder(Material.DIAMOND_SWORD).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(
                        "§cStatTrak Schwert").setLore("",
                        "§7Preis§8: §a250 PvP Punkte").build());

        player.openInventory(inventory);
    }

}
