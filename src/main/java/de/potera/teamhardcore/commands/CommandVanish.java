package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandVanish implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player p = (Player) cs;

        if (!p.hasPermission("potera.vanish")) {
            p.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length > 1) {
            p.sendMessage(StringDefaults.PREFIX + "§cVerwendung: §7/" + label + " [Spieler]");
            return true;
        }

        if (args.length == 0) {
            if (!Main.getInstance().getGeneralManager().getPlayersInVanish().contains(p)) {
                Main.getInstance().getGeneralManager().vanishAll(p);
                UserData userData = Main.getInstance().getUserManager().getUser(p.getUniqueId()).getUserData();
                userData.setVanished(true);
                p.sendMessage(StringDefaults.PREFIX + "§7Dein Vanishmode wurde aktiviert.");
            } else {
                Main.getInstance().getGeneralManager().unvanishAll(p);
                UserData userData = Main.getInstance().getUserManager().getUser(p.getUniqueId()).getUserData();
                userData.setVanished(false);
                p.sendMessage(StringDefaults.PREFIX + "§7Dein Vanishmode wurde deaktiviert.");
            }
        }


        if (args.length == 1) {

            if (!p.hasPermission("potera.vanish.other")) {
                p.sendMessage(StringDefaults.NO_PERM);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                p.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            if (target == p) {
                p.sendMessage(StringDefaults.PREFIX + "§cBenutze /" + label + " um dich selbst zu vanishen.");
                return true;
            }

            if (!Main.getInstance().getGeneralManager().getPlayersInVanish().contains(target)) {
                Main.getInstance().getGeneralManager().vanishAll(target);
                UserData userData = Main.getInstance().getUserManager().getUser(p.getUniqueId()).getUserData();
                userData.setVanished(true);
                target.sendMessage(StringDefaults.PREFIX + "§eDein Vanishmode wurde §aaktiviert§e.");
                p.sendMessage(
                        StringDefaults.PREFIX + "§eVanishmode von §7" + target.getName() + " §ewurde §aaktiviert§e.");
            } else {
                Main.getInstance().getGeneralManager().unvanishAll(target);
                UserData userData = Main.getInstance().getUserManager().getUser(p.getUniqueId()).getUserData();
                userData.setVanished(false);
                target.sendMessage(StringDefaults.PREFIX + "§eDein Vanishmode wurde §cdeaktiviert§e.");
                p.sendMessage(
                        StringDefaults.PREFIX + "§eVanishmode von §7" + target.getName() + " §ewurde §cdeaktiviert§e.");
            }
        }


        return true;
    }
}
