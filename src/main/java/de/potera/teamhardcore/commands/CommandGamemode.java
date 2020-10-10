package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.utils.StringDefaults;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandGamemode implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Du musst ein Spieler sein.");
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("potera.gamemode")) {
            p.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length > 2) {
            p.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <0|1|2> [Spieler]");
            return true;
        }

        if (args.length == 0) {

            GameMode gm = getGameModeFromString(label);

            if (gm == null) {
                p.sendMessage(StringDefaults.PREFIX + "§cBitte gebe einen gültigen Gamemode an.");
                return true;
            }

            p.setGameMode(gm);
            p.sendMessage(StringDefaults.PREFIX + "§eDein Gamemode wurde auf §7" + StringUtils.capitalize(
                    gm.name().toLowerCase()) + " §egesetzt.");
        }


        if (args.length == 1) {
            GameMode gm = getGameModeFromString(label);
            Player target = null;

            if (gm != null) {
                target = Bukkit.getPlayer(args[0]);

                if (target == null) {
                    p.sendMessage(StringDefaults.NOT_ONLINE);
                    return true;
                }
            } else {
                gm = getGameModeFromString(args[0]);

                if (gm == null) {
                    p.sendMessage(StringDefaults.PREFIX + "§cBitte gebe einen gültigen Gamemode an. ");
                    return true;
                }
            }

            if (target != null) {
                if (!p.hasPermission("potera.gamemode.other")) {
                    p.sendMessage(StringDefaults.NO_PERM);
                    return true;
                }

                if (target == p) {
                    p.sendMessage(StringDefaults.PREFIX + "§cBitte nutze den Befehl ohne deinen Namen.");
                    return true;
                }

                target.setGameMode(gm);
                target.sendMessage(StringDefaults.PREFIX + "§eDein Gamemode wurde auf §7" + StringUtils.capitalize(
                        gm.name().toLowerCase()) + " §egesetzt.");
                p.sendMessage(
                        StringDefaults.PREFIX + "§eDer Gamemode von §7" + target.getName() + " §ewurde auf §7" + StringUtils.capitalize(
                                gm.name().toLowerCase()) + " §egesetzt.");
            } else {
                p.setGameMode(gm);
                p.sendMessage(StringDefaults.PREFIX + "§eDein Gamemode wurde auf §7" + StringUtils.capitalize(
                        gm.name().toLowerCase()) + " §egesetzt.");
            }
        }


        if (args.length == 2) {
            if (!p.hasPermission("potera.gamemode.other")) {
                p.sendMessage(StringDefaults.NO_PERM);
                return true;
            }

            GameMode gm = getGameModeFromString(args[0]);
            Player target = Bukkit.getPlayer(args[1]);

            if (gm == null) {
                p.sendMessage(StringDefaults.PREFIX + "§cBitte gebe einen gültigen Gamemode an.");
                return true;
            }

            if (target == null) {
                p.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            if (target == p) {
                p.sendMessage(StringDefaults.PREFIX + "§cBitte nutze den Befehl ohne deinen Namen.");
                return true;
            }

            target.setGameMode(gm);
            target.sendMessage(StringDefaults.PREFIX + "§eDein Gamemode wurde auf §7" + StringUtils.capitalize(
                    gm.name().toLowerCase()) + " §egesetzt.");
            p.sendMessage(
                    StringDefaults.PREFIX + "§eDer Gamemode von §7" + target.getName() + " §ewurde auf §7" + StringUtils.capitalize(
                            gm.name().toLowerCase()) + " §egesetzt.");
        }
        return true;
    }

    private GameMode getGameModeFromString(String gmString) {
        if (gmString.equalsIgnoreCase("survival") || gmString.equalsIgnoreCase("gms") || gmString.equalsIgnoreCase(
                "0") || gmString.equalsIgnoreCase("s"))
            return GameMode.SURVIVAL;
        if (gmString.equalsIgnoreCase("creative") || gmString.equalsIgnoreCase("gmc") || gmString.equalsIgnoreCase(
                "1") || gmString.equalsIgnoreCase("c"))
            return GameMode.CREATIVE;
        if (gmString.equalsIgnoreCase("adventure") || gmString.equalsIgnoreCase("gma") || gmString.equalsIgnoreCase(
                "2") || gmString.equalsIgnoreCase("a"))
            return GameMode.ADVENTURE;
        if (gmString.equalsIgnoreCase("spectator") || gmString.equalsIgnoreCase("gmsp") || gmString.equalsIgnoreCase(
                "3") || gmString.equalsIgnoreCase("sp"))
            return GameMode.SPECTATOR;
        return null;
    }
}
