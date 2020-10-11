package de.potera.realmeze.punishment.controller;

import de.potera.realmeze.punishment.event.PunishListener;
import de.potera.realmeze.punishment.model.Punishment;
import de.potera.realmeze.punishment.model.PunishmentResult;
import de.potera.realmeze.punishment.model.PunishmentType;
import de.potera.realmeze.punishment.service.PunishmentService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@Setter
@NoArgsConstructor
public class PunishmentController {

    private HashMap<UUID, ArrayList<Punishment>> punishments;
    private PunishmentService punishmentService;

    private void addOrInit(UUID affectedPlayer, Punishment punishment) {
        if (getPunishments().containsKey(affectedPlayer)) {
            ArrayList<Punishment> existingPunishments = getPunishments().get(affectedPlayer);
            existingPunishments.add(punishment);
            getPunishments().put(affectedPlayer, existingPunishments);
        } else {
            getPunishments().put(affectedPlayer, new ArrayList<Punishment>() {{
                add(punishment);
            }});
        }
    }

    private PunishmentResult addToPunishments(UUID affectedPlayer, Punishment punishment) {
        Optional<Punishment> potentialExistingPunishment = getPunishment(affectedPlayer, punishment.getPunishmentType());
        if (potentialExistingPunishment.isPresent()) {
            return PunishmentResult.ALREADY_PUNISHED;
        }
        addOrInit(affectedPlayer, punishment);
        return PunishmentResult.SUCCESS;
    }

    private void saveInDatabase(Punishment punishment) {
        getPunishmentService().save(punishment);
    }

    public void loadFromDatabase() {
        setPunishments(new HashMap<>());
     //   List<Punishment> loadedPunishments = getPunishmentService().loadAll();
    //    loadedPunishments.stream().forEach(punishment -> {
     //       UUID playerKey = punishment.getPunishmentReceiver();
     //       addOrInit(playerKey, punishment);
     //   });
    }

    private Punishment buildPunishment(UUID issuer, UUID receiver, String reason, Instant expiresAt, PunishmentType punishmentType) {
        Instant issuedAt = Instant.now();
        Punishment punishment = new Punishment();
        punishment.setPunishmentIssuer(issuer);
        punishment.setPunishmentReceiver(receiver);
        punishment.setReason(reason);
        punishment.setIssuedAt(issuedAt);
        punishment.setExpiresAt(expiresAt);
        punishment.setPunishmentType(punishmentType);
        return punishment;
    }

    private UUID getUUIDFromPlayer(Player player) {
        return player.getUniqueId();
    }

    private UUID getUUIDFromOfflinePlayer(OfflinePlayer offlinePlayer) {
        return offlinePlayer.getUniqueId();
    }

    public Punishment buildKick(Player issuer, Player receiver, String reason) {
        Punishment kickPunishment = buildPunishment(getUUIDFromPlayer(issuer), getUUIDFromPlayer(receiver), reason, null, PunishmentType.KICK);
        return kickPunishment;
    }

    public Punishment buildMute(Player issuer, Player receiver, String reason, Instant expiresAt) {
        Punishment mutePunishment = buildPunishment(getUUIDFromPlayer(issuer), getUUIDFromPlayer(receiver), reason, expiresAt, PunishmentType.MUTE);
        return mutePunishment;
    }

    public Punishment buildBan(Player issuer, OfflinePlayer receiver, String reason, Instant expiresAt) {
        Punishment banPunishment = buildPunishment(getUUIDFromPlayer(issuer), getUUIDFromOfflinePlayer(receiver), reason, expiresAt, PunishmentType.BAN);
        return banPunishment;
    }

    private boolean cantPunish(UUID receiverId) {
        OfflinePlayer offTarget = Bukkit.getOfflinePlayer(receiverId);
        if (offTarget.isOnline()) {
            Player target = (Player) offTarget;
            return !target.hasPermission("potera.punishment.bypass");
        } else {
            // TODO implement check with luckperms if it supports offpermissions(should)
            return false;
        }
    }

