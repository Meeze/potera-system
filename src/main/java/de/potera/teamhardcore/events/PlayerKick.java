package de.potera.teamhardcore.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKick implements Listener {

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (event.getReason().equals("disconnect.spam"))
            event.setCancelled(true);
    }

}
