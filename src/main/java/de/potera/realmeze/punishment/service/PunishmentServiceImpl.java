package de.potera.realmeze.punishment.service;

import de.potera.realmeze.punishment.model.Punishment;
import de.potera.realmeze.punishment.model.PunishmentType;
import de.potera.teamhardcore.db.CompletionState;

import java.util.List;
import java.util.UUID;

public class PunishmentServiceImpl implements PunishmentService, CompletionState {
    @Override
    public void save(Punishment punishment) {
        UUID uuid = UUID.randomUUID();
        String tableName = punishment.getPunishmentType().getName() + "_database";
        
    }

    @Override
    public Punishment load(UUID id) {
        return null;
    }

    @Override
    public List<Punishment> loadAll() {
        return null;
    }

    @Override
    public List<Punishment> loadAll(PunishmentType type) {
        return null;
    }

    @Override
    public List<Runnable> getReadyExecutors() {
        return null;
    }

    @Override
    public void addReadyExecutor(Runnable paramRunnable) {

    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReady(boolean paramBoolean) {

    }
}
