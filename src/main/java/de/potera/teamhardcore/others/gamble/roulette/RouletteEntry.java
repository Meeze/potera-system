package de.potera.teamhardcore.others.gamble.roulette;

import java.util.UUID;

public class RouletteEntry {

    private final UUID uuid;
    private final double multiplier;
    private final long entry;

    public RouletteEntry(UUID uuid, double multiplier, long entry) {
        this.uuid = uuid;
        this.multiplier = multiplier;
        this.entry = entry;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public long getEntry() {
        return entry;
    }

}
