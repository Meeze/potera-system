package de.potera.teamhardcore.others;

import org.bukkit.Location;

import java.util.UUID;

public class Home {
    private final UUID playerUuid;
    private final String name;
    private final Location position;
    private final long creationDate;
    private long lastTeleportDate;

    public Home(UUID playerUuid, String name, Location position, long creationDate, long lastTeleportDate) {
        this.playerUuid = playerUuid;
        this.name = name;
        this.position = position;
        this.creationDate = creationDate;
        this.lastTeleportDate = lastTeleportDate;
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public String getName() {
        return this.name;
    }

    public Location getPosition() {
        return this.position;
    }

    public long getCreationDate() {
        return this.creationDate;
    }

    public long getLastTeleportDate() {
        return this.lastTeleportDate;
    }

    public void setLastTeleportDate(long lastTeleportDate) {
        this.lastTeleportDate = lastTeleportDate;
    }

    public boolean isHomeSafe() {
        Location homeLoc = this.position.getBlock().getLocation();
        Location homeLocTop = homeLoc.clone().add(0.0D, 1.0D, 0.0D);
        return homeLoc.getBlock().getType().isTransparent() && homeLocTop.getBlock().getType().isTransparent();
    }

}
