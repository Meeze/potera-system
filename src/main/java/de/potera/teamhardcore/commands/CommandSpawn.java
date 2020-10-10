package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.TPDelay;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSpawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player)) return true;

        Player player = (Player) cs;

        if (label.equalsIgnoreCase("spawn")) {
            if (Main.getInstance().getWarpManager().getWarp("Spawn") == null) {
                player.sendMessage(StringDefaults.PREFIX + "§cEs existiert noch kein Spawn.");
                return true;
            }

            if (Main.getInstance().getGeneralManager().getTeleportDelays().containsKey(player)) {
                player.sendMessage(StringDefaults.PREFIX + "§cEs läuft bereits ein Teleportvorgang.");
                return true;
            }

            if (player.hasPermission("potera.teleport.nodelay")) {
                Main.getInstance().getGeneralManager().getLastPositions().put(player, player.getLocation());
                player.teleport(Main.getInstance().getWarpManager().getWarp("Spawn").getLocation());
                player.sendMessage(StringDefaults.PREFIX + "§eDu wurdest zum Spawn teleportiert.");
            } else {
                player.sendMessage(StringDefaults.PREFIX + "§eDu wirst in §72 Sekunden §eteleportiert.");

                TPDelay tpDelay = new TPDelay(player, 0, 2) {
                    @Override
                    public boolean onTick() {
                        return false;
                    }

                    @Override
                    public void onEnd() {
                        Main.getInstance().getGeneralManager().getLastPositions().put(getPlayer(),
                                getPlayer().getLocation());
                        getPlayer().teleport(Main.getInstance().getWarpManager().getWarp("Spawn").getLocation());
                        player.sendMessage(StringDefaults.PREFIX + "§eDu wurdest zum Spawn teleportiert.");
                    }
                };
                Main.getInstance().getGeneralManager().getTeleportDelays().put(player, tpDelay);
            }
        }

        if (label.equalsIgnoreCase("setspawn")) {
            if (!player.hasPermission("potera.setspawn")) {
                player.sendMessage(StringDefaults.NO_PERM);
                return true;
            }

            Main.getInstance().getWarpManager().addWarp("Spawn", player.getLocation());
            player.sendMessage(StringDefaults.PREFIX + "§eDu hast den Spawn gesetzt.");
        }
        return true;
    }
}