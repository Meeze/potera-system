package de.potera.rysefoxx.commands;

import de.potera.rysefoxx.utils.TimeUtils;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ItemCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("potera.item")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }


        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("aus") || args[0].equalsIgnoreCase("deactivate")) {
                if (!Main.getPlugin(Main.class).getItemManager().isActive()) {
                    player.sendMessage(StringDefaults.PREFIX + "§7[item] wurde bereits deaktiviert.");
                    return true;
                }
                Main.getPlugin(Main.class).getItemManager().setActive(false);
                player.sendMessage(StringDefaults.PREFIX + "§7[item] wurde erfolgreich deaktiviert");
            } else if (args[0].equalsIgnoreCase("an") || args[0].equalsIgnoreCase("activate")) {
                if (Main.getPlugin(Main.class).getItemManager().isActive()) {
                    player.sendMessage(StringDefaults.PREFIX + "§7[item] wurde bereits aktiviert.");
                    return true;
                }
                Main.getPlugin(Main.class).getItemManager().setActive(true);
                player.sendMessage(StringDefaults.PREFIX + "§7[item] wurde erfolgreich aktiviert");
            }
            return true;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("cooldown")) {
                long time;
                try {
                    time = Long.parseLong(args[1].replace("s", "").replace("m", "").replace("h", "").replace("d", "").replace("w", ""));
                } catch (Exception e) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte gib eine valide Zahl ein!");
                    return true;
                }
                TimeUtils timeUtils = TimeUtils.getUnitShortCut(args[1].substring(args[1].length() - 1));
                if (timeUtils == null) return true;
                long timeMillis = time * timeUtils.getToSecond();
                Main.getInstance().getItemManager().setCoolDown(timeMillis);
                player.sendMessage(StringDefaults.PREFIX + "§7Der Cooldown ist nun auf §c" + TimeUtils.shortIntegerWithText(Main.getPlugin(Main.class).getItemManager().getCoolDown()));

            }
        } else {
            sendHelp(player);
        }


        return false;
    }

    private void sendHelp(Player player) {
        player.sendMessage(StringDefaults.PREFIX + "§7/Item an");
        player.sendMessage(StringDefaults.PREFIX + "§7/Item aus");
        player.sendMessage(StringDefaults.PREFIX + "§7/Item cooldown <Zeit>");
    }

}
