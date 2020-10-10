package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.others.gamble.roulette.RouletteGame;
import de.potera.teamhardcore.others.gamble.roulette.RouletteSetup;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.ItemDefaults;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouletteManager {

    private final Map<Player, RouletteSetup> buildingRoulettes;
    private RouletteGame rouletteGame;

    public RouletteManager() {
        this.buildingRoulettes = new HashMap<>();
        this.rouletteGame = null;
    }

    public void startRouletteGame() {
        if (this.rouletteGame != null) return;

        this.rouletteGame = new RouletteGame();
        this.rouletteGame.gotoPhase(0);

        Bukkit.broadcastMessage(StringDefaults.ROLL_PREFIX + "§7Eine neue §a§lX-Roulette §7Runde ist gestartet!");
    }

    public void stopRouletteGame() {
        this.rouletteGame.cancelTask();
        this.rouletteGame = null;
    }

    public void openGUI(Player player, boolean anvil) {
        if (this.rouletteGame == null) return;

        Inventory inventory = Bukkit.createInventory(null, 9 * 6, StringDefaults.INVENTORY_PREFIX + "Roulette Einsatz");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        inventory.setItem(21, new ItemBuilder(Material.STONE_BUTTON).setDisplayName("§cNiedriger").build());
        inventory.setItem(23, new ItemBuilder(Material.STONE_BUTTON).setDisplayName("§aHöher").build());

        inventory.setItem(30, new ItemBuilder(Material.STONE_BUTTON).setDisplayName("§cNiedriger").build());
        inventory.setItem(32, new ItemBuilder(Material.STONE_BUTTON).setDisplayName("§aHöher").build());

        inventory.setItem(22, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("§aEinsatz").setSkullOwner(
                player.getName()).setDurability(3).build());
        inventory.setItem(31, new ItemBuilder(Material.SKULL_ITEM).setDisplayName("§aMultiplier").setSkullOwner(
                player.getName()).setDurability(3).build());

        inventory.setItem(43,
                new ItemBuilder(Material.STAINED_CLAY).setDurability(13).setDisplayName("§aBestätigen").build());

        if (!anvil) {
            RouletteSetup setup = new RouletteSetup(player);
            this.buildingRoulettes.put(player, setup);
        }

        updateRouletteBuilder(player, inventory);
        player.openInventory(inventory);
    }

    public void updateRouletteBuilder(Player player, Inventory inventory) {
        if (!this.buildingRoulettes.containsKey(player)) return;
        if (!inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Roulette Einsatz")) return;

        RouletteSetup rouletteSetup = this.buildingRoulettes.get(player);

        ItemStack entry = inventory.getItem(22);
        ItemMeta entryMeta = entry.getItemMeta();
        List<String> entryLore = new ArrayList<>();

        entryLore.add("§7Dein Einsatz§8: ");
        entryLore.add("");
        entryLore.add("§e" + Util.formatNumber(rouletteSetup.getEntry()) + "$");
        entryMeta.setLore(entryLore);
        entry.setItemMeta(entryMeta);

        ItemStack multiplier = inventory.getItem(31);
        ItemMeta multiplierMeta = multiplier.getItemMeta();
        List<String> multiplierLore = new ArrayList<>();

        multiplierLore.add("§7Wähle deinen Multiplier§8: ");
        multiplierLore.add("");
        multiplierLore.add("§a§lx" + rouletteSetup.getMultiplier());
        multiplierMeta.setLore(multiplierLore);
        multiplier.setItemMeta(multiplierMeta);
    }

    public Map<Player, RouletteSetup> getBuildingRoulettes() {
        return buildingRoulettes;
    }

    public RouletteGame getRouletteGame() {
        return rouletteGame;
    }
}
