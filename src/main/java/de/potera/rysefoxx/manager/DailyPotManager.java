package de.potera.rysefoxx.manager;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.files.FileBase;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.utils.DateFormats;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DailyPotManager {

    private List<String> members;
    private long deployment;
    private boolean canJoin;
    private final FileBase config;

    public DailyPotManager() {
        this.config = new FileBase("", "dailypot");
        if (this.config.getConfig().getStringList("members") != null) {
            members = this.config.getConfig().getStringList("members");
        } else {
            members = new ArrayList<>();
        }
        this.deployment = this.config.getConfig().getLong("deployment");
        this.canJoin = this.config.getConfig().getBoolean("canjoin");
        load();
    }

    public void reset() {
        this.deployment = 0;
        this.members = new ArrayList<>();
        this.canJoin = true;
    }

    public void load() {
        String time = DateFormats.FORMAT_TIME.format(new Date());

        new BukkitRunnable() {
            int tickedSeconds = 0;

            @Override
            public void run() {
                switch (time) {
                    case "17:44:59":
                        Bukkit.broadcastMessage(StringDefaults.DAILYPOT_PREFIX + "§7Der Dailypot wird in §c15 Minuten §7ausgelost.");
                        break;
                    case "17:54:59":
                        Bukkit.broadcastMessage(StringDefaults.DAILYPOT_PREFIX + "§7Der Dailypot wird in §c5 Minuten §7ausgelost.");
                        break;
                    case "17:59:59":
                        if (members.isEmpty()) {
                            Bukkit.broadcastMessage(StringDefaults.PREFIX + "§cDailypot wurde beendet! Nicht genügend Teilnehmer.");
                            reset();
                            cancel();
                            return;
                        }

                        Bukkit.broadcastMessage(StringDefaults.DAILYPOT_PREFIX + "§7Der Dailypot wird nun ausgelost!");
                        canJoin = false;

                        tickedSeconds++;

                        if (tickedSeconds == 3) {
                            Bukkit.broadcastMessage(StringDefaults.DAILYPOT_PREFIX + "§7Von §c" + Util.formatBigNumber(Main.getInstance().getDailyPotManager().getMembers().size()) + " Spielern §7wird nur einer die §6" + Util.formatBigNumber(Main.getInstance().getDailyPotManager().getDeployment()) + "$ §7gewinnen.");
                        } else if (tickedSeconds == 6) {
                            Bukkit.broadcastMessage(StringDefaults.DAILYPOT_PREFIX + "§7Der Gewinner lautet..");
                        } else if (tickedSeconds == 8) {
                            Collections.shuffle(Main.getInstance().getDailyPotManager().getMembers());

                            // GETTING RANDOM UUID FROM LIST //

                            String playerUUID = Main.getInstance().getDailyPotManager().getMembers().get(Util.randInt(0, Main.getInstance().getDailyPotManager().getMembers().size()));

                            // ANNOUNCE WINNER //

                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
                            Bukkit.broadcastMessage(StringDefaults.DAILYPOT_PREFIX + "§c" + offlinePlayer.getName() + "! §7Herzlichen Glückwunsch!");

                            //  ADDING DAILYPOT //

                            User user = Main.getInstance().getUserManager().getUser(UUID.fromString(playerUUID));
                            user.getUserCurrency().addMoney(Main.getInstance().getDailyPotManager().getDeployment());
                            if (offlinePlayer.isOnline()) {
                                ((Player) offlinePlayer).sendMessage(StringDefaults.DAILYPOT_PREFIX + "§7Du hast §c" + Util.formatBigNumber(Main.getInstance().getDailyPotManager().getDeployment()) + " Münzen §7gewonnen!");
                            }

                            // RESET //
                            reset();
                            cancel();

                        }

                        break;
                    default:
                        break;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);


    }

    public void onDisable() {
        config.getConfig().set("deployment", this.deployment);
        config.getConfig().set("members", this.members);
        config.getConfig().set("canjoin", this.canJoin);
        config.saveConfig();
    }

    public void addDeployment(long deployment) {
        this.deployment += deployment;
    }

    public void addPlayer(Player player) {
        members.add(player.getUniqueId().toString());
    }

    public void removePlayer(Player player) {
        members.remove(player.getUniqueId().toString());
    }

    public boolean alreadyJoined(Player player) {
        return members.contains(player.getUniqueId().toString());
    }

    public long getDeployment() {
        return deployment;
    }

    public List<String> getMembers() {
        return members;
    }

    public boolean canJoin() {
        return canJoin;
    }

    public FileBase getConfig() {
        return config;
    }
}
