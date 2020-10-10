package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CommandIgnore implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.ignore")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length != 1) {
            sendHelp(player);
            return true;
        }

        UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();

        if (args[0].equalsIgnoreCase("list")) {
            if (userData.getIgnoredPlayers().isEmpty()) {
                player.sendMessage(StringDefaults.IGNORE_PREFIX + "§cDu ignorierst derzeit niemandem.");
                return true;
            }

            StringBuilder builder = new StringBuilder();
            for (UUID uuid : userData.getIgnoredPlayers()) {
                OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                if (op == null)
                    continue;
                builder.append("§e").append(op.getName()).append("§7,");
            }

            String output = builder.substring(0, builder.length() - 5);
            player.sendMessage(StringDefaults.HEADER);
            player.sendMessage(" ");
            player.sendMessage(
                    StringDefaults.PREFIX + "§7Du ignorierst derzeit §e" + userData.getIgnoredPlayers().size() + " §7Spieler§8: ");
            player.sendMessage(" ");
            player.sendMessage(" " + output);
            player.sendMessage(" ");
            player.sendMessage(StringDefaults.FOOTER);
            return true;
        }

        Player targetOnline = Bukkit.getPlayer(args[0]);

        if (targetOnline != null) {
            if (targetOnline == player) {
                player.sendMessage(StringDefaults.IGNORE_PREFIX + "§cDu kannst dich nicht selber ignorieren.");
                return true;
            }

            if (!userData.getIgnoredPlayers().contains(targetOnline.getUniqueId())) {
                userData.addIgnoredPlayer(targetOnline.getUniqueId());
                player.sendMessage(
                        StringDefaults.IGNORE_PREFIX + "§7Du ignorierst nun §e" + targetOnline.getName() + "§7.");
            } else {
                userData.removeIgnoredPlayer(targetOnline.getUniqueId());
                player.sendMessage(
                        StringDefaults.IGNORE_PREFIX + "§7Du ignorierst nun nicht mehr §e" + targetOnline.getName() + "§7.");
            }
        } else {
            String targetName = args[0];
            UUIDFetcher.getUUID(targetName, uuid -> {
                if (uuid == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§cDieser Spieler konnte nicht gefunden werden.");
                    return;
                }
                OfflinePlayer opTarget = Bukkit.getOfflinePlayer(uuid);
                if (opTarget == null || !opTarget.hasPlayedBefore()) {
                    player.sendMessage(StringDefaults.PREFIX + "§cDieser Spieler war noch nie auf dem Server.");
                    return;
                }
                if (!userData.getIgnoredPlayers().contains(uuid)) {
                    userData.addIgnoredPlayer(uuid);
                    player.sendMessage(
                            StringDefaults.IGNORE_PREFIX + "§7Du ignorierst nun §e" + targetName + "§7.");
                } else {
                    userData.removeIgnoredPlayer(uuid);
                    player.sendMessage(
                            StringDefaults.IGNORE_PREFIX + "§7Du ignorierst nun nicht mehr §e" + targetName + "§7.");
                }
            });
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/ignore <list|Spieler>");
    }
}
