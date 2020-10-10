package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CommandRename implements CommandExecutor {


    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.rename.remove") && !player.hasPermission("potera.rename")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        ItemStack hand = player.getItemInHand();

        if (hand == null || hand.getType() == Material.AIR) {
            player.sendMessage(StringDefaults.PREFIX + "§cDu musst ein Item in der Hand halten.");
            return true;
        }

        ItemMeta meta = hand.getItemMeta();

        if (args.length == 0) {
            if (meta == null || hand.getItemMeta().getDisplayName() == null) {
                player.sendMessage(StringDefaults.PREFIX + "§cDas Item in der Hand besitzt keinen Namen.");
                return true;
            }
            meta.setDisplayName(null);
            hand.setItemMeta(meta);
            player.sendMessage(StringDefaults.PREFIX + "§eDer Name wurde zurückgesetzt.");
            return true;
        }

        if (!player.hasPermission("potera.rename")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        StringBuilder sb = new StringBuilder();

        for (String arg : args) {
            sb.append(arg).append(" ");
        }
        String newName = ChatColor.translateAlternateColorCodes('&', sb.substring(0, sb.length() - 1));

        meta.setDisplayName(newName);
        hand.setItemMeta(meta);

        player.sendMessage(StringDefaults.PREFIX + "§eDer Name wurde geändert.");

        return true;
    }
}