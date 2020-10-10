package de.potera.rysefoxx.utils;

import de.potera.teamhardcore.Main;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class HologramAPI {

    public static double LINE_HEIGHT = 0.25;
    private Location location;
    private List<String> lines;
    private List<ArmorStand> entities;
    private boolean spawned;
    public static HashMap<HologramAPI, List<ArmorStand>> holograms = new HashMap<>();

    public HologramAPI(Location location, List<String> lines) {
        this.lines = lines;
        this.entities = new LinkedList<>();
        this.spawned = false;
        this.location = location;
        holograms.put(this, new ArrayList<>());
        spawn();
    }

    public void spawn() {
        Chunk cnk = this.location.getChunk();
        if (!cnk.isLoaded()) {
            cnk.load();
        }
        holograms.get(this).forEach(Entity::remove);
        holograms.put(this, new ArrayList<>());
        for (int i = 0; i < this.lines.size(); ++i) {
            Location spawn = this.location.clone().subtract(0.0, i * 0.25, 0.0);
            ArmorStand entity = (ArmorStand) this.location.getWorld().spawnEntity(spawn, EntityType.ARMOR_STAND);
            entity.setGravity(false);
            entity.setBasePlate(false);
            entity.setVisible(false);
            entity.setCustomNameVisible(true);
            entity.setCustomName(this.lines.get(i));
            this.entities.add(entity);
            holograms.get(this).add(entity);
        }
        this.spawned = true;
    }

    public void onDisable(HologramAPI hologramAPI) {
        holograms.get(hologramAPI).forEach(Entity::remove);
        //  entities.forEach(Entity::remove);
    }

    public void update() {
        this.remove();
        new BukkitRunnable() {
            @Override
            public void run() {
                spawn();
            }
        }.runTaskLater(Main.getPlugin(Main.class), 5L);
    }

    public void destroy() {
        this.lines.clear();
        this.remove();
    }

    public ArmorStand get(int index) {
        return this.entities.get(index);
    }

    public HologramAPI add(String line) {
        this.lines.add(line);
        if (this.spawned) {
            this.update();
        }
        return this;
    }

    public void set(int index, String line) {
        holograms.get(this).get(index).setCustomName(line);
        //this.lines.set(index, line);
        //if (this.spawned) {
        //  this.update();
        //}
        //return this;
    }

    private boolean removeEntity(ArmorStand entity) {
        this.lines.remove(entity.getCustomName());
        entity.remove();
        boolean removed = this.entities.remove(entity);
        if (removed && this.spawned) {
            this.update();
        }
        return removed;
    }

    public void remove() {
        for (ArmorStand stand : this.entities) {
            stand.remove();
        }
    }

    public boolean remove(String line) {
        for (ArmorStand entity : this.entities) {
            if (entity.getCustomName().equals(line)) {
                return this.removeEntity(entity);
            }
        }
        return false;
    }
}