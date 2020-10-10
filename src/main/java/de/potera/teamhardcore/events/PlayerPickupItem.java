package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class PlayerPickupItem implements Listener {

    @EventHandler
    public void onPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        Item item = event.getItem();

        if (Main.getInstance().getGeneralManager().getPlayersInVanish().contains(player)) {
            event.setCancelled(true);
        }
        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(player.getUniqueId()))
            event.setCancelled(true);

    }

}
