package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRanking implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;
        Main.getInstance().getRankingManager().openRankingInventory(player, 0);
        return true;
    }
}
