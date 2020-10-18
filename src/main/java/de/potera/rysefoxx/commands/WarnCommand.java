package de.potera.rysefoxx.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserWarn;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;


        if (!player.hasPermission("potera.warn")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }
        if (args.length == 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!target.isOnline() && !target.hasPlayedBefore()) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }
            User userTarget = Main.getInstance().getUserManager().getUser(target.getUniqueId());
            UserWarn userWarn = userTarget.getUserWarn();
            player.sendMessage(StringDefaults.WARN_PREFIX + "§4" + target.getName() + " §7hat §c" + userWarn.getWarns() + " Warns.");
        } else if (args.length == 3) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!target.isOnline() && !target.hasPlayedBefore()) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }
            if (!Util.isInt(args[1])) {
                player.sendMessage(StringDefaults.PREFIX + "§7Bitte gib eine valide Zahl ein!");
                return true;
            }
            User userTarget = Main.getInstance().getUserManager().getUser(target.getUniqueId());
            int warnsToAdd = Integer.parseInt(args[1]);
            UserWarn userWarn = userTarget.getUserWarn();
            userWarn.addWarn(warnsToAdd);
            Bukkit.broadcastMessage(StringDefaults.WARN_PREFIX + "§4" + target.getName() + " §cwurde von §4" + player.getName() + " §6" + warnsToAdd + "x §cverwarnt!");
            Bukkit.broadcastMessage(StringDefaults.WARN_PREFIX + "§cGrund§8: §4" + Util.messageBuilder(2, args));
        } else {
            Util.sendBigMessage(player, StringDefaults.PREFIX, "§7Warn (Spieler) <Anzahl> [Grund]", "§7Warn (Spieler)");
        }


        return false;
    }


}
