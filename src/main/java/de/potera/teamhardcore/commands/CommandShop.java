package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandShop implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return true;
        }

        Player p = (Player) sender;
        Main.getInstance().getShopManager().openMainInventory(p);
        return true;
    }
}
