package de.potera.rysefoxx.commands;

import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChestCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;


        if (!player.hasPermission("potera.enderchest")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (!Main.getInstance().getEnderChestManager().isAccessible()) {
            player.sendMessage(StringDefaults.PREFIX + "Die Enderchests werden gerade geladen.");
            return true;
        }


        if (args.length == 0) {
            InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 5).withTitle("§7Deine Enderchest");
            if (Main.getInstance().getEnderChestManager().getContents().containsKey(player.getUniqueId())) {
                inventoryMenuBuilder.getInventory().setContents(Main.getInstance().getEnderChestManager().getContents().get(player.getUniqueId()));
            }
            inventoryMenuBuilder.show(player);
        } else if (args.length == 1) {
            /*

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }
            InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 5).withTitle("§7EC von §c" + target.getName());
            if (Main.getInstance().getEnderChestManager().getContents().containsKey(target.getUniqueId())) {
                inventoryMenuBuilder.getInventory().setContents(Main.getInstance().getEnderChestManager().getContents().get(target.getUniqueId()));
            }
            inventoryMenuBuilder.show(player);
            inventoryMenuBuilder.withEventHandler(event -> event.setCancelled(true));
             */

        }


        return false;
    }

}
