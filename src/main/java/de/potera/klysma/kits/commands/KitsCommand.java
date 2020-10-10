package de.potera.klysma.kits.commands;

import de.potera.klysma.kits.Kit;
import de.potera.klysma.kits.TimeUtils;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class KitsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
						  String[] args) {

		if(!(sender instanceof Player)) return true;
		Player p = (Player) sender;

		JSONMessage message = new JSONMessage("§aKits: ");
		List<Kit> kits = Kit.getKitsFor(p);
		if(kits.size() == 0){
			message.then("§c Keine Kits gefunden");
			message.send(p);
			return true;
		}
		addKit(kits.remove(0), p.getPlayer(), message, false);
		kits.forEach(kit -> addKit(kit, p.getPlayer(), message, true));

		message.send(p);
		return true;
	}

	private void addKit(Kit kit, Player player, JSONMessage message, boolean isFirst){
		if(!isFirst) message.then("§c, ");

		JSONMessage.MessagePart part = new JSONMessage.MessagePart(kit.getName());
		part.setColor(ChatColor.RED);
		if(kit.hasCooldown(player)) part.addStyle(ChatColor.STRIKETHROUGH);
		if(kit.hasCooldown(player)){
			part.setOnHover(JSONMessage.HoverEvent
					.showText("§aCooldown: §c" + TimeUtils.getTime(kit.getCooldown(player))));
		}else{
			part.setOnHover(JSONMessage.HoverEvent
					.showText("§aVerfügbar\n§8Klicke um es zu nehmen."));
			part.setOnClick(JSONMessage.ClickEvent.runCommand("/kit " + kit.getName()));
		}
		message.then(part);
	}

}
