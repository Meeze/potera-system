package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.StatsPeriod;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.users.UserMine;
import de.potera.teamhardcore.users.UserStats;
import de.potera.teamhardcore.utils.StringDefaults;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class UserManager {

    private final Map<UUID, User> cachedUsers = new HashMap<>();

    public UserManager() {
        Main.getInstance().getDatabaseManager().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `Currency` (`UUID` VARCHAR(36), `Money` BIGINT, UNIQUE KEY(`UUID`))");
        Main.getInstance().getDatabaseManager().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `Stats` (`UUID` VARCHAR(36), `Kills` INT, `DKills` INT, `WKills` INT, `Deaths` INT, `DDeaths` INT, `WDeaths` INT, `Killstreak` INT, `PvPCoins` INT, `Kopfgeld` BIGINT, `Playtime` BIGINT, `DPlaytime` BIGINT, `WPlaytime` BIGINT, `Elo` INT, UNIQUE KEY (`UUID`))");
        Main.getInstance().getDatabaseManager().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `Homes` (`UUID` VARCHAR(36), `Name` VARCHAR(20), `X` DOUBLE, `Y` DOUBLE, `Z` DOUBLE, `Yaw` FLOAT, `Pitch` FLOAT, `World` VARCHAR(50), `CreationDate` BIGINT, `LastTeleportDate` BIGINT)");
        Main.getInstance().getDatabaseManager().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `Mines` (`UUID` VARCHAR(36), `Level` INT, `Minepoints` BIGINT, `AvailableTime` BIGINT, UNIQUE KEY(`UUID`))");

        loadUsersForAll();
        startCheckStatsPeriodTask();
        startCheckMineAccessTask();
        startCheckStateTask();
    }

    private void loadUsersForAll() {
        for (Player all : Bukkit.getOnlinePlayers())
            addToCache(all.getUniqueId());
    }

    public void addToCache(UUID uuid) {
        if (this.cachedUsers.containsKey(uuid))
            return;
        User user = new User(uuid);
        this.cachedUsers.put(uuid, user);
    }

    public void removeFromCache(UUID uuid) {
        if (!this.cachedUsers.containsKey(uuid))
            return;
        User user = this.cachedUsers.get(uuid);
        user.unload();
        this.cachedUsers.remove(uuid);
        LogManager.getLogger(UserManager.class).info("User " + user.getUuid().toString() + " unload finished");
        if (this.cachedUsers.containsKey(uuid))
            LogManager.getLogger(UserManager.class).error(
                    "User " + uuid.toString() + " could not be removed from cache");
    }

    public User getUser(UUID uuid) {
        if (!this.cachedUsers.containsKey(uuid)) {
            addToCache(uuid);
            System.out.println("------------- ERROR DETECTED -------------");
            for (StackTraceElement el : Thread.currentThread().getStackTrace())
                System.out.println(el);
            System.out.println("-------------------");
        }
        return this.cachedUsers.get(uuid);
    }

    private void startCheckStateTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player all : Bukkit.getOnlinePlayers()) {
                    UserData userData = getUser(all.getUniqueId()).getUserData();
                    userData.checkState();
                }
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    private void startCheckMineAccessTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (User user : UserManager.this.cachedUsers.values()) {
                    UserMine userMine = user.getUserMine();
                    userMine.checkTime();
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20L, 20L);
    }

    private void startCheckStatsPeriodTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                UserManager.this.checkStatsPeriod();
            }
        }.runTaskTimer(Main.getInstance(), 20L, 1200L);
    }

    private void checkStatsPeriod() {
        Calendar pastCalendar = Calendar.getInstance(Locale.GERMAN);
        pastCalendar.setTimeInMillis(
                (Long) Main.getInstance().getGeneralManager().getSystemData().get("StatsPeriodTime"));
        Calendar nowCalendar = Calendar.getInstance(Locale.GERMAN);
        nowCalendar.setTimeInMillis(System.currentTimeMillis());

        boolean newDay = (pastCalendar.get(Calendar.DAY_OF_YEAR) != nowCalendar.get(
                Calendar.DAY_OF_YEAR) || pastCalendar.get(
                Calendar.YEAR) != pastCalendar.get(Calendar.YEAR));

        boolean newWeek = (pastCalendar.get(Calendar.WEEK_OF_YEAR) != nowCalendar.get(
                Calendar.WEEK_OF_YEAR) || pastCalendar.get(
                Calendar.YEAR) != nowCalendar.get(Calendar.YEAR));

        if (newDay || newWeek) {
            for (Player all : Bukkit.getOnlinePlayers())
                all.playSound(all.getLocation(), Sound.LEVEL_UP, 1.0F, 0.5F);
            Main.getInstance().getGeneralManager().getSystemData().put("StatsPeriodTime", System.currentTimeMillis());
            Main.getInstance().getGeneralManager().saveSystemData();
        }

        if (newWeek) {
            resetStatsForPeriod(StatsPeriod.WEEKLY);
            Bukkit.broadcastMessage(
                    StringDefaults.STATS_PREFIX + "§7Die wöchentlichen Statistiken wurden zurückgesetzt.");
        }

        if (newDay) {
            resetStatsForPeriod(StatsPeriod.DAILY);
            Bukkit.broadcastMessage(
                    StringDefaults.STATS_PREFIX + "§7Die täglichen Statistiken wurden zurückgesetzt.");
        }
    }

    private void resetStatsForPeriod(StatsPeriod period) {
        if (period == StatsPeriod.ALL)
            return;
        Main.getInstance().getDatabaseManager().getExecutor().execute(() -> {
            Connection conn = null;
            try {
                String tablePrefix = (period == StatsPeriod.WEEKLY) ? "W" : "D";
                String killTable = tablePrefix + "Kills";
                String deathTable = tablePrefix + "Deaths";
                String playtimeTable = tablePrefix + "Playtime";
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement st = conn.prepareCall(
                        "UPDATE `Stats` SET `" + killTable + "` = 0, `" + deathTable + "` = 0, `" + playtimeTable + "` = 0");

                Main.getInstance().getDatabaseManager().executeUpdate(st);
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
        });
        for (Map.Entry<UUID, User> userEntry : this.cachedUsers.entrySet()) {
            User user = userEntry.getValue();
            UserStats us = user.getUserStats();
            us.setKills(0, period);
            us.setDeaths(0, period);
            us.setPlaytime(0L, period);
        }
    }

    public int getAdjustedElo(Player loser, Player winner) {
        UserStats statsLoser = getUser(loser.getUniqueId()).getUserStats();
        UserStats statsWinner = getUser(winner.getUniqueId()).getUserStats();
        long eloLoser = statsLoser.getElo();
        long eloWinner = statsWinner.getElo();
        double diff = (eloLoser - eloWinner);
        double elo = Math.min(eloLoser * 0.1D, 50.0D);

        if (diff >= 0.0D) {
            if (diff > 2000.0D) {
                elo *= 2.0D;
            } else if (diff > 1000.0D) {
                elo *= (1.6D + (diff - 1000.0D) / 1000.0D * 0.4D);
            } else if (diff > 800.0D) {
                elo *= (1.45D + (diff - 800.0D) / 200.0D * 0.15D);
            } else if (diff > 600.0D) {
                elo *= (1.3D + (diff - 600.0D) / 200.0D * 0.15D);
            } else if (diff > 400.0D) {
                elo *= (1.15D + (diff - 400.0D) / 200.0D * 0.15D);
            } else if (diff > 200.0D) {
                elo *= (1.0D + (diff - 200.0D) / 200.0D * 0.15D);
            } else if (eloLoser < 500 && diff > 100.0D) {
                elo *= 0.75D;
            }
        } else if (diff < -2000.0D) {
            elo = 0.0D;
        } else if (diff < -1000.0D) {
            elo *= (0.4D - (diff + 1000.0D) / -1000.0D * 0.4D);
        } else if (diff < -800.0D) {
            elo *= (0.55D - (diff + 800.0D) / -200.0D * 0.15D);
        } else if (diff < -600.0D) {
            elo *= (0.7D - (diff + 600.0D) / -200.0D * 0.15D);
        } else if (diff < -400.0D) {
            elo *= (0.85D - (diff + 400.0D) / -200.0D * 0.15D);
        } else if (diff < -200.0D) {
            elo *= (1.0D - (diff + 200.0D) / -200.0D * 0.15D);
        }
        return (int) elo;
    }

}
