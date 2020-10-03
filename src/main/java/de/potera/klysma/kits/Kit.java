package de.potera.klysma.kits;

import de.potera.klysma.CooldownAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Kit implements Comparable<Kit> {

	private static List<Kit> KITS = new ArrayList<>();

	public static List<Kit> getKits(){
		return KITS;
	}

	public static List<Kit> getKitsFor(Player player){
		return Kit.getKits().stream()
				.filter(kit -> kit.hasPermission(player))
				.sorted()
				.collect(Collectors.toList());
	}

	public static void removeKit(Kit kit) {
		kit.removeAllCooldowns();
		KITS.remove(kit);

	}

	public void removeAllCooldowns(){
		String name = getName();
		try{
			for(OfflinePlayer pp : Bukkit.getOfflinePlayers()){
				if(CooldownAPI.hasCooldown(pp, "kit_" + name))
					CooldownAPI.removeCooldown(pp, "kit_" + name);
			}
		}catch(Exception ignored){}
	}

	public static Kit getKit(String name) {
		return KITS.stream()
				.filter(kit -> kit.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}

	public static Kit createKit(String name, ItemStack displayitem, String permission, long cooldown){
		Kit kit = new Kit(name, displayitem, permission, cooldown);
		KITS.add(kit);
		return kit;
	}

	private final String name;
	private final Inventory inv;
	private long cooldown;
	private String permission;
	private ItemStack displayitem;
	private int kit_statistics_used = 0;
	private boolean isstarterkit = false;

	private Kit(String name, ItemStack displayitem, String permission, long cooldown) {
		this.name = name;
		this.displayitem = displayitem;
		this.permission = permission;
		this.cooldown = cooldown;
		inv = Bukkit.createInventory(null, 4 * 9, "§6Kit:§e " + getName());
	}

	public boolean loadKit() {
		try {
			inv.clear();

			//Load from Database

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.broadcastMessage("§cBeim laden vom Kit §4" + getName() + "§c gab es einen Fehler! Bitte in der Konsole nachsehen!");
		}
		return false;
	}

	public boolean saveKit() {
		try {

			//Save to Database

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.broadcastMessage("§cBeim speichern vom Kit §4" + getName() + "§c gab es einen Fehler! Bitte in der Konsole nachsehen!");
		}
		return false;
	}

	public String getName() {
		return this.name;
	}

	public void editItems(Player p) {
		p.openInventory(inv);
		KitListener.openKits.put(p, this);
	}

	public void previewItems(Player p) {
		Preview.previewItems(p, "§6KitView: §e" + getName(), inv.getContents());
	}

	public void previewItems(Player p, String title) {
		Preview.previewItems(p, "§l§f" + title, inv.getContents());
	}

	public ItemStack[] getItems() {
		ArrayList<ItemStack> items = new ArrayList<>();
		for (int i = 0; i < inv.getSize(); i++) {
			if (inv.getItem(i) != null) {
				if (inv.getItem(i).getType() != Material.AIR)
					items.add(inv.getItem(i).clone());
			}
		}
		return items.toArray(new ItemStack[] {});
	}

	public void updateItems(ItemStack[] items){
		inv.setContents(items);
	}

	public boolean canUseKit(Player p) {
		if (!hasPermission(p))
			return false;
		return !hasCooldown(p);
	}

	public String getTime(long seks){
		long time = seks;

		int days = 0;
		int hours = 0;
		int minutes = 0;
		long seconds = 0;

		while(time >= (60*60)*24){
			days++;
			time = time - ((60*60)*24);
		}

		while(time >= 60*60){
			hours++;
			time = time - (60*60);
		}

		while(time >= 60){
			minutes++;
			time = time - (60);
		}

		seconds = time;
		String ret = "";
		if(days > 0){
			if(days == 1){
				ret = ret + ", ein Tag";
			}else{
				ret = ret + ", " + days + " Tage";
			}
		}
		if(hours > 0){
			if(hours == 1){
				ret = ret + ", eine Stunde";
			}else{
				ret = ret + ", " + hours + " Stunden";
			}
		}
		if(minutes > 0){
			if(minutes == 1){
				ret = ret + ", eine Minute";
			}else{
				ret = ret + ", " + minutes + " Minuten";
			}
		}

		if(!ret.equalsIgnoreCase("")){
			if(seconds == 1){
				ret = ret + " und eine Sekunde";
			}else{
				ret = ret + " und " + seconds + " Sekunden";
			}
		}else{
			if(seconds == 1){
				ret = ret + "eine Sekunde";
			}else{
				ret = ret + "" + seconds + " Sekunden";
			}
		}
		ret = ret.replaceFirst(", ", "");
		return ret;
	}

	public boolean hasCooldown(Player p) {
		return CooldownAPI.hasCooldown(p, "kit_" + getName());
	}

	public int getStatisticKitUsed() {
		return kit_statistics_used;
	}

	public int getStatisticKitUsedInPercent() {
		if (getStatisticKitUsed() == 0) return 0;
		long max = 0;
		for (Kit k : KITS)
			max += k.getStatisticKitUsed();
		return (int) ((getStatisticKitUsed() * 100) / max);
	}

	public long getCooldown(Player p) {
		return CooldownAPI.getCooldown(p, "kit_" + getName());
	}

	public void resetStatisticKitUsed() {
		kit_statistics_used = 0;
	}

	public String getPermission() {
		return permission;
	}

	public boolean hasPermission(Player p) {
		return p.hasPermission(this.getPermission());
	}

	public void setPermission(String permission) {
		this.permission = permission;
		saveKit();
	}

	public long getKitCooldown() {
		return cooldown;
	}

	public void setKitCooldown(long cooldownseks) {
		this.cooldown = cooldownseks;
	}

	public boolean giveKit(Player p, boolean forceallow){
		return giveKit(p, forceallow, false);
	}

	public boolean giveKit(Player p, boolean forceallow, boolean silent) {
		if (!canUseKit(p) && !forceallow)
			return false;

		int itemsdropped = 0;
		ItemStack[] items = getItems();
		boolean suited = false;

		boolean isempty = false;

		{
			int usedslots = 0;
			for (int i = 0; i < p.getInventory().getSize(); i++) {
				ItemStack item = p.getInventory().getItem(i);
				if (item != null) {
					if (item.getType() != Material.AIR)
						usedslots++;
				}
			}
			for (int i = 0; i < p.getInventory().getArmorContents().length; i++) {
				ItemStack item = p.getInventory().getArmorContents()[i];
				if (item != null) {
					if (item.getType() != Material.AIR)
						usedslots++;
				}
			}
			if (usedslots == 0)
				isempty = true;
		}

		if (isempty) {
			ItemStack[] ii = inv.getContents();
			for (int i = 0; i < ii.length; i++) {
				if (ii[i] != null)
					ii[i] = ii[i].clone();
			}
			items = ii;
		}

		int index = -1;
		for (ItemStack item : items) {
			index++;

			boolean isair = false;
			if (item == null)
				isair = true;
			else if (item.getType() == Material.AIR)
				isair = true;

			if (!isair) {
				try {
					boolean isarmor = false;
					if ((item.getType().toString().endsWith("_HELMET") || item.getType() == Material.SKULL_ITEM)
							&& p.getInventory().getHelmet() == null && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						p.getInventory().setHelmet(item);
						isarmor = true;
						suited = true;
					}

					if (item.getType().toString().endsWith("_CHESTPLATE")
							&& p.getInventory().getChestplate() == null && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						p.getInventory().setChestplate(item);
						isarmor = true;
						suited = true;
					}

					if (item.getType().toString().endsWith("_LEGGINGS")
							&& p.getInventory().getLeggings() == null && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						p.getInventory().setLeggings(item);
						isarmor = true;
						suited = true;
					}

					if (item.getType().toString().endsWith("_BOOTS")
							&& p.getInventory().getBoots() == null && !p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
						p.getInventory().setBoots(item);
						isarmor = true;
						suited = true;
					}

					if (!isarmor) {

						int leftslots = 0;
						for (int i = 0; i < p.getInventory().getSize(); i++) {
							if (p.getInventory().getItem(i) == null) {
								leftslots++;
							} else if (p.getInventory().getItem(i).getType() == Material.AIR) {
								leftslots++;
							} else if (p.getInventory().getItem(i)
									.isSimilar(item)) {
								if ((item.getAmount() + p.getInventory()
										.getItem(i).getAmount()) < item
										.getMaxStackSize()) {
									leftslots++;
								}
							}
						}

						if (leftslots == 0) {
							p.getWorld().dropItem(
									p.getLocation().add(0, 0.2, 0), item);
							itemsdropped++;
						} else {
							if (isempty) {
								p.getInventory().setItem(index, item);
							} else {
								p.getInventory().addItem(item);
							}
						}

					}

				} catch (Exception e) {
				}
			}
		}

		if (!forceallow && this.cooldown > 0)
			CooldownAPI.setCooldown(p, "kit_" + getName(), cooldown);

		if(!silent){
			p.sendMessage("§aDu hast das Kit §c" + getName() + "§a erhalten.");

			if (itemsdropped > 0)
				p.sendMessage("§cDein Inventar ist voll! Es wurden §4" + itemsdropped + "§c gedroppt.");

			if (suited)
				p.sendMessage("§aDas Kit wurde dir automatisch angezogen.");
		}

		if (!forceallow)
			kit_statistics_used++;

		return true;
	}

	public boolean isStarterKit() {
		return isstarterkit;
	}

	public void setStarterKit(boolean isstarter) {
		if (isstarter) {
			for (Kit kit : KITS) {
				if (kit.isStarterKit() && this != kit) {
					kit.setStarterKit(false);
					kit.saveKit();
				}
			}
		}
		isstarterkit = isstarter;
	}

	public ItemStack getDisplayItem() {
		return displayitem.clone();
	}

	public Inventory getInventory(){
		return this.inv;
	}

	public void setDisplayItem(ItemStack item) {
		this.displayitem = item.clone();
	}

	@Override
	public int compareTo(Kit o) {
		return getName().compareTo(o.getName());
	}

}
