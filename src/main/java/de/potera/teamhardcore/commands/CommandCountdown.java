package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.others.Countdown;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCountdown implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;

        if (!p.hasPermission("potera.countdown")) {
            p.sendMessage(StringDefaults.PREFIX + "§cFür diese Aktion besitzt du keine Rechte.");
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            sendHelp(p, label);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stop")) {

                if (!Countdown.isCurrentlyRunning()) {
                    p.sendMessage(StringDefaults.PREFIX + "§cEs l§uft gerade kein Countdown.");
                    return true;
                }

                Countdown.stop();
                p.sendMessage(StringDefaults.COUNTDOWN_PREFIX + "§eDer Countdown wurde gestoppt.");
            } else {

                sendHelp(p, label);
                return true;
            }
        }


        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("start")) {

                int time;

                try {
                    time = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    p.sendMessage(StringDefaults.PREFIX + "§cBitte gebe eine gültige Zeit an.");
                    return true;
                }

                if (Countdown.isCurrentlyRunning()) {
                    p.sendMessage(StringDefaults.COUNTDOWN_PREFIX + "§cDerzeit läuft ein anderer Countdown.");
                    return true;
                }

                if (time > 3600) {
                    p.sendMessage(StringDefaults.COUNTDOWN_PREFIX + "§cDer Countdown dauert zu lange.");
                    p.sendMessage(StringDefaults.COUNTDOWN_PREFIX + "§cMaximal mögliche Zeit: 1 Stunde (3600s)");
                    return true;
                }

                Countdown.setTime(time);
                Countdown.start();

                for (Player all : Bukkit.getOnlinePlayers()) {
                    all.sendMessage(StringDefaults.COUNTDOWN_PREFIX + "§aEin Countdown wurde gestartet.");
                }
            } else {
                sendHelp(p, label);
                return true;
            }
        }


        return true;
    }

    private void sendHelp(Player p, String label) {
        p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " start <Zeit in Sekunden>");
        p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " stop");
    }
}