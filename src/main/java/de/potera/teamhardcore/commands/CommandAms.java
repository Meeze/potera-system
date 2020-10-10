package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.ams.Ams;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandAms implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length != 1) {
            openAms(player, player.getUniqueId());
        }

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target != null)
                openAms(player, target.getUniqueId());
            else {
                UUIDFetcher.getUUID(args[0], uuid -> {
                    if (uuid == null) {
                        player.sendMessage(StringDefaults.AMS_PREFIX + "§cDer Spieler konnte nicht gefunden werden.");
                        return;
                    }
                    OfflinePlayer opTarget = Bukkit.getOfflinePlayer(uuid);
                    if (!opTarget.hasPlayedBefore()) {
                        player.sendMessage(StringDefaults.AMS_PREFIX + "§cDieser Spieler war noch nie auf diesem Server.");
                        return;
                    }
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        openAms(player, uuid);
                    });
                });
            }

        }

        return true;
    }

    private void openAms(Player player, UUID amsOwner) {
        Ams ams = Main.getInstance().getAmsManager().getAms(amsOwner);
        if (ams == null) {
            player.sendMessage(StringDefaults.AMS_PREFIX + "§cFehler beim Laden der AMS.");
            return;
        }

        if (!ams.hasPermission(player)) {
            player.sendMessage(StringDefaults.AMS_PREFIX + "§cDu hast keinen Zugriff auf die AMS.");
            return;
        }

        Main.getInstance().getAmsManager().openGui(player, ams, 1);
    }
}
