package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.inventories.RewardInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandPvPShop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] strings) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        RewardInventory.openPvPShopInventory(player);
        return true;
    }
}
