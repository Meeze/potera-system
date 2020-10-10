package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGod implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.god")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + (player.hasPermission(
                    "potera.god.other") ? " [Spieler]" : ""));
            return true;
        }

        if (args.length == 0) {
            if (Main.getInstance().getGeneralManager().getPlayersInGodMode().contains(player)) {
                Main.getInstance().getGeneralManager().getPlayersInGodMode().remove(player);
                player.sendMessage(StringDefaults.PREFIX + "§eDein Godmodus wurde deaktiviert.");
                return true;
            }
            Main.getInstance().getGeneralManager().getPlayersInGodMode().add(player);
            player.sendMessage(StringDefaults.PREFIX + "§eDein Godmodus wurde aktiviert.");
            return true;
        }

        if (!player.hasPermission("potera.god.other")) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label);
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (Main.getInstance().getGeneralManager().getPlayersInGodMode().contains(target)) {
            Main.getInstance().getGeneralManager().getPlayersInGodMode().remove(target);
            target.sendMessage(StringDefaults.PREFIX + "§eDein Godmodus wurde deaktiviert.");
            player.sendMessage(
                    StringDefaults.PREFIX + "§eDer Godmodus von §7" + target.getName() + " §ewurde deaktiviert.");

            return true;
        }
        Main.getInstance().getGeneralManager().getPlayersInGodMode().add(target);
        target.sendMessage(StringDefaults.PREFIX + "§eDein Godmodus wurde aktiviert.");
        player.sendMessage(StringDefaults.PREFIX + "§eDer Godmodus von §7" + target.getName() + " §ewurde aktiviert.");
        return true;
    }
}
