package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Support;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSupport implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length == 0) {
            if (player.hasPermission("potera.support")) {
                sendHelp(player, label);
                return true;
            }

            if (Main.getInstance().getSupportManager().isWaiting(player)) {
                player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDu hast bereits eine Supportanfrage gestellt.");
                return true;
            }

            Main.getInstance().getSupportManager().setWaiting(player, true);
            player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§eDeine Supportanfrage wurde geschickt.");

            String broadcast = StringDefaults.SUPPORT_PREFIX + "§7" + player.getName() + " §cbenötigt Support!";
            String acceptHere = StringDefaults.SUPPORT_PREFIX + "§7Klicke hier, um die Anfrage zu bearbeiten.";

            for (Player all : Bukkit.getOnlinePlayers()) {
                if (!all.hasPermission("potera.support"))
                    continue;
                all.sendMessage(broadcast);
                new JSONMessage(acceptHere).tooltip("§eSupport bearbeiten").runCommand(
                        "/support " + player.getName()).send(all);
                all.playSound(all.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
            }

            return true;
        }

        if (args.length == 1) {
            if (!player.hasPermission("potera.support")) {
                player.sendMessage(StringDefaults.NO_PERM);
                return true;
            }

            if (args[0].equalsIgnoreCase("beenden")) {
                Support support = Main.getInstance().getSupportManager().getSupport(player);

                if (support == null) {
                    player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDu befindest dich in keinem Supportchat.");
                    return true;
                }

                for (Player inSupport : support.getSupportPlayers().keySet()) {
                    Main.getInstance().getSupportManager().getSupports().remove(inSupport);
                    if (inSupport == player) continue;
                    inSupport.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDer Support wurde beendet.");
                }
                player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDu hast den Support beendet.");
                return true;
            }

            if (args[0].equalsIgnoreCase("info")) {
                if (Main.getInstance().getSupportManager().getWaiting().isEmpty()) {
                    player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cEs gibt keine wartenden Spieler.");
                    return true;
                }

                player.sendMessage(" ");
                player.sendMessage(StringDefaults.PREFIX + "§cFolgende Spieler warten auf Support§8: ");
                for (Player all : Main.getInstance().getSupportManager().getWaiting()) {
                    new JSONMessage(" §8- §c" + all.getName() + " §7[Klicke, zum annehmen]").runCommand(
                            "/support " + all.getName()).tooltip("§eSupportanfrage annehmen").send(player);
                }
                player.sendMessage("");
                player.sendMessage(" §eKlicke auf einen Namen zum annehmen");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            if (!Main.getInstance().getSupportManager().isWaiting(target)) {
                player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDieser Spieler benötigt keinen Support.");
                return true;
            }

            if (Main.getInstance().getSupportManager().getSupport(target) != null) {
                player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDer Spieler wird bereits supportet.");
                return true;
            }

            if (Main.getInstance().getSupportManager().getSupport(player) != null) {
                player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDu befindest dich bereits in einem Supportchat.");
                return true;
            }

            if (target == player) {
                player.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDu kannst dich nicht selber supporten.");
                return true;
            }

            Main.getInstance().getSupportManager().setWaiting(target, false);
            Main.getInstance().getSupportManager().createSupport(player, target);

            player.sendMessage(
                    StringDefaults.SUPPORT_PREFIX + "§eDu befindest dich nun mit §7" + target.getName() + " §eim Support.");
            target.sendMessage(
                    StringDefaults.SUPPORT_PREFIX + "§eDu befindest dich nun mit §7" + player.getName() + " §eim Support.");

            return true;
        }

        return true;
    }

    private void sendHelp(Player player, String label) {
        player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Spieler>");
        player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " stop");
        player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " info");
    }
}
