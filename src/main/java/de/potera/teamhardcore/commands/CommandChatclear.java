package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandChatclear implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (label.equalsIgnoreCase("icc") || label.equalsIgnoreCase("ichatclear")) {
            player.sendMessage(new String[100]);
            player.sendMessage(StringDefaults.PREFIX + "§eDein eigener Chat wurde geleert.");
            return true;
        }

        if (!player.hasPermission("potera.chatclear")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        String[] clear = new String[100];

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!all.hasPermission("potera.chatclear.bypass")) {
                all.sendMessage(clear);
            }
            all.sendMessage(StringDefaults.PREFIX + "§6Der globale Chat wurde geleert.");
        }
        return true;
    }
}
