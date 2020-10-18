package de.potera.rysefoxx.commands;

import de.potera.rysefoxx.manager.TeamManager;
import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;


        if (!player.hasPermission("potera.team")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length == 0) {
            InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 5).withTitle(StringDefaults.INVENTORY_PREFIX + "Team");
            Util.fill(inventoryMenuBuilder.getInventory(), 9 * 5);
            inventoryMenuBuilder.withItem(12, new ItemBuilder(Material.CLAY).setDurability((short) 14).setDisplayName("§4Owner").setLore(Arrays.asList(
                    "",
                    Main.getPlugin(Main.class).getTeamManager().getOwner())).build());
            inventoryMenuBuilder.withItem(14, new ItemBuilder(Material.CLAY).setDurability((short) 6).setDisplayName("§cAdmin").setLore(Arrays.asList(
                    "",
                    Main.getPlugin(Main.class).getTeamManager().getAdmin())).build());
            inventoryMenuBuilder.withItem(29, new ItemBuilder(Material.WOOL).setDurability((short) 3).setDisplayName("§bDeveloper").setLore(Arrays.asList(
                    "",
                    Main.getPlugin(Main.class).getTeamManager().getDeveloper())).build());
            inventoryMenuBuilder.withItem(30, new ItemBuilder(Material.WOOL).setDurability((short) 1).setDisplayName("§6Architekt").setLore(Arrays.asList(
                    "",
                    Main.getPlugin(Main.class).getTeamManager().getArchitect())).build());
            inventoryMenuBuilder.withItem(31, new ItemBuilder(Material.WOOL).setDurability((short) 10).setDisplayName("§5Moderator").setLore(Arrays.asList(
                    "",
                    Main.getPlugin(Main.class).getTeamManager().getModerator())).build());
            inventoryMenuBuilder.withItem(32, new ItemBuilder(Material.WOOL).setDurability((short) 5).setDisplayName("§aSupporter").setLore(Arrays.asList(
                    "",
                    Main.getPlugin(Main.class).getTeamManager().getSupporter())).build());
            inventoryMenuBuilder.withItem(33, new ItemBuilder(Material.WOOL).setDurability((short) 9).setDisplayName("§3Guide").setLore(Arrays.asList(
                    "",
                    Main.getPlugin(Main.class).getTeamManager().getGuide())).build());
            inventoryMenuBuilder.withEventHandler(event -> event.setCancelled(true));
            inventoryMenuBuilder.show(player);
        } else if (args.length == 3) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (args[0].equalsIgnoreCase("add")) {
                if (Main.getInstance().getTeamManager().inGroup(target)) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieser Spieler befindet sich bereits in einer Gruppe.");
                    return true;
                }
                if (Main.getInstance().getTeamManager().isNotValidRank(args[2])) {
                    player.sendMessage(StringDefaults.PREFIX + "§c" + args[2] + " §7ist kein gültiger Team Rang.");
                    return true;
                }
                TeamManager.TeamRanks teamRanks = TeamManager.TeamRanks.forName(args[2]);
                if (teamRanks == null) return true;
                Main.getInstance().getTeamManager().addPlayer(target, teamRanks);
                player.sendMessage(StringDefaults.PREFIX + "§c" + target.getName() + " §7wurde als §e" + teamRanks.getName() + " §7in das Team platziert.");
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
                if (!Main.getInstance().getTeamManager().inGroup(target)) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieser Spieler befindet sich in keiner Gruppe.");
                    return true;
                }
                Main.getInstance().getTeamManager().removePlayer(target);
                player.sendMessage(StringDefaults.PREFIX + "§c" + target.getName() + " §7wurde aus dem Team entfernt.");
            }
        }


        return false;
    }
}
