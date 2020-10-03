package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSudo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.sudo")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Spieler> <Nachricht>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
            return true;
        }

        if (target == player) {
            player.sendMessage(StringDefaults.PREFIX + "§cBenutze bitte deinen eigenen Chat.");
            return true;
        }

        if (target.hasPermission("potera.sudo.prevent")) {
            player.sendMessage(StringDefaults.PREFIX + "§cDu darfst über diesen Spieler nichts ausführen.");
            return true;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        String output = builder.substring(0, builder.length() - 1);
        target.chat(output);
        player.sendMessage(StringDefaults.PREFIX + "§cDie Nachricht wurde über §7" + target.getName() + " §egesendet.");
        return true;
    }
}
