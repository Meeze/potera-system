package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTptop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.tptop")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        Location top = player.getLocation().getWorld().getHighestBlockAt(
                player.getLocation()).getLocation().clone().add(0.5D,
                0.5D, 0.5D);

        player.teleport(top);
        player.sendMessage(StringDefaults.PREFIX + "§eDu wurdest auf den höchsten Block teleportiert.");
        return true;
    }
}

