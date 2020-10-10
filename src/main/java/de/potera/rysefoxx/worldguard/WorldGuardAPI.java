package de.potera.rysefoxx.worldguard;

import com.google.common.collect.Lists;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.potera.teamhardcore.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

import java.util.*;

public class WorldGuardAPI {
    public static WorldGuardPlugin WorldGuard;
    public static ArrayList<Player> messagecooldown = new ArrayList();
    public static ArrayList<String> protection = new ArrayList();
    public Map<Player, Set<ProtectedRegion>> playerRegions;
    private Main plugin;

    public WorldGuardAPI(Main plugin) {
        this.plugin = plugin;
        this.playerRegions = new HashMap();
        WorldGuard = (WorldGuardPlugin) Main.getPlugin(Main.class).getServer().getPluginManager().getPlugin("WorldGuard");
    }

    public Map<Player, Set<ProtectedRegion>> getPlayerRegions() {
        return this.playerRegions;
    }

    boolean updateRegions(final Player player, final MovementWay movement, Location to, final PlayerEvent event) {
        Set<ProtectedRegion> regions;
        if (this.playerRegions.get(player) == null) {
            regions = new HashSet<ProtectedRegion>();
        } else {

            regions = new HashSet<ProtectedRegion>((Collection) this.playerRegions.get(player));
        }
        Set<ProtectedRegion> oldRegions = new HashSet<ProtectedRegion>(regions);
        RegionManager rm = WorldGuard.getRegionManager(to.getWorld());
        if (rm == null) {
            return false;
        }
        ApplicableRegionSet appRegions = rm.getApplicableRegions(to);
        for (ProtectedRegion region : appRegions) {
            if (!regions.contains(region)) {
                RegionEnterEvent e = new RegionEnterEvent(region, player, movement, event);
                this.plugin.getServer().getPluginManager().callEvent(e);
                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
                    public void run() {
                        RegionEnteredEvent e = new RegionEnteredEvent(region, player, movement, event);
                        WorldGuardAPI.this.plugin.getServer().getPluginManager().callEvent(e);
                    }
                }, 1L);
                regions.add(region);
            }
        }
        List<ProtectedRegion> app = Lists.newArrayList(appRegions.iterator());
        Iterator<ProtectedRegion> itr = regions.iterator();
        while (itr.hasNext()) {
            final ProtectedRegion region2 = (ProtectedRegion) itr.next();
            if (!app.contains(region2)) {
                if (rm.getRegion(region2.getId()) != region2) {
                    itr.remove();
                    continue;
                }
                RegionLeaveEvent e2 = new RegionLeaveEvent(region2, player, movement, event);
                this.plugin.getServer().getPluginManager().callEvent(e2);
                if (e2.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable() {
                    public void run() {
                        RegionLeftEvent e = new RegionLeftEvent(region2, player, movement, event);
                        WorldGuardAPI.this.plugin.getServer().getPluginManager().callEvent(e);
                    }
                }, 1L);
                itr.remove();
            }
        }

        this.playerRegions.put(player, regions);
        return false;
    }
}
