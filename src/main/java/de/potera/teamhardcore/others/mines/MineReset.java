package de.potera.teamhardcore.others.mines;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.Map;

public class MineReset {

    private final Mine mine;

    public MineReset(Mine mine, boolean timed) {
        this.mine = mine;
        processInit(timed);
    }

    public void processInit(boolean timed) {
        if (timed) {
            for (Player all : this.mine.getSpawn().getWorld().getPlayers()) {
                if (!this.mine.isInside(all.getLocation())) continue;
                all.sendMessage(StringDefaults.MINES_PREFIX + "§eDie Mine wird in §710 Sekunden §ezurückgesetzt!");
            }
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::handleReset, 200L);
            return;
        }
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), this::handleReset, 1L);
    }

    public void handleReset() {
        for (Player all : this.mine.getSpawn().getWorld().getPlayers()) {
            if (!this.mine.isInside(all.getLocation())) continue;
            all.sendMessage(StringDefaults.MINES_PREFIX + "§eDie wird jetzt zurückgesetzt!");
            all.teleport(this.mine.getSpawn());
        }
        this.mine.setAccessable(false);
        EditSession session = new EditSessionBuilder(
                FaweAPI.getWorld(this.mine.getMinPos().getWorld().getName())).fastmode(true).build();

        session.addNotifyTask(this::processDone);

        RandomPattern randomPattern = new RandomPattern();
        for (Map.Entry<MaterialData, Double> entry : this.mine.getMaterials().entrySet()) {
            randomPattern.add(new BaseBlock(entry.getKey().getItemTypeId(),
                    entry.getKey().getData()), entry.getValue());
            session.setBlocks(new CuboidRegion(
                    new Vector(this.mine.getMinPos().getBlockX(), this.mine.getMinPos().getBlockY(), this.mine
                            .getMinPos().getBlockZ()),
                    new Vector(this.mine.getMaxPos().getBlockX(), this.mine.getMaxPos().getBlockY(), this.mine
                            .getMaxPos().getBlockZ())), randomPattern);
            session.flushQueue();
        }
    }

    public void processDone() {
        Main.getInstance().getMinesManager().getMineResets().remove(this.mine);
        this.mine.setAccessable(true);
        this.mine.setBrokenBlocks(0);
    }

}
