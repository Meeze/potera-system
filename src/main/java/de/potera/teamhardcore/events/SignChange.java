package de.potera.teamhardcore.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChange implements Listener {
    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("potera.sign.color")) {
            for (int i = 0; i < event.getLines().length; i++) {
                String line = event.getLine(i);
                line = ChatColor.translateAlternateColorCodes('&', line);
                event.setLine(i, line);
            }
        }
    }

}
