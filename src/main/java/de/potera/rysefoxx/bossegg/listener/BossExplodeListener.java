package de.potera.rysefoxx.bossegg.listener;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;


public class BossExplodeListener implements Listener {

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        Entity entity = e.getEntity();

        if (!entity.hasMetadata("BOSS")) return;
        
        e.setCancelled(true);

    }

}
