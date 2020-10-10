package de.potera.teamhardcore.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

public class EntityPortal implements Listener {

    @EventHandler
    public void onEntityPortal(EntityPortalEvent event) {
        Entity entity = event.getEntity();

        if (!(entity instanceof Player))
            event.setCancelled(true);
    }

}
