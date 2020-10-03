package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.TPDelay;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CommandBack implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;
        Player player = (Player) cs;

        if (!player.hasPermission("potera.back")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (!Main.getInstance().getGeneralManager().getLastPositions().containsKey(player)) {
            player.sendMessage(StringDefaults.PREFIX + "§cDu warst an keiner vorherigen Position.");
            return true;
        }

        if (Main.getInstance().getGeneralManager().getTeleportDelays().containsKey(player)) {
            player.sendMessage(StringDefaults.PREFIX + "§cEs läuft bereits ein Teleportvorgang.");
            return true;
        }

        Location last = Main.getInstance().getGeneralManager().getLastPositions().get(player);

        if (player.hasPermission("potera.teleport.nodelay")) {
            Main.getInstance().getGeneralManager().getLastPositions().put(player, player.getLocation());
            player.teleport(last, PlayerTeleportEvent.TeleportCause.COMMAND);
            player.sendMessage(StringDefaults.PREFIX + "§eDu wurdest an deine vorherige Position teleportiert.");
        } else {
            player.sendMessage(StringDefaults.PREFIX + "§eBereite dich auf deine Teleportation vor...");
            TPDelay tpDelay = new TPDelay(player, 0, 3) {
                public boolean onTick() {
                    return false;
                }

                public void onEnd() {
                    Main.getInstance().getGeneralManager().getLastPositions().put(getPlayer(),
                            getPlayer().getLocation());
                    getPlayer().teleport(last, PlayerTeleportEvent.TeleportCause.COMMAND);
                    getPlayer().sendMessage(
                            StringDefaults.PREFIX + "§eDu wurdest an deine vorherige Position teleportiert.");
                }
            };

            Main.getInstance().getGeneralManager().getTeleportDelays().put(player, tpDelay);

            return true;
        }
        return true;
    }
}
