package de.potera.teamhardcore.commands;

import com.google.common.base.Charsets;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.inventories.HomeInventory;
import de.potera.teamhardcore.others.Home;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserHomes;
import de.potera.teamhardcore.utils.DateFormats;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.UUIDFetcher;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class CommandHome implements CommandExecutor {

    private final CharsetEncoder encoder = Charsets.ISO_8859_1.newEncoder();

    public static void teleportToHome(Player player, String target, String name, UserHomes homes) {
        boolean self = player.getUniqueId().equals(homes.getUser().getUuid());
        Home home = homes.getHome(name);

        if (home == null) {
            player.sendMessage(
                    StringDefaults.PREFIX + "§cDer Home existiert§7" + (self ? "" : (" §cbei §7" + target)) + " §cnicht.");
            return;
        }

        if (!homes.isReady()) {
            player.sendMessage(
                    StringDefaults.PREFIX + "§cBitte warte einen Moment, während die Homes geladen werden...");
            return;
        }

        player.teleport(home.getPosition());
        if (self) {
            home.setLastTeleportDate(System.currentTimeMillis());
            homes.updateLastTeleportTime(name, true);
        }
        player.sendMessage(
                StringDefaults.PREFIX + "§eDu wurdest zum Home §7" + home.getName() + (self ? "" : (" §evon §7" + target)) + " §eteleportiert.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (label.equalsIgnoreCase("home")) {
            if (args.length == 0 || args.length > 2) {
                player.sendMessage(
                        StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Name> " + (player.hasPermission(
                                "potera.home.other") ? "[Spieler]" : ""));
                return true;
            }

            String homeName = args[0];

            if (args.length == 2) {
                if (!player.hasPermission("potera.home.other")) {
                    player.sendMessage(
                            StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Name> " + (player.hasPermission(
                                    "potera.home.other") ? "[Spieler]" : ""));
                    return true;
                }

                Player targetOnline = Bukkit.getPlayer(args[1]);

                if (targetOnline != null) {
                    User tUser = Main.getInstance().getUserManager().getUser(targetOnline.getUniqueId());
                    UserHomes tHomes = tUser.getUserHomes();
                    teleportToHome(player, targetOnline.getName(), homeName, tHomes);
                    return true;
                }

                UUIDFetcher.getUUID(args[1], uuid -> {
                    if (uuid == null) {
                        player.sendMessage(StringDefaults.PREFIX + "§cDer Spieler war noch nie auf diesem Server.");
                        return;
                    }

                    User user = new User(uuid);
                    teleportToHome(player, args[1], homeName, user.getUserHomes());
                });

            }

            User user = Main.getInstance().getUserManager().getUser(player.getUniqueId());
            UserHomes homes = user.getUserHomes();
            teleportToHome(player, player.getName(), homeName, homes);
        }

        if (label.equalsIgnoreCase("homes")) {
            if (args.length > 1) {
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + (player.hasPermission(
                        "potera.home.other") ? " [Spieler]" : ""));
                return true;
            }

            if (args.length == 1) {
                if (!player.hasPermission("potera.homes.seeother")) {
                    player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label);
                    return true;
                }

                Player targetOnline = Bukkit.getPlayer(args[0]);

                if (targetOnline != null) {
                    User tUser = Main.getInstance().getUserManager().getUser(targetOnline.getUniqueId());
                    UserHomes tHomes = tUser.getUserHomes();
                    sendHomeList(player, targetOnline.getName(), tHomes);
                    return true;
                }

                UUIDFetcher.getUUID(args[0], uuid -> {
                    if (uuid == null) {
                        player.sendMessage(StringDefaults.PREFIX + "§cDer Spieler war noch nie auf diesem Server.");
                        return;
                    }

                    User user = new User(uuid);
                    sendHomeList(player, args[0], user.getUserHomes());
                });
                return true;
            }

            HomeInventory.openHomeInventory(player);
        }

        if (label.equalsIgnoreCase("sethome")) {
            if (args.length != 1) {
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Spieler>");
                return true;
            }

            User user = Main.getInstance().getUserManager().getUser(player.getUniqueId());

            String homeName = args[0].toLowerCase();
            Home home = user.getUserHomes().getHome(homeName);

            if (home != null) {
                player.sendMessage(
                        StringDefaults.PREFIX + "§cDu besitzt bereits ein Home mit dem Namen §7" + args[0] + "§c.");
                return true;
            }

            if (user.getHomeLimit() != -1 && user.getUserHomes().getHomes().size() >= user.getHomeLimit()) {
                player.sendMessage(user.getUserHomes().getHomes().size() + "/" + user.getHomeLimit());
                player.sendMessage(StringDefaults.PREFIX + "§cDu hast bereits die maximale Anzahl an Homes erreicht.");
                return true;
            }

            if (homeName.matches("[^\\\\dA-Za-z0-9]")) {
                player.sendMessage(StringDefaults.PREFIX + "§cBitte verwende keine Sonderzeichen.");
                return true;
            }

            if (!this.encoder.canEncode(homeName)) {
                player.sendMessage(StringDefaults.PREFIX + "§cDer Name enthält unerlaubte Sonderzeichen.");
                return true;
            }

            if (homeName.length() > 20) {
                player.sendMessage(
                        StringDefaults.PREFIX + "§cDer Name des Homes darf nicht länger als 20 Zeichen sein.");
                return true;
            }

            Location playerLocation = player.getLocation();
            Location homeLocation = new Location(player.getWorld(), playerLocation.getBlockX() + 0.5D,
                    playerLocation.getBlockY(), playerLocation.getBlockZ() + 0.5D, playerLocation.getYaw(),
                    playerLocation.getPitch());
            user.getUserHomes().addHome(homeName, homeLocation, true);
            player.sendMessage(StringDefaults.PREFIX + "§eDu hast den Home §7" + homeName + " §eerstellt.");
        }

        if (label.equalsIgnoreCase("delhome")) {
            if (args.length != 1) {
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Spieler>");
                return true;
            }

            User user = Main.getInstance().getUserManager().getUser(player.getUniqueId());

            String homeName = args[0].toLowerCase();
            Home home = user.getUserHomes().getHome(homeName);

            if (home == null) {
                player.sendMessage(
                        StringDefaults.PREFIX + "§cDu besitzt keinen Home mit dem Namen §7" + args[0] + "§c.");
                return true;
            }

            user.getUserHomes().removeHome(homeName, true);
            player.sendMessage(StringDefaults.PREFIX + "§eDu hast den Home §7" + homeName + " §egelöscht.");
        }

        return true;
    }

    private void sendHomeList(Player player, String target, UserHomes userHomes) {
        boolean self = player.getUniqueId().equals(userHomes.getUser().getUuid());

        if (!userHomes.isReady()) {
            player.sendMessage(
                    StringDefaults.PREFIX + "§cBitte warte einen Moment, während die Homes geladen werden...");
            return;
        }

        Map<String, Home> homes = userHomes.getHomes();
        if (homes == null || homes.isEmpty()) {
            player.sendMessage(
                    StringDefaults.PREFIX + "§c" + (self ? "Du" : ("§7" + target)) + " §cbesitzt keine Homes.");
            return;
        }

        player.sendMessage(StringDefaults.HEADER);
        player.sendMessage(" ");
        player.sendMessage("§7Alle Homes§8:");
        player.sendMessage(" ");

        Iterator<Map.Entry<String, Home>> iterator = homes.entrySet().iterator();
        JSONMessage message = new JSONMessage("");
        while (iterator.hasNext()) {
            Map.Entry<String, Home> entry = iterator.next();
            String homeName = entry.getKey();
            Home home = entry.getValue();

            ArrayList<String> tooltip = new ArrayList<>();
            tooltip.add("§7Teleportiere dich zum Home §e" + homeName + (self ? "" : " §7von §e" + target));

            if (player.hasPermission("potera.home.other")) {
                tooltip.add(" ");
                tooltip.add("§7Weitere Informationen§8:");
                tooltip.add(
                        " §8■ §7Erstellt am§8: §e" + DateFormats.FORMAT_HOME.format(new Date(home.getCreationDate())));
                tooltip.add(
                        " §8■ §7Letzter Teleport§8: §e" + ((home.getLastTeleportDate() == -1 ? "-" : DateFormats.FORMAT_HOME.format(
                                new Date(home.getLastTeleportDate())))));
            }

            message.then("§e" + homeName).tooltip(tooltip).runCommand("/home " + homeName + (self ? "" : " " + target));
            if (iterator.hasNext()) {
                message.then("§7, ");
            }
        }
        message.send(player);
        player.sendMessage(" ");
        player.sendMessage(StringDefaults.FOOTER);

    }

}
