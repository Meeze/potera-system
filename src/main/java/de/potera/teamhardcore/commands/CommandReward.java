package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.inventories.RewardInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandReward implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] strings) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;
        RewardInventory.openDailyInventory(player);

        return true;
    }
}
