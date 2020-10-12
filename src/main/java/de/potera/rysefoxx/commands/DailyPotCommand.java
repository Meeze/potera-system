package de.potera.rysefoxx.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyPotCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;


        if (args.length == 1) {
            if (Main.getInstance().getDailyPotManager().alreadyJoined(player)) {
                player.sendMessage(StringDefaults.PREFIX + "§7Du hast dich bereits für den Dailypot angemeldet.");
                return true;
            }
            if (!Main.getInstance().getDailyPotManager().canJoin()) {
                player.sendMessage(StringDefaults.PREFIX + "§7Du bist zuspät! Der Dailypot wirde gerade ausgelost.");
                return true;
            }
            Main.getInstance().getDailyPotManager().addPlayer(player);
            player.sendMessage(StringDefaults.DAILYPOT_PREFIX + "§7Du hast dich für den Dailypot erfolgreich angemeldet.");
        } else if (args.length == 0) {
            player.sendMessage(StringDefaults.DAILYPOT_PREFIX + "§7Der Dailypot hat derzeit eine Größe von §c" + Util.formatBigNumber(Main.getInstance().getDailyPotManager().getDeployment()) + "$");
        } else {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " join");
        }

        return false;
    }
}
