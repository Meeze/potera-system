package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandList implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();
        Player player = (Player) cs;

        player.sendMessage(" ");
        player.sendMessage(
                StringDefaults.PREFIX + "§eEs " + ((online == 1) ? "ist" : "sind") + " §7" + online + " §8/ §7" + max + " §eSpieler online.");
        if (player.hasPermission("potera.list.advanced")) {
            player.sendMessage(" ");
            player.sendMessage(
                    StringDefaults.PREFIX + "§eEs sind §7" + Bukkit.getOfflinePlayers().length + " Spieler §eim System registriert.");
        }
        player.sendMessage(" ");
        return true;
    }
}
