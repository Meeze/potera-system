package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPing implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length > 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " [Spieler]");
            return true;
        }

        if (args.length == 0) {
            sendPingMessage(player, player);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
            return true;
        }

        sendPingMessage(player, target);
        return true;
    }

    private void sendPingMessage(Player player, Player target) {
        int ping = Util.getPing(target);
        String pingMsg;

        if (ping <= 50) {
            pingMsg = "§2" + ping + "ms";
        } else if (ping <= 100) {
            pingMsg = "§a" + ping + "ms";
        } else if (ping <= 150) {
            pingMsg = "§e" + ping + "ms";
        } else if (ping <= 250) {
            pingMsg = "§c" + ping + "ms";
        } else {
            pingMsg = "§4" + ping + "ms";
        }
        player.sendMessage(
                StringDefaults.PREFIX + (player == target ? "§7Dein" : "§6" + target.getName() + "'s") + " §7Ping liegt momentan bei " + pingMsg);

    }
}
