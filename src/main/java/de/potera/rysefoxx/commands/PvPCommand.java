package de.potera.rysefoxx.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PvPCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("potera.pvp")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (player.getWorld().getPVP()) {
            for (World world : Bukkit.getWorlds()) {
                world.setPVP(false);
            }
            Bukkit.broadcastMessage(StringDefaults.PVP_PREFIX + "§7PvP wurde in allen Welten deaktiviert.");
        } else {
            for (World world : Bukkit.getWorlds()) {
                world.setPVP(true);
            }
            Bukkit.broadcastMessage(StringDefaults.PVP_PREFIX + "§7PvP wurde in allen Welten aktiviert.");
        }

        return false;
    }
}
