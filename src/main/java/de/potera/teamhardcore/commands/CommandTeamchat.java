package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTeamchat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player p = (Player) cs;

        if (!p.hasPermission("potera.teamchat")) {
            p.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length < 1) {
            p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " <Nachricht>");
            return true;
        }

        StringBuilder sb = new StringBuilder();

        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String msg = sb.substring(0, sb.length() - 1);

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (!all.hasPermission("potera.teamchat"))
                continue;
            all.sendMessage(StringDefaults.TC_PREFIX + "§c" + p.getName() + "§8: §6" + msg);
        }

        return true;
    }
}
