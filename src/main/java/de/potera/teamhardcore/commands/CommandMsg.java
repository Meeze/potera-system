package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumSettings;
import de.potera.teamhardcore.others.SpyMode;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMsg implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (label.equalsIgnoreCase("msg") || label.equalsIgnoreCase("tell") || label.equalsIgnoreCase(
                "pn") || label.equalsIgnoreCase("whisper") || label.equalsIgnoreCase("t") || label.equalsIgnoreCase(
                "m") || label.equalsIgnoreCase("w")) {

            if (args.length < 2) {
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Spieler> <Nachricht>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            if (target == player) {
                player.sendMessage(StringDefaults.PREFIX + "§cDu kannst nicht mit dir selber schreiben.");
                return true;
            }

            if (!player.hasPermission("potera.settings.bypass")) {
                UserData userDataTarget = Main.getInstance().getUserManager().getUser(
                        target.getUniqueId()).getUserData();
                if (userDataTarget.getSettingsOption(
                        EnumSettings.PRIVATE_MESSAGE) == 1 || userDataTarget.getIgnoredPlayers().contains(
                        player.getUniqueId())) {
                    player.sendMessage(
                            StringDefaults.MSG_PREFIX + "§7" + target.getName() + " §cmöchte keine Nachrichten empfangen.");
                    return true;
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < args.length; i++)
                builder.append(args[i]).append(" ");

            String output = builder.substring(0, builder.length() - 1);

            player.sendMessage(StringDefaults.MSG_PREFIX + "§6Du -> " + target.getName() + "§8: §r§7" + output);
            target.sendMessage(StringDefaults.MSG_PREFIX + "§6" + player.getName() + " -> Dir§8: §r§7" + output);

            for (Player inSpy : Main.getInstance().getGeneralManager().getPlayersInSpy()) {
                if (inSpy == player || inSpy == target) continue;

                UserData userData = Main.getInstance().getUserManager().getUser(inSpy.getUniqueId()).getUserData();
                if (!userData.hasSpyModeActive(SpyMode.SpyModeType.MESSAGE)) continue;
                SpyMode mode = userData.getSpyMode(SpyMode.SpyModeType.MESSAGE);
                if (mode.isAll() || mode.getPlayers().contains(player.getUniqueId()) || mode.getPlayers().contains(
                        target.getUniqueId()))
                    inSpy.sendMessage(
                            StringDefaults.SPY_PREFIX + "§c" + player.getName() + " §6» §c" + target.getName() + "§6: §r" + output);
            }

            Main.getInstance().getGeneralManager().getLastMessageContacts().put(player, target);
            Main.getInstance().getGeneralManager().getLastMessageContacts().put(target, player);
        }

        if (label.equalsIgnoreCase("r") || label.equalsIgnoreCase("reply") || label.equalsIgnoreCase("antworten")) {
            if (args.length == 0) {
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " <Nachricht>");
                return true;
            }

            if (!Main.getInstance().getGeneralManager().getLastMessageContacts().containsKey(player)) {
                player.sendMessage(StringDefaults.MSG_PREFIX + "§cDu hast niemanden zuvor geschrieben.");
                return true;
            }

            Player target = Main.getInstance().getGeneralManager().getLastMessageContacts().get(player);

            if (!player.hasPermission("potera.settings.bypass")) {
                UserData userDataTarget = Main.getInstance().getUserManager().getUser(
                        target.getUniqueId()).getUserData();
                if (userDataTarget.getSettingsOption(
                        EnumSettings.PRIVATE_MESSAGE) == 1 || userDataTarget.getIgnoredPlayers().contains(
                        player.getUniqueId())) {
                    player.sendMessage(
                            StringDefaults.MSG_PREFIX + "§7" + target.getName() + " §cmöchte keine Nachrichten empfangen.");
                    return true;
                }
            }

            StringBuilder builder = new StringBuilder();

            for (String arg : args) {
                builder.append(arg).append(" ");
            }

            String output = builder.substring(0, builder.length() - 1);

            player.sendMessage(StringDefaults.MSG_PREFIX + "§6Du -> " + target.getName() + "§8: §r§7" + output);
            target.sendMessage(StringDefaults.MSG_PREFIX + "§6" + player.getName() + " -> Dir§8: §r§7" + output);

            for (Player inSpy : Main.getInstance().getGeneralManager().getPlayersInSpy()) {
                if (inSpy == player || inSpy == target) continue;

                UserData userData = Main.getInstance().getUserManager().getUser(inSpy.getUniqueId()).getUserData();
                if (!userData.hasSpyModeActive(SpyMode.SpyModeType.MESSAGE)) continue;
                SpyMode mode = userData.getSpyMode(SpyMode.SpyModeType.MESSAGE);
                if (mode.isAll() || mode.getPlayers().contains(player.getUniqueId()) || mode.getPlayers().contains(
                        target.getUniqueId()))
                    inSpy.sendMessage(
                            StringDefaults.SPY_PREFIX + "§c" + player.getName() + " §6» §c" + target.getName() + "§6: §r" + output);
            }

            Main.getInstance().getGeneralManager().getLastMessageContacts().put(player, target);
            Main.getInstance().getGeneralManager().getLastMessageContacts().put(target, player);
        }

        return true;
    }
}
