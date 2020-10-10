package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.SpyMode;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMessagespy implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.messagespy")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <all|Spieler>");
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {

            UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();

            if (userData.hasSpyModeActive(SpyMode.SpyModeType.MESSAGE)) {
                SpyMode mode = userData.getSpyMode(SpyMode.SpyModeType.MESSAGE);

                if (mode.isAll()) {
                    userData.changeSpyMode(SpyMode.SpyModeType.MESSAGE, false, false);
                    player.sendMessage(
                            StringDefaults.SPY_PREFIX + "§eDu hast den Messagespy Modus §7All §edeaktiviert.");
                } else {
                    userData.changeSpyMode(SpyMode.SpyModeType.MESSAGE, true, true);
                    player.sendMessage(
                            StringDefaults.SPY_PREFIX + "§eDer Messagespy Modus wurde auf §7All §egeändert.");
                }
                return true;
            }
            userData.changeSpyMode(SpyMode.SpyModeType.MESSAGE, true, true);
            player.sendMessage(
                    StringDefaults.SPY_PREFIX + "§eDer Messagespy Modus §7All §ewurde aktiviert.");
        } else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            if (target == player) {
                player.sendMessage(StringDefaults.SPY_PREFIX + "§cDu kannst dich nicht selber spionieren.");
                return true;
            }

            UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();
            if (userData.hasSpyModeActive(SpyMode.SpyModeType.MESSAGE)) {
                SpyMode mode = userData.getSpyMode(SpyMode.SpyModeType.MESSAGE);

                if (!mode.isAll()) {
                    if (mode.getPlayers().contains(target.getUniqueId())) {
                        userData.editSpyList(SpyMode.SpyModeType.MESSAGE, false, target.getUniqueId());
                        player.sendMessage(
                                StringDefaults.SPY_PREFIX + "§7" + target.getName() + " §ewurde vom Messagespy ausgeschlossen.");
                    } else {
                        userData.editSpyList(SpyMode.SpyModeType.MESSAGE, true, target.getUniqueId());
                        player.sendMessage(
                                StringDefaults.SPY_PREFIX + "§7" + target.getName() + " §ewurde zum Messagespy hinzugefügt.");

                    }
                } else {
                    userData.changeSpyMode(SpyMode.SpyModeType.MESSAGE, true, false, target.getUniqueId());
                    player.sendMessage(
                            StringDefaults.SPY_PREFIX + "§7" + target.getName() + " §ewurde zum Messagespy hinzugefügt.");
                }
                return true;
            }
            userData.changeSpyMode(SpyMode.SpyModeType.MESSAGE, true, false, target.getUniqueId());
            player.sendMessage(
                    StringDefaults.SPY_PREFIX + "§7" + target.getName() + " §ewurde zum Messagespy hinzugefügt.");
        }

        return true;
    }
}
