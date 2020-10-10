package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockBreakPlace implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!event.isCancelled() && player.hasPermission(
                "potera.build.protect") && !Main.getInstance().getGeneralManager().getPlayersInBuildmode().contains(
                player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (!event.isCancelled() && player.hasPermission(
                "potera.build.protect") && !Main.getInstance().getGeneralManager().getPlayersInBuildmode().contains(
                player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

}
