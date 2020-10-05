package de.potera.rysefoxx.commands;

import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrashCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 5).withTitle("§7Abfall");
        inventoryMenuBuilder.show(player);
        return false;
    }

}
