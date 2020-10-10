package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class CommandRandom implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.random")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        Random random = new Random();
        Player randomPlayer = new ArrayList<>(Bukkit.getOnlinePlayers()).get(
                random.nextInt(Bukkit.getOnlinePlayers().size()));

        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(
                    StringDefaults.PREFIX + "§bDer Spieler §6§l" + randomPlayer.getName() + " §bwurde zufällig ausgesucht.");
        }

        return true;
    }
}
