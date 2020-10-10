package de.potera.teamhardcore.others.crates;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrateAddon {
    private static final ItemStack DEFAULT_ITEM = new ItemStack(Material.SKULL_ITEM);
    private final String name;
    private final String displayName;

    private final List<ContentPiece> crateContent;
    private final Map<ContentPiece, Double> cachedChances;

    private ItemStack displayItem;

    public CrateAddon(String name, String displayName) {
        this.crateContent = new ArrayList<>();
        this.cachedChances = new HashMap<>();

        this.name = name;
        this.displayName = displayName;
        this.displayItem = DEFAULT_ITEM;
    }

    public String getName() {
        return name;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    public void setDisplayItem(ItemStack displayItem) {
        this.displayItem = displayItem;
    }

    public ItemStack getCrateItem() {
        ItemStack item = this.displayItem.clone();
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Inhalt§8: §a" + this.crateContent.size() + " Items");
        lore.add("");
        lore.add("§7[Linkslick] : §eInhalt ansehen");
        lore.add("§7[Rechtsklick] : §eCrate öffnen");

        meta.setDisplayName(getDisplayName());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<ContentPiece> getCrateContent() {
        return crateContent;
    }

    public void addContent(ContentPiece piece) {
        if (this.crateContent.contains(piece)) return;
        this.crateContent.add(piece);
        refreshPercentChances();
    }

    public void removeContent(ContentPiece piece) {
        if (!this.crateContent.contains(piece)) return;
        this.crateContent.remove(piece);
        refreshPercentChances();
    }

    private void refreshPercentChances() {
        this.cachedChances.clear();
        for (ContentPiece piece : this.crateContent)
            this.cachedChances.put(piece, getPercentChance(piece));
    }

    public Double getPercentChance(ContentPiece piece) {
        if (this.cachedChances.containsKey(piece))
            return this.cachedChances.get(piece);
        int fullWeight = 0;
        for (ContentPiece contentPiece : this.crateContent)
            fullWeight += contentPiece.getChanceWeight();
        return (double) piece.getChanceWeight() / fullWeight * 100.0D;
    }

}
