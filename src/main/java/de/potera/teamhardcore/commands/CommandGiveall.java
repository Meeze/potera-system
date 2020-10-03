package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGiveall implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.giveall")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        ItemStack hand = player.getItemInHand();

        if (hand == null || hand.getType() == Material.AIR) {
            player.sendMessage(StringDefaults.PREFIX + "§cDu musst ein Item in der Hand halten.");
            return true;
        }

        int amount = hand.getAmount();

        String name = (hand.getItemMeta() == null) ? hand.getType().name() : ((hand.getItemMeta().getDisplayName() == null) ? hand.getType().name() : hand.getItemMeta().getDisplayName());

        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(
                    StringDefaults.PREFIX + "§aAlle Spieler haben §7" + amount + "x §6§l" + name + " §aerhalten.");

            if (all != player){
                Util.addItem(all, hand);
            }
        }

        return true;
    }
}
