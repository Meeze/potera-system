package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFakemessage implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.fakemessage")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        StringBuilder builder = new StringBuilder();

        for (String arg : args)
            builder.append(ChatColor.translateAlternateColorCodes('&', arg)).append(" ");

        String output = builder.substring(0, builder.length() - 1);
        Bukkit.broadcastMessage(output);

        return true;
    }
}
