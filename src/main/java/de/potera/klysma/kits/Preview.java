package de.potera.klysma.kits;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Preview implements Listener {

    private static List<Player> viewer = new ArrayList<>();

    public static void previewItems(Player p, String title, ItemStack[] contents) {
        preview(title, p, contents);
    }

    private static void preview(String title, Player p, ItemStack[] contents){
        if(title.length() > 32)
            title = title.substring(0, 32);
        Inventory previewInv = Bukkit.createInventory(null, contents.length, title);

        for(int i = 0; i < previewInv.getSize(); i++)
            previewInv.setItem(i, contents[i]);

        p.openInventory(previewInv);
        viewer.add(p);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInvClose(InventoryCloseEvent e){
        if(!(e.getPlayer() instanceof Player)) return;
        Player p = (Player) e.getPlayer();
        viewer.remove(p);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInvClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        if(viewer.contains(p))e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrag(InventoryDragEvent e){
        if(!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();

        if(viewer.contains(p))e.setCancelled(true);
    }
}
