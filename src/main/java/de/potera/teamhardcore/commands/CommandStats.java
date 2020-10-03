package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.StatsPeriod;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.users.UserDataType;
import de.potera.teamhardcore.users.UserStats;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.TimeUtil;
import de.potera.teamhardcore.utils.UUIDFetcher;
import de.potera.teamhardcore.utils.Util;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStats implements CommandExecutor {
    public static void sendStats(Player player, OfflinePlayer target, StatsPeriod period) {
        User user = target.isOnline() ? Main.getInstance().getUserManager().getUser(target.getUniqueId()) : new User(
                target.getUniqueId(), true, false,
                UserDataType.CURRENCY, UserDataType.STATS);
        UserStats stats = user.getUserStats();
        UserCurrency currency = user.getUserCurrency();

        String clanString = (Main.getInstance().getClanManager().hasClan(
                target.getUniqueId()) ? Main.getInstance().getClanManager().getClan(
                target.getUniqueId()).getClanColor() + Main.getInstance().getClanManager().getClan(
                target.getUniqueId()).getName() : "§7Kein Clan");

        player.sendMessage(StringDefaults.HEADER);
        if (period == StatsPeriod.ALL) {
            player.sendMessage("§7§lGesamtstatistiken");
            player.sendMessage(" ");
            player.sendMessage("§7Name§8: §e" + target.getName() + " §8/ §7Rang§8: §eKein Rang(Template)");
            if (stats.isReady()) {
                player.sendMessage(
                        "§7Kills§8: §e" + stats.getKills(StatsPeriod.ALL) + " §8/ §7Tode§8: §e" + stats.getDeaths(
                                StatsPeriod.ALL) + " §8/ §7KD§8: §e" + stats.getKD(StatsPeriod.ALL));
                player.sendMessage(
                        "§7Killstreak§8: §e" + stats.getKillstreak() + " §8/ §7Elo8: §e" + Util.formatNumber(
                                stats.getElo()) + " §8/ §7Kopfgeld§8: §e" + Util.formatBigNumber(
                                stats.getKopfgeld()));
                player.sendMessage("§7Clan§8: §e" + clanString);
                player.sendMessage("§7Geld§8: §e" + Util.formatBigNumber(currency.getMoney()) + "$");
                player.sendMessage(
                        "§7Spielzeit§8: §e" + TimeUtil.timeToString(stats.getPlaytime(StatsPeriod.ALL), false));
            }
            player.sendMessage(" ");
            new JSONMessage("§7Zeitrahmen§8: ")
                    .then("§7§l[Gesamt] ")
                    .then("§e§l[Tag] ").runCommand("/stats " + target.getName() + " T")
                    .then("§e§l[Woche]").runCommand("/stats " + target.getName() + " W").send(player);
        } else if (period == StatsPeriod.WEEKLY) {
            player.sendMessage("§7§lWochenstatistiken");
            player.sendMessage(" ");
            if (stats.isReady()) {
                player.sendMessage(
                        "§7Kills§8: §e" + stats.getKills(StatsPeriod.WEEKLY) + " §8/ §7Tode§8: §e" + stats.getDeaths(
                                StatsPeriod.WEEKLY) + " §8/ §7KD§8: §e" + stats.getKD(StatsPeriod.WEEKLY));
                player.sendMessage(
                        "§7Spielzeit§8: §e" + TimeUtil.timeToString(stats.getPlaytime(StatsPeriod.WEEKLY), false));
            }
            player.sendMessage(" ");
            new JSONMessage("§7Zeitrahmen§8: ")
                    .then("§e§l[Gesamt] ").runCommand("/stats " + target.getName())
                    .then("§e§l[Tag] ").runCommand("/stats " + target.getName() + " T")
                    .then("§7§l[Woche] ").send(player);
        } else if (period == StatsPeriod.DAILY) {
            player.sendMessage("§7§lTagesstatistiken");
            player.sendMessage(" ");
            if (stats.isReady()) {
                player.sendMessage(
                        "§7Kills§8: §e" + stats.getKills(StatsPeriod.DAILY) + " §8/ §7Tode§8: §e" + stats.getDeaths(
                                StatsPeriod.DAILY) + " §8/ §7KD§8: §e" + stats.getKD(StatsPeriod.DAILY));
                player.sendMessage(
                        "§7Spielzeit§8: §e" + TimeUtil.timeToString(stats.getPlaytime(StatsPeriod.DAILY), false));
            }
            player.sendMessage(" ");
            new JSONMessage("§7Zeitrahmen§8: ")
                    .then("§e§l[Gesamt] ").runCommand("/stats " + target.getName())
                    .then("§7§l[Tag] ")
                    .then("§e§l[Woche]").runCommand("/stats " + target.getName() + " W").send(player);
        }
        player.sendMessage(StringDefaults.FOOTER);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length > 2) {
            p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " [Spieler] [T/M]");
            return true;
        }

        if (args.length == 0) {
            sendStats(p, p, StatsPeriod.ALL);
            return true;
        }

        if (args.length == 1) {
            StatsPeriod period = StatsPeriod.getPeriodByInput(args[0]);

            if (period != null) {
                sendStats(p, p, period);
                return true;
            }

            Player targetOnline = Bukkit.getPlayer(args[0]);

            if (targetOnline != null) {
                sendStats(p, targetOnline, StatsPeriod.ALL);
                return true;
            }

            UUIDFetcher.getUUID(args[0], uuid -> {
                if (uuid == null) {
                    p.sendMessage(StringDefaults.PREFIX + "§cDieser Spieler wurde nicht gefunden.");
                } else {
                    OfflinePlayer opTarget = Bukkit.getOfflinePlayer(uuid);
                    if (opTarget == null || !opTarget.hasPlayedBefore()) {
                        p.sendMessage(StringDefaults.PREFIX + "§cDer Spieler war noch nie auf dem Server.");
                        return;
                    }
                    sendStats(p, opTarget, StatsPeriod.ALL);
                }
            });
        }

        if (args.length == 2) {

            StatsPeriod period = StatsPeriod.getPeriodByInput(args[1]);

            if (period == null) {
                p.sendMessage(StringDefaults.PREFIX + "§cBitte gebe einen gültigen Zeitrahmen an.");
                return true;
            }

            Player targetOnline = Bukkit.getPlayer(args[0]);

            if (targetOnline != null) {
                sendStats(p, targetOnline, period);
                return true;
            }

            UUIDFetcher.getUUID(args[0], uuid -> {
                if (uuid == null) {
                    p.sendMessage(StringDefaults.PREFIX + "§cDieser Spieler wurde nicht gefunden.");
                } else {
                    OfflinePlayer opTarget = Bukkit.getOfflinePlayer(uuid);
                    if (opTarget == null || !opTarget.hasPlayedBefore()) {
                        p.sendMessage(StringDefaults.PREFIX + "§cDer Spieler war noch nie auf dem Server.");
                        return;
                    }
                    sendStats(p, opTarget, period);
                }
            });
        }
        return true;
    }

}
