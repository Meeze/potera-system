package de.potera.rysefoxx.bossegg;

import de.potera.teamhardcore.Main;
import lombok.Getter;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;


@Getter

public class BossEggAbilities {

    private BukkitTask pickUpTask;

    public void pickUpPlayerLocation(Player player, Location teleportTo) {
        if (player == null) {
            Main.getInstance().getLogger().warning("BossEggAbilities Zeile 23 - Player null");
            return;
        }
        if (teleportTo == null) {
            Main.getInstance().getLogger().warning("BossEggAbilities Zeile 27 - Location null");
            return;
        }
        player.playSound(player.getLocation(), Sound.ANVIL_USE,3,2);
        pickUpTask = new BukkitRunnable() {
            @Override
            public void run() {
                player.teleport(teleportTo);
            }
        }.runTaskTimer(Main.getPlugin(Main.class), 0L, 1L);

    }

    public void throwPlayer(Player player, Location teleportTo) {
        pickUpPlayerLocation(player, teleportTo);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (pickUpTask != null) {
                    pickUpTask.cancel();
                }

                Vector playerVector = player.getLocation().toVector();
                Vector spawnVector = player.getWorld().getSpawnLocation().toVector();
                Vector vectorToSet = playerVector.clone().subtract(spawnVector).multiply(-2 / spawnVector.distance(playerVector)).setY(0.7);
                player.setVelocity(vectorToSet);
            }
        }.runTaskLater(Main.getPlugin(Main.class), 20L);
    }

    public void throwPlayerUp(Player player, Location teleportTo) {
        pickUpPlayerLocation(player, teleportTo);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (pickUpTask != null) {
                    pickUpTask.cancel();
                }
                Vector playerVector = player.getLocation().toVector();
                Vector worldVector = player.getWorld().getSpawnLocation().toVector();
                Vector vectorToSet = playerVector.clone().multiply(-2 / worldVector.distance(playerVector)).setY(3);
                player.setVelocity(vectorToSet);
            }
        }.runTaskLater(Main.getPlugin(Main.class), 20L);

    }

    public void throwNearbyPlayers(LivingEntity livingEntity, double v, double v1, double v2) {
        if (livingEntity == null) return;

        for (Entity entity : livingEntity.getNearbyEntities(v, v1, v2)) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            player.setVelocity(player.getLocation().getDirection().multiply(-5).normalize().multiply(5).setY(0.9));
            player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 2);

        }
    }

    public void throwNearbyPlayersUp(LivingEntity livingEntity, double v, double v1, double v2) {
        if (livingEntity == null) return;

        for (Entity entity : livingEntity.getNearbyEntities(v, v1, v2)) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            Vector playerVector = player.getLocation().toVector();
            Vector worldVector = player.getWorld().getSpawnLocation().toVector();
            Vector vectorToSet = playerVector.clone().multiply(-2 / worldVector.distance(playerVector)).setY(3);
            player.setVelocity(vectorToSet);

        }
    }

    public void teleportPlayer(Player player, Location bossLocation, int xToAdd, int yToAdd, int zToAdd) {
        if (player == null) return;
        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 5, 5);
        player.teleport(bossLocation.add(xToAdd, yToAdd, zToAdd));
    }

    public void teleportNearbyPlayers(LivingEntity livingEntity, double v, double v1, double v2, Location bossLocation, int xToAdd, int yToAdd, int zToAdd) {
        if (livingEntity == null) return;
        for (Entity entity : livingEntity.getNearbyEntities(v, v1, v2)) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 5, 5);
            player.teleport(bossLocation.add(xToAdd, yToAdd, zToAdd));
        }
    }
}
