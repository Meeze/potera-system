package de.potera.klysma;

import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class CooldownAPI {

	private static Properties cooldowns = new Properties();

	public static void loadCooldowns(File file){
		try{
			cooldowns.load(new FileInputStream(file));
		}catch(Exception e){}

	}

	public static void saveCooldowns(File file){
		try{
			removeUselessCooldowns();
			cooldowns.store(new FileOutputStream(file), "CooldownAPI von EnderKill98 fuer SoulPvP.de");
		}catch(Exception e){}
	}

	public static void removeUselessCooldowns(){
		String[] keys = new String[]{};
		keys = cooldowns.keySet().toArray(keys);
		long ctime = System.currentTimeMillis();

		for(String key : keys){
			long cooldown = Long.parseLong(cooldowns.getProperty(key));
			if(cooldown < ctime){
				cooldowns.remove(key);
			}
		}

	}

	public static void setCooldown(OfflinePlayer player, String type, long Sek){
		String cooldownname = player != null ? player.getUniqueId().toString() + "!" + type.toLowerCase() : "" + type.toLowerCase();
		cooldowns.setProperty(cooldownname, "" + (System.currentTimeMillis() + (1000*Sek)));
	}

	public static boolean hasCooldown(OfflinePlayer player, String type){
		String cooldownname = player != null ? player.getUniqueId().toString() + "!" + type.toLowerCase() : "" + type.toLowerCase();
		if(cooldowns.containsKey(cooldownname)){
			long ctime = System.currentTimeMillis();
			long cooldown = Long.parseLong(cooldowns.getProperty(cooldownname));
			if(ctime < cooldown){
				return true;
			}else{
				cooldowns.remove(cooldownname);
			}
		}
		return false;
	}

	public static boolean removeCooldown(OfflinePlayer player, String type){
		String cooldownname = player != null ? player.getUniqueId().toString() + "!" + type.toLowerCase() : "" + type.toLowerCase();
		if(cooldowns.containsKey(cooldownname)){
			cooldowns.remove(cooldownname);
			return true;
		}
		return false;
	}

	public static long getCooldown(OfflinePlayer player, String type){
		String cooldownname = player != null ? player.getUniqueId().toString() + "!" + type.toLowerCase() : "" + type.toLowerCase();
		if(cooldowns.containsKey(cooldownname)){
			long ctime = System.currentTimeMillis();
			long cooldown = Long.parseLong(cooldowns.getProperty(cooldownname));
			if(ctime < cooldown){
				long seks = (cooldown - ctime);
				seks = seks / 1000;
				return seks;
			}else{
				cooldowns.remove(cooldownname);
				return -1;
			}
		}
		return -1;
	}

}
