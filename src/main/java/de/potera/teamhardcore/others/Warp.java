package de.potera.teamhardcore.others;

import org.bukkit.Location;

public class Warp {

    private final String warpName;
    private final Location location;

    public Warp(String warpName, Location location) {
        this.warpName = warpName;
        this.location = location;
    }


    public String getName() {
        return this.warpName;
    }


    public Location getLocation() {
        return this.location;
    }

}
