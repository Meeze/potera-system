package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandFeed implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.feed")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + (player.hasPermission(
                    "potera.feed.other") ? " [Spieler]" : ""));
            return true;
        }

        if (args.length == 0) {
            player.setFoodLevel(30);
            player.sendMessage(StringDefaults.PREFIX + "§eDein Hunger wurde gestillt.");
            player.playSound(player.getLocation(), Sound.BURP, 1.0F, 1.0F);
            return true;
        }

        if (!player.hasPermission("potera.feed.other")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        target.setFoodLevel(30);
        target.playSound(target.getLocation(), Sound.BURP, 1.0F, 1.0F);
        target.sendMessage(StringDefaults.PREFIX + "§eDein Hunger wurde gestillt.");
        player.sendMessage(StringDefaults.PREFIX + "§eDer Hunger von §7" + target.getName() + " §ewurde gestillt.");
        return true;
    }
}
