package de.potera.teamhardcore.others.gamble.roulette;


import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserCurrency;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RouletteGame {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final Random RANDOM = new Random();
    private static final int[] slots = {9, 10, 11, 12, 13, 14, 15, 16, 17};

    private final Map<Player, RouletteEntry> players;
    private final Inventory inventory;

    private int gamePhase;
    private BukkitTask gameTask;

    public RouletteGame() {
        this.players = new HashMap<>();
        this.gamePhase = 0;
        this.gameTask = null;

        this.inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "X-Roulette");

        for (int i = 0; i < 9; i++) {
            this.inventory.setItem(i, ItemDefaults.PLACEHOLDER);
            this.inventory.setItem(i + 18, ItemDefaults.PLACEHOLDER);
        }
    }

    private List<Double> getRandomMultipliers() {
        List<Double> list = new ArrayList<>();
        double baseMin = 1.00D;
        double baseMax = 1.40D;

        for (int i = 0; i < 12; i++) {
            if (RANDOM.nextInt(8) == 1) {
                baseMax = 2.70D;
            }

            if (RANDOM.nextInt(24) == 1) {
                baseMax = 5.50D;
            }

            if (RANDOM.nextInt(60) == 1) {
                baseMax = (RANDOM.nextDouble() * 100);
            }

            double multiplier = ThreadLocalRandom.current().nextDouble(baseMin, baseMax);
            list.add(multiplier);
        }

        return list;
    }

    private String formatMultiplier(double multiplier) {
        String color = "§a";

        if (multiplier >= 0 || multiplier <= 3)
            color = "§a";
        if (multiplier >= 4 || multiplier <= 6)
            color = "§e";
        if (multiplier >= 5 || multiplier <= 9)
            color = "§6";
        if (multiplier >= 10)
            color = "§c";

        return color + "x" + DECIMAL_FORMAT.format(multiplier);
    }

    private Map<Player, RouletteEntry> getWinners(double currentMultiplier) {
        Map<Player, RouletteEntry> players = new HashMap<>();

        for (Map.Entry<Player, RouletteEntry> entry : this.players.entrySet()) {
            if (entry.getValue().getMultiplier() <= currentMultiplier) {
                players.put(entry.getKey(), entry.getValue());
            }
        }
        return players;
    }

    private Material getMaterialForMultiplier(double multiplier) {
        Material material = Material.INK_SACK;

        if (multiplier >= 1.01 && multiplier <= 1.99)
            material = Material.COAL;
        if (multiplier >= 2 && multiplier <= 4.99)
            material = Material.IRON_INGOT;
        if (multiplier >= 5 && multiplier <= 9.99)
            material = Material.GOLD_INGOT;
        if (multiplier >= 10 && multiplier <= 29.99)
            material = Material.DIAMOND;
        if (multiplier >= 30 && multiplier <= 79.99)
            material = Material.EMERALD;
        if (multiplier >= 80 && multiplier <= 100)
            material = Material.NETHER_STAR;

        return material;
    }

    private void updateGUI(Player player, int newSeconds) {
        if (this.gamePhase != 0) return;

        if (player.getOpenInventory().getTopInventory() == null ||
                !player.getOpenInventory().getTopInventory().getName().equalsIgnoreCase(
                        StringDefaults.INVENTORY_PREFIX + "X-Roulette")) return;

        Inventory inventory = player.getOpenInventory().getTopInventory();

        if (!this.players.containsKey(player)) return;

        ItemStack sign = inventory.getItem(13);
        ItemMeta meta = sign.getItemMeta();
        List<String> lore = meta.getLore();
        lore.set(3, "§7Verbleibende Zeit§8: §e" + newSeconds + (newSeconds == 1 ? " Sekunde" : " Sekunden"));
        meta.setLore(lore);
        sign.setItemMeta(meta);
    }

    public void openGUI(Player player, int state) {
        if (state == 0) {
            Inventory inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "X-Roulette");

            for (int i = 0; i < inventory.getSize(); i++)
                inventory.setItem(i, ItemDefaults.PLACEHOLDER);

            if (!this.players.containsKey(player)) {
                inventory.setItem(13,
                        new ItemBuilder(Material.BARRIER).setDisplayName("§cNicht teilgenommen.")
                                .setLore("", "§7[Linksklick] : §eTeilnehmen").build());
                return;
            }

            RouletteEntry entry = this.players.get(player);

            inventory.setItem(13, new ItemBuilder(Material.SIGN).setDisplayName("§aErfolgreich teilgenommen").setLore(
                    "§7Dein Einsatz§8: §e" + Util.formatNumber(entry.getEntry()) + "$",
                    "§7Dein Multiplier§8: §a§lx" + entry.getMultiplier(), "",
                    "§7Verbleibende Zeit§8: §e- Sekunden").build());
            player.openInventory(inventory);
            return;
        }

        if (state == 1) {
            player.openInventory(this.inventory);
        }
    }

    public void gotoPhase(int gamePhase) {
        this.gamePhase = gamePhase;

        if (this.gamePhase == 0) {
            this.gameTask = new BukkitRunnable() {
                int timeLeft = 60;

                @Override
                public void run() {
                    if (this.timeLeft == 0) {
                        cancel();
                        RouletteGame.this.gotoPhase(1);
                        return;
                    }

                    for (Player player : RouletteGame.this.players.keySet()) {
                        updateGUI(player, this.timeLeft);
                    }

                    this.timeLeft--;
                }
            }.runTaskTimer(Main.getInstance(), 20L, 20L);
        }

        if (this.gamePhase == 1) {
            for (Player player : this.players.keySet()) {
                if (player == null) continue;

                if (player.getOpenInventory().getTopInventory() == null ||
                        !player.getOpenInventory().getTopInventory().getName().equalsIgnoreCase(
                                StringDefaults.INVENTORY_PREFIX + "X-Roulette")) continue;

                RouletteGame.this.openGUI(player, 1);
            }

            List<Double> multipliers = getRandomMultipliers();
            double rndmMultiplier = multipliers.get(RANDOM.nextInt(multipliers.size()));
            Map<Player, RouletteEntry> winners = getWinners(rndmMultiplier);
            List<Player> loser = new ArrayList<>(this.players.keySet());
            loser.removeIf(winners::containsKey);

            this.gameTask = new BukkitRunnable() {
                int timeout = 0;
                int rolls = 40;

                @Override
                public void run() {
                    if (this.timeout != 0) {
                        this.timeout--;
                        return;
                    }

                    if (this.rolls == 0) {
                        cancel();

                        for (Map.Entry<Player, RouletteEntry> entry : winners.entrySet()) {
                            Player player = entry.getKey();
                            RouletteEntry rouletteEntry = entry.getValue();

                            long coinsToAdd = (long) (rouletteEntry.getEntry() * rouletteEntry.getMultiplier());

                            if (player == null) {
                                UserCurrency opCurrency = Main.getInstance().getUserManager().getUser(
                                        rouletteEntry.getUuid()).getUserCurrency();
                                opCurrency.addMoney(coinsToAdd);
                                continue;
                            }

                            UserCurrency uc = Main.getInstance().getUserManager().getUser(
                                    player.getUniqueId()).getUserCurrency();
                            uc.addMoney(coinsToAdd);

                            player.sendMessage(
                                    StringDefaults.ROLL_PREFIX + "§7Du hast die §a§lX-Roulette §7Runde gewonnen.");
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§7Dein Gewinn§8: §e" + Util.formatNumber(
                                    coinsToAdd) + " Münzen");
                            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);

                        }

                        for (Player player : loser) {
                            if (player == null) continue;
                            player.sendMessage(
                                    StringDefaults.ROLL_PREFIX + "§7Du hast die §a§lX-Roulette §7Runde verloren.");
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§7Viel Glück beim nächsten Mal!");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                kickViewers();
                                Main.getInstance().getRouletteManager().stopRouletteGame();
                            }
                        }.runTaskLater(Main.getInstance(), 30L);
                        return;
                    }

                    if (this.rolls <= 10 && this.rolls > 5) {
                        this.timeout = 1;
                    } else if (this.rolls <= 5 && this.rolls >= 2) {
                        this.timeout = 4;
                    } else if (this.rolls == 1) {
                        this.timeout = 6;
                    }

                    for (int i = 1; i < 9; i++) {
                        if (RouletteGame.this.inventory.getItem(slots[i]) != null)
                            RouletteGame.this.inventory.setItem((slots[i] - 1),
                                    RouletteGame.this.inventory.getItem(slots[i]));
                    }

                    double multiplier = multipliers.get(RANDOM.nextInt(multipliers.size() - 1));
                    multipliers.add(multiplier);

                    if (this.rolls == 5) {
                        ItemStack itemStack = new ItemBuilder(getMaterialForMultiplier(rndmMultiplier)).setDurability(
                                (rndmMultiplier < 1.1 ? 8 : 0)).setDisplayName(
                                formatMultiplier(rndmMultiplier)).build();
                        RouletteGame.this.inventory.setItem(17, itemStack);
                    } else {
                        RouletteGame.this.inventory.setItem(17,
                                new ItemBuilder(getMaterialForMultiplier(multiplier)).setDurability(
                                        (multiplier < 1.1 ? 8 : 0)).setDisplayName(
                                        formatMultiplier(multiplier)).build());
                    }

                    for (HumanEntity entity : RouletteGame.this.inventory.getViewers()) {
                        Player player = (Player) entity;
                        if (player == null || player.getOpenInventory().getTopInventory() == null
                                || !player.getOpenInventory().getTopInventory().getName().equalsIgnoreCase(
                                StringDefaults.INVENTORY_PREFIX + "X-Roulette")) continue;
                        player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                        player.updateInventory();
                    }

                    this.rolls--;
                }
            }.runTaskTimer(Main.getInstance(), 0L, 2L);
        }
    }

    public void cancelTask() {
        this.gameTask.cancel();
        this.gameTask = null;
    }

    public int getGamePhase() {
        return gamePhase;
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

    public Map<Player, RouletteEntry> getPlayers() {
        return players;
    }
}
