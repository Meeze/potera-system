package de.potera.teamhardcore.others.gamble;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.ItemDefaults;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Coinflip {
    private static final SecureRandom RANDOM = new SecureRandom();

    private static final Integer[] INVENTORY_SLOTS = new Integer[]{12, 13, 14, 24, 33, 41, 40, 39, 29, 20};
    private static final ItemStack GREEN_GLASS_ITEM = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(
            4).build();

    private final long entryPrice;
    private final List<Player> entries;
    private final List<ItemStack> items;

    private BukkitTask gameTask;
    private int gamePhase;
    private Inventory inventory;

    public Coinflip(long entryPrice, Player firstEntry) {
        this.entries = new ArrayList<>(2);
        this.items = new ArrayList<>();
        this.entryPrice = entryPrice;
        this.entries.add(firstEntry);

        this.gamePhase = -1;
        registerInventory();
    }

    private void registerItems() {
        Player player = this.entries.get(0);
        Player target = this.entries.get(1);

        ItemStack pSkull = new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(player.getName()).setDisplayName(
                "§a" + player.getName()).setDurability(3).setAmount(2).build();
        ItemStack tSkull = new ItemBuilder(Material.SKULL_ITEM).setSkullOwner(target.getName()).setDisplayName(
                "§a" + target.getName()).setDurability(3).build();

        for (int i = 0; i != 5; i++) {
            this.items.add(pSkull);
        }

        for (int i = 0; i != 5; i++) {
            this.items.add(tSkull);
        }
    }

    private void registerInventory() {
        this.inventory = Bukkit.createInventory(null, 9 * 6, StringDefaults.INVENTORY_PREFIX + "Coinflip");

        for (int i = 0; i < this.inventory.getSize(); i++)
            this.inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        for (int slot : INVENTORY_SLOTS)
            this.inventory.setItem(slot, new ItemBuilder(Material.AIR).build());

        this.inventory.setItem(4, GREEN_GLASS_ITEM);
        this.inventory.setItem(22, GREEN_GLASS_ITEM);
    }

    private void moveItems() {
        for (int i = 0; i < INVENTORY_SLOTS.length; i++) {
            this.inventory.setItem(INVENTORY_SLOTS[i], this.items.get(i));
        }

        this.items.add(this.items.get(0).clone());
        this.items.remove(0);
    }

    public void gotoPhase(int phase) {
        this.gamePhase = phase;
        if (phase == 0) {
            registerItems();

            for (Player player : this.entries) {
                player.openInventory(this.inventory);
            }

            Player winner = this.entries.get(RANDOM.nextInt(2));
            long winningPrice = this.entryPrice * 2;

            this.gameTask = new BukkitRunnable() {
                int rolls = (winner == entries.get(0) ? 60 : 55);
                int timeout = 0;

                @Override
                public void run() {
                    if (this.timeout != 0) {
                        this.timeout--;
                        return;
                    }

                    if (this.rolls <= 10 && this.rolls > 5) {
                        this.timeout = 1;
                    } else if (this.rolls <= 5 && this.rolls >= 2) {
                        this.timeout = 4;
                    } else if (this.rolls <= 1) {
                        this.timeout = 6;
                    }

                    if (this.rolls <= 0 && Coinflip.this.inventory.getItem(
                            13).getItemMeta().getDisplayName().equalsIgnoreCase("§a" + winner.getName())) {

                        long coinsToAdd = (winningPrice / 100) * 95;
                        long casinoTax = (winningPrice / 100) * 5;

                        Main.getInstance().getDailyPotManager().addDeployment(casinoTax);

                        for (int i = 0; i < Coinflip.this.entries.size(); i++) {
                            Player self = Coinflip.this.entries.get(i);
                            if (winner == self) {
                                self.sendMessage(
                                        StringDefaults.CF_PREFIX + "§7Du hast gewonnen! Dir wurden §e" + Util.formatNumber(
                                                coinsToAdd) + " Münzen §7gutgeschrieben!");
                                self.playSound(self.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                            } else {
                                self.sendMessage(
                                        StringDefaults.CF_PREFIX + "§7Du hast leider verloren. Viel Glück beim nächsten Mal!");
                                self.playSound(self.getLocation(), Sound.VILLAGER_NO, 1.0F, 1.0F);
                            }
                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                kickViewers();
                                Main.getInstance().getCoinflipManager().stopCoinflip(Coinflip.this);
                            }
                        }.runTaskLater(Main.getInstance(), 40L);

                        cancel();
                        return;
                    }

                    moveItems();
                    this.rolls--;
                }
            }.runTaskTimer(Main.getInstance(), 2L, 2L);
        }
    }

    public void kickViewers() {
        executeSync(() -> {
            List<HumanEntity> list = new ArrayList<>(this.inventory.getViewers());
            for (HumanEntity entity : list)
                entity.closeInventory();
        });
    }

    public void cancelTask() {
        this.gameTask.cancel();
        this.gameTask = null;
    }

    public int getGamePhase() {
        return gamePhase;
    }

    public List<Player> getEntries() {
        return entries;
    }

    public long getEntryPrice() {
        return entryPrice;
    }

    public Inventory getInventory() {
        return inventory;
    }

    private void executeSync(Runnable runnable) {
        if (Thread.currentThread().getName().contains("Server")) {
            runnable.run();
        } else if (Main.getInstance().isEnabled()) {
            Bukkit.getScheduler().runTask(Main.getInstance(), runnable);
        } else {
            runnable.run();
        }
    }
}
