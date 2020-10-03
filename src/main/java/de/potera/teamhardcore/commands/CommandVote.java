package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandVote implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        player.sendMessage("");
        player.sendMessage(" §7Um für §a§lpotera §7zu voten, klicke");
        player.sendMessage("");
        new JSONMessage("§a§lHIER§7, für den ersten Votelink.").tooltip("§eKlicke für den 1. Votelink").send(player);
        new JSONMessage("§a§lHIER§7, für den zweiten Votelink.").tooltip("§eKlicke für den 2. Votelink").send(player);
        player.sendMessage("");
        player.sendMessage(" §7Du wirst deine Belohnung innerhalb weniger Minuten erhalten.");
        player.sendMessage(" §c§oBeachte, du musst online sein um die Belohnung zu erhalten.");
        player.sendMessage("");
        return true;
    }
}
