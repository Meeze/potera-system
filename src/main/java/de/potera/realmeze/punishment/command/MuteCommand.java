package de.potera.realmeze.punishment.command;

import de.potera.realmeze.punishment.controller.PunishmentController;
import de.potera.realmeze.punishment.model.Punishment;
import de.potera.realmeze.punishment.model.PunishmentResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Arrays;

@Getter
@AllArgsConstructor
public class MuteCommand implements CommandExecutor {

    private final PunishmentController punishmentController;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("mute")) {
            return false;
        }
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        switch (args.length) {
            case 0:
            case 1:
            case 2:
                showUsage(player);
                break;
            default:
                Player receiver = Bukkit.getPlayer(args[0]);
                Instant expiresAt = getPunishmentController().parsePunishTime(args[1]);
                String[] reason = Arrays.copyOfRange(args, 2, args.length);
                mute(player, receiver, expiresAt, reason);
                break;
        }
        return true;
    }

    private void showUsage(Player player) {
        player.sendMessage("USAGE: mute [Player] <time(1h, 1w, perma)> <Reason...>");
    }

    private void mute(Player player, Player receiver, Instant expiresAt, String... reasonArgs) {
        String reason = getPunishmentController().buildReason(reasonArgs, null);
        if (null == expiresAt) {
            player.sendMessage("Bitte gib eine valide zahl ein!");
            showUsage(player);
            return;
        }
        if(null == receiver) {
            player.sendMessage("Bitte gib einen validen Spieler ein!");
            return;
        }
        Punishment mute = getPunishmentController().buildMute(player, receiver, reason, expiresAt);
        PunishmentResult result = getPunishmentController().executePunishment(mute);
        if (result == PunishmentResult.SUCCESS) {
            player.sendMessage("Du hast " + receiver.getName() + " gemuted! Grund: " + mute.getReason());
        } else if (result == PunishmentResult.PERMISSION_DENIED) {
            player.sendMessage("du kannst " + receiver.getName() + " nicht muten");
        } else if (result == PunishmentResult.ALREADY_PUNISHED) {
            player.sendMessage(receiver.getName() + " ist bereits gemuted!");
        } else {
            Bukkit.broadcastMessage("idk what happened tbh");
        }
    }

}
