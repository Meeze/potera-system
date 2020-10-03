package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandBroadcast implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!cs.hasPermission("potera.broadcast")) {
            cs.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length == 0) {
            cs.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Nachricht>");
            return true;
        }

        StringBuilder builder = new StringBuilder();

        for (String arg : args) {
            builder.append(ChatColor.translateAlternateColorCodes('&', arg));
            builder.append(" ");
        }

        String output = builder.substring(0, builder.length() - 1);
        Bukkit.broadcastMessage(StringDefaults.SERVER_PREFIX + output);
        return true;
    }
}
