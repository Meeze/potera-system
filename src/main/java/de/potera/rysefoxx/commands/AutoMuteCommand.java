package de.potera.rysefoxx.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutoMuteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;


        if (!player.hasPermission("potera.automute")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "add":
                case "hinzufügen":
                    Main.getInstance().getAutoMuteManager().addWord(player, args[1]);
                    break;
                case "remove":
                case "entfernen":
                    Main.getInstance().getAutoMuteManager().removeWord(player, args[1]);
                    break;
                default:
                    break;
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("liste")) {
                if (Main.getInstance().getAutoMuteManager().getDisallowedWords().isEmpty()) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Es befinden sich keine Wörter in der Liste.");
                    return true;
                }
                for (String words : Main.getInstance().getAutoMuteManager().getDisallowedWords()) {
                    player.sendMessage(StringDefaults.PREFIX + " §6" + words);
                }
            }
        } else {
            showUsage(player);
        }


        return false;
    }

    private void showUsage(Player player){
        player.sendMessage("USAGE: §6Automute add <Word>");
        player.sendMessage("USAGE: §6Automute remove <Word>");
        player.sendMessage("USAGE: §6Automute list");
    }

}
