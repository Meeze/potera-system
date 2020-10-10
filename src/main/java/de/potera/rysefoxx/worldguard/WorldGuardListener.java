package de.potera.rysefoxx.worldguard;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.potera.teamhardcore.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Set;

public class WorldGuardListener implements Listener {
    public static WorldGuardAPI wgAPI = new WorldGuardAPI(Main.getPlugin(Main.class));

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Set<ProtectedRegion> regions = (Set) wgAPI.playerRegions.remove(e.getPlayer());
        if (regions != null) {
            for (ProtectedRegion region : regions) {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
                Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(leaveEvent);
                Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(leftEvent);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
//		Set<ProtectedRegion> regions = (Set) wgAPI.playerRegions.remove(e.getPlayer());
//		if (regions != null) {
//			for (ProtectedRegion region : regions) {
//				RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
//				RegionLeftEvent leftEvent = new RegionLeftEvent(region, e.getPlayer(), MovementWay.DISCONNECT, e);
//				Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(leaveEvent);
//				Main.getPlugin(Main.class).getServer().getPluginManager().callEvent(leftEvent);
//			}
//		}
    }

    @EventHandler
    public void onFlyToggle(PlayerToggleFlightEvent e) {
//		if (WGUtils.isWithinRegion(e.getPlayer(), "spawn-safezone") && !e.getPlayer().hasPermission(S.perm + "fly.bypass") && e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
//
//			e.getPlayer().setFlying(false);
//			e.getPlayer().setAllowFlight(false);
//			e.getPlayer().sendMessage(String.valueOf(S.pr) + "Du kannst hier nicht Fliegen.");
//		}
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
//		if (WGUtils.isWithinRegion(e.getPlayer(), "spawn-safezone") && !e.getPlayer().hasPermission(S.perm + "fly.bypass") && e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
//
//			e.getPlayer().setFlying(false);
//			e.getPlayer().setAllowFlight(false);
//			e.getPlayer().sendMessage(String.valueOf(S.pr) + "Du kannst hier nicht Fliegen.");
//		}
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(wgAPI.updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        e.setCancelled(wgAPI.updateRegions(e.getPlayer(), MovementWay.TELEPORT, e.getTo(), e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        wgAPI.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation(), e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        wgAPI.updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation(), e);
    }
}
