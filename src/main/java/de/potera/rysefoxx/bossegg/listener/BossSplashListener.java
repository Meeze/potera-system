package de.potera.rysefoxx.bossegg.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;

import java.util.Collection;

public class BossSplashListener implements Listener {

    @EventHandler
    public void onSplash(PotionSplashEvent e) {
        if (!(e.getPotion().getShooter() instanceof Player)) return;

        Collection<LivingEntity> collection = e.getAffectedEntities();
        for (LivingEntity livingEntity : collection) {
            if (!(livingEntity instanceof Monster)) continue;
            if (!(livingEntity.hasMetadata("BOSS"))) continue;
            if (e.getPotion().getItem().getData().getData() == 37 || e.getPotion().getItem().getData().getData() == 69)
                e.setCancelled(true);

        }

    }

}
