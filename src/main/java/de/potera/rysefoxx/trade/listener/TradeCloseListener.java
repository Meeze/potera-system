package de.potera.rysefoxx.trade.listener;

import de.potera.teamhardcore.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class TradeCloseListener implements Listener {

    @EventHandler
    public void on(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) return;

        Player player = (Player) e.getPlayer();

        if (Main.getInstance().getTradeManager().getTradePartner().containsKey(player)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Main.getInstance().getTradeManager().stopTrade(player);
                }
            }.runTaskLater(Main.getInstance(), 1L);
        }


    }

}
