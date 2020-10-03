package de.potera.teamhardcore.users;

import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {

    private final UUID uuid;
    private final Player player;

    private final int homeLimit;

    private UserData userData;
    private UserCurrency userCurrency;
    private UserStats userStats;
    private UserHomes userHomes;
    private UserMine userMine;
    private UserWarn userWarn;

    public User(UUID uuid) {
        this(uuid, true, true, UserDataType.values());
    }

    public User(UUID uuid, boolean async, boolean timedUpdate, UserDataType... types) {
        Set<UserDataType> loadingTypes = new HashSet<>(Arrays.asList(types));
        this.uuid = uuid;
        this.player = Bukkit.getPlayer(this.uuid);

        this.homeLimit = 100;

        if (loadingTypes.contains(UserDataType.DATA)) {
            this.userData = new UserData(this, async);
        }
        if (loadingTypes.contains(UserDataType.CURRENCY)) {
            this.userCurrency = new UserCurrency(this, async, timedUpdate);
        }
        if (loadingTypes.contains(UserDataType.STATS))
            this.userStats = new UserStats(this, async, timedUpdate);
        if (loadingTypes.contains(UserDataType.HOME))
            this.userHomes = new UserHomes(this, async);
        if (loadingTypes.contains(UserDataType.MINE))
            this.userMine = new UserMine(this, async, timedUpdate);
        if (loadingTypes.contains(UserDataType.WARN))
            this.userWarn = new UserWarn(this, async, timedUpdate);
    }

    public void unload() {
        if (this.userCurrency != null) {
            this.userCurrency.saveData();
            this.userCurrency.getHandlerGroup().removeHandler(this.userCurrency);
        }

        if (this.userStats != null) {
            this.userStats.saveData();
            this.userStats.getHandlerGroup().removeHandler(this.userStats);
        }

        if (this.userMine != null) {
            this.userMine.saveDataAsync();
            this.userMine.getHandlerGroup().removeHandler(this.userMine);
        }
        LogManager.getLogger(User.class).info("User - " + getUuid().toString() + " unload called");
    }

    public int getHomeLimit() {
        return homeLimit;
    }

    public UserMine getUserMine() {
        return userMine;
    }

    public UserHomes getUserHomes() {
        return userHomes;
    }

    public UserStats getUserStats() {
        return userStats;
    }

    public UserCurrency getUserCurrency() {
        return userCurrency;
    }

    public UserWarn getUserWarn() {
        return userWarn;
    }

    public UserData getUserData() {
        return userData;
    }

    public Player getPlayer() {
        return player;
    }

    public UUID getUuid() {
        return uuid;
    }

}
