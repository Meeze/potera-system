package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class CommandFreeze implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.freeze")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7" + label + " <Spieler>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
            return true;
        }

        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(target.getUniqueId())) {
            Main.getInstance().getGeneralManager().getPlayersFreezed().remove(target.getUniqueId());
            player.sendMessage(StringDefaults.FREEZE_PREFIX + "§e" + target.getName() + " §7ist nun nicht mehr eingefroren.");
            target.sendMessage(StringDefaults.FREEZE_PREFIX + "§7Du bist nun nicht mehr eingefroren.");
        } else {
            if (target.hasPermission("potera.freeze.preven")) {
                player.sendMessage(StringDefaults.FREEZE_PREFIX + "§cDu darfst diesen Spieler nicht einfrieren.");
                return true;
            }

            teleportToSafePosition(target);
            Main.getInstance().getGeneralManager().getPlayersFreezed().add(target.getUniqueId());
            player.sendMessage(StringDefaults.FREEZE_PREFIX + "§e" + target.getName() + " §7ist nun eingefroren.");
            target.sendMessage(
                    StringDefaults.FREEZE_PREFIX + "§7Du bist eingefroren. Melde dich bei einem Teammitglied.");
        }

        return true;
    }

    private void teleportToSafePosition(Player p) {
        Location loc = p.getLocation();
        Location newLoc = null;
        for (int y = loc.getBlockY() - 1; y > 0; y--) {
            Location tmpNewLoc = new Location(loc.getWorld(), loc.getX(), y, loc.getZ());
            Block locBlock = tmpNewLoc.getBlock();
            if (locBlock.getType().isSolid()) {
                newLoc = tmpNewLoc;
                break;
            }
        }
        if (newLoc == null) {
            return;
        }
        newLoc.setY(newLoc.getY() + 1.0D);
        p.teleport(newLoc, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
    }

}
