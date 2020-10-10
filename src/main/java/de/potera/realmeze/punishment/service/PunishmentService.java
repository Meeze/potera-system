package de.potera.realmeze.punishment.service;

import de.potera.realmeze.punishment.model.Punishment;
import de.potera.realmeze.punishment.model.PunishmentType;

import java.util.List;
import java.util.UUID;

public interface PunishmentService {

    void save(Punishment punishment);
    Punishment load(UUID id);
    List<Punishment> loadAll();
    List<Punishment> loadAll(PunishmentType type);
}
