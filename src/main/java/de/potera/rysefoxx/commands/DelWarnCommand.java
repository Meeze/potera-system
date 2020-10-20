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

public class DelWarnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;


        if (!player.hasPermission("potera.delwarn")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length == 2) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

            if (!target.isOnline() && !target.hasPlayedBefore()) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }
            if (!Util.isInt(args[1])) {
                player.sendMessage(StringDefaults.PREFIX + "§7Bitte gib eine valide Zahl ein!");
                return true;
            }
            int warnsToRemove = Integer.parseInt(args[1]);
            User userTarget = Main.getInstance().getUserManager().getUser(target.getUniqueId());
            UserWarn userWarn = userTarget.getUserWarn();

            if (userWarn.getWarns() < warnsToRemove) {
                player.sendMessage(StringDefaults.PREFIX + "§7Der Spieler §c" + target.getName() + " §7hat keine §6" + warnsToRemove + " Warns");
                return true;
            }

            userWarn.deleteWarn(warnsToRemove);
            Bukkit.broadcastMessage(StringDefaults.WARN_PREFIX + "§4" + player.getName() + " §chat §4" + target.getName() + " §6" + warnsToRemove + " Warns §centfernt.");
        } else {
            player.sendMessage(StringDefaults.PREFIX + "§7Delwarn (Spieler) <Anzahl>");
        }

        return false;
    }

}
