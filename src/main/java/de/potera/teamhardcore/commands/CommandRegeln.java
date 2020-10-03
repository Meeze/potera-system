package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRegeln implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        player.sendMessage("");
        player.sendMessage(StringDefaults.PREFIX + "§7Alle gültigen Regeln findest du im Discord.");
        player.sendMessage(StringDefaults.PREFIX + "§7Discord§8: §adiscord.potera.eu");
        player.sendMessage("");

        return true;
    }
}
