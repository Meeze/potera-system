package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTime implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.time")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length == 0 || args.length > 2) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <day|night|ticks> [Welt]");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("day") || args[0].equalsIgnoreCase("tag")) {
                player.getWorld().setTime(0L);
                player.sendMessage(
                        StringDefaults.PREFIX + "r§eEs ist nun Tag in Welt §7" + player.getWorld().getName() + "§e.");
            } else if (args[0].equalsIgnoreCase("night") || args[0].equalsIgnoreCase("nacht")) {
                player.getWorld().setTime(14000L);
                player.sendMessage(
                        StringDefaults.PREFIX + "§eEs ist nun Nacht in Welt §7" + player.getWorld().getName() + "§e.");
            } else {
                long time;
                try {
                    time = Long.parseLong(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(StringDefaults.PREFIX + "§cBitte gebe eine gültige Zeit an.");
                    return true;
                }
                player.getWorld().setTime(time);
                player.sendMessage(
                        StringDefaults.PREFIX + "§eDie Zeit wurde auf §7" + time + " Ticks §ein Welt §7" + player.getWorld().getName() + " §egesetzt.");
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("day")) {

                World world = Bukkit.getWorld(args[1]);

                if (world == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§cWelt konnte nicht gefunden werden.");
                    return true;
                }

                world.setTime(0L);
                player.sendMessage(StringDefaults.PREFIX + "§eEs ist nun Tag in Welt §7" + world.getName() + "§e.");


            } else if (args[0].equalsIgnoreCase("night")) {

                World world = Bukkit.getWorld(args[1]);

                if (world == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§cWelt konnte nicht gefunden werden.");
                    return true;
                }

                world.setTime(14000L);
                player.sendMessage(StringDefaults.PREFIX + "§eEs ist nun Nacht in Welt §7" + world.getName() + "§e.");
            } else {
                long time;

                World world = Bukkit.getWorld(args[1]);

                if (world == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§cWelt konnte nicht gefunden werden.");
                    return true;
                }

                try {
                    time = Long.parseLong(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(StringDefaults.PREFIX + "§cBitte gebe eine gültige Zeit an.");
                    return true;
                }

                world.setTime(time);
                player.sendMessage(
                        StringDefaults.PREFIX + "§eDie Zeit wurde auf §7" + time + " Ticks §ein Welt §7" + world.getName() + " §egesetzt.");
            }
        }

        return true;
    }
}
