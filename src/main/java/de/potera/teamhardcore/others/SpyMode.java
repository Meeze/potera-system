package de.potera.teamhardcore.others;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpyMode {

    private final List<UUID> players;
    private boolean all;

    public SpyMode(boolean all) {
        this.all = all;
        this.players = new ArrayList<>();
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
        if (all)
            this.players.clear();
    }

    public void addPlayer(UUID uuid) {
        if (this.players.contains(uuid)) return;
        this.players.add(uuid);
    }

    public void addPlayers(UUID... uuids) {
        for (UUID uuid : uuids)
            addPlayer(uuid);
    }

    public void removePlayer(UUID uuid) {
        if (!this.players.contains(uuid)) return;
        this.players.remove(uuid);
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public enum SpyModeType {
        MESSAGE,
        COMMAND;
    }
}
