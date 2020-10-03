package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Warp;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.ItemDefaults;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

public class WarpManager {

    private Inventory mainInventory;

    public WarpManager() {
        registerMainInventory();
    }

    private void registerMainInventory() {
        this.mainInventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "Warps");

        for (int i = 0; i < this.mainInventory.getSize(); i++)
            this.mainInventory.setItem(i, ItemDefaults.PLACEHOLDER);

        this.mainInventory.setItem(10,
                new ItemBuilder(Material.STAINED_CLAY).setDurability(9).setDisplayName("§7Warp§8: §cFPS")
                        .setLore("§7Teleportiert dich zum Warp FPS", "",
                                "§7[Linksklick] : §eTeleportation starten").build());
        this.mainInventory.setItem(12,
                new ItemBuilder(Material.NETHER_BRICK).setDisplayName("§7Warp§8: §cNether")
                        .setLore("§7Teleportiert dich zum Warp Nether", "",
                                "§7[Linksklick] : §eTeleportation starten").build());
        this.mainInventory.setItem(14,
                new ItemBuilder(Material.DOUBLE_PLANT).setDisplayName("§7Warp§8: §cCasino")
                        .setLore("§7Teleportiert dich zum Warp Casino", "",
                                "§7[Linksklick] : §eTeleportation starten").build());
        this.mainInventory.setItem(16,
                new ItemBuilder(Material.ENCHANTMENT_TABLE).setDisplayName("§7Warp§8: §cEnchanter")
                        .setLore("§7Teleportiert dich zum Warp Enchanter", "",
                                "§7[Linksklick] : §eTeleportation starten").build());

    }

    public void openMainInventory(Player player) {
        player.openInventory(mainInventory);
    }

    public Warp getWarp(String name) {
        return Main.getInstance().getFileManager().getWarpFile().getWarp(name);
    }

    public void addWarp(String warpName, Location location) {
        Main.getInstance().getFileManager().getWarpFile().addWarp(warpName, location);
    }

    public void removeWarp(String warpName) {
        Main.getInstance().getFileManager().getWarpFile().removeWarp(warpName);
    }

    public Inventory getMainInventory() {
        return mainInventory;
    }

    public Map<String, Warp> getWarps() {
        return Main.getInstance().getFileManager().getWarpFile().getWarps();
    }


}
