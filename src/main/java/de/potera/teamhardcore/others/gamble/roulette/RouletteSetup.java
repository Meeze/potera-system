package de.potera.teamhardcore.others.gamble.roulette;

import org.bukkit.entity.Player;

public class RouletteSetup {

    public static final int ENTRY_VALUE = 10000;
    public static final double MULTIPLIER_VALUE = 0.5D;
    public static final double MAX_MULTIPLIER = 100.0D;
    public static final double MIN_MULTIPLIER = 1.1D;
    public static final int MIN_ENTRY = 1;

    private final Player player;
    private double multiplier;
    private long entry;

    public RouletteSetup(Player player) {
        this.player = player;
        this.multiplier = MIN_MULTIPLIER;
        this.entry = MIN_ENTRY;
    }

    public Player getPlayer() {
        return player;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }

    public long getEntry() {
        return entry;
    }

    public void setEntry(long entry) {
        this.entry = entry;
    }
}
