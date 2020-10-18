package de.potera.rysefoxx.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.crates.BaseCrate;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        /*
        /crate get <Name>

         */

        if (!player.hasPermission("potera.crate")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("get")) {
                BaseCrate baseCrate = Main.getInstance().getCrateManager().getCrate(args[1]);
                if (baseCrate == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§cDiese Crate existiert nicht.");
                    return true;
                }
                player.getInventory().addItem(baseCrate.getAddon().getCrateItem());
            }
        }


        return false;
    }

}
