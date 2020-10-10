package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGlobalmute implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player p = (Player) cs;

        if (!p.hasPermission("potera.globalmute")) {
            p.sendMessage(StringDefaults.PREFIX + "§cFür diese Aktion besitzt du keine Rechte.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " <1-2>");
            p.sendMessage(StringDefaults.PREFIX + "§cStufe 1: §7Nur Chat §cStufe 2: §7Chat + MSG");
            return true;
        }

        int current = Main.getInstance().getGeneralManager().getGlobalmuteTier();
        int tier;

        try {
            tier = Integer.parseInt(args[0]);
            if (tier < 1 || tier > 2)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            p.sendMessage(StringDefaults.PREFIX + "§cBitte gebe eine gültige Stufe an.");
            return true;
        }

        if (current != 0 && current != tier) {
            p.sendMessage(StringDefaults.PREFIX + "§cEs läuft bereits Stufe " + current + ".");
            return true;
        }

        if (current == tier) {
            Main.getInstance().getGeneralManager().setGlobalmuteTier(0);
            for (Player all : Bukkit.getOnlinePlayers())
                all.sendMessage(StringDefaults.PREFIX + "§6Der globale Chat wurde aktiviert.");
            return true;
        }

        Main.getInstance().getGeneralManager().setGlobalmuteTier(tier);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(StringDefaults.PREFIX + "§6Der globale Chat wurde deaktiviert.");
        }

        return true;
    }
}
