package de.potera.teamhardcore.inventories;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumSettings;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsInventory {

    public static void openSettingsInventory(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "Settings");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        inventory.setItem(11, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("§aCrate Animation").build());
        inventory.setItem(12,
                new ItemBuilder(Material.DIAMOND_SWORD).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(
                        "§aTodesnachrichten").build());
        inventory.setItem(13, new ItemBuilder(Material.ITEM_FRAME).setDisplayName("§aTradeanfragen").build());
        inventory.setItem(14, new ItemBuilder(Material.COMPASS).setDisplayName("§aTeleportanfragen").build());
        inventory.setItem(15, new ItemBuilder(Material.PAPER).setDisplayName("§aPrivatnachrichten").build());

        updateSettingsInventory(player, inventory);
        player.openInventory(inventory);
    }

    public static void updateSettingsInventory(Player player, Inventory inventory) {
        if (!inventory.getTitle().equals(StringDefaults.INVENTORY_PREFIX + "Settings")) {
            return;
        }

        UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();
        int optionCrateAnimation = userData.getSettingsOption(EnumSettings.CRATE_ANIMATION);
        int optionDeathMessage = userData.getSettingsOption(EnumSettings.DEATH_MSG);
        int optionTradeRequests = userData.getSettingsOption(EnumSettings.TRADE_REQUESTS);
        int optionTpRequests = userData.getSettingsOption(EnumSettings.TP_REQUESTS);
        int optionPrivateMessage = userData.getSettingsOption(EnumSettings.PRIVATE_MESSAGE);

        List<String> lore = new ArrayList<>(Arrays.asList("§7Aktiviere oder deaktiviere deine Crate Animation.", "",
                "§7Status§8: §a" + EnumSettings.CRATE_ANIMATION.getOption(
                        optionCrateAnimation)));

        ItemStack item = inventory.getItem(11);
        ItemMeta meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        lore.clear();

        lore.addAll(Arrays.asList("§7Wähle aus, ob du die", "§7Todesnachrichten im Chat sehen willst.", "",
                "§7Status§8: §a" + EnumSettings.DEATH_MSG.getOption(
                        optionDeathMessage)));
        item = inventory.getItem(12);
        meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        lore.clear();

        lore.addAll(Arrays.asList("§7Wähle aus, ob dir Spieler", "§7Tradeanfragen schicken können.", "",
                "§7Status§8: §a" + EnumSettings.TRADE_REQUESTS.getOption(
                        optionTradeRequests)));
        item = inventory.getItem(13);
        meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        lore.clear();

        lore.addAll(Arrays.asList("§7Wähle aus, ob du Teleportanfragen erhalten willst.", "",
                "§7Status§8: §a" + EnumSettings.TP_REQUESTS.getOption(
                        optionTpRequests)));
        item = inventory.getItem(14);
        meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        lore.clear();

        lore.addAll(Arrays.asList("§7Wähle aus, ob du Privatnachrichten erhalten willst.", "",
                "§7Status§8: §a" + EnumSettings.PRIVATE_MESSAGE.getOption(
                        optionPrivateMessage)));
        item = inventory.getItem(15);
        meta = item.getItemMeta();
        meta.setLore(lore);
        item.setItemMeta(meta);
        lore.clear();

    }

}
