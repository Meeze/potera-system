package de.potera.klysma.kits.commands;

import de.potera.klysma.kits.Kit;
import de.potera.klysma.kits.TimeUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitManagerCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
							 String[] args) {

		if(!(sender instanceof Player)){
			sender.sendMessage("Dieser Befehl ist nur für Spieler.");
			return true;
		}

		Player p = (Player) sender;

		if(args.length == 0){
			printCommandHelp(p);
			return true;
		}

		if(args[0].equalsIgnoreCase("add")){
			if(args.length != 4){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}
			String name = args[1];
			String permission = args[2];
			long cooldown = TimeUtils.parseCooldown(args[3]);
			ItemStack displayitem = p.getItemInHand();

			if(Kit.getKit(name) != null){
				p.sendMessage("§cFehler: Das Kit existiert bereits.");
				return true;
			}

			if(cooldown == -1){
				p.sendMessage("§cFehler: Der Cooldown ist ungültig.");
				return true;
			}

			if(displayitem == null){
				p.sendMessage("§cFehler: Das displayItem in deiner Hand darf nicht Luft sein.");
				return true;
			}

			if(displayitem.getType() == Material.AIR){
				p.sendMessage("§cFehler: Das displayItem in deiner Hand darf nicht Luft sein.");
				return true;
			}

			displayitem = displayitem.clone();
			displayitem.setAmount(1);

			Kit kit = Kit.createKit(name, displayitem, permission, cooldown);

			p.sendMessage("§aDas Kit §c" + kit.getName() + " §awurde erstellt.");
			p.sendMessage("§aCooldown: §c" + TimeUtils.getTime(kit.getKitCooldown()));
			p.sendMessage("§aDisplayItem: §c" + kit.getDisplayItem().getType().toString().toLowerCase().replaceAll("_", ""));
			p.sendMessage("§aPermission: §c" + kit.getPermission());

			return true;
		}

		if(args[0].equalsIgnoreCase("edit")){
			if(args.length != 2){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}
			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			p.sendMessage("§aAlle Items für das Kit §c" + kit.getName() + "§a bitte hierrein tun.");
			kit.editItems(p);
			return true;
		}

		if(args[0].equalsIgnoreCase("del")){
			if(args.length != 2){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			String name = kit.getName();
			Kit.removeKit(kit);
			p.sendMessage("§aDas Kit §c" + name + "§a wurde gelöscht!");
			return true;
		}

		if(args[0].equalsIgnoreCase("resetcooldowns")){
			if(args.length != 2){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			String name = kit.getName();
			kit.removeAllCooldowns();
			p.sendMessage("§aKeiner hat mehr einen Cooldown auf dem Kit §c" + name + "§a!");
			return true;
		}

		if(args[0].equalsIgnoreCase("setPermissions")){
			if(args.length != 3){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			String rang = args[2];

			kit.setPermission(rang);
			kit.saveKit();

			p.sendMessage("§aDie Permission für dieses Kit ist jetzt §c" + rang + "§a.");
			return true;
		}

		if(args[0].equalsIgnoreCase("resetstarter")){
			if(args.length != 1){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			if(!p.hasPermission("kit.admin")){
				p.sendMessage("§cFür diesen Subbefehl musst du Admin sein!");
				return true;
			}

			for(Kit kit : Kit.getKits()){
				if(kit.isStarterKit()){
					kit.setStarterKit(false);
					kit.saveKit();
				}
			}
			p.sendMessage("§aEs gibt jetzt kein Starterkit mehr.");
			return true;
		}


		if(args[0].equalsIgnoreCase("resetStats")){
			if(args.length != 2){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			if(!p.hasPermission("kit.admin")){
				p.sendMessage("§cFür diesen Subbefehl musst du Admin sein!");
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			kit.resetStatisticKitUsed();
			kit.saveKit();
			p.sendMessage("§aDie Statistiken vom Kit §c" + kit.getName() + "§awurden erfolgreich resettet.");
			return true;
		}

		if(args[0].equalsIgnoreCase("info")){
			if(args.length != 2){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			p.sendMessage("§6----- KitInfo -----");

			p.sendMessage("§2> §aName: §c" + kit.getName());
			p.sendMessage("§2> §aCooldown: §c" + TimeUtils.getTime(kit.getKitCooldown()));
			p.sendMessage("§2> §aPermission: §c" + kit.getPermission());
			p.sendMessage("§2> §aDisplayItem: §c" + kit.getDisplayItem().getType().toString().toLowerCase().replaceAll("_", ""));
			p.sendMessage("§2> §aItems: §c" + kit.getItems().length);
			p.sendMessage("§2> §aStarterkit: §c" + (kit.isStarterKit() ? "Ja" : "Nein"));
			p.sendMessage("§2> §aBenutzt: §c" + kit.getStatisticKitUsed() + " mal (" + kit.getStatisticKitUsedInPercent() + "%)");

			p.sendMessage("§6-------------------");

			return true;
		}

		if(args[0].equalsIgnoreCase("setcooldown")){
			if(args.length != 3){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			long cooldown = TimeUtils.parseCooldown(args[2]);

			if(cooldown == -1){
				p.sendMessage("§cDer Cooldown ist ungültig. Syntax ZAHL + s(Sekunden)/m(Minuten)/h(Stunden)/d(Tage)");
				return true;
			}

			kit.setKitCooldown(cooldown);
			kit.saveKit();
			p.sendMessage("§aDer neues Cooldown vom Kit ist: §c" + TimeUtils.getTime(kit.getKitCooldown()));
			return true;
		}

		if(args[0].equalsIgnoreCase("setstarter")){
			if(args.length != 2){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			if(!p.hasPermission("kit.admin")){
				p.sendMessage("§cFür diesen Subbefehl musst du Admin sein!");
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			kit.setStarterKit(true);
			kit.saveKit();
			p.sendMessage("§aDieses Kit ist jetzt das §cStarterkit§a!");
			return true;
		}

		if(args[0].equalsIgnoreCase("setitem")){
			if(args.length != 2){
				p.sendMessage("§cUngültiger Syntax!");
				printCommandHelp(p);
				return true;
			}

			Kit kit = Kit.getKit(args[1]);

			if(kit == null){
				p.sendMessage("§cDas Kit §4" + args[1] + " §cwurde nicht gefunden.");
				return true;
			}

			ItemStack displItem = p.getItemInHand();


			if(displItem == null){
				p.sendMessage("§cFehler: Das displayItem in deiner Hand darf nicht Luft sein.");
				return true;
			}

			if(displItem.getType() == Material.AIR){
				p.sendMessage("§cFehler: Das displayItem in deiner Hand darf nicht Luft sein.");
				return true;
			}

			kit.setDisplayItem(displItem);
			p.sendMessage("§aDas DisplayItem vom Kit wurde in §c" + displItem.getType().toString().toLowerCase().replaceAll("_", "") + "§a geändert.");
			kit.saveKit();
			return true;
		}

		p.sendMessage("§6Unbekanntes Kommando.");
		printCommandHelp(p);
		return true;
	}

	public static void printCommandHelp(Player p){
		p.sendMessage("§6----- Befehle: -----");
		p.sendMessage("§2> §a/kitmgr add <Kitname> <Permissions> <Cooldown(XXXs/m/h/d)> //Item in Hand = DisplayItem");
		p.sendMessage("§2> §a/kitmgr edit <Kitname>");
		p.sendMessage("§2> §a/kitmgr del <Kitname>");
		p.sendMessage("§2> §a/kitmgr info <Kitname>");
		p.sendMessage("§2> §a/kitmgr resetCooldowns <Kitname>");
		p.sendMessage("§2> §a/kitmgr setCooldown <Kitname> <Cooldown(XXXs/m/h/d)>");
		p.sendMessage("§2> §a/kitmgr setPermissions <Permission>");
		p.sendMessage("§2> §a/kitmgr setItem //Item in Hand = DisplayItem");

		if(p.hasPermission("kit.admin")){
			p.sendMessage("§2> §a/kitmgr setStarter <Kitname>");
			p.sendMessage("§2> §a/kitmgr resetStarter");
			p.sendMessage("§2> §a/kitmgr resetStats <Kitname>");
		}

		p.sendMessage("§6--------------------");
	}

}
