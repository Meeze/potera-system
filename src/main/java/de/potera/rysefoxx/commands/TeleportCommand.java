package de.potera.rysefoxx.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("potera.teleport")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }
        if (args.length == 4) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            int x;
            int y;
            int z;

            try {
                x = Integer.parseInt(args[1]);
                y = Integer.parseInt(args[2]);
                z = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                player.sendMessage(StringDefaults.PREFIX + "§cBitte gib eine valide Zahl ein!");
                return true;
            }

            target.teleport(new Location(target.getWorld(), x, y, z));
            player.sendMessage(StringDefaults.PREFIX + "§7Du hast §c" + target.getName() + " §7zu den Koordinaten §c" + x + "§8, §c" + y + "§8, §c" + z + " §7teleportiert.");
            target.sendMessage(StringDefaults.PREFIX + "§7Du wurdest zu den Koordinaten §8<§a" + x + "§8|§a" + y + "§8|§a" + z + "§8> §7teleportiert.");


        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase(player.getName())) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(StringDefaults.NOT_ONLINE);
                    return true;
                }
                player.teleport(target);

                player.sendMessage(StringDefaults.PREFIX + "§7Du hast dich zu §c" + target.getName() + " §7teleportiert.");
                if (!Main.getInstance().getGeneralManager().getPlayersInVanish().contains(player)) {
                    target.sendMessage(StringDefaults.PREFIX + "§c" + player.getName() + " §7hat sich zu dir teleportiert.");
                }

            } else if (args[1].equalsIgnoreCase(player.getName())) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(StringDefaults.NOT_ONLINE);
                    return true;
                }
                target.teleport(player);
                target.sendMessage(StringDefaults.PREFIX + "§7Du wurdest zu §c" + player.getName() + " §7teleportiert.");
                player.sendMessage(StringDefaults.PREFIX + "§7Du hast §c" + target.getName() + " §7erfolgreich zu dir teleportiert.");


            } else {
                Player player1 = Bukkit.getPlayer(args[0]);
                Player target = Bukkit.getPlayer(args[1]);
                if (player1 == null || target == null) {
                    player.sendMessage(StringDefaults.NOT_ONLINE);
                    return true;
                }
                player1.teleport(target);
                player1.sendMessage(StringDefaults.PREFIX + "§7Du wurdest zu §c" + target.getName() + " §7teleportiert.");
                target.sendMessage(StringDefaults.PREFIX + "§c" + player1.getName() + " §7wurde zu dir teleportiert.");
            }

        } else if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }
            player.teleport(target);
            player.sendMessage(StringDefaults.PREFIX + "§7Du hast dich zu §c" + target.getName() + " §7teleportiert.");
        }


        return false;
    }

}
