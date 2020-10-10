package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.users.UserStats;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandKopfgeld implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length != 2) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Spieler> <Kopfgeld>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
            return true;
        }

        if (target == player) {
            player.sendMessage(StringDefaults.PVP_PREFIX + "§cDu kannst auf dich selber kein Kopfgeld aussetzen.");
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[1]);

            if (amount <= 0L)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            player.sendMessage(StringDefaults.PVP_PREFIX + "§cBitte gebe einen gültigen Betrag an.");
            return true;
        }

        if (amount < 1000L) {
            player.sendMessage(StringDefaults.PVP_PREFIX + "§cDas Kopfgeld muss mindestens §71,000$ §cbetragen.");
            return true;
        }

        UserCurrency uc = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();

        if (uc.getMoney() < amount) {
            player.sendMessage(StringDefaults.PVP_PREFIX + "§cDu besitzt soviel Geld nicht.");
            return true;
        }

        UserStats targetStats = Main.getInstance().getUserManager().getUser(target.getUniqueId()).getUserStats();
        targetStats.setKopfgeld(targetStats.getKopfgeld() + amount);

        uc.removeMoney(amount);

        Bukkit.broadcastMessage(
                StringDefaults.PVP_PREFIX + "§e" + player.getName() + " §7hat ein Kopfgeld von §a" + Util.formatNumber(
                        amount) + "$ §7auf §e" + target.getName() + " §7gesetzt!");
        return true;
    }
}
