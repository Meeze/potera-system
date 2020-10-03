package de.potera.klysma.kits;

import de.potera.teamhardcore.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class KitListener implements Listener {

	public static HashMap<Player, Kit> openKits = new HashMap<>();

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInvClose(InventoryCloseEvent e){
		if(!(e.getPlayer() instanceof Player)) return;
		Player p = (Player) e.getPlayer();
		if(openKits.containsKey(p)){
			Kit kit = openKits.get(p);
			kit.saveKit();
			p.sendMessage("§aÄnderungen am Kit §c" + kit.getName() + "§a wurden gespeichert.");
			openKits.remove(p);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInvClick(InventoryClickEvent e){
		if(!(e.getWhoClicked() instanceof Player)) return;
		Player p = (Player) e.getWhoClicked();

		if(e.getInventory().getTitle().equals("§2Kitauswahl:")){
			ItemStack item = e.getCurrentItem();
			e.setCancelled(true);
			if(item.getItemMeta().getDisplayName() == null) return;
			if(!item.getItemMeta().getDisplayName().startsWith("§a")) return;
			Kit kit = Kit.getKit(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
			if(kit == null) return;
			if(kit.canUseKit(p)){
				p.closeInventory();
				kit.giveKit(p, p.isOp());
			}
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		if(e.getPlayer().hasPlayedBefore()) return;

		Kit starter = null;
		for(Kit k : Kit.getKits()){
			if(k.isStarterKit()) starter = k;
		}

		if(starter == null) return;
		starter.giveKit(e.getPlayer(), true);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent e){
		if(!e.getPlayer().isOp()) return;
		if(e.getLine(0).equalsIgnoreCase("<kit>")){
			e.setLine(0, "§8<§6Kit§8>");
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if(e.getClickedBlock().getState() instanceof Sign){
			Sign sign = (Sign) e.getClickedBlock().getState();
			if(sign.getLine(0).equals("§8<§6Kit§8>")){
				String kitname = ChatColor.stripColor(sign.getLine(1));
				final Kit kit = Kit.getKit(kitname);
				if(kit == null){
					e.getPlayer().sendMessage("§4Kit nicht gefunden!");
					return;
				}
				final Player p = e.getPlayer();
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () ->
						Bukkit.dispatchCommand(p, "kit " + kit.getName()));
			}
		}
	}

}
