package de.potera.teamhardcore.others.ams;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.TimedDatabaseUpdate;
import de.potera.teamhardcore.others.ams.upgrades.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Ams extends TimedDatabaseUpdate {

    public static final Random RANDOM = new Random();

    private final List<AmsFriend> friends = new ArrayList<>(5);
    private final Map<EnumAmsUpgrade, AmsUpgradeBase> upgrades = new HashMap<>();

    private UUID owner;
    private String ownerName;
    private long spawner;
    private double coins;
    private double maxCoins = 5000000.0D;
    private int prestigeLevel;

    private double currentBoost;
    private long boostTime;

    public Ams(UUID owner, boolean async) {
        super("AMS", true);
        this.owner = owner;

        checkOwnerName();
        initUpgrades();

        if (async)
            loadDataAsync();
        else loadData();
    }

    public static double getCoinsBySpawners(long spawner) {
        return spawner * 0.05D;
    }

    public static boolean shouldStayAlive(Ams ams) {
        return (Bukkit.getPlayer(ams.getOwner()) != null || (ams.getSpawner() > 0L && ams.getUpgrades().containsKey(
                EnumAmsUpgrade.OFFLINE_GEN)) || (Main.getInstance().getAmsManager() != null && Main.getInstance().getAmsManager().getAmsGuiCache().containsValue(
                ams)));
    }

    public UUID getOwner() {
        return owner;
    }

    public Map<EnumAmsUpgrade, AmsUpgradeBase> getUpgrades() {
        return this.upgrades;
    }

    public void addUpgrade(AmsUpgradeBase upgrade) {
        this.upgrades.put(upgrade.getUpgrade(), upgrade);
        setUpdate(true);
    }

    public void clearUpgrades() {
        this.upgrades.clear();
        setUpdate(true);
    }

    private void initUpgrades() {
        for (EnumAmsUpgrade upgrade : EnumAmsUpgrade.values())
            this.upgrades.put(upgrade, upgrade.create(this, 0));
    }

    public int getUpgradeAmount() {
        int amount = 0;
        for (AmsUpgradeBase upgradeBase : this.upgrades.values())
            amount += upgradeBase.getLevel();
        return amount;
    }

    public long getSpawner() {
        return spawner;
    }

    public void setSpawner(long spawner) {
        if (this.spawner == spawner)
            return;

        this.spawner = spawner;
        if (this.spawner < 0L)
            this.spawner = 0L;
        setUpdate(true);
        Main.getInstance().getAmsManager().updateGuiAll(this, 1, 2);
    }

    public long getCoins() {
        return (long) coins;
    }

    public void setCoins(double coins) {
        if (this.coins == coins)
            return;
        this.coins = coins;
        if (this.coins > this.maxCoins)
            this.coins = this.maxCoins;
        setUpdate(true);
        Main.getInstance().getAmsManager().updateGuiAll(this, 1, 3);
    }

    public double getMaxCoins() {
        return maxCoins;
    }

    public void setMaxCoins(double maxCoins) {
        this.maxCoins = maxCoins;
    }

    public List<AmsFriend> getFriends() {
        return friends;
    }

    public boolean isFriend(UUID uuid) {
        return (getFriend(uuid) != null);
    }

    public AmsFriend getFriend(UUID uuid) {
        for (AmsFriend friend : this.friends) {
            if (friend.getUuid().equals(uuid))
                return friend;
        }
        return null;
    }

    public void addFriend(UUID uuid) {
        if (isFriend(uuid) || this.friends.size() >= 5) return;
        AmsFriend friend = new AmsFriend(uuid);
        this.friends.add(friend);
        setUpdate(true);
    }

    public void removeFriend(UUID uuid) {
        if (!isFriend(uuid))
            return;
        AmsFriend friend = null;
        for (AmsFriend amsFriend : this.friends) {
            if (amsFriend.getUuid().equals(uuid)) {
                friend = amsFriend;
                break;
            }
        }
        this.friends.remove(friend);
    }

    public int getPrestigeLevel() {
        return this.prestigeLevel;
    }

    public void setPrestigeLevel(int prestigeLevel) {
        if (this.prestigeLevel == prestigeLevel)
            return;
        this.prestigeLevel = prestigeLevel;
        setUpdate(true);
        Main.getInstance().getAmsManager().updateGuiAll(this, 6);
    }

    public void tick() {
        if (this.spawner == 0L || this.coins >= this.maxCoins)
            return;

        Player offlinePlayer = Bukkit.getPlayer(this.owner);
        double newCoins = getCoinsBySpawners(this.spawner);

        if (offlinePlayer == null && !this.upgrades.containsKey(EnumAmsUpgrade.OFFLINE_GEN))
            return;

        if (this.upgrades.containsKey(EnumAmsUpgrade.POWER)) {
            PowerUpgrade upgrade = (PowerUpgrade) this.upgrades.get(EnumAmsUpgrade.POWER);
            newCoins *= upgrade.getMultiplier();
        }

        if (this.upgrades.containsKey(EnumAmsUpgrade.DOUBLE_COINS)) {
            DoubleCoinsUpgrade upgrade = (DoubleCoinsUpgrade) this.upgrades.get(EnumAmsUpgrade.DOUBLE_COINS);
            if (RANDOM.nextInt(100) < upgrade.getChance())
                newCoins *= 2.0D;
        }

        if (offlinePlayer == null && this.upgrades.containsKey(EnumAmsUpgrade.OFFLINE_GEN)) {
            OfflineGenUpgrade offlineGenUpgrade = (OfflineGenUpgrade) this.upgrades.get(EnumAmsUpgrade.OFFLINE_GEN);
            newCoins *= offlineGenUpgrade.getMultiplier();
        }

        if (getBoostTime() > 0L && getCurrentBoost() != 0.0D) {
            newCoins *= getCurrentBoost();
            setBoostTime(getBoostTime() - 1000L);
        }

        setCoins(this.coins + newCoins);
    }

    public boolean isOfflineModeActivate() {
        Player offlinePlayer = Bukkit.getPlayer(this.owner);
        return (offlinePlayer == null && this.upgrades.containsKey(EnumAmsUpgrade.OFFLINE_GEN));
    }

    private void checkOwnerName() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.owner);
        if (!player.isOnline() && !player.hasPlayedBefore())
            return;
        this.ownerName = player.getName();
    }

    private JSONArray serializeAmsUpgrades() {
        JSONArray mainArray = new JSONArray();
        for (Map.Entry<EnumAmsUpgrade, AmsUpgradeBase> upgrades : this.upgrades.entrySet()) {
            if (upgrades.getValue().getLevel() == 0)
                continue;
            JSONArray upgradeArray = new JSONArray();
            upgradeArray.put(upgrades.getKey().name());
            upgradeArray.put(upgrades.getValue().getLevel());
            mainArray.put(upgradeArray);
        }
        return mainArray;
    }

    private void deserializeAmsUpgrades(String jsonStr) {
        JSONArray mainArray = new JSONArray(jsonStr);
        for (Object obj : mainArray) {
            JSONArray upgradeArray = (JSONArray) obj;
            EnumAmsUpgrade upgradeType = EnumAmsUpgrade.getByName(upgradeArray.getString(0));
            int level = upgradeArray.getInt(1);
            assert upgradeType != null;
            this.upgrades.put(upgradeType, upgradeType.create(this, level));
        }
    }

    private JSONArray serializeAmsFriends() {
        JSONArray mainArray = new JSONArray();
        for (AmsFriend amsFriend : this.friends) {
            JSONObject friendObject = new JSONObject();
            friendObject.put("uuid", amsFriend.getUuid().toString());
            mainArray.put(friendObject);
        }
        return mainArray;
    }

    private void deserializeAmsFriends(String jsonStr) {
        JSONArray mainArray = new JSONArray(jsonStr);
        for (Object obj : mainArray) {
            JSONObject friendObject = (JSONObject) obj;
            UUID uuid = UUID.fromString(friendObject.getString("uuid"));
            this.friends.add(new AmsFriend(uuid));
        }
    }

    public boolean hasPermission(Player player) {
        if (player.getUniqueId().equals(this.owner))
            return true;

        if (player.hasPermission("potera.ams.admin"))
            return true;

        return isFriend(player.getUniqueId());
    }

    public double getCurrentBoost() {
        return currentBoost;
    }

    public void setCurrentBoost(double currentBoost) {
        this.currentBoost = currentBoost;
        setUpdate(true);
    }

    public long getBoostTime() {
        return boostTime;
    }

    public void setBoostTime(long boostTime) {
        this.boostTime = boostTime;
        setUpdate(true);
    }

    @Override
    public void saveData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement stCheck = conn.prepareStatement("SELECT `Owner` FROM `AMS` WHERE `Owner` = ?");
            stCheck.setString(1, this.owner.toString());
            ResultSet rsCheck = stCheck.executeQuery();
            JSONArray arrAmsFriendData = serializeAmsFriends();
            JSONArray arrAmsUpgradeData = serializeAmsUpgrades();
            if (!rsCheck.next()) {
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO `AMS` (`Owner`, `OwnerName`, `Spawners`, `Coins`, `PrestigeLevel`, `Friends`, `Upgrades`, `Boost`, `BoostTime`) VALUES (?,?,?,?,?,?,?,?,?)");

                st.setString(1, this.owner.toString());
                st.setString(2, this.ownerName);
                st.setLong(3, this.spawner);
                st.setDouble(4, this.coins);
                st.setInt(5, this.prestigeLevel);
                st.setString(6, (arrAmsFriendData.length() == 0) ? null : arrAmsFriendData.toString());
                st.setString(7, (arrAmsUpgradeData.length() == 0) ? null : arrAmsUpgradeData.toString());
                st.setDouble(8, this.currentBoost);
                st.setLong(9, this.boostTime);

                Main.getInstance().getDatabaseManager().executeUpdate(st);
            } else {
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE `AMS` SET `OwnerName` = ?, `Spawners` = ?, `Coins` = ?, `PrestigeLevel` = ?, `Friends` = ?, `Upgrades` = ?, `Boost` = ?, `BoostTime` = ? WHERE `Owner` = ?");

                st.setString(1, this.ownerName);
                st.setLong(2, this.spawner);
                st.setDouble(3, this.coins);
                st.setInt(4, this.prestigeLevel);
                st.setString(5, (arrAmsFriendData.length() == 0) ? null : arrAmsFriendData.toString());
                st.setString(6, (arrAmsUpgradeData.length() == 0) ? null : arrAmsUpgradeData.toString());
                st.setDouble(7, this.currentBoost);
                st.setLong(8, this.boostTime);
                st.setString(9, this.owner.toString());
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            }
            Main.getInstance().getDatabaseManager().close(stCheck, rsCheck);
        } catch (SQLException e) {
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

    @Override
    public void loadData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `AMS` WHERE `Owner` = ?");
            st.setString(1, this.owner.toString());
            ResultSet rs = st.executeQuery();
            if (!rs.next()) {
                saveData();
            } else {
                this.ownerName = rs.getString("OwnerName");
                this.spawner = rs.getLong("Spawners");
                this.coins = rs.getDouble("Coins");
                this.prestigeLevel = rs.getInt("PrestigeLevel");

                this.currentBoost = rs.getDouble("Boost");
                this.boostTime = rs.getLong("BoostTime");

                String amsUpgradeDataStr = rs.getString("Upgrades");

                if (amsUpgradeDataStr != null)
                    deserializeAmsUpgrades(amsUpgradeDataStr);

                String amsFriendDataStr = rs.getString("Friends");
                if (amsFriendDataStr != null)
                    deserializeAmsFriends(amsFriendDataStr);
            }
            Main.getInstance().getDatabaseManager().close(st, rs);
            setReady(true);
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
        Bukkit.getScheduler().runTask(Main.getInstance(), this::checkOwnerName);
    }

    @Override
    public void deleteData() {
        getHandlerGroup().removeHandler(this);
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM `AMS` WHERE `Owner` = ?");
            st.setString(1, this.owner.toString());
            Main.getInstance().getDatabaseManager().executeUpdate(st);
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

    @Override
    public void setReady(boolean ready) {
        super.setReady(ready);
        Bukkit.getScheduler().runTask(Main.getInstance(),
                () -> Main.getInstance().getAmsManager().updateGuiAll(this, 1, 2, 3));
    }
}
