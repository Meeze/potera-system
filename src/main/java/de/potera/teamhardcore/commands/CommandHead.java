package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class CommandHead implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("potera.head")) {
            p.sendMessage(StringDefaults.PREFIX + "§cFür diese Aktion besitzt du keine Rechte.");
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " [Spieler]");
            return true;
        }

        if (args.length == 0) {

            ItemStack skull = getSkull(p.getName());
            Util.addItem(p, skull);

            p.sendMessage(StringDefaults.PREFIX + "§eDein Kopf wurde dir gegeben.");
        }


        if (args.length == 1) {
            ItemStack skull = getSkull(args[0]);
            Util.addItem(p, skull);
            p.sendMessage(StringDefaults.PREFIX + "§eDer Kopf von §7" + args[0] + " §ewurde dir gegeben.");
        }
        return true;
    }

    private ItemStack getSkull(String pName) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(pName);
        item.setItemMeta(meta);
        return item;
    }
}
