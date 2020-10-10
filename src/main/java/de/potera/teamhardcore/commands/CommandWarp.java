package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.TPDelay;
import de.potera.teamhardcore.others.Warp;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandWarp implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (label.equalsIgnoreCase("warp")) {
            if (args.length == 0) {
                Main.getInstance().getWarpManager().openMainInventory(player);
                return true;
            }
            if (args.length == 1) {
                String name = args[0];

                if (Main.getInstance().getWarpManager().getWarp(name) == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§cDieser Warp existiert nicht.");
                    return true;
                }

                Warp warp = Main.getInstance().getWarpManager().getWarp(name);

                if (Main.getInstance().getGeneralManager().getTeleportDelays().containsKey(player)) {
                    player.sendMessage(StringDefaults.PREFIX + "§cEs läuft bereits ein Teleportvorgang.");
                    return true;
                }

                if (player.hasPermission("potera.teleport.nodelay")) {
                    Main.getInstance().getGeneralManager().getLastPositions().put(player, player.getLocation());
                    player.teleport(warp.getLocation());
                    player.sendMessage(
                            StringDefaults.PREFIX + "§eDu wurdest zum Warp §7" + warp.getName() + " §eteleportiert.");
                } else {
                    player.sendMessage(StringDefaults.PREFIX + "§eDu wirst in §72 Sekunden §eteleportiert.");

                    TPDelay tpDelay = new TPDelay(player, 0, 2) {
                        @Override
                        public boolean onTick() {
                            if (Main.getInstance().getWarpManager().getWarp(warp.getName()) == null) {
                                player.sendMessage(StringDefaults.PREFIX + "§cDie Teleportation wurde abgebrochen.");
                                return true;
                            }
                            return false;
                        }

                        @Override
                        public void onEnd() {
                            Main.getInstance().getGeneralManager().getLastPositions().put(getPlayer(),
                                    getPlayer().getLocation());
                            getPlayer().teleport(warp.getLocation());
                            getPlayer().sendMessage(
                                    StringDefaults.PREFIX + "§eDu wurdest zum Warp §7" + warp.getName() + " §eteleportiert.");
                        }
                    };
                    Main.getInstance().getGeneralManager().getTeleportDelays().put(player, tpDelay);
                }
            }

            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("set")) {
                    if (!player.hasPermission("potera.warp.edit")) {
                        Main.getInstance().getWarpManager().openMainInventory(player);
                        return true;
                    }

                    String name = args[1];

                    if (Main.getInstance().getWarpManager().getWarp(name) != null) {
                        player.sendMessage(StringDefaults.PREFIX + "§cDieser Warp existiert bereits.");
                        return true;
                    }

                    Main.getInstance().getWarpManager().addWarp(name, player.getLocation());
                    player.sendMessage(
                            StringDefaults.PREFIX + "§eDu hast den Warp §7" + name + " §eerfolgreich erstellt.");
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (!player.hasPermission("potera.warp.edit")) {
                        Main.getInstance().getWarpManager().openMainInventory(player);
                        return true;
                    }

                    String name = args[1];

                    if (Main.getInstance().getWarpManager().getWarp(name) == null) {
                        player.sendMessage(StringDefaults.PREFIX + "§cDieser Warp existiert nicht.");
                        return true;
                    }

                    Main.getInstance().getWarpManager().removeWarp(name);
                    player.sendMessage(
                            StringDefaults.PREFIX + "§eDu hast den Warp §7" + name + " §eerfolgreich gelöscht.");

                } else {
                    if (player.hasPermission("potera.warp.edit")) {
                        sendHelp(player);
                    } else {
                        Main.getInstance().getWarpManager().openMainInventory(player);
                    }
                }
            }
        }


        if (label.equalsIgnoreCase("warps")) {
            if (!player.hasPermission("potera.warp.edit")) {
                Main.getInstance().getWarpManager().openMainInventory(player);
                return true;
            }

            Map<String, Warp> warps = Main.getInstance().getFileManager().getWarpFile().getWarps();

            if (warps.isEmpty()) {
                player.sendMessage(StringDefaults.PREFIX + "§cEs existieren keine Warps.");
                return true;
            }

            List<Warp> warpList = new ArrayList<>(warps.values());
            JSONMessage message = new JSONMessage("§eFolgende Warps existieren§8: ");

            for (int i = 0; i < warpList.size(); i++) {
                Warp warp = warpList.get(i);
                message.then("§7" + warp.getName()).tooltip(
                        "§eTeleportiere dich zum Warp §7" + warp.getName()).runCommand("/warp " + warp.getName());

                if (i >= warpList.size() - 1) continue;
                message.then("§e, ");
            }

            message.send(player);
        }


        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/warp [Name]");
        if (player.hasPermission("potera.warp.edit"))
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/warp <set|remove> <Warp>");
    }

}
