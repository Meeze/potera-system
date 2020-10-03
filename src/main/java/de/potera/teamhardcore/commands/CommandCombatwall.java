package de.potera.teamhardcore.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.CombatWall;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCombatwall implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.combatwall")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length != 1) {
            sendHelp(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            Selection selection = Main.getInstance().getWorldEditPlugin().getSelection(player);

            if (selection == null) {
                player.sendMessage(StringDefaults.PVP_PREFIX + "§cBitte markiere vorher die Wand-Region.");
                return true;
            }

            CombatWall combatWall = new CombatWall(selection.getMinimumPoint(), selection.getMaximumPoint());
            Main.getInstance().getCombatManager().setCombatWall(combatWall);
            player.sendMessage(StringDefaults.PVP_PREFIX + "§aDu hast die Combatwall gesetzt.");
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (Main.getInstance().getCombatManager().getCombatWall() == null) {
                player.sendMessage(StringDefaults.PVP_PREFIX + "§cEx existiert keine Combatwall.");
                return true;
            }

            Main.getInstance().getCombatManager().setCombatWall(null);
            player.sendMessage(StringDefaults.PVP_PREFIX + "§cDie Combatwall wurde gelöscht.");

        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/combatwall <set|remove>");
    }
}
