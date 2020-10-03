package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.gamble.Coinflip;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CoinflipManager {

    private final List<Coinflip> coinflips;

    private Inventory inventory;

    public CoinflipManager() {
        this.coinflips = new ArrayList<>();
        registerInventory();
    }

    private void registerInventory() {
        this.inventory = Bukkit.createInventory(null, 9 * 6, StringDefaults.INVENTORY_PREFIX + "Coinflips");
    }

    public void openInventory(Player player) {
        player.openInventory(this.inventory);
    }

    public void startCoinflip(Coinflip coinflip, Player player) {
        if (coinflip.getEntries().size() > 1 || !this.coinflips.contains(coinflip))
            return;

        UserCurrency currency = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();
        currency.removeMoney(coinflip.getEntryPrice());

        coinflip.getEntries().add(player);
        coinflip.gotoPhase(0);

        Player first = coinflip.getEntries().get(0);
        int slot = this.coinflips.indexOf(coinflip);
        ItemStack itemStack = new ItemBuilder(Material.IRON_FENCE).setDisplayName(
                "§e" + first.getName() + " §cvs. §e" + player.getName()).setLore("",
                "§7Münzen§8: §e" + Util.formatNumber(coinflip.getEntryPrice() * 2), "",
                "§7[Linksklick] : §eCoinflip zuschauen").build();
        this.inventory.setItem(slot, itemStack);
    }

    public void openCoinflip(Player player, long entryPrice) {
        if (hasOpenCoinflip(player)) return;

        Coinflip coinflip = new Coinflip(entryPrice, player);
        UserCurrency currency = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();
        currency.removeMoney(entryPrice);
        this.coinflips.add(coinflip);

        ItemStack itemStack = new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(player.getName()).setDurability(
                3).setDisplayName(
                "§a" + player.getName()).setLore(
                "",
                "§7Münzen§8: §e" + Util.formatNumber(entryPrice) + " Münzen",
                "",
                "§7[Linksklick] : §eCoinflip beitreten").build();
        this.inventory.setItem(this.inventory.firstEmpty(), itemStack);
    }

    public void stopCoinflip(Coinflip coinflip) {
        if (!this.coinflips.contains(coinflip)) return;

        int slot = this.coinflips.indexOf(coinflip);
        this.inventory.setItem(slot, new ItemBuilder(Material.AIR).build());

        for (int pos = slot + 1; pos <= this.coinflips.size(); pos++) {
            ItemStack toSwitch = this.inventory.getItem(pos);
            this.inventory.setItem(pos - 1, toSwitch);
        }

        this.coinflips.remove(coinflip);
    }

    public void handlePlayerQuit(Player player) {
        List<Coinflip> toRemove = new ArrayList<>();

        for (Coinflip coinflip : this.coinflips) {
            if (coinflip.getEntries().contains(player)) {
                coinflip.kickViewers();
                coinflip.cancelTask();

                for (Player all : coinflip.getEntries()) {
                    UserCurrency uc = Main.getInstance().getUserManager().getUser(all.getUniqueId()).getUserCurrency();
                    uc.addMoney(coinflip.getEntryPrice());
                }


                int index = coinflip.getEntries().indexOf(player);
                Player opposite = coinflip.getEntries().get((index == 0) ? 1 : 0);
                opposite.sendMessage(
                        StringDefaults.CF_PREFIX + "§7" + player.getName() + " §chat den Server verlassen.");
                opposite.sendMessage(StringDefaults.CF_PREFIX + "§cDer Coinflip wurde abgebrochen.");
                toRemove.add(coinflip);
            }
        }

        for (Coinflip coinflip : toRemove)
            stopCoinflip(coinflip);
    }

    public Coinflip getOpenCoinflip(Player player) {
        for (Coinflip coinflip : this.coinflips) {
            if (!coinflip.getEntries().isEmpty() && (coinflip.getEntries().get(0)).equals(player))
                return coinflip;
        }
        return null;
    }

    public Coinflip getCoinflipBySlot(int index) {
        if (index >= this.coinflips.size()) return null;
        return this.coinflips.get(index);
    }

    public boolean hasOpenCoinflip(Player player) {
        return (getOpenCoinflip(player) != null);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<Coinflip> getCoinflips() {
        return coinflips;
    }
}
