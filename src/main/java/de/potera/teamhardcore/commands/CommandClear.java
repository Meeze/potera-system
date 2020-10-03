package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClear implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length > 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + (player.hasPermission(
                    "potera.clear.other") ? " [Spieler]" : ""));
            return true;
        }


        if (args.length == 0) {
            if (!player.hasPermission("potera.clear")) {
                player.sendMessage(StringDefaults.NO_PERM);
                return true;
            }

            Util.clearInventory(player);
            player.sendMessage(StringDefaults.PREFIX + "§eDein Inventar wurde erfolgreich geleert.");
            return true;
        }

        if (!player.hasPermission("potera.clear.other")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
            return true;
        }

        Util.clearInventory(target);
        target.sendMessage(StringDefaults.PREFIX + "§eDein Inventar wurde geleert.");
        player.sendMessage(StringDefaults.PREFIX + "§eDas Inventar von §7" + target.getName() + " §ewurde geleert.");
        return true;
    }
}
