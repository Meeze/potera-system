package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (Main.getInstance().getGeneralManager().getPlayersInGodMode().contains(player))
                event.setCancelled(true);

            if (event.getCause() == EntityDamageEvent.DamageCause.FALL)
                event.setCancelled(true);

            if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(player.getUniqueId()))
                event.setCancelled(true);

        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();

            if (damager == player) return;

            if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(damager.getUniqueId())) {
                event.setCancelled(true);
                return;
            }

            if (Main.getInstance().getClanManager().hasClan(
                    player.getUniqueId()) && Main.getInstance().getClanManager().hasClan(damager.getUniqueId())) {
                Clan pClan = Main.getInstance().getClanManager().getClan(player.getUniqueId());
                Clan dClan = Main.getInstance().getClanManager().getClan(damager.getUniqueId());

                if (pClan == dClan && !pClan.isPvpAllowed()) {
                    event.setCancelled(true);
                    damager.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§7Du bist mit §e" + player.getName() + " §7im selben Clan.");
                    return;
                }
            }
            return;
        }

        if (event.getDamager() instanceof ThrownPotion) {
            ThrownPotion potion = (ThrownPotion) event.getDamager();

            if (potion.getShooter() instanceof Player) {
                Player damager = (Player) potion.getShooter();

                if (damager == player) {
                    for (PotionEffect effect : potion.getEffects()) {
                        if (effect.getType().equals(PotionEffectType.HARM)) {
                            event.setCancelled(true);
                        }
                    }
                    return;
                }

                if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(damager.getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }

                if (Main.getInstance().getClanManager().hasClan(
                        player.getUniqueId()) && Main.getInstance().getClanManager().hasClan(damager.getUniqueId())) {
                    Clan pClan = Main.getInstance().getClanManager().getClan(player.getUniqueId());
                    Clan dClan = Main.getInstance().getClanManager().getClan(damager.getUniqueId());

                    if (pClan == dClan && !pClan.isPvpAllowed()) {
                        event.setCancelled(true);
                        damager.sendMessage(
                                StringDefaults.CLAN_PREFIX + "§7Du bist mit §e" + player.getName() + " §7im selben Clan.");
                        return;
                    }
                }
            }
            return;
        }

        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Player) {
                Player damager = (Player) projectile.getShooter();

                if (damager == player) {
                    return;
                }

                if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(damager.getUniqueId())) {
                    event.setCancelled(true);
                    return;
                }

                if (Main.getInstance().getClanManager().hasClan(
                        player.getUniqueId()) && Main.getInstance().getClanManager().hasClan(damager.getUniqueId())) {
                    Clan pClan = Main.getInstance().getClanManager().getClan(player.getUniqueId());
                    Clan dClan = Main.getInstance().getClanManager().getClan(damager.getUniqueId());

                    if (pClan == dClan && !pClan.isPvpAllowed()) {
                        event.setCancelled(true);
                        damager.sendMessage(
                                StringDefaults.CLAN_PREFIX + "§7Du bist mit §e" + player.getName() + " §7im selben Clan.");
                        return;
                    }
                }
            }
            return;
        }

    }

}
