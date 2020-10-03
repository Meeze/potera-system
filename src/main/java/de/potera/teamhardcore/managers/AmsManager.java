package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.ams.Ams;
import de.potera.teamhardcore.others.ams.AmsFriend;
import de.potera.teamhardcore.others.ams.upgrades.AmsUpgradeBase;
import de.potera.teamhardcore.others.ams.upgrades.EnumAmsUpgrade;
import de.potera.teamhardcore.others.ams.upgrades.OfflineGenUpgrade;
import de.potera.teamhardcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;

public class AmsManager {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private final Map<UUID, Ams> amsCache;
    private final Map<Player, Ams> amsGuiCache;
    private final Map<Player, int[]> amsStateCache;

    public AmsManager() {
        this.amsCache = new HashMap<>();
        this.amsGuiCache = new HashMap<>();
        this.amsStateCache = new HashMap<>();

        Main.getInstance().getDatabaseManager().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `AMS` (`Owner` VARCHAR(36), `OwnerName` VARCHAR(16) NOT NULL, `Spawners` BIGINT NOT NULL, `Coins` DECIMAL(15,2) NOT NULL, `PrestigeLevel` INT NOT NULL, `Friends` TEXT NULL, `Upgrades` TEXT NULL,`Boost` DOUBLE NOT NULL, `BoostTime` BIGINT NOT NULL, UNIQUE KEY(`Owner`))");

        loadAllAms();
        startTickTask();
        startCheckStayAliveTask();
    }

