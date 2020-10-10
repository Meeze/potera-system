package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKill implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.kill")) {
            player.setHealth(0.0D);
            player.sendMessage(StringDefaults.PREFIX + "§eDu hast dich selbst getötet.");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " [Spieler]");
            return true;
        }

        if (args.length == 0) {
            player.setHealth(0.0D);
            player.sendMessage(StringDefaults.PREFIX + "§eDu hast dich selbst getötet.");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
            return true;
        }
        target.setHealth(0.0D);
        player.sendMessage(StringDefaults.PREFIX + "§eDu hast §7" + target.getName() + " §egetötet!");
        return true;
    }
}
