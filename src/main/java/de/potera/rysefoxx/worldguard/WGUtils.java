package de.potera.rysefoxx.worldguard;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class WGUtils {
    public static boolean isWithinRegion(Player player, String region) {
        return isWithinRegion(player.getLocation(), region);
    }

    public static boolean isWithinRegion(Block block, String region) {
        return isWithinRegion(block.getLocation(), region);
    }

    public static boolean isWithinRegion(Location loc, String region) {
        WorldGuardPlugin guard = WorldGuardAPI.WorldGuard;
        RegionManager manager = guard.getRegionManager(loc.getWorld());
        ApplicableRegionSet set = manager.getApplicableRegions(loc);
        for (ProtectedRegion each : set) {
            if (each.getId().equalsIgnoreCase(region)) {
                return true;
            }
        }
        return false;
    }

    public static boolean regionNameContains(Location loc, String region) {
        WorldGuardPlugin guard = WorldGuardAPI.WorldGuard;
        RegionManager manager = guard.getRegionManager(loc.getWorld());
        ApplicableRegionSet set = manager.getApplicableRegions(loc);
        for (ProtectedRegion each : set) {
            if (each.getId().startsWith(region)) {
                return true;
            }
        }
        return false;
    }

    public static ProtectedRegion getRegion(Block b) {
        ApplicableRegionSet ar = WorldGuardAPI.WorldGuard.getRegionManager(b.getWorld()).getApplicableRegions(b.getLocation());
        Iterator<ProtectedRegion> prs = ar.iterator();
        if (prs.hasNext()) {
            return (ProtectedRegion) prs.next();
        }
        return null;
    }

    public static ProtectedRegion getRegion(Player p) {
        ApplicableRegionSet ar = WorldGuardAPI.WorldGuard.getRegionManager(p.getWorld()).getApplicableRegions(p.getLocation());
        Iterator<ProtectedRegion> prs = ar.iterator();
        if (prs.hasNext()) {
            return (ProtectedRegion) prs.next();
        }
        return null;
    }

    public static boolean allows(StateFlag flag, Player p) {
        return WorldGuardPlugin.inst().getGlobalRegionManager().allows(flag, p.getLocation());
    }

    public static boolean allows(StateFlag flag, Block b) {
        return WorldGuardPlugin.inst().getGlobalRegionManager().allows(flag, b.getLocation());
    }

    public static boolean can(StateFlag flag, Block block) {
        WorldGuardPlugin wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
        RegionManager regionManager = wg.getRegionManager(block.getLocation().getWorld());
        if (regionManager == null)
            return true;

        ApplicableRegionSet set1 = regionManager.getApplicableRegions(block.getLocation());

        if (set1 == null)
            return true;

        return (set1.queryState(null, new StateFlag[]{flag}) != StateFlag.State.DENY);
    }
}
