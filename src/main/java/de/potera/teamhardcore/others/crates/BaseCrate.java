package de.potera.teamhardcore.others.crates;

import de.potera.teamhardcore.files.FileBase;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BaseCrate extends FileBase {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.###");

    private final CrateAddon addon;

    private Inventory inventory;
    private boolean disabled = false;

    public BaseCrate(CrateAddon addon) {
        super(File.separator + "crates" + File.separator + "data", addon.getName());

        this.addon = addon;
        setDefaults();
        loadData();
        loadContentInventory();
    }

    private void setDefaults() {
        FileConfiguration cfg = getConfig();
        cfg.addDefault("Disabled", Boolean.FALSE);
        cfg.options().copyDefaults(true);
        saveConfig();
    }

    private void loadData() {
        FileConfiguration cfg = getConfig();
        if (cfg.get("") == null) return;

        this.disabled = cfg.getBoolean("Disabled");
    }

    private void loadContentInventory() {
        int lines = Math.max(Math.min((int) Math.ceil(this.addon.getCrateContent().size() / 9.0D), 6), 1);
        this.inventory = Bukkit.createInventory(null, 9 * lines, StringDefaults.INVENTORY_PREFIX + "Crate-Inhalt");
        List<ContentPiece> contents = new ArrayList<>(this.addon.getCrateContent());
        //todo: sort

        int count = 0;
        for (ContentPiece content : contents) {
            if (count >= this.inventory.getSize()) break;

            ItemStack displayItem = content.getDisplayItem().clone();
            ItemMeta meta = displayItem.getItemMeta();
            List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
            lore.add(" ");
            lore.add("§7Wert§8: §6-");
            lore.add("§7Chance§8: §a" + DECIMAL_FORMAT.format(this.addon.getPercentChance(content)).replace(".",
                    ",") + "%");
            meta.setLore(lore);
            displayItem.setItemMeta(meta);
            this.inventory.addItem(displayItem);
            count++;
        }
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        FileConfiguration cfg = getConfig();
        cfg.set("Disabled", disabled);
        saveConfig();
    }

    public Inventory getInventory() {
        return inventory;
    }

    public CrateAddon getAddon() {
        return addon;
    }
}
