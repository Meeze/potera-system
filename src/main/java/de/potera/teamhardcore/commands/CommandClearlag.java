package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandClearlag implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.clearlag")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        Main.getInstance().getAntilagManager().clearEntities();
        player.sendMessage(StringDefaults.LAG_PREFIX + "§eAlle Entities wurden erfolgreich entfernt.");
        return true;
    }
}
