package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Report;
import de.potera.teamhardcore.utils.DateFormats;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.UUIDFetcher;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;

public class CommandReport implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length == 0 || args.length > 2) {
            sendHelp(player, label);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("info")) {
                if (!player.hasPermission("potera.report.mod")) {
                    sendHelp(player, label);
                    return true;
                }

                if (Main.getInstance().getReportManager().getReports().isEmpty()) {
                    player.sendMessage(StringDefaults.REPORT_PREFIX + "§cEs gibt dereit keine offenen Reports.");
                    return true;
                }

                player.sendMessage(" ");
                player.sendMessage(StringDefaults.PREFIX + "§cFolgende Spieler wurden reportet:");
                for (UUID repUuid : Main.getInstance().getReportManager().getReports().keySet()) {
                    OfflinePlayer opRep = Bukkit.getOfflinePlayer(repUuid);
                    if (opRep == null || !opRep.hasPlayedBefore())
                        return true;
                    JSONMessage message = new JSONMessage(" §8-");
                    message.then((opRep.isOnline() ? "§a" : "§7") + opRep.getName()).runCommand(
                            "/report info " + opRep.getName());
                    message.send(player);
                }

                player.sendMessage("");
                player.sendMessage(" §eKlicke auf einen Namen für weitere Details.");
            } else {
                sendHelp(player, label);
                return true;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("info")) {
                if (!player.hasPermission("potera.report.mod")) {
                    sendHelp(player, label);
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target != null) {
                    if (!Main.getInstance().getReportManager().isReported(target.getUniqueId())) {
                        player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDer Spieler wurde nicht reportet.");
                        return true;
                    }

                    Report report = Main.getInstance().getReportManager().getReport(target.getUniqueId());
                    player.sendMessage(" ");
                    player.sendMessage(" §cFolgende Spieler haben §7" + target.getName() + " §creportet:");

                    for (Report.ReportEntry entry : report.getReportEntries()) {
                        OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getUuid());
                        if (op == null)
                            continue;
                        player.sendMessage(
                                " §8- §r" + (op.isOnline() ? "§a" : "§7") + op.getName() + " §7§o(" + entry.getReason() + ", " + DateFormats.FORMAT_SIMPLE.format(
                                        new Date(entry.getTimestamp())) + ")");
                    }

                    player.sendMessage(" ");
                } else {
                    UUIDFetcher.getUUID(args[1], uuid -> {
                        if (uuid == null) {
                            player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDer Spieler wurde nicht gefunden.");
                            return;
                        }

                        if (!Main.getInstance().getReportManager().isReported(uuid)) {
                            player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDer Spieler wurde nicht reportet.");
                            ;
                        }

                        Report report = Main.getInstance().getReportManager().getReport(uuid);
                        player.sendMessage(" ");
                        player.sendMessage(" §cFolgende Spieler haben §7" + args[1] + " §creportet:");

                        for (Report.ReportEntry entry : report.getReportEntries()) {
                            OfflinePlayer op = Bukkit.getOfflinePlayer(entry.getUuid());
                            if (op == null)
                                continue;
                            player.sendMessage(
                                    " §8- §r" + (op.isOnline() ? "§a" : "§7") + op.getName() + " §7§o(" + entry.getReason() + ", " + DateFormats.FORMAT_SIMPLE.format(
                                            new Date(entry.getTimestamp())) + ")");
                        }
                        player.sendMessage(" ");
                    });
                    return true;
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("close")) {
                if (!player.hasPermission("potera.report.mod")) {
                    sendHelp(player, label);
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target != null) {
                    if (!Main.getInstance().getReportManager().isReported(target.getUniqueId())) {
                        player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDer Spieler wurde nicht reportet.");
                        return true;
                    }

                    Main.getInstance().getReportManager().closeReport(target.getUniqueId());
                    player.sendMessage(StringDefaults.REPORT_PREFIX + "§aDu hast den Report geschlossen.");
                } else {
                    UUIDFetcher.getUUID(args[1], uuid -> {
                        if (uuid == null) {
                            player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDer Spieler wurde nicht gefunden.");
                            return;
                        }
                        if (!Main.getInstance().getReportManager().isReported(uuid)) {
                            player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDer Spieler wurde nicht reportet.");
                            return;
                        }
                        Main.getInstance().getReportManager().closeReport(uuid);
                        player.sendMessage(StringDefaults.REPORT_PREFIX + "§aDu hast den Report geschlossen.");
                    });
                    return true;
                }
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            if (target == player) {
                player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDu kannst dich nicht selber reporten.");
                return true;
            }

            if (target.hasPermission("potera.report.mod")) {
                player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDieser Spieler kann nicht reportet werden.");
                player.sendMessage(StringDefaults.REPORT_PREFIX + "§cSolltest du ein Fehlverhalten feststellen, melde");
                player.sendMessage(StringDefaults.REPORT_PREFIX + "§cdich bei einem Admin im Discord.");
                return true;
            }

            String reason = args[1];

            if (Main.getInstance().getReportManager().hasPlayerReported(player.getUniqueId(), target.getUniqueId())) {
                player.sendMessage(StringDefaults.REPORT_PREFIX + "§cDu hast den Spieler bereits reportet.");
                return true;
            }

            Main.getInstance().getReportManager().addReport(target.getUniqueId(), player.getUniqueId(), reason);
            player.sendMessage(StringDefaults.REPORT_PREFIX + "§eDu hast §7" + target.getName() + " §ereportet.");

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all == player)
                    continue;
                if (all.hasPermission("potera.report.mod")) {
                    all.sendMessage(
                            StringDefaults.REPORT_PREFIX + "§cDer Spieler §6" + target.getName() + " §cwurde von §6" + player.getName()
                                    + " §caufgrund §6" + reason + " §creportet.");
                    all.playSound(all.getLocation(), Sound.VILLAGER_HIT, 1.0F, 1.0F);
                }
            }
            return true;
        }

        return true;
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Spieler> <Grund>");
        if (player.hasPermission("potera.report.mod")) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " info [Spieler]");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " close <Spieler>");
        }
    }
}
