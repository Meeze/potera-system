package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandFix implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;
        boolean success = false;

        for (ItemStack contents : player.getInventory().getContents()) {
            if (contents != null && contents.getType() != Material.AIR) {
                if (contents.getType().getMaxDurability() != 0 && contents.getDurability() != 0) {

                    contents.setDurability((short) 0);
                    success = true;
                }
            }
        }
        for (ItemStack armorContents : player.getInventory().getArmorContents()) {
            if (armorContents != null && armorContents.getType() != Material.AIR) {
                if (armorContents.getType().getMaxDurability() != 0 && armorContents.getDurability() != 0) {

                    armorContents.setDurability((short) 0);
                    success = true;
                }
            }
        }

        if (success) {
            player.sendMessage(StringDefaults.PREFIX + "§eDeine Items wurden erfolgreich repariert.");
        } else {
            player.sendMessage(StringDefaults.PREFIX + "§cEs konnten keine Items repariert werden.");
        }

        return true;
    }
}
