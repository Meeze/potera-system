package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.crates.CrateOpening;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class InventoryClose implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;

        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        Main.getInstance().getGeneralManager().getPlayersInInvsee().remove(player);

        if (Main.getInstance().getCrateManager().getPlayersInOpening().containsKey(player)) {
            if (player.getOpenInventory().getTopInventory().getName().equals(
                    StringDefaults.INVENTORY_PREFIX + "Crate-Opening")) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        CrateOpening opening = Main.getInstance().getCrateManager().getPlayersInOpening().get(player);
                        player.openInventory(opening.getInventory());
                    }
                }.runTaskLater(Main.getInstance(), 1L);
            }
        }

        if (Main.getInstance().getAmsManager().getAmsGuiCache().containsKey(player)) {
            if (!Main.getInstance().getAmsManager().getAmsStateCache().containsKey(player)) {
                Main.getInstance().getAmsManager().getAmsGuiCache().remove(player);
                return;
            }
            int state = Main.getInstance().getAmsManager().getAmsStateCache().get(player)[0];
            if (inventory.getType() == InventoryType.ANVIL)
                return;
            if (inventory.getTitle().equals(StringDefaults.INVENTORY_PREFIX + "AMS") && state != 0)
                return;

            if (inventory.getTitle().equals(StringDefaults.INVENTORY_PREFIX + "AMS Freunde") && state != 3)
                return;

            if (inventory.getTitle().equals(StringDefaults.INVENTORY_PREFIX + "AMS Upgrades") && state != 6)
                return;

            Main.getInstance().getAmsManager().getAmsGuiCache().remove(player);
            Main.getInstance().getAmsManager().getAmsStateCache().remove(player);
        }

    }

}
