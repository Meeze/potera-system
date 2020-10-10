package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBuild implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("potera.build.protect")) {
            p.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (Main.getInstance().getGeneralManager().getPlayersInBuildmode().contains(p.getUniqueId())) {
            Main.getInstance().getGeneralManager().getPlayersInBuildmode().remove(p.getUniqueId());
            p.sendMessage(StringDefaults.PREFIX + "§eDu kannst die Map nun nicht mehr verändern.");
        } else {
            Main.getInstance().getGeneralManager().getPlayersInBuildmode().add(p.getUniqueId());
            p.sendMessage(StringDefaults.PREFIX + "§eDu kannst die Map nun verändern.");
        }

        return true;
    }
}
