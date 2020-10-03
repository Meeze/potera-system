package de.potera.teamhardcore.listener;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.events.custom.PlayerCombatTaggedEvent;
import de.potera.teamhardcore.managers.CombatManager;
import de.potera.teamhardcore.utils.StringDefaults;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CombatListener implements Listener {

    private CombatManager combatManager;

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();
        if (combatManager.isTagged(p) && p.getGameMode() != GameMode.CREATIVE && !p.hasPermission("potera.combat.admin"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (combatManager.isTagged(p) && !p.hasPermission("potera.combat.admin"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onGamemode(PlayerGameModeChangeEvent e) {
        Player p = e.getPlayer();
        if (combatManager.isTagged(p) && !p.hasPermission("potera.combat.admin")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionSplash(PotionSplashEvent e) {
        ThrownPotion potion = e.getPotion();

        for (PotionEffect effect : potion.getEffects()) {
            if (effect.getType() == PotionEffectType.INVISIBILITY) {
                List<Player> toRemove = new ArrayList<>();
                for (LivingEntity affected : e.getAffectedEntities()) {
                    if (!(affected instanceof Player)) continue;
                    Player target = (Player) affected;
                    if (combatManager.isTagged(target)) {
                        toRemove.add(target);
                    }
                }
                toRemove.forEach(p -> e.setIntensity(p, 0.0D));
            }
        }
    }

    public void onConsume(PlayerItemConsumeEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (!combatManager.isTagged(p)) {
            return;
        }
        if (item.getType() == Material.POTION) {
            PotionMeta meta = (PotionMeta) item.getItemMeta();
            for (PotionEffect effect : meta.getCustomEffects()) {
                if (effect.getType() == PotionEffectType.INVISIBILITY) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (!combatManager.locationChanged(from, to) || !combatManager.isTagged(p)) return;
        combatManager.updateWall(p);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (combatManager.isTagged(p))
            combatManager.setTagged(p, false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (combatManager.isTagged(p)) {
            p.setHealth(0.0D);
            Bukkit.broadcastMessage(
                    StringDefaults.PVP_PREFIX + "§7" + p.getName() + " §chat sich im Kampf ausgeloggt.");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (combatManager.isTagged(p)) {
            combatManager.setTagged(p, false);
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        String cmd = e.getMessage().substring(1).toLowerCase();

        if (combatManager.isTagged(p) && !p.hasPermission("potera.combat.admin")) {
            for (String allowedCommands : Main.getInstance().getFileManager().getConfigFile().getCombatCommands()) {
                if (cmd.startsWith(allowedCommands)) {
                    return;
                }
            }
            e.setCancelled(true);
            p.sendMessage(StringDefaults.PREFIX + "§cDieser Befehl kann im Kampf nicht ausgeführt werden.");
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.isCancelled() || !(e.getEntity() instanceof Player)) {
            return;
        }
        Player ent = (Player) e.getEntity();

        if (e.getDamager() instanceof Player) {
            Player damager = (Player) e.getDamager();

            if (damager == ent) {
                return;
            }

            if (combatManager.isTagged(damager)) {
                combatManager.updateTime(damager);
            } else {
                PlayerCombatTaggedEvent event = new PlayerCombatTaggedEvent(damager, true);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    combatManager.setTagged(damager, true);
                    damager.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDu bist nun im Kampf! Logge dich nicht aus.");
                }
            }

            if (combatManager.isTagged(ent)) {
                combatManager.updateTime(ent);
            } else {
                PlayerCombatTaggedEvent event = new PlayerCombatTaggedEvent(ent, true);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    combatManager.setTagged(ent, true);
                    ent.sendMessage(StringDefaults.PVP_PREFIX + "§cDu bist nun im Kampf! Logge dich nicht aus.");
                }
            }
        }


        if (e.getDamager() instanceof Projectile) {
            Projectile pro = (Projectile) e.getDamager();

            if (!(pro.getShooter() instanceof Player)) {
                return;
            }
            Player damager = (Player) pro.getShooter();

            if (damager == ent) {
                return;
            }

            if (combatManager.isTagged(damager)) {
                combatManager.updateTime(damager);
            } else {
                PlayerCombatTaggedEvent event = new PlayerCombatTaggedEvent(damager, true);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    combatManager.setTagged(damager, true);
                    damager.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDu bist nun im Kampf! Logge dich nicht aus.");
                }
            }

            if (combatManager.isTagged(ent)) {
                combatManager.updateTime(ent);
            } else {
                PlayerCombatTaggedEvent event = new PlayerCombatTaggedEvent(ent, true);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    combatManager.setTagged(ent, true);
                    damager.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDu bist nun im Kampf! Logge dich nicht aus.");
                }
            }
        }
    }

}
