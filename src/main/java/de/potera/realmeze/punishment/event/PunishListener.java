package de.potera.realmeze.punishment.event;

import de.potera.realmeze.punishment.controller.PunishmentController;
import org.bukkit.event.Listener;

public interface PunishListener extends Listener {
    void setPunishmentController(PunishmentController punishmentController);
}
