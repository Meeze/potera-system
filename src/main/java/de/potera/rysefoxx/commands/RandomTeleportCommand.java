package de.potera.rysefoxx.commands;

import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.TPDelay;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RandomTeleportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (Bukkit.getWorld("world") == null) {
            player.sendMessage(StringDefaults.PREFIX + "§7Es konnte kein Teleport stattfinden.");
            return true;
        }

        //Todo: Adden das der Spieler nur 1x sich teleportieren können. (Über Permissions machen)

        player.sendMessage(StringDefaults.PREFIX + "§eDu wirst in §73 Sekunden §eteleportiert.");
        TPDelay tpDelay = new TPDelay(player, 0, 3) {
            @Override
            public boolean onTick() {
                return false;
            }

            @Override
            public void onEnd() {
                Main.getInstance().getGeneralManager().getLastPositions().put(getPlayer(), getPlayer().getLocation());
                Location location = new Location(Bukkit.getWorld("world"), Util.randInt(5000, 13000), 100, Util.randInt(5000, 13000));
                getPlayer().teleport(location);
                player.sendMessage(StringDefaults.PREFIX + "§eDu wurdest zur einer zufälligen Location teleportiert.");
            }
        };
        Main.getInstance().getGeneralManager().getTeleportDelays().put(player, tpDelay);


        return false;
    }

}
