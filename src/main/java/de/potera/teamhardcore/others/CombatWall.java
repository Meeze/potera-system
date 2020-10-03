package de.potera.teamhardcore.others;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class CombatWall {

    private World world;
    private Location minPos;
    private Location maxPos;
    private List<Location> wallLocations;

    public CombatWall(Location minPos, Location maxPos) {
        this.wallLocations = new ArrayList<>();
        this.world = minPos.getWorld();
        this.minPos = minPos;
        this.maxPos = maxPos;

        calculateWall();
    }

    private void calculateWall() {
        for (int x = this.minPos.getBlockX(); x <= this.maxPos.getBlockX(); x++) {
            for (int y = this.minPos.getBlockY(); y <= this.maxPos.getBlockY(); y++) {
                for (int z = this.minPos.getBlockZ(); z <= this.maxPos.getBlockZ(); z++) {
                    Location loc = new Location(this.world, x, y, z);
                    if (contains(loc))
                        this.wallLocations.add(loc);
                }
            }
        }
    }

    public boolean contains(Location loc) {
        if (this.world != loc.getWorld())
            return false;
        return (loc.getY() <= this.maxPos.getY() && loc.getY() >= this.minPos.getY() && ((loc
                .getX() == this.minPos.getX() && loc.getZ() >= this.minPos.getZ() && loc.getZ() <= this.maxPos.getZ()) || (loc
                .getZ() == this.minPos.getZ() && loc.getX() >= this.minPos.getX() && loc.getX() <= this.maxPos.getX()) || (loc
                .getX() == this.maxPos.getX() && loc.getZ() >= this.minPos.getZ() && loc.getZ() <= this.maxPos.getZ()) || (loc
                .getZ() == this.maxPos.getZ() && loc.getX() >= this.minPos.getX() && loc.getX() <= this.maxPos.getX())));
    }

    public Location getMaxPos() {
        return maxPos;
    }

    public Location getMinPos() {
        return minPos;
    }

    public List<Location> getWallLocations() {
        return wallLocations;
    }

    public World getWorld() {
        return world;
    }
}
