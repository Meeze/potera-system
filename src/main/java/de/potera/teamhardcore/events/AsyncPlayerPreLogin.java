package de.potera.teamhardcore.events;

import de.potera.realmeze.punishment.controller.PunishmentController;
import de.potera.realmeze.punishment.event.PunishListener;
import de.potera.realmeze.punishment.model.PunishmentType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;

@Getter
@Setter
@NoArgsConstructor
public class AsyncPlayerPreLogin implements PunishListener {

    private PunishmentController punishmentController;

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent event) {
        punishmentController.getPunishment(event.getUniqueId(), PunishmentType.BAN).ifPresent(punishment -> {
            if(getPunishmentController().isPunishmentExpired(punishment)){
                getPunishmentController().unban(Bukkit.getOfflinePlayer(event.getUniqueId()));
            } else {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, punishment.getReason());
            }
        });

        if (Bukkit.getPlayer(event.getUniqueId()) != null || Bukkit.getPlayer(event.getName()) != null)
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§cDu bist bereits auf dem Server.");
    }

}
