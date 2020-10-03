package de.potera.teamhardcore.others.mines;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.files.FileBase;
import de.potera.teamhardcore.utils.Util;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.material.MaterialData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mine extends FileBase {

    private final String name;
    private final Map<MaterialData, Double> materials = new HashMap<>();

    private Location minPos;
    private Location maxPos;
    private Location spawn;
    private String region;
    private int level;
    private boolean accessable;
    private int brokenBlocks;
    private int maxBlocks;

    public Mine(String name) {
        super(File.separator + "mines", name);

        this.name = name;

        loadData();
    }

    public Mine(String name, Location location) {
        super(File.separator + "mines", name);

        this.materials.put(new MaterialData(Material.COBBLESTONE), 50.0D);
        this.materials.put(new MaterialData(Material.STONE), 50.0D);

        this.name = name;

        this.level = 0;
        this.spawn = location;
        this.brokenBlocks = 0;
        saveData();
    }

    public void checkMineState() {
        if (this.maxBlocks == 0)
            return;

        if ((this.brokenBlocks / this.maxBlocks) > 0.5D)
            Main.getInstance().getMinesManager().scheduledMineReset(this, true);

    }

    public void checkMaxBlocks() {
        this.maxBlocks = (this.minPos != null && this.maxPos != null) ? getBlockAmount() : 0;
    }

    public String getName() {
        return name;
    }

    public Location getMinPos() {
        return minPos;
    }

    public void setMinPos(Location minPos) {
        this.minPos = minPos;
    }

    public Location getMaxPos() {
        return maxPos;
    }

    public void setMaxPos(Location maxPos) {
        this.maxPos = maxPos;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Map<MaterialData, Double> getMaterials() {
        return materials;
    }

    public boolean isAccessable() {
        return accessable;
    }

    public void setAccessable(boolean accessable) {
        this.accessable = accessable;
    }

    public int getBrokenBlocks() {
        return brokenBlocks;
    }

    public void setBrokenBlocks(int brokenBlocks) {
        this.brokenBlocks = brokenBlocks;
    }

    public int getMaxBlocks() {
        return maxBlocks;
    }

    public void setMaxBlocks(int maxBlocks) {
        this.maxBlocks = maxBlocks;
    }

    public boolean containsBlock(Material material) {
        for (MaterialData materialData : this.materials.keySet())
            if (materialData.getItemType().equals(material))
                return true;
        return false;
    }

    public void addMaterial(Material material, double chance) {
        this.materials.put(new MaterialData(material), chance);
    }

    public void removeMaterial(Material material) {
        MaterialData toRemove = null;

        for (MaterialData materialData : this.materials.keySet())
            if (materialData.getItemType().equals(material))
                toRemove = materialData;

        if (toRemove != null)
            this.materials.remove(toRemove);
    }

    public boolean isInside(Location loc) {
        return (loc.getWorld() == this.minPos.getWorld() && this.minPos.getX() <= loc.getX() && this.maxPos.getX() >= loc.getX() && this.minPos
                .getY() <= loc.getY() && this.maxPos.getY() >= loc.getY() && this.minPos.getZ() <= loc.getZ() && this.maxPos.getZ() >= loc.getZ());

    }

    private int getBlockAmount() {
        int maxX = Math.max(this.maxPos.getBlockX(), this.minPos.getBlockX());
        int maxY = Math.max(this.maxPos.getBlockY(), this.minPos.getBlockY());
        int maxZ = Math.max(this.maxPos.getBlockZ(), this.minPos.getBlockZ());
        int minX = Math.min(this.maxPos.getBlockX(), this.minPos.getBlockX());
        int minY = Math.min(this.maxPos.getBlockY(), this.minPos.getBlockY());
        int minZ = Math.min(this.maxPos.getBlockZ(), this.minPos.getBlockZ());
        return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
    }

    public void loadData() {
        FileConfiguration cfg = getConfig();

        if (cfg.get("Spawn") != null)
            this.setSpawn(Util.stringToLocation(cfg.getString("Spawn")));

        if (cfg.get("MaxPos") != null)
            this.setMaxPos(Util.stringToLocation(cfg.getString("MaxPos")));

        if (cfg.get("MinPos") != null)
            this.setMinPos(Util.stringToLocation(cfg.getString("MinPos")));

        if (cfg.get("Region") != null)
            this.setRegion(cfg.getString("Region"));

        if (cfg.get("Level") != null)
            this.setLevel(cfg.getInt("Level"));


        List<String> materialList = cfg.getStringList("Materials");
        for (String entryStr : materialList) {
            String[] split = entryStr.split("@@@");
            this.materials.put(new MaterialData(Material.getMaterial(split[0])), Double.parseDouble(split[1]));
        }

        this.maxBlocks = (this.minPos != null && this.maxPos != null) ? getBlockAmount() : 0;
        this.brokenBlocks = 0;
        LogManager.getLogger(Mine.class).info("Mine " + this.name + " successfully loaded");
    }

    public void saveData() {
        FileConfiguration cfg = getConfig();
        cfg.set("Spawn", Util.locationToString(this.getSpawn()));
        cfg.set("MaxPos", Util.locationToString(this.getMaxPos()));
        cfg.set("MinPos", Util.locationToString(this.getMinPos()));
        cfg.set("Region", this.getRegion());
        cfg.set("Level", this.getLevel());

        List<String> materialList = new ArrayList<>();

        for (Map.Entry<MaterialData, Double> entry : this.materials.entrySet()) {
            materialList.add(entry.getKey().getItemType().name() + "@@@" + entry.getValue());
        }

        cfg.set("Materials", materialList);
        saveConfig();
    }

    public void deleteData() {
        File file = getFile();
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }
}
