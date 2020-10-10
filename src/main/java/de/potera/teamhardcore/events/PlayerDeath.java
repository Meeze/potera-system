package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumLiga;
import de.potera.teamhardcore.others.EnumSettings;
import de.potera.teamhardcore.others.KillstreakData;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.users.UserStats;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public class PlayerDeath implements Listener {

    private static final Random RANDOM = new Random();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();

        event.setDeathMessage(null);

        User userPlayer = Main.getInstance().getUserManager().getUser(player.getUniqueId());

        if (killer != null && killer != player && killer.isOnline()) {
            player.sendMessage(StringDefaults.PREFIX + "§eDu wurdest von §7" + killer.getName() + " §egetötet!");
            killer.sendMessage(StringDefaults.PREFIX + "§eDu hast §7" + player.getName() + " §egetötet!");

            User userKiller = Main.getInstance().getUserManager().getUser(killer.getUniqueId());

            KillstreakData killstreakData = Main.getInstance().getGeneralManager().getPlayerKillstreaks().get(
                    killer.getUniqueId());

            if (killstreakData == null) {
                killstreakData = new KillstreakData(killer.getUniqueId());
                Main.getInstance().getGeneralManager().getPlayerKillstreaks().put(killer.getUniqueId(), killstreakData);
            }

            boolean streakAccepted = killstreakData.addStreak(player);
            int streak = killstreakData.getStreak();

            if (!streakAccepted) {
                killer.sendMessage(
                        StringDefaults.PVP_PREFIX + "§cDer Kill an §7" + player.getName() + " §cwurde nicht gewertet.");
                killer.sendMessage(
                        StringDefaults.PVP_PREFIX + "§cWarte eine Stunde, bis du diesen Spieler wieder töten kannst.");
            } else {
                if (streak % 10 == 0) {
                    Bukkit.broadcastMessage(
                            StringDefaults.PVP_PREFIX + "§e" + killer.getName() + " §7eine eine Killstreak von §e" + streak + "§7!");
                }

                UserStats userStatsPlayer = userPlayer.getUserStats();
                userStatsPlayer.addDeaths(1);
                userStatsPlayer.setKillstreak(0);

                if (Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    Clan clan = Main.getInstance().getClanManager().getClan(player.getUniqueId());
                    clan.setDeaths(clan.getDeaths() + 1);
                }

                UserStats userStatsKiller = userKiller.getUserStats();
                userStatsKiller.addKills(1);
                userStatsKiller.addPvPCoins(1);
                userStatsKiller.setKillstreak(killstreakData.getStreak());

                if (Main.getInstance().getClanManager().hasClan(killer.getUniqueId())) {
                    Clan clanKiller = Main.getInstance().getClanManager().getClan(killer.getUniqueId());
                    clanKiller.setKills(clanKiller.getKills() + 1);
                }

                if (userStatsPlayer.getKopfgeld() > 0L) {
                    long kopfgeld = userStatsPlayer.getKopfgeld();
                    UserCurrency ucKiller = Main.getInstance().getUserManager().getUser(
                            killer.getUniqueId()).getUserCurrency();
                    ucKiller.addMoney(kopfgeld);
                    userStatsPlayer.setKopfgeld(0L);

                    String broadcast = StringDefaults.PVP_PREFIX + "§e" + killer.getName() + " §7hat sich das Kopfgeld von §e" + player.getName() + " §7geholt! §8(§a" + Util.formatNumber(
                            kopfgeld) + "$§8)";

                    Bukkit.getOnlinePlayers().stream().filter(all -> (all != killer)).forEach(
                            all -> all.sendMessage(broadcast));
                    Bukkit.getConsoleSender().sendMessage(broadcast);

                    killer.sendMessage(
                            StringDefaults.PVP_PREFIX + "§7Du hast dir das Kopfgeld von §e" + player.getName() + " §7geholt. §8(§a" + Util.formatNumber(
                                    kopfgeld) + "$§8)");
                }

                int elo = Main.getInstance().getUserManager().getAdjustedElo(player, killer);

                if (userStatsPlayer.getElo() < elo)
                    elo = (int) userStatsPlayer.getElo();

                long eloBeforePlayer = userStatsPlayer.getElo();
                long eloBeforeKiller = userStatsKiller.getElo();

                userStatsPlayer.setElo((int) (userStatsPlayer.getElo() - elo));
                userStatsKiller.setElo((int) (userStatsKiller.getElo() + elo));

                Util.sendActionbarMessage(player, "§c§lELO " + StringDefaults.PREFIX + "§c§l- " + elo + " §6§lElo");
                Util.sendActionbarMessage(killer, "§c§lELO " + StringDefaults.PREFIX + "§a§l+ " + elo + " §2§lElo");

                if (EnumLiga.checkRankswitch((int) eloBeforePlayer, (int) userStatsPlayer.getElo())) {
                    player.sendMessage("§c§lELO " + StringDefaults.PREFIX + "§6Du bist abgestiegen!");
                    player.sendMessage("§c§lELO " + StringDefaults.PREFIX + "§6Deine neue Liga§8: " + EnumLiga.getLiga(
                            userStatsPlayer.getElo()).getDisplayName());
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                }

                if (EnumLiga.checkRankswitch((int) eloBeforeKiller, (int) userStatsKiller.getElo())) {
                    killer.sendMessage("§c§lELO " + StringDefaults.PREFIX + "§6Du bist aufgestiegen!");
                    killer.sendMessage("§c§lELO " + StringDefaults.PREFIX + "§6Deine neue Liga§8: " + EnumLiga.getLiga(
                            userStatsKiller.getElo()).getDisplayName());
                    killer.playSound(killer.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                }

                if (Main.getInstance().getCombatManager().isTagged(killer)) {
                    Main.getInstance().getCombatManager().setTagged(killer, false);
                    killer.sendMessage(
                            StringDefaults.PVP_PREFIX + "§aDu bist nicht mehr im Kampf! Du kannst dich sicher ausloggen.");
                }

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (all == player || all == killer) continue;
                    UserData userDataAll = Main.getInstance().getUserManager().getUser(all.getUniqueId()).getUserData();
                    if (userDataAll.getSettingsOption(EnumSettings.DEATH_MSG) == 0)
                        all.sendMessage(
                                StringDefaults.PREFIX + "§7" + player.getName() + " §ewurde von §7" + killer.getName() + " §egetötet!");
                }

            }
        } else {
            player.sendMessage(StringDefaults.PREFIX + "§eDu bist gestorben!");

            UserStats userStatsPlayer = userPlayer.getUserStats();
            userStatsPlayer.addDeaths(1);

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all == player) continue;
                UserData userDataAll = Main.getInstance().getUserManager().getUser(all.getUniqueId()).getUserData();
                if (userDataAll.getSettingsOption(EnumSettings.DEATH_MSG) == 0)
                    all.sendMessage(
                            StringDefaults.PREFIX + "§7" + player.getName() + " §eist gestorben!");
            }
        }
    }

}
