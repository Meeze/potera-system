package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntilagManager {

    private final List<Material> ignoredMaterials;
    private BukkitTask clearTask;

    public AntilagManager() {
        this.ignoredMaterials = new ArrayList<>(
                Arrays.asList(Material.DIAMOND_SWORD, Material.DIAMOND_PICKAXE, Material.DIAMOND_HELMET,
                        Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS));

        startLagMonitoringTask();
    }

    public boolean isClearable(Entity entity) {
        if (entity instanceof org.bukkit.entity.LivingEntity) {
            return !(entity instanceof org.bukkit.entity.Tameable) && !(entity instanceof org.bukkit.entity.Player);
        }
        if (entity instanceof Item) {
            Item item = (Item) entity;
            if (this.ignoredMaterials.contains(item.getItemStack().getType()))
                return false;
        }
        return entity instanceof Item;
    }

    public void clearEntities() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity ent : world.getEntities()) {
                if (!isClearable(ent)) continue;
                ent.remove();
            }
        }
    }

    private void startLagMonitoringTask() {
        new BukkitRunnable() {
            int counterTime = 0;

            public void run() {
                if (AntilagManager.this.clearTask != null) return;
                if (AntilagManager.this.identifyCriticalLag()) {
                    AntilagManager.this.startClearLag(1);
                    return;
                }
                this.counterTime++;
                if (this.counterTime >= 27) {
                    AntilagManager.this.startClearLag(3);
                    this.counterTime = 0;
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 30L, 200L);
    }

    private void startClearLag(final int fifthteens) {
        this.clearTask = (new BukkitRunnable() {
            int count = fifthteens;

            public void run() {
                if (this.count == 1) {
                    int countCopy = this.count;
                    if (Bukkit.getOnlinePlayers().size() > 0 || AntilagManager.this.identifyCriticalLag())
                        Bukkit.getScheduler().runTask(Main.getInstance(), () -> Bukkit.broadcastMessage(
                                StringDefaults.LAG_PREFIX + "§7Alle Entities werden in §a" + (countCopy * 15) + " Sekunden §7entfernt."));
                }
                if (this.count <= 0) {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        boolean crit = AntilagManager.this.identifyCriticalLag();
                        AntilagManager.this.clearEntities();
                        if (Bukkit.getOnlinePlayers().size() > 0 || crit)
                            Bukkit.broadcastMessage(
                                    StringDefaults.LAG_PREFIX + "§7Alle Entities wurden erfolgreich entfernt.");
                    });
                    AntilagManager.this.clearTask = null;
                    cancel();
                    return;
                }
                this.count--;
            }
        }).runTaskTimerAsynchronously(Main.getInstance(), 0L, 300L);
    }

    private boolean identifyCriticalLag() {
        int countEntities = 0;
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (isClearable(entity))
                    countEntities++;
            }
        }
        return (countEntities >= 1000);
    }

}
