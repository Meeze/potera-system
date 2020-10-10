package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CommandRelore implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("potera.relore")) {
            p.sendMessage(StringDefaults.PREFIX + "§cFür diese Aktion besitzt du keine Rechte.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(p, label);
            return true;
        }

        ItemStack hand = p.getItemInHand();

        if (hand == null || hand.getType() == Material.AIR) {
            p.sendMessage(StringDefaults.PREFIX + "§cDu musst ein Item in der Hand halten.");
            return true;
        }

        ItemMeta meta = hand.getItemMeta();

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {

                if (!meta.hasLore()) {
                    p.sendMessage(StringDefaults.PREFIX + "§cDas Item besitzt keine Lore.");
                    return true;
                }

                meta.setLore(null);
                hand.setItemMeta(meta);

                p.sendMessage(StringDefaults.PREFIX + "§eDie Lore des Items wurde zurückgesetzt.");


            } else if (args[0].equalsIgnoreCase("remove")) {

                if (!meta.hasLore()) {
                    p.sendMessage(StringDefaults.PREFIX + "§cDas Item besitzt keine Lore.");
                    return true;
                }

                List<String> lore = meta.getLore();

                if (lore.size() == 1) {
                    lore = null;
                } else {
                    lore.remove(lore.size() - 1);
                }
                meta.setLore(lore);
                hand.setItemMeta(meta);

                p.sendMessage(StringDefaults.PREFIX + "§eDie letzte Zeile der Lore wurde gelöscht.");
            } else {

                sendHelp(p, label);
                return true;
            }
        }


        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                List<String> lore;

                if (!meta.hasLore()) {
                    lore = new ArrayList<>();
                } else {
                    lore = meta.getLore();
                }
                String newText = ChatColor.translateAlternateColorCodes('&', args[1]);

                lore.add(newText);
                meta.setLore(lore);
                hand.setItemMeta(meta);

                p.sendMessage(StringDefaults.PREFIX + "§eEs wurde eine Zeile der Lore hinzugefügt.");


            } else if (args[0].equalsIgnoreCase("remove")) {
                int pos;
                if (!meta.hasLore()) {
                    p.sendMessage(StringDefaults.PREFIX + "§cDas Item besitzt keine Lore.");
                    return true;
                }

                List<String> lore = meta.getLore();


                try {
                    pos = Integer.parseInt(args[1]);
                    if (pos < 1 || pos > lore.size())
                        throw new NumberFormatException();
                    pos--;
                } catch (NumberFormatException e) {
                    p.sendMessage(
                            StringDefaults.PREFIX + "§cBitte gebe eine gültige Position ein. (1-" + lore.size() + ")");
                    return true;
                }

                lore.remove(pos);

                if (lore.isEmpty()) {
                    lore = null;
                }
                meta.setLore(lore);
                hand.setItemMeta(meta);

                p.sendMessage(StringDefaults.PREFIX + "§eDie Zeile §7" + ++pos + " §eder Lore wurde gelöscht.");
            } else {

                sendHelp(p, label);
                return true;
            }
        }


        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("add")) {
                int pos;

                List<String> lore;
                if (!meta.hasLore()) {
                    lore = new ArrayList<>();
                } else {
                    lore = meta.getLore();
                }


                try {
                    pos = Integer.parseInt(args[1]);
                    if (pos < 1 || pos > lore.size() + 1)
                        throw new NumberFormatException();
                    pos--;
                } catch (NumberFormatException e) {
                    pos = -1;
                }

                StringBuilder sb = new StringBuilder();

                for (int i = (pos == -1) ? 1 : 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }

                String newText = ChatColor.translateAlternateColorCodes('&', sb.substring(0, sb.length() - 1));

                if (pos == -1) {
                    lore.add(newText);
                } else {
                    lore.add(pos, newText);
                }
                meta.setLore(lore);
                hand.setItemMeta(meta);

                p.sendMessage(
                        StringDefaults.PREFIX + "§eEs wurde" + ((pos == -1) ? "" : (" an der Stelle §7" + ++pos + "§e")) + " eine Zeile der Lore hinzugefügt.");


            } else if (args[0].equalsIgnoreCase("set")) {
                int pos;

                List<String> lore;
                if (!meta.hasLore()) {
                    lore = new ArrayList<>();
                } else {
                    lore = meta.getLore();
                }


                try {
                    pos = Integer.parseInt(args[1]);
                    if (pos < 1 || pos > lore.size())
                        throw new NumberFormatException();
                    pos--;
                } catch (NumberFormatException e) {
                    p.sendMessage(StringDefaults.PREFIX + "§cBitte gebe eine gültige Zeile an.");
                    return true;
                }

                StringBuilder sb = new StringBuilder();

                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]).append(" ");
                }

                String newText = ChatColor.translateAlternateColorCodes('&', sb.substring(0, sb.length() - 1));

                lore.set(pos, newText);
                meta.setLore(lore);
                hand.setItemMeta(meta);

                p.sendMessage(StringDefaults.PREFIX + "§eEs wurde die Zeile §7" + ++pos + " §eder Lore editiert.");
            } else {

                sendHelp(p, label);
                return true;
            }
        }


        return true;
    }

    private void sendHelp(Player p, String label) {
        p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " reset");
        p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " add [Position] <Text>");
        p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " remove [Position]");
        p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " set <Position> <Text>");
    }
}
