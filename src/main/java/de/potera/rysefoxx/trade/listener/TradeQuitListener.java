package de.potera.rysefoxx.trade.listener;

import de.potera.teamhardcore.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TradeQuitListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (Main.getInstance().getTradeManager().getTradePartner().containsKey(player)) {
            Main.getInstance().getTradeManager().stopTrade(player);
        }


    }

}
