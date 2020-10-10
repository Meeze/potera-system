package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropItem implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(StringDefaults.PREFIX + "§7Du ist eingefroren. Melde dich bei einem Teammitglied.");
            return;
        }

    }

}
