package de.potera.teamhardcore.others.kits;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.TimeUtil;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Kit {

    private String name;
    private ItemStack[] items;
    private long cooldownTime;

    private Inventory previewInventory;

    public Kit(String name, ItemStack[] items, long cooldownTime) {
        this.name = name;
        this.items = items;
        this.cooldownTime = cooldownTime;
        createPreviewInventory();
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getItems() {
        return items;
    }

    public long getCooldownTime() {
        return cooldownTime;
    }

    public void giveItems(Player player) {
        for (ItemStack item : this.items) {
            Util.addItem(player, item);
        }
    }

    public boolean giveKit(Player player) {
        UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();
        long diff = userData.getKitCooldowns().containsKey(getName()) ? (userData.getKitCooldowns().get(
                getName()) - System.currentTimeMillis()) : -1L;

        if (diff / 1000L > 0L) {
            player.sendMessage(
                    StringDefaults.PREFIX + "§7Das Kit ist erst in §e" + TimeUtil.timeToString(diff,
                            false) + " §7wieder verfügbar.");
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
            return false;
        }

        userData.addKitCooldown(getName(), getCooldownTime() + System.currentTimeMillis());
        giveItems(player);
        player.sendMessage(StringDefaults.PREFIX + "§7Du hast das Kit erhalten.");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        return true;
    }

    public void openPreviewInventory(Player player) {
        player.openInventory(this.previewInventory);
    }

    private void createPreviewInventory() {
        ItemStack[] previewItems = getItems();
        int invSize = (int) (Math.ceil(previewItems.length / 9.0D) + 2.0D);
        if (invSize > 6) {
            invSize = 6;
        }
        this.previewInventory = Bukkit.createInventory(null, invSize * 9,
                StringDefaults.INVENTORY_PREFIX + "Kit Vorschau");
        this.previewInventory.addItem(this.items);
        this.previewInventory.setItem(this.previewInventory.getSize() - 9,
                new ItemBuilder(Material.WOOD_DOOR).setDisplayName("§cZurück").build());
    }

}
