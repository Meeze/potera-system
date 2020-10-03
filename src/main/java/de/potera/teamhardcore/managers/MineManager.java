package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.mines.Mine;
import de.potera.teamhardcore.others.mines.MineEvents;
import de.potera.teamhardcore.others.mines.MineReset;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class MineManager {

    public static final List<Material> MINE_BLOCKS = new ArrayList<>(Arrays.asList(
            Material.STONE, Material.COBBLESTONE, Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE,
            Material.REDSTONE_ORE,
            Material.GLOWING_REDSTONE_ORE, Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE,
            Material.QUARTZ_ORE,
            Material.ENDER_STONE, Material.REDSTONE_BLOCK, Material.GOLD_BLOCK, Material.IRON_BLOCK,
            Material.LAPIS_BLOCK,
            Material.COAL_BLOCK, Material.OBSIDIAN, Material.EMERALD_BLOCK, Material.DIAMOND_BLOCK));


    public static final Long[] TIME_DROPS = new Long[]{1800000L, 900000L, 2700000L, 3600000L, 5400000L, 7200000L, 10800000L, 18000000L};

    private final Set<Mine> mines;
    private final Map<Mine, MineReset> mineResets;

    public MineManager() {
        this.mines = new HashSet<>();
        this.mineResets = new HashMap<>();

        loadMines();
        startMineResetTask();

        Bukkit.getPluginManager().registerEvents(new MineEvents(), Main.getInstance());
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            for (Mine mine : this.mines) {
                if (mine.getMaxPos() == null || mine.getMinPos() == null || mine.getSpawn() == null)
                    continue;
                scheduledMineReset(mine, false);
            }
        }, 1L);

    }

    private void loadMines() {
        File folder = new File(Main.getInstance().getDataFolder().getAbsolutePath() + File.separator + "mines");

        if (!folder.exists())
            return;

        try {
            for (File file : Objects.requireNonNull(folder.listFiles())) {
                if (file.isFile() && file.exists()) {

                    String fileName = file.getAbsoluteFile().getName().replace(".yml", "");
                    Mine mine = new Mine(fileName);
                    this.mines.add(mine);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void startMineResetTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Mine mine : MineManager.this.mines) {
                    mine.checkMineState();
                }
            }
        }.runTaskTimer(Main.getInstance(), 1200L, 1200L);
    }

    public void scheduledMineReset(Mine mine, boolean timed) {
        if (this.mineResets.containsKey(mine)) return;
        MineReset reset = new MineReset(mine, timed);
        this.mineResets.put(mine, reset);
    }

    public void createMine(String name, Location location) {
        if (getMine(name) != null) return;
        Mine mine = new Mine(name, location);
        this.mines.add(mine);
    }

    public void removeMine(String name) {
        if (getMine(name) == null) return;
        Mine mine = getMine(name);
        mine.deleteData();
        this.mines.remove(mine);
    }

    public Mine getMine(String name) {
        Optional<Mine> mines = getMines().stream().filter(mine -> mine.getName().equalsIgnoreCase(name)).findFirst();
        return mines.orElse(null);
    }

    public Mine getMine(long level) {
        Optional<Mine> optional = getMines().stream().filter(mine -> mine.getLevel() <= level)
                .reduce((mine, mine2) -> mine.getLevel() > mine2.getLevel() ? mine : mine2);
        return optional.orElse(null);
    }

    public int getMinePointsToNextLevel(int baseLevel) {
        return (int) (2500.0D * Math.pow(1.15D, (Math.min(baseLevel, 100) - 1)));
    }

    public double getCrateDropChance(int baseLevel) {
        return 0.025D * baseLevel;
    }

    public int getAccessableMine(int mineLevel) {
        return mineLevel / 10 + ((mineLevel == 100) ? 0 : 1);
    }

    public Set<Mine> getMines() {
        return mines;
    }

    public Map<Mine, MineReset> getMineResets() {
        return mineResets;
    }
}
