package de.potera.realmeze.punishment.command;

import de.potera.realmeze.punishment.controller.PunishmentController;
import de.potera.realmeze.punishment.model.Punishment;
import de.potera.realmeze.punishment.model.PunishmentResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public class BanCommand implements CommandExecutor {

    private final PunishmentController punishmentController;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("ban")) {
            return false;
        }
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        switch (args.length) {
            case 0:
                showUsage(player);
                break;
            case 1:
                OfflinePlayer receiver = Bukkit.getOfflinePlayer(args[0]);
                ban(player, receiver, Instant.MAX, "-vocal", "Generic Reason");
                break;
            case 2:
                receiver = Bukkit.getOfflinePlayer(args[0]);
                Instant expiresAt = getPunishmentController().parsePunishTime(args[1]);
                ban(player, receiver, expiresAt, "-vocal", "Generic Reason");
                break;
            default:
                receiver = Bukkit.getOfflinePlayer(args[0]);
                expiresAt = getPunishmentController().parsePunishTime(args[1]);
                String[] reason = Arrays.copyOfRange(args, 2, args.length);
                ban(player, receiver, expiresAt, args[args.length - 1], reason);
                break;
        }
        return true;
    }

    private void showUsage(Player player) {
        player.sendMessage("USAGE: ban [Player] <time(1h, 1w, perma)> <Reason...> <-silent|-vocal>");
    }

    private void ban(Player player, OfflinePlayer receiver, Instant expiresAt, String potentialSilentArg, String... reasonArgs) {
        Boolean isSilent = parseSilent(potentialSilentArg);
        String reason = getPunishmentController().buildReason(reasonArgs, isSilent);
        if (null == isSilent) {
            player.sendMessage("(Info) Kein Broadcast parameter gesetzt, defaulting to -vocal.");
            isSilent = false;
        }
        if (null == expiresAt) {
            player.sendMessage("Bitte gib eine valide zahl ein!");
            showUsage(player);
            return;
        }
        Punishment ban = getPunishmentController().buildBan(player, receiver, reason, expiresAt);
        PunishmentResult result = getPunishmentController().executePunishment(ban);
        if(result == PunishmentResult.SUCCESS){
            player.sendMessage("Du hast " + receiver.getName() + " gebannt! Grund: " + ban.getReason());
            if (!isSilent) {
                Bukkit.broadcastMessage(receiver.getName() + " wurde gebannt! Grund: " + ban.getReason());
            }
        } else if(result == PunishmentResult.PERMISSION_DENIED) {
            player.sendMessage("du kannst " + receiver.getName() + " nicht bannen");
        } else if(result == PunishmentResult.ALREADY_PUNISHED) {
            player.sendMessage(receiver.getName() + " ist bereits gebannt!");
        } else {
            Bukkit.broadcastMessage("idk what happened tbh");
        }
    }

    private Boolean parseSilent(String toParse) {
        if (toParse.equalsIgnoreCase("-s") || toParse.equalsIgnoreCase("-silent")) {
            return true;
        } else if (toParse.equalsIgnoreCase("-v") || toParse.equalsIgnoreCase("-vocal")) {
            return false;
        } else {
            return null;
        }
    }
}
