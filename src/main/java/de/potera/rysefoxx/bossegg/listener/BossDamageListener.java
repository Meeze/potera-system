package de.potera.rysefoxx.bossegg.listener;

import de.potera.rysefoxx.bossegg.BossEgg;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.MetadataValue;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BossDamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && !(e.getEntity() instanceof Player)) {
            LivingEntity livingEntity = (LivingEntity) e.getEntity();

            if (!livingEntity.hasMetadata("BOSS")) return;

            for (MetadataValue string : livingEntity.getMetadata("BOSS")) {
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forID(string.asString());
                livingEntity.getPassenger().setCustomName(bossEgg.getHoloText().replace("%health%", String.valueOf(Math.round(livingEntity.getHealth()))).replace("%maxHealth%", String.valueOf(Math.round(livingEntity.getMaxHealth()))));
                if (!bossEgg.isCanUseAbilities()) return;

                if (Util.getChance(bossEgg.getAbilityChance())) {
                    bossEgg.useRandomAbility(livingEntity);
                }

            }
        } else if (e.getDamager() instanceof Monster) {
            if (!e.getDamager().hasMetadata("BOSS")) return;
            for (MetadataValue string : e.getDamager().getMetadata("BOSS")) {
                double damageDone = Main.getPlugin(Main.class).getBossEggManager().getDamageDone().get(string.asString());
                damageDone += round(e.getFinalDamage() / 2, 2);
                Main.getPlugin(Main.class).getBossEggManager().getDamageDone().put(string.asString(), damageDone);
            }
        }
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
