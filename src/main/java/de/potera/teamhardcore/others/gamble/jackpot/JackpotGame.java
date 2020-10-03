package de.potera.teamhardcore.others.gamble.jackpot;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;

public class JackpotGame {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final Random RANDOM = new Random();
    private static final int[] slots = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    private final List<JackpotEntry> entries;
    private final Map<JackpotEntry, Double> cachedChances;

    private final long maxBet;
    private final Inventory inventory;

    private long reward;
    private BukkitTask gameTask;
    private int gamePhase;

    public JackpotGame(long maxBet) {
        this.cachedChances = new HashMap<>();
        this.entries = new ArrayList<>();
        this.maxBet = maxBet;
        this.gamePhase = 0;

        this.reward = 0L;

        this.inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "Jackpot");

        for (int i = 0; i < 9; i++) {
            this.inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build());
            this.inventory.setItem(i + 18, new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build());
        }

        this.inventory.setItem(4,
                new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(4).setDisplayName(" ").build());
        this.inventory.setItem(22,
                new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(4).setDisplayName(" ").build());
    }

    public void openGUI(Player player, int state) {
        if (state == 0) {
            Inventory inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "Jackpot");

            for (int i = 0; i < inventory.getSize(); i++)
                inventory.setItem(i, ItemDefaults.PLACEHOLDER);

            if (!containsEntry(player.getUniqueId())) {
                inventory.setItem(13,
                        new ItemBuilder(Material.SIGN).setDisplayName("§6§lJackpot Informationen").setLore(
                                "",
                                "§7Jackpot: §e#1",
                                "§7Verbleibende Zeit§8: §e- Sekunden",
                                "§7Teilnehmer§8: §e-",
                                "§7Gewinn§8: §e-",
                                "§7Max. Einsatz§8: §e" + Util.formatNumber(this.maxBet) + "$",
                                "",
                                "§7[Linksklick] : §eAm Jackpot teilnehmen").build());

                player.openInventory(inventory);
                return;
            }

            inventory.setItem(13,
                    new ItemBuilder(Material.SIGN).setDisplayName("§6§lJackpot Informationen").setLore(
                            "",
                            "§7Jackpot: §e#1",
                            "§7Verbleibende Zeit§8: §e- Sekunden",
                            "§7Teilnehmer§8: §e-",
                            "§7Gewinn§8: §e-",
                            "§7Max. Einsatz§8: §e" + Util.formatNumber(this.maxBet) + "$",
                            "",
                            "§aDu hast bereits teilgenommen.").build());
            player.openInventory(inventory);
            return;
        }

        if (state == 1) {
            player.openInventory(this.inventory);
        }
    }

    private void updateGUI(Player player, Object[] objects) {
        if (this.gamePhase != 0) return;

        if (player.getOpenInventory().getTopInventory() == null ||
                !player.getOpenInventory().getTopInventory().getName().equalsIgnoreCase(
                        StringDefaults.INVENTORY_PREFIX + "Jackpot")) return;

        Inventory inventory = player.getOpenInventory().getTopInventory();

        int seconds = (Integer) objects[0];
        int members = (Integer) objects[1];
        long reward = (Long) objects[2];

        ItemStack sign = inventory.getItem(13);
        ItemMeta meta = sign.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(2, "§7Verbleibende Zeit§8: §e" + seconds + (seconds == 1 ? " Sekunde" : " Sekunden"));
        lore.set(3, "§7Teilnehmer§8: §e" + members);
        lore.set(4, "§7Gewinn§8: §e" + Util.formatNumber(reward) + "$");
        meta.setLore(lore);
        sign.setItemMeta(meta);
    }

    public void goToPhase(int phase) {
        this.gamePhase = phase;

        if (phase == 0) {
            this.gameTask = new BukkitRunnable() {
                int leftTime = 60;

                @Override
                public void run() {

                    if (this.leftTime == 0) {
                        cancel();
                        goToPhase(1);
                        return;
                    }

                    for (Player player : Bukkit.getOnlinePlayers())
                        updateGUI(player, new Object[]{leftTime, entries.size(), reward});

                    this.leftTime--;
                }
            }.runTaskTimer(Main.getInstance(), 20L, 20L);
        }

        if (phase == 1) {
            List<ItemStack> tmpItemStacks = new ArrayList<>();

            for (JackpotEntry entry : this.entries) {
                Player player = Bukkit.getPlayer(entry.getUuid());
                double chance = getPercentChance(entry);

                if (player == null || !player.isOnline()) continue;

                ItemStack item = new ItemBuilder(Material.SKULL_ITEM).setLore(
                        "§7Einsatz§8: §e" + Util.formatNumber(entry.getEntry()) + "$").setDisplayName("§6" +
                        player.getName()).setSkullOwner(player.getName()).setDurability(3).build();

                for (int i = 0; i < chance; i++)
                    tmpItemStacks.add(item);
            }
            Collections.shuffle(tmpItemStacks);

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getOpenInventory().getTopInventory() == null ||
                        !player.getOpenInventory().getTopInventory().getName().equalsIgnoreCase(
                                StringDefaults.INVENTORY_PREFIX + "Jackpot")) continue;

                JackpotGame.this.openGUI(player, 1);
            }

            JackpotEntry tmpPlayer = getRandomPlayer();

            if (tmpPlayer == null) {
                cancelTask();
                Bukkit.getOnlinePlayers().forEach(
                        player -> player.sendMessage(
                                StringDefaults.JACKPOT_PREFIX + "§cDer Jackpot wurde abgebrochen."));
                return;
            }

            final JackpotEntry rndmEntry = tmpPlayer;
            String entryName = UUIDFetcher.getName(rndmEntry.getUuid());

            this.gameTask = new BukkitRunnable() {

                int rolls = 60;
                int timeout = 0;

                @Override
                public void run() {
                    if (this.timeout != 0) {
                        this.timeout--;
                        return;
                    }

                    if (this.rolls == 0 && JackpotGame.this.inventory.getItem(
                            13).getItemMeta().getDisplayName().equalsIgnoreCase("§e" + entryName)) {

                        cancel();

                        Bukkit.broadcastMessage(
                                StringDefaults.JACKPOT_PREFIX + "§7Der Spieler §e" + entryName + " §7hat den" +
                                        " Jackpot in Höhe von §e§l" + Util.formatNumber(
                                        getReward()) + "$ §7gewonnen! §8(§e§l" + DECIMAL_FORMAT.format(
                                        getPercentChance(rndmEntry)) + "%§8)");


                        Player player = Bukkit.getPlayer(rndmEntry.getUuid());

                        if (player != null) {
                            player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§7Du hast den Jackpot gewonnen.");
                            player.sendMessage(
                                    StringDefaults.JACKPOT_PREFIX + "§7Dein Gewinn wurde dir gutgeschrieben.");
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                        }

                        UserCurrency uc = Main.getInstance().getUserManager().getUser(
                                rndmEntry.getUuid()).getUserCurrency();
                        uc.addMoney(getReward());

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                kickViewers();
                                Main.getInstance().getJackpotManager().stopJackpotGame();
                            }
                        }.runTaskLater(Main.getInstance(), 30L);
                        return;
                    }

                    if (this.rolls <= 10 && this.rolls > 5) {
                        this.timeout = 1;
                    } else if (this.rolls <= 5 && this.rolls >= 2) {
                        this.timeout = 4;
                    } else if (this.rolls <= 1) {
                        this.timeout = 8;
                    }

                    for (int i = 1; i < 9; i++) {
                        if (JackpotGame.this.inventory.getItem(slots[i]) != null)
                            JackpotGame.this.inventory.setItem((slots[i] - 1),
                                    JackpotGame.this.inventory.getItem(slots[i]));
                    }


                    for (HumanEntity entity : JackpotGame.this.inventory.getViewers()) {
                        Player player = (Player) entity;
                        if (player != null && player.getOpenInventory().getTopInventory() != null
                                && player.getOpenInventory().getTopInventory().getName().equalsIgnoreCase(
                                StringDefaults.INVENTORY_PREFIX + "Jackpot")) {
                            player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                            player.updateInventory();
                        }
                    }

                    ItemStack rndmItem = tmpItemStacks.get(RANDOM.nextInt(tmpItemStacks.size() - 1));
                    tmpItemStacks.add(rndmItem);

                    if (this.rolls == 5) {
                        ItemStack item = new ItemBuilder(Material.SKULL_ITEM).setLore(
                                "§7Einsatz§8: §e" + Util.formatNumber(rndmEntry.getEntry()) + "$").setSkullOwner(
                                entryName)
                                .setDisplayName("§e" + entryName).setDurability(3).build();
                        JackpotGame.this.inventory.setItem(17, item);
                    } else {
                        JackpotGame.this.inventory.setItem(17, rndmItem);
                    }

                    this.rolls--;
                }
            }.runTaskTimer(Main.getInstance(), 0L, 2L);
        }
    }

    public JackpotEntry getRandomPlayer() {
        List<JackpotEntry> entries = new ArrayList<>();

        for (JackpotEntry entry : this.entries) {
            Player tmpPlayer = Bukkit.getPlayer(entry.getUuid());

            if (tmpPlayer == null || !tmpPlayer.isOnline()) continue;
            entries.add(entry);
        }

        if (entries.isEmpty()) {
            cancelTask();

            Bukkit.getOnlinePlayers().forEach(
                    player -> player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cDer Jackpot wurde abgebrochen."));
            return null;
        }

        entries.forEach(jackpotEntry -> System.out.println(jackpotEntry.getUuid()));
        return entries.get(RANDOM.nextInt(entries.size()));
    }

    public void cancelTask() {
        if (this.gameTask == null) return;
        this.gameTask.cancel();
        this.gameTask = null;
    }

    public JackpotEntry getJackpotEntry(UUID uuid) {
        for (JackpotEntry entry : this.entries)
            if (entry.getUuid().equals(uuid))
                return entry;
        return null;
    }

    public boolean containsEntry(UUID uuid) {
        for (JackpotEntry entry : this.entries)
            if (entry.getUuid().equals(uuid))
                return true;
        return false;
    }

    public void addEntry(UUID uuid, long value) {
        if (containsEntry(uuid)) return;

        JackpotEntry entry = new JackpotEntry(uuid, value);
        this.entries.add(entry);
        setReward(getReward() + value);
        refreshPercentChances();
    }

    public int getGamePhase() {
        return gamePhase;
    }

    private void refreshPercentChances() {
        this.cachedChances.clear();
        for (JackpotEntry entry : this.entries) {
            this.cachedChances.put(entry, getPercentChance(entry));
        }
    }

    private double getPercentChance(JackpotEntry entry) {
        if (this.cachedChances.containsKey(entry))
            return this.cachedChances.get(entry);

        return (double) entry.getEntry() / this.getReward() * 100.0D;
    }

    public long getMaxBet() {
        return maxBet;
    }

    public long getReward() {
        return reward;
    }

    public void setReward(long reward) {
        this.reward = reward;
    }

    public void kickViewers() {
        executeSync(() -> {
            List<HumanEntity> list = new ArrayList<>(this.inventory.getViewers());
            for (HumanEntity entity : list)
                entity.closeInventory();
        });
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