    /**
     * @param punishment from builder
     * @return result of action, handle in command accordingly
     */
    public PunishmentResult executePunishment(Punishment punishment) {
        if (cantPunish(punishment.getPunishmentReceiver())) {
            return PunishmentResult.PERMISSION_DENIED;
        }
        PunishmentType type = punishment.getPunishmentType();
        if (type.equals(PunishmentType.BAN)) {
            return doBan(punishment);
        } else if (type.equals(PunishmentType.KICK)) {
            return doKick(punishment);
        } else if (type.equals(PunishmentType.MUTE)) {
            return doMute(punishment);
        }
        return PunishmentResult.UNKNOWN_ERROR;
    }

    private PunishmentResult doBan(Punishment punishment) {
        OfflinePlayer offTarget = Bukkit.getOfflinePlayer(punishment.getPunishmentReceiver());
        if (offTarget.isOnline()) {
            Player target = (Player) offTarget;
            target.kickPlayer(punishment.getReason());
        }
        return addToPunishments(punishment.getPunishmentReceiver(), punishment);
    }

    private PunishmentResult doKick(Punishment punishment) {
        //dont add to punishments as kicks arent persistent
        Player player = Bukkit.getPlayer(punishment.getPunishmentReceiver());
        player.kickPlayer(punishment.getReason());
        return PunishmentResult.SUCCESS;
    }

    private PunishmentResult doMute(Punishment punishment) {
        return addToPunishments(punishment.getPunishmentReceiver(), punishment);
    }

    public Optional<Punishment> getPunishment(UUID playerId, PunishmentType punishmentType) {
        ArrayList<Punishment> punishments = getPunishments().getOrDefault(playerId, new ArrayList<>());
        return punishments.stream().filter(p -> p.getPunishmentType().equals(punishmentType)).findFirst();
    }

    public boolean isPunishmentExpired(UUID playerId, PunishmentType punishmentType) {
        Optional<Punishment> potentialActivePunishment = getPunishment(playerId, punishmentType);
        AtomicBoolean isExpired = new AtomicBoolean(false);
        potentialActivePunishment.ifPresent(punishment -> isExpired.set(punishment.getExpiresAt().isAfter(Instant.now())));
        return isExpired.get();
    }

    public Instant parsePunishTime(String timeToAdd) {
        if (timeToAdd.equalsIgnoreCase("perma")) {
            return Instant.MAX;
        } else {
            String timeWithoutUnit = timeToAdd.substring(0, timeToAdd.length() - 1);
            if (!timeWithoutUnit.matches("\\d+")) {
                return null;
            }
            // last char is unit
            switch (timeToAdd.charAt(timeToAdd.length() - 1)) {
                case 'w':
                    return Instant.now().plus(Integer.parseInt(timeWithoutUnit) * 7, ChronoUnit.DAYS);
                case 'd':
                    return Instant.now().plus(Integer.parseInt(timeWithoutUnit), ChronoUnit.DAYS);
                case 'h':
                    return Instant.now().plus(Integer.parseInt(timeWithoutUnit), ChronoUnit.HOURS);
                case 'm':
                    return Instant.now().plus(Integer.parseInt(timeWithoutUnit), ChronoUnit.MINUTES);
                default:
                    return null;
            }
        }
    }

    public boolean unban(OfflinePlayer offlinePlayer) {
        Optional<Punishment> ban = getPunishment(offlinePlayer.getUniqueId(), PunishmentType.BAN);
        if(ban.isPresent()){
            Punishment punish = ban.get();
            getPunishments().get(offlinePlayer.getUniqueId()).remove(punish);
            return true;
        } else {
            return false;
        }
    }
    public boolean unmute(OfflinePlayer offlinePlayer) {
        Optional<Punishment> mute = getPunishment(offlinePlayer.getUniqueId(), PunishmentType.MUTE);
        if(mute.isPresent()){
            Punishment punish = mute.get();
            getPunishments().get(offlinePlayer.getUniqueId()).remove(punish);
            return true;
        } else {
            return false;
        }
    }

    public String buildReason(String[] reasonArgs, Boolean isSilent) {
        StringBuilder stringBuilder = new StringBuilder();
        //fix for not including reason sometimes idk why this even happens
        if(reasonArgs.length == 1){
            return stringBuilder.append(reasonArgs[0]).toString();
        }
        for (int i = 0; i < reasonArgs.length; i++) {
            if (isSilent != null && i == reasonArgs.length-1) {
                // dont include silent arg (-s/.v)
                continue;
            } else {
                stringBuilder.append(reasonArgs[i]).append(" ");
            }
        }
        return stringBuilder.toString();
    }

}
