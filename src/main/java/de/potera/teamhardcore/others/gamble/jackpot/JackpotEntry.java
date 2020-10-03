package de.potera.teamhardcore.others.gamble.jackpot;

import java.util.UUID;

public class JackpotEntry {

    private final UUID uuid;
    private final long entry;

    public JackpotEntry(UUID uuid, long entry) {
        this.uuid = uuid;
        this.entry = entry;
    }

    public UUID getUuid() {
        return uuid;
    }

    public long getEntry() {
        return entry;
    }

}
