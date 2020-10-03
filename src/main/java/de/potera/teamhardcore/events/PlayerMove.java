package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerMove implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onMoveFreeze(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(p.getUniqueId())){
            if(from.getBlockX() != to.getBlockX() || from.getBlockY() < to.getBlockY() || from.getBlockZ() != to.getBlockZ()){
                e.getPlayer().sendMessage("§cDu bist gefreezed!");
                Location newto = from;
                newto.setX(newto.getBlockX() + 0.5);
                newto.setZ(newto.getBlockZ() + 0.5);
                e.setCancelled(true);
                e.getPlayer().teleport(newto, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onTP(PlayerTeleportEvent e){
        if(Main.getInstance().getGeneralManager().getPlayersFreezed().contains(e.getPlayer().getUniqueId())){
            if(e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) return;
            e.getPlayer().sendMessage("§cDu bist gefreezed!");
            e.setCancelled(true);
        }
    }

}
