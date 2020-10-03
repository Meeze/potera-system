package de.potera.klysma.kits.commands;

import de.potera.klysma.kits.Kit;
import de.potera.teamhardcore.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitPreviewCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
							 String[] args) {
		
		if(args.length == 0){
			sender.sendMessage("§6/kitpreview <Name>");
			Bukkit.dispatchCommand(sender, "kits");
			return true;
		}
		
		Player ta = null;
		
		if((sender instanceof Player) && args.length < 2)
			ta = (Player) sender;
		
		if(args.length > 1 && sender.isOp())
			ta = Bukkit.getPlayer(args[1]);
		
		if(ta == null){
			sender.sendMessage("Kein Zielspieler angegeben!");
			return true;
		}
		
		String kitname = args[0];
		
		Kit kit = Kit.getKit(kitname);
		
		if(kit == null){
			sender.sendMessage("§cDas Kit §4" + kitname + " §cwurde nicht gefunden!");
			return true;
		}
		
		kit.previewItems(ta);
		return true;
	}

}
