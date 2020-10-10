package de.potera.rysefoxx.bossegg.listener;

import de.potera.rysefoxx.bossegg.BossEgg;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.MetadataValue;

public class BossDeathListener implements Listener {

    @EventHandler
    public void on(EntityDeathEvent e) {
        LivingEntity livingEntity = e.getEntity();
        if (livingEntity.hasMetadata("BOSS")) {
            for (MetadataValue metadataValue : livingEntity.getMetadata("BOSS")) {

                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forID(metadataValue.asString());

                int rewardAmount = Util.randInt(bossEgg.getMinDropAmount(), bossEgg.getMaxDropAmount());

                e.setDroppedExp(0);
                e.getDrops().clear();


                if (bossEgg.isCanSpawnHologram()) {
                    bossEgg.createHologram(livingEntity, bossEgg, rewardAmount, metadataValue.asString());
                }
                if (bossEgg.isBroadcast()) {
                    bossEgg.sendInformations(livingEntity.getKiller(), bossEgg, rewardAmount);
                }

                bossEgg.onDespawn(metadataValue.asString(), bossEgg, livingEntity);
                bossEgg.dropReward(livingEntity, rewardAmount);

            }
        }

    }

}
