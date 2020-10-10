package de.potera.teamhardcore.files;

import de.potera.teamhardcore.others.Warp;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WarpFile extends FileBase {

    private final Map<String, Warp> warps = new HashMap<>();

    public WarpFile() {
        super("", "warps");
        loadWarps();
    }

    private void loadWarps() {
        this.warps.clear();
        FileConfiguration cfg = getConfig();

        if (cfg.get("Warps") == null)
            return;

        for (String warpName : cfg.getConfigurationSection("Warps").getKeys(false)) {
            Location location = Util.stringToLocation(cfg.getString("Warps." + warpName));
            Warp warp = new Warp(warpName, location);
            this.warps.put(warpName, warp);
            System.out.println(warpName + " - " + location.toString());
        }
    }

    public void addWarp(String warpName, Location loc) {
        if (this.warps.containsKey(warpName))
            return;
        this.warps.put(warpName, new Warp(warpName, loc));

        FileConfiguration cfg = getConfig();
        cfg.set("Warps." + warpName, Util.locationToString(loc));
        saveConfig();
    }

    public void removeWarp(String warpName) {
        if (!this.warps.containsKey(warpName))
            return;
        this.warps.remove(warpName);
        FileConfiguration cfg = getConfig();
        cfg.set("Warps." + warpName, null);
        saveConfig();
    }

    public Warp getWarp(String warpName) {
        Optional<String> warp = this.warps.keySet().stream().filter(s -> s.equalsIgnoreCase(warpName)).findFirst();
        return warp.map(this.warps::get).orElse(null);
    }

    public Map<String, Warp> getWarps() {
        return warps;
    }
}
