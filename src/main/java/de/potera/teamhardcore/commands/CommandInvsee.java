package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInvsee implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.invsee")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
        }

        if (target == player) {
            player.sendMessage(StringDefaults.PREFIX + "§cDu kannst dein eigenes Inventar nicht öffnen.");
            return true;
        }

        Main.getInstance().getGeneralManager().getPlayersInInvsee().add(player);
        player.openInventory(target.getInventory());

        return true;
    }
}
