package de.potera.klysma.kits.commands;

import de.potera.klysma.kits.Kit;
import de.potera.klysma.kits.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String lable,
							 String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("Dieser Befehl ist nur für Spieler.");
			return true;
		}

		if(args.length == 0){

			Player p = (Player) sender;
			sender.sendMessage("§6/kit <Name>");
			Bukkit.dispatchCommand(p, "kits");
			return true;
		}

		String kitname = args[0];
		Player ta = (Player) sender;
		boolean forceallow = false;

		if(args.length == 2 && sender.hasPermission("kits.force")){
			ta = Bukkit.getPlayer(args[1]);
			forceallow = true;
		}

		if(ta == null){
			sender.sendMessage("§cSpieler nicht gefunden!");
			return true;
		}

		if(ta.getName().equalsIgnoreCase(sender.getName())){
			if(ta.isOp()) forceallow = true;
		}

		Kit kit = Kit.getKit(kitname);

		if(kit == null){
			sender.sendMessage("§cDas Kit §4" + kitname + " §cwurde nicht gefunden!");
			return true;
		}

		boolean success = kit.giveKit(ta, forceallow);

		if(!success){
			if(!kit.hasPermission(ta)){
				ta.sendMessage("§cDu hast nicht die benötigten Rechte für dieses Kit");
			}else if(kit.hasCooldown(ta)){
				ta.sendMessage("§cDu kannst dieses Kit erst wieder in §a" + TimeUtils.getTimeWithN(kit.getCooldown(ta)) + "§c benutzen.");
				return true;
			}
		}

		return true;
	}

}
