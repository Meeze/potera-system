package de.potera.klysma.kits.commands;

import de.potera.klysma.kits.Kit;
import de.potera.klysma.kits.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class KitInvCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
							 String[] args) {

		if(!(sender instanceof Player)){
			sender.sendMessage("Dieser Befehl ist nur für Spieler.");
			return true;
		}
		System.out.println("Executed");

		Player p = (Player) sender;

		List<Kit> kits = Kit.getKitsFor(p);

		int size = 9;
		if(kits.size() > 9) size = 9*2;
		if(kits.size() > 9*2) size = 9*3;
		if(kits.size() > 9*3) size = 9*4;
		if(kits.size() > 9*4) size = 9*5;
		if(kits.size() > 9*5) size = 9*6;

		if(kits.size() > 9*6){
			kits = kits.subList(0, 9*6);
			p.sendMessage("§6Hinweis: Es gibt mehr Kits als Platz in diesem Inventar. Alle Kits siehst du mit §e/kit§6.");
		}

		Inventory kitinv = Bukkit.createInventory(null, size, "§2Kitauswahl:");

		List<Kit> finalKits = kits;
		IntStream.range(0, kits.size()).forEach(i -> forKit(kitinv, i, finalKits.get(i), p));

		p.openInventory(kitinv);
		return true;
	}

	private void forKit(Inventory kitInv, int current, Kit kit, Player player){
		ItemStack item = kit.getDisplayItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§a" + kit.getName());
		if(kit.hasCooldown(player)){
			meta.setLore(Arrays.asList("§aVerfügar in §c" + TimeUtils.getTimeWithN(kit.getCooldown(player)), "§6Beliebtheit: §e" + kit.getStatisticKitUsedInPercent() + "%"));
		}else{
			meta.setLore(Arrays.asList("§aVerfügbar", "§6Beliebtheit: §e" + kit.getStatisticKitUsedInPercent() + "%", "§8Anklicken um das Kit zu erhalten."));
		}
		item.setItemMeta(meta);
		kitInv.setItem(current, item);
	}

}
