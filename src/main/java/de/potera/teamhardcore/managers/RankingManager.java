package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.JSONArray;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RankingManager {

    public static final ItemStack NOT_OCCUPIED_ITEM = new ItemBuilder(Material.BARRIER).setDisplayName(
            "§cNicht vergeben").build();

    private final LinkedHashMap<UUID, Integer> killRanking;
    private final LinkedHashMap<UUID, Long> moneyRanking;
    private final LinkedHashMap<UUID, Long> playtimeRanking;
    private final LinkedHashMap<UUID, Object[]> amsRanking;
    private final LinkedList<Clan> clanRanking;

    private final ScheduledExecutorService executorService;

    public RankingManager() {
        this.killRanking = new LinkedHashMap<>();
        this.moneyRanking = new LinkedHashMap<>();
        this.playtimeRanking = new LinkedHashMap<>();
        this.amsRanking = new LinkedHashMap<>();
        this.clanRanking = new LinkedList<>();

        this.executorService = Executors.newSingleThreadScheduledExecutor();

        startUpdater();
    }

    private void startUpdater() {
        this.executorService.scheduleAtFixedRate(() -> {
            this.moneyRanking.clear();
            this.killRanking.clear();
            this.playtimeRanking.clear();
            this.amsRanking.clear();
            this.clanRanking.clear();

            Connection conn = null;

            try {
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement stKills = conn.prepareStatement(
                        "SELECT `UUID`,`Kills` FROM `Stats` ORDER BY `Kills` DESC");
                PreparedStatement stPlaytime = conn.prepareStatement(
                        "SELECT `UUID`,`Playtime` FROM `Stats` ORDER BY `Playtime` DESC");
                PreparedStatement stClan = conn.prepareStatement(
                        "SELECT `ClanName` FROM `Clan` ORDER BY `Kills` DESC");
                PreparedStatement stAms = conn.prepareStatement(
                        "SELECT `Owner`,`Spawners`,`PrestigeLevel`,`Upgrades` FROM `AMS` ORDER BY `Spawners` DESC, `PrestigeLevel` DESC");
                PreparedStatement stMoney = conn.prepareStatement(
                        "SELECT `UUID`,`Money` FROM `Currency` ORDER BY `Money` DESC");

                ResultSet rsKills = stKills.executeQuery();
                ResultSet rsPlaytime = stPlaytime.executeQuery();
                ResultSet rsClan = stClan.executeQuery();
                ResultSet rsAms = stAms.executeQuery();
                ResultSet rsMoney = stMoney.executeQuery();

                while (rsKills.next()) {
                    UUID uuid = UUID.fromString(rsKills.getString("UUID"));
                    int kills = rsKills.getInt("Kills");
                    this.killRanking.put(uuid, kills);
                }

                while (rsMoney.next()) {
                    UUID uuid = UUID.fromString(rsMoney.getString("UUID"));
                    long money = rsMoney.getLong("Money");
                    this.moneyRanking.put(uuid, money);
                }

                while (rsPlaytime.next()) {
                    UUID uuid = UUID.fromString(rsPlaytime.getString("UUID"));
                    long playtime = rsPlaytime.getLong("Playtime");
                    this.playtimeRanking.put(uuid, playtime);
                }

                while (rsClan.next()) {
                    Clan clan = Main.getInstance().getClanManager().getClan(rsClan.getString("ClanName"));
                    this.clanRanking.add(clan);
                }

                while (rsAms.next()) {
                    UUID uuid = UUID.fromString(rsAms.getString("Owner"));
                    long spawners = rsAms.getLong("Spawners");
                    int prestige = rsAms.getInt("PrestigeLevel");
                    String upgradeStr = rsAms.getString("Upgrades");
                    this.amsRanking.put(uuid, new Object[]{spawners, prestige, upgradeStr});
                }

                Main.getInstance().getDatabaseManager().close(stKills, stMoney, stPlaytime, stClan, stAms);
                Main.getInstance().getDatabaseManager().close(rsKills, rsMoney, rsPlaytime, rsClan, rsAms);
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

        }, 1L, 45, TimeUnit.SECONDS);
    }

    public void onDisable() {
        this.executorService.shutdown();
    }

    public void openRankingInventory(Player player, int state) {
        Inventory inventory = Bukkit.createInventory(null, 9 * 6, StringDefaults.INVENTORY_PREFIX + "Ranking");

        for (int i = 0; i < inventory.getSize(); i++)
            inventory.setItem(i, ItemDefaults.PLACEHOLDER);

        if (state == 0) {
            inventory.setItem(13, new ItemBuilder(Material.SIGN).setDisplayName("§aDein Ranking").build());

            inventory.setItem(28, new ItemBuilder(Material.DOUBLE_PLANT).setDisplayName("§aMoney Ranking").build());
            inventory.setItem(29, new ItemBuilder(Material.FURNACE).setDisplayName("§aAMS Ranking").build());
            inventory.setItem(31, new ItemBuilder(Material.WATCH).setDisplayName("§aSpielzeit Ranking").build());
            inventory.setItem(33,
                    new ItemBuilder(Material.DIAMOND_SWORD).addItemFlags(ItemFlag.HIDE_ATTRIBUTES).setDisplayName(
                            "§aKill Ranking").build());
            inventory.setItem(34,
                    new ItemBuilder(Material.BANNER).setDurability(15).setDisplayName("§aClan Ranking").build());
        }

        if (state != 0)
            inventory.setItem(45, new ItemBuilder(Material.WOOD_DOOR).setDisplayName("§cZurück zur Übersicht").build());

        updateRankingInventory(player, inventory, state);
        player.openInventory(inventory);
    }

    public void updateRankingInventory(Player player, Inventory inventory, int state) {
        if (!inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Ranking")) return;

        int counter = 0;

        if (state == 0) {
            ItemStack signItem = inventory.getItem(13);
            ItemMeta meta = signItem.getItemMeta();
            List<String> lore = new ArrayList<>();

            Clan clan = (Main.getInstance().getClanManager().hasClan(
                    player.getUniqueId()) ? Main.getInstance().getClanManager().getClan(player.getUniqueId()) : null);

            List<UUID> killRanking = new ArrayList<>(this.killRanking.keySet());
            List<UUID> amsRanking = new ArrayList<>(this.amsRanking.keySet());
            List<UUID> moneyRanking = new ArrayList<>(this.moneyRanking.keySet());
            List<UUID> playtimeRanking = new ArrayList<>(this.playtimeRanking.keySet());
            List<Clan> clanRanking = new ArrayList<>(this.clanRanking);

            lore.add("§7Kill Ranking§8: §a" + (killRanking.indexOf(player.getUniqueId()) + 1) + ". Platz");
            lore.add("§7AMS Ranking§8: §a" + (amsRanking.indexOf(player.getUniqueId()) + 1) + ". Platz");
            if (clan != null)
                lore.add("§7Clan Ranking§8: §a" + (clanRanking.indexOf(clan) + 1) + ". Platz");
            lore.add("§7Money Ranking§8: §a" + (moneyRanking.indexOf(player.getUniqueId()) + 1) + ". Platz");
            lore.add("§7Spielzeit Ranking§8: §a" + (playtimeRanking.indexOf(player.getUniqueId()) + 1) + ". Platz");
            meta.setLore(lore);
            signItem.setItemMeta(meta);
        }

        if (state == 1) {
            for (Map.Entry<UUID, Long> entry : this.moneyRanking.entrySet()) {
                UUID uuid = entry.getKey();
                long money = entry.getValue();

                if (counter >= 10) break;

                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                if (op == null || !op.hasPlayedBefore()) continue;

                ItemStack display = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setDisplayName(
                        "§7" + (counter + 1) + ". §e" + op.getName()).setLore(
                        Collections.singletonList("§7Münzen: §e" + Util.formatNumber(money))).setSkullOwner(
                        op.getName()).build();

                if (counter >= 5) {
                    inventory.setItem(20 + counter + 4, display);
                } else {
                    inventory.setItem(20 + counter, display);
                }

                counter++;
            }
        }

        if (state == 2) {
            for (Map.Entry<UUID, Object[]> entry : this.amsRanking.entrySet()) {
                UUID uuid = entry.getKey();
                long spawners = (Long) entry.getValue()[0];
                int prestige = (Integer) entry.getValue()[1];

                if (counter >= 10) break;

                String upgradeString = (String) entry.getValue()[2];
                int upgrades = (upgradeString == null ? 0 : getAmountOfUpgrades(upgradeString));


                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                if (op == null || !op.hasPlayedBefore()) continue;

                ItemStack display = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setDisplayName(
                        "§7" + (counter + 1) + ". §e" + op.getName()).setLore(Arrays.asList(
                        "§7Spawner: §e" + Util.formatNumber(spawners),
                        "§7Upgrades§8: §e" + upgrades + "/400",
                        "§7Prestige§8: §e" + prestige))
                        .setSkullOwner(op.getName()).build();

                if (counter >= 5) {
                    inventory.setItem(20 + counter + 4, display);
                } else {
                    inventory.setItem(20 + counter, display);
                }

                counter++;
            }
        }

        if (state == 3) {
            for (Map.Entry<UUID, Long> entry : this.playtimeRanking.entrySet()) {
                UUID uuid = entry.getKey();
                long playtime = entry.getValue();

                if (counter >= 10) break;

                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                if (op == null || !op.hasPlayedBefore()) continue;

                ItemStack display = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setDisplayName(
                        "§7" + (counter + 1) + ". §e" + op.getName()).setLore(
                        Collections.singletonList(
                                "§7Spielzeit: §e" + TimeUtil.timeToString(playtime, false))).setSkullOwner(
                        op.getName()).build();

                if (counter >= 5) {
                    inventory.setItem(20 + counter + 4, display);
                } else {
                    inventory.setItem(20 + counter, display);
                }

                counter++;
            }
        }

        if (state == 4) {
            for (Map.Entry<UUID, Integer> entry : this.killRanking.entrySet()) {
                UUID uuid = entry.getKey();
                int kills = entry.getValue();

                if (counter >= 10) break;

                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                if (op == null || !op.hasPlayedBefore()) continue;

                ItemStack display = new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setDisplayName(
                        "§7" + (counter + 1) + ". §e" + op.getName()).setLore(
                        Collections.singletonList("§7Kills: §e" + kills)).setSkullOwner(
                        op.getName()).build();

                if (counter >= 5) {
                    inventory.setItem(20 + counter + 4, display);
                } else {
                    inventory.setItem(20 + counter, display);
                }
                counter++;
            }
        }

        if (state == 5) {
            for (Clan clan : this.clanRanking) {
                if (counter >= 10) break;
                Material material = (counter > 2) ? Material.LEATHER_HELMET : ((counter > 1) ? Material.GOLD_HELMET : ((counter > 0) ? Material.IRON_HELMET : Material.DIAMOND_HELMET));

                ItemStack display = new ItemBuilder(material).setDisplayName(
                        "§7" + (counter + 1) + ". §e" + clan.getName()).setLore(
                        Collections.singletonList("§7Kills: §e" + clan.getKills())).build();

                if (counter >= 5) {
                    inventory.setItem(20 + counter + 4, display);
                } else {
                    inventory.setItem(20 + counter, display);
                }
                counter++;
            }
        }

        if (state != 0 && counter < 10) {
            for (int i = counter; i < 10; i++) {
                if (counter >= 5) {
                    inventory.setItem(20 + counter + 4, NOT_OCCUPIED_ITEM);
                } else {
                    inventory.setItem(20 + counter, NOT_OCCUPIED_ITEM);
                }
                counter++;
            }
        }
    }

    private int getAmountOfUpgrades(String jsonString) {
        int amount = 0;

        JSONArray mainArray = new JSONArray(jsonString);
        for (Object obj : mainArray) {
            JSONArray upgradeArray = (JSONArray) obj;
            int level = upgradeArray.getInt(1);
            amount += level;
        }

        return amount;
    }

}