    private void loadAllAms() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT `Owner`,`OwnerName` FROM `AMS`");
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                UUID owner = UUID.fromString(rs.getString("Owner"));
                Ams ams = new Ams(owner, false);
                if (Ams.shouldStayAlive(ams)) {
                    this.amsCache.put(owner, ams);
                    continue;
                }
                ams.getHandlerGroup().removeHandler(ams);
            }

            Main.getInstance().getDatabaseManager().close(st, rs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed())
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void startTickTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                AmsManager.this.amsCache.forEach((uuid, ams) -> ams.tick());
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    private void startCheckStayAliveTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<UUID> toRemove = null;
                for (Map.Entry<UUID, Ams> entry : AmsManager.this.amsCache.entrySet()) {
                    if (!Ams.shouldStayAlive(entry.getValue())) {
                        if (toRemove == null)
                            toRemove = new ArrayList<>();
                        toRemove.add(entry.getKey());
                    }
                }
                if (toRemove != null) {
                    for (UUID uuid : toRemove) {
                        Ams ams = AmsManager.this.amsCache.get(uuid);
                        ams.getHandlerGroup().removeHandler(ams);
                        AmsManager.this.amsCache.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 600L);
    }

    public void onDisable() {
        this.amsGuiCache.keySet().forEach(HumanEntity::closeInventory);
    }

    public void loadAms(UUID uuid) {
        if (this.amsCache.containsKey(uuid)) return;
        Ams ams = new Ams(uuid, true);
        this.amsCache.put(uuid, ams);
    }

    public Ams getAms(UUID uuid) {
        if (!this.amsCache.containsKey(uuid)) loadAms(uuid);
        return this.amsCache.get(uuid);
    }

    public void openGui(Player player, Ams ams, int type) {
        if (type != 1 && type != 2 && type != 3)
            return;
        this.amsGuiCache.put(player, ams);
        Inventory inventory = null;

        if (type == 1) {
            inventory = Bukkit.createInventory(null, 9 * 4, StringDefaults.INVENTORY_PREFIX + "AMS");

            for (int i = 0; i < inventory.getSize(); i++)
                inventory.setItem(i, ItemDefaults.PLACEHOLDER);

            ItemStack info = new ItemBuilder(Material.SIGN).setDisplayName("§3AMS Informationen")
                    .setLore("", "Boost", "", "Spawner", "Coins", "MaxCoins", "", "Sek", "Min", "Stunde",
                            "Tag").build();

            ItemStack money = new ItemBuilder(Material.DOUBLE_PLANT).setDisplayName("§eCoins")
                    .setLore("Guthaben", "", "§7[Linksklick] : §eMünzen abheben").build();

            ItemStack spawner = new ItemBuilder(Material.MOB_SPAWNER).setDisplayName("§3Spawner")
                    .setLore("Spawner", "", "§7[Linksklick] : §eAlle Spawner im Inventar hinzufügen"
                            , "§7[Rechtsklick] : §e16 Spawner aus der AMS nehmen").build();

            ItemStack friends = new ItemBuilder(Material.WATCH).setDisplayName("§eFreunde hinzufügen")
                    .setLore("", "§7[Linsklick] : §eFreunde zur AMS hinzufügen").build();

            ItemStack upgrades = new ItemBuilder(Material.NETHER_STAR).setDisplayName("§3AMS Upgrades")
                    .setLore("", "§7[Linsklick] : §eUpgrades verwalten").build();

            inventory.setItem(13, info);
            inventory.setItem(20, money);
            inventory.setItem(21, spawner);
            inventory.setItem(23, upgrades);
            inventory.setItem(24, friends);

            updateGui(player, inventory, new int[]{0}, 1, 2, 3);
        }

        if (type == 2) {
            inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "AMS Freunde");
            updateGui(player, inventory, new int[]{3});
        }

        if (type == 3) {
            inventory = Bukkit.createInventory(null, 9 * 3, StringDefaults.INVENTORY_PREFIX + "AMS Upgrades");
            updateGui(player, inventory, new int[]{6});
        }

        player.openInventory(inventory);
    }

    public void updateGui(Player player, Inventory inventory, int[] states, int... metaTypes) {
        if (!this.amsGuiCache.containsKey(player)) return;
        int[] oldStateData = this.amsStateCache.getOrDefault(player, null);
        int oldState = (oldStateData != null) ? oldStateData[0] : -1;
        int state = states[0];

        Ams ams = this.amsGuiCache.get(player);

        if (state != oldState) {
            this.amsStateCache.put(player, states);
            if (state != 0) {
                for (int i = 0; i < inventory.getSize(); i++)
                    inventory.setItem(i, ItemDefaults.PLACEHOLDER);
            }
        }

        if (state == 0) {
            for (int type : metaTypes) {
                double spawnerPerSecond;
                double powerMult;
                double offGenMult;

                double currentBoost;
                long boostTime;

                List<String> lore;
                ItemMeta meta;
                ItemStack item;

                switch (type) {
                    case 1:
                        item = inventory.getItem(13);
                        meta = item.getItemMeta();
                        lore = meta.getLore();

                        offGenMult = ams.isReady() ? ((ams.isOfflineModeActivate() && ams.getUpgrades().containsKey(
                                EnumAmsUpgrade.OFFLINE_GEN)) ? ((OfflineGenUpgrade) ams.getUpgrades().get(
                                EnumAmsUpgrade.OFFLINE_GEN)).getMultiplier() : 0.0D) : 0.0D;

                        powerMult = ams.isReady() ? (ams.getUpgrades().containsKey(
                                EnumAmsUpgrade.POWER) ? (ams.getUpgrades().get(
                                EnumAmsUpgrade.POWER).getLevel() / 100.0D + 1.0D) : 1.0D) : 1.0D;

                        currentBoost = ams.isReady() ? ams.getCurrentBoost() : 0;
                        boostTime = ams.isReady() ? ams.getBoostTime() : 0L;

                        boolean activeBoost = currentBoost != 0 && boostTime > 0L;

                        spawnerPerSecond = Ams.getCoinsBySpawners(ams.getSpawner());
                        if (powerMult > 1.0D)
                            spawnerPerSecond *= powerMult;
                        if (offGenMult > 0.0D)
                            spawnerPerSecond *= offGenMult;
                        if (activeBoost)
                            spawnerPerSecond *= currentBoost;

                        lore.set(1,
                                "§7Aktueller Boost§8: " + (!activeBoost ? "§cNicht aktiv" : "§e" + DECIMAL_FORMAT.format(
                                        currentBoost) + "% §7für §e" + TimeUtil.timeToString(
                                        boostTime, false)));

                        lore.set(3, "§7Spawner§8: §3" + (ams.isReady() ? Util.formatNumber(
                                ams.getSpawner()) + " Spawner" : "-"));
                        lore.set(4, "§7Coins§8: §e" + (ams.isReady() ? Util.formatNumber(ams.getCoins()) + "$" : "-"));
                        lore.set(5,
                                "§7Max. Coins§8: §e" + (ams.isReady() ? Util.formatNumber(
                                        ams.getMaxCoins()) + "$" : "-"));
                        lore.set(7,
                                "§7Coins / Sekunde§8: §4" + (ams.isReady() ? Util.formatNumber(
                                        spawnerPerSecond) + "$" : "-"));
                        lore.set(8,
                                "§7Coins / Minute§8: §c" + (ams.isReady() ? Util.formatNumber(
                                        spawnerPerSecond * 60.0D) + "$" : "-"));
                        lore.set(9,
                                "§7Coins / Stunde§8: §6" + (ams.isReady() ? Util.formatNumber(
                                        spawnerPerSecond * 60.0D * 60.0D) + "$" : "-"));
                        lore.set(10,
                                "§7Coins / Tag§8: §e" + (ams.isReady() ? Util.formatNumber(
                                        spawnerPerSecond * 60.0D * 60.0D * 24.0D) + "$" : "-"));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        break;
                    case 2:
                        item = inventory.getItem(21);
                        meta = item.getItemMeta();
                        lore = meta.getLore();
                        lore.set(0, "§7Es befinden sich §e" + (ams.isReady() ? Util.formatNumber(
                                ams.getSpawner()) : "-") + " §7Spawner in der AMS.");
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        break;
                    case 3:
                        item = inventory.getItem(20);
                        meta = item.getItemMeta();
                        lore = meta.getLore();

                        lore.set(0, "§7Es wurden §e" + (ams.isReady() ? Util.formatNumber(
                                ams.getCoins()) : "-") + " §7Coins gesammelt.");
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        break;
                }

            }
        }

        if (state == 3) {
            ItemStack addFriend = new ItemBuilder(Material.STAINED_CLAY).setDurability(13).setDisplayName(
                    "§aFreund hinzufügen").build();
            ItemStack notUsed = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(14).setDisplayName(
                    "§cNicht vergeben").build();
            ItemStack back = new ItemBuilder(Material.WOOD_DOOR).setDisplayName("§cZurück zur Übersicht").build();

            inventory.setItem(10, addFriend);
            inventory.setItem(18, back);

            LinkedHashMap<AmsFriend, OfflinePlayer> playerEquivalents = new LinkedHashMap<>();

            for (AmsFriend friend : ams.getFriends()) {
                OfflinePlayer opTarget = Bukkit.getOfflinePlayer(friend.getUuid());
                if (opTarget == null || !opTarget.hasPlayedBefore())
                    continue;
                playerEquivalents.put(friend, opTarget);
            }

            AmsFriend[] friends = playerEquivalents.keySet().toArray(new AmsFriend[0]);

            for (int count = 0; count < 5; count++) {
                if (playerEquivalents.size() <= count)
                    inventory.setItem(12 + count, notUsed);
                else {
                    AmsFriend friend = friends[count];
                    OfflinePlayer offlinePlayer = playerEquivalents.get(friend);
                    ItemStack skull = new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setSkullOwner(
                            offlinePlayer.getName())
                            .setDisplayName("§6" + offlinePlayer.getName()).setLore("",
                                    "§7[Linksklick] : §eFreund entfernen").build();
                    inventory.setItem(12 + count, skull);
                }
            }
        }

        if (state == 6) {
            Map<EnumAmsUpgrade, AmsUpgradeBase> upgrades = ams.getUpgrades();

            int upgradeAmount = ams.getUpgradeAmount();
            List<String> lore = new ArrayList<>(
                    Arrays.asList("", "§7Deine Upgrades§8: §e" + upgradeAmount + "§6/§e400"));
            if (upgradeAmount < 400) {
                lore.addAll(Arrays.asList(
                        "§7Preis für das §6§l" + (upgradeAmount + 1) + ". §7Upgrade§8: §e" + Util.formatNumber(
                                getUpgradePrice(ams)) + "$",
                        "",
                        "§7[Linksklick] : §eZufälliges Upgrade kaufen"));
            }

            ItemStack buy = new ItemBuilder(Material.NETHER_STAR).setDisplayName("§cUpgrade kaufen").setLore(
                    lore).build();

            lore.clear();
            lore.addAll(Arrays.asList("§7Dein Prestiglevel§8: §e" + ams.getPrestigeLevel(), "",
                    "§7[Linksklick] : §ePrestige betreten"));

            ItemStack prestige = new ItemBuilder(Material.EMERALD).setDisplayName("§cPrestige").setLore(lore).build();

            lore.clear();
            int levelPower = upgrades.containsKey(EnumAmsUpgrade.POWER) ? upgrades.get(
                    EnumAmsUpgrade.POWER).getLevel() : 0;
            lore.addAll(Arrays.asList("§7Deine AMS generiert §e" + levelPower + "% §7mehr Coins.", "",
                    "§7Maximum§8: §e100%"));

            if (ams.getPrestigeLevel() > 0)
                lore.addAll(Arrays.asList("", "§7Prestigeboost§8: §e" + (ams.getPrestigeLevel() * 10) + "%"));

            ItemStack power = new ItemBuilder(Material.BEACON).setDisplayName("§aCoinboost").setLore(lore).build();

            lore.clear();
            int levelDoubleCoins = upgrades.containsKey(EnumAmsUpgrade.DOUBLE_COINS) ? upgrades.get(
                    EnumAmsUpgrade.DOUBLE_COINS).getLevel() : 0;
            lore.addAll(Arrays.asList("§7Deine AMS generiert mit einer Chance",
                    "§7von §e" + levelDoubleCoins + "% §7die doppelte Anzahl an Coins.", "", "§7Maximum§8: §e100%"));

            if (ams.getPrestigeLevel() > 0)
                lore.addAll(Arrays.asList("", "§7Prestigeboost§8: §e" + (ams.getPrestigeLevel() * 10) + "%"));

            ItemStack doubleCoins = new ItemBuilder(Material.DOUBLE_PLANT).setDisplayName("§aDoppelte Coins").setLore(
                    lore).build();

            lore.clear();
            int levelOfflineGen = upgrades.containsKey(EnumAmsUpgrade.OFFLINE_GEN) ? upgrades.get(
                    EnumAmsUpgrade.OFFLINE_GEN).getLevel() : 0;
            lore.addAll(
                    Arrays.asList("§7Deine AMS generiert §e" + levelOfflineGen + "% §7während", "§7du offline bist", "",
                            "§7Maximum§8: §e100%"));

            if (ams.getPrestigeLevel() > 0)
                lore.addAll(Arrays.asList("", "§7Prestigeboost§8: §e" + (ams.getPrestigeLevel() * 10) + "%"));

            ItemStack offlineGen = new ItemBuilder(Material.WATCH).setDisplayName("§aOffline-Generation").setLore(
                    lore).build();

            lore.clear();
            int levelMoneyCapacity = upgrades.containsKey(EnumAmsUpgrade.MONEY_CAPACITY) ? upgrades.get(
                    EnumAmsUpgrade.MONEY_CAPACITY).getLevel() : 0;
            lore.addAll(
                    Arrays.asList("§7Deine AMS besitzt §e" + levelMoneyCapacity + "% §7mehr Speicherplatz für Coins.",
                            "",
                            "§7Maximum§8: §e100%"));

            if (ams.getPrestigeLevel() > 0)
                lore.addAll(Arrays.asList("", "§7Prestigeboost§8: §e" + (ams.getPrestigeLevel() * 10) + "%"));

            ItemStack moneyCapacity = new ItemBuilder(Material.CHEST).setDisplayName("§aMehr Speicherplatz").setLore(
                    lore).build();

            ItemStack back = new ItemBuilder(Material.WOOD_DOOR).setDisplayName("§cZurück zur Übersicht").build();

            inventory.setItem(10, buy);
            inventory.setItem(11, prestige);
            inventory.setItem(13, power);
            inventory.setItem(14, doubleCoins);
            inventory.setItem(15, offlineGen);
            inventory.setItem(16, moneyCapacity);
            inventory.setItem(18, back);
        }
    }

    public void updateGuiAll(Ams ams, int... types) {
        this.amsGuiCache.entrySet().stream().filter(entry -> (entry.getValue() == ams))
                .filter(entry -> (this.amsStateCache.containsKey(entry.getKey()) &&
                        this.amsStateCache.get(entry.getKey())[0] == 0)).forEach(entry ->
                updateGui(entry.getKey(), entry.getKey().getOpenInventory().getTopInventory(), new int[]{0}, types));
    }

    public Map<UUID, Ams> getAmsCache() {
        return amsCache;
    }

    public Map<Player, Ams> getAmsGuiCache() {
        return amsGuiCache;
    }

    public Map<Player, int[]> getAmsStateCache() {
        return amsStateCache;
    }

    public long getUpgradePrice(Ams ams) {
        double multiplierPrestige = 1.01D + ams.getPrestigeLevel() * 0.003D;
        double basePrice = 50000.0D;
        for (int i = 0; i < ams.getPrestigeLevel(); i++) {
            double tmpMultPrestige = 1.01D + i * 0.003D;
            basePrice *= tmpMultPrestige;
        }
        return (long) Math.ceil(basePrice * Math.pow(multiplierPrestige, ams.getUpgradeAmount()));
    }
}
