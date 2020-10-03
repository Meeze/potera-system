package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.ShopItem;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.ItemDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopManager {

    private final Map<ShopItem.Category, List<ShopItem>> shopItems;
    private final Map<ShopItem.Category, Inventory> inventories;
    private Inventory mainInventory;

    public ShopManager() {
        this.shopItems = new HashMap<>();
        this.inventories = new HashMap<>();

        loadItems();
        initInventories();
    }

    private void loadItems() {
        FileConfiguration cfg = Main.getInstance().getFileManager().getShopFile().getConfig();
        for (String catStr : cfg.getConfigurationSection("").getKeys(false)) {
            ShopItem.Category category = ShopItem.Category.getByName(catStr);
            if (category == null)
                continue;
            List<ShopItem> items = new ArrayList<>();
            for (String itemStr : cfg.getStringList(catStr)) {
                try {
                    String[] dataSpl = itemStr.split("@");
                    ShopItem.Type type = ShopItem.Type.getByName(dataSpl[0]);
                    if (type == null)
                        continue;
                    Material material = Material.matchMaterial(dataSpl[1]);
                    short data = Short.parseShort(dataSpl[2]);
                    long price = Long.parseLong(dataSpl[3]);
                    String displayname = dataSpl[4];
                    String inventoryname = ChatColor.translateAlternateColorCodes('&', dataSpl[5]);
                    ShopItem shopItem = new ShopItem(category, type, material, data, price, displayname, inventoryname);
                    items.add(shopItem);
                } catch (Exception e) {
                    System.out.println(itemStr);
                    e.printStackTrace();
                }
            }
            this.shopItems.put(category, items);
        }
    }

    private void initMainInventory() {
        this.mainInventory = Bukkit.createInventory(null, 45, "§c§lShop");

        for (int i = 0; i < this.mainInventory.getSize(); i++)
            this.mainInventory.setItem(i, ItemDefaults.PLACEHOLDER);

        this.mainInventory.setItem(11, ShopItem.Category.BLOCKS.getDisplayItem());
        this.mainInventory.setItem(13, ShopItem.Category.MISC.getDisplayItem());
        this.mainInventory.setItem(15, ShopItem.Category.REDSTONE.getDisplayItem());
        this.mainInventory.setItem(19, ShopItem.Category.TOOLS.getDisplayItem());
        this.mainInventory.setItem(25, ShopItem.Category.DECORATION.getDisplayItem());
        this.mainInventory.setItem(29, ShopItem.Category.FOOD.getDisplayItem());
        this.mainInventory.setItem(31, ShopItem.Category.SELL.getDisplayItem());
        this.mainInventory.setItem(33, ShopItem.Category.BREWING.getDisplayItem());
    }

    private void initInventories() {
        initMainInventory();

        ItemStack back = new ItemBuilder(Material.WOOD_DOOR).setDisplayName("§c§lZurück zur Übersicht").build();

        for (ShopItem.Category category : ShopItem.Category.values()) {
            List<ShopItem> items = this.shopItems.get(category);
            int lines = (items != null) ? (int) Math.ceil((items.size() / 6)) : 6;
            lines = (lines <= 2) ? 3 : lines;

            Inventory inventory = Bukkit.createInventory(null, lines * 9,
                    "§c§lShop (" + category.getDisplayname() + ")");

            for (int i = 0; i < inventory.getSize(); i++)
                inventory.setItem(i, ItemDefaults.PLACEHOLDER);

            int count = 0;
            inventory.setItem(0, category.getDisplayItem());
            inventory.setItem(inventory.getSize() - 9, back);

            if (items != null) {
                for (int row = 0; row < 6; row++) {
                    for (int column = 0; column < 7; column++) {
                        int slot = 2 + row * 9 + column;

                        if (items.size() - 1 >= count) {
                            ShopItem item = items.get(count);

                            boolean isBuy = item.getType() == ShopItem.Type.BUY;
                            List<String> lore = new ArrayList<>();
                            lore.add("§c" + (isBuy ? "Kaufbar" : "Verkaufbar") + " für §7" + Util.formatNumber(
                                    item.getPrice()) + "$ / Stück");
                            lore.add(" ");
                            for (int i = 0; i < 3; i++) {
                                int multiplier = (i == 0) ? 1 : ((i == 1) ? 16 : 64);
                                long prize = multiplier * item.getPrice();
                                lore.add(
                                        "§8■ §a" + multiplier + "x " + (isBuy ? "Kaufen" : "Verkaufen") + ": §7" + Util.formatNumber(
                                                prize) + "§8§o(§7§o" + ((i == 0) ? "Linksklick" : ((i == 1) ? "Rechtsklick" : "Shiftklick")) + "§8§o)");
                            }
                            ItemStack displayItem = new ItemBuilder(item.getMaterial()).setDurability(
                                    item.getData()).setDisplayName(item.getInventoryname()).setLore(lore).build();
                            inventory.setItem(slot, displayItem);
                        }
                        count++;
                    }
                }
            }
            this.inventories.put(category, inventory);
        }
    }

    public void openCategoryInventory(Player p, ShopItem.Category category) {
        if (!this.inventories.containsKey(category))
            return;
        p.openInventory(this.inventories.get(category));
    }

    public void openMainInventory(Player p) {
        p.openInventory(this.mainInventory);
    }

    public Map<ShopItem.Category, List<ShopItem>> getShopItems() {
        return this.shopItems;
    }


}
