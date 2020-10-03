package de.potera.teamhardcore.events;

import com.mewin.WGRegionEvents.events.RegionEnterEvent;
import com.mewin.WGRegionEvents.events.RegionLeaveEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.managers.ScoreboardManager;
import de.potera.teamhardcore.others.mines.Mine;
import de.potera.teamhardcore.users.UserMine;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class RegionEnterLeave implements Listener {

    @EventHandler
    public void onLeave(RegionLeaveEvent event) {
        Player player = event.getPlayer();
        ProtectedRegion region = event.getRegion();

        Optional<Mine> enteredMine = Main.getInstance().getMinesManager().getMines().stream().filter(
                mine -> (mine.getRegion() != null && mine.getRegion().equals(region.getId()))).findFirst();

        if (!enteredMine.isPresent()) {
            return;
        }

        Main.getInstance().getScoreboardManager().activateScoreboard(player, ScoreboardManager.ScoreboardType.MAIN);
    }

    @EventHandler
    public void onEnter(RegionEnterEvent event) {
        Player player = event.getPlayer();
        ProtectedRegion region = event.getRegion();

        Optional<Mine> enteredMine = Main.getInstance().getMinesManager().getMines().stream().filter(
                mine -> (mine.getRegion() != null && mine.getRegion().equals(region.getId()))).findFirst();

        if (!enteredMine.isPresent()) {
            return;
        }
        UserMine userMine = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserMine();

        if (enteredMine.get().getLevel() > userMine.getLevel() && !player.hasPermission("potera.mines.admin")) {
            event.setCancelled(true);
            player.sendMessage(
                    StringDefaults.MINES_PREFIX + "§cDie Mine ist erst ab §7Level " + enteredMine.get().getLevel() + " §cverfügbar.");
            return;
        }

        Main.getInstance().getScoreboardManager().activateScoreboard(player, ScoreboardManager.ScoreboardType.MINES);
    }

}
