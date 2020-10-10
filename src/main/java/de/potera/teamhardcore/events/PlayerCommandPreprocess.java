package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.SpyMode;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpTopic;

public class PlayerCommandPreprocess implements Listener {

    private final String[] globalmuteBlockedCmds = new String[]{"msg", "tell", "whisper", "t", "m", "pn", "r", "reply", "antworten", "sup", "support"};
    private final String[] blockedCmds = new String[]{"ver", "version", "pl", "plugins", "me", "?", "icanhasbukkit", "about", "lp", "luckperms", "/calc", "iac", "lag", "setslots", "knockback", "kb"};
    private final String[] freezeAllowedCmds = new String[]{"sup", "support"};

    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().contains("̇") || event.getMessage().equalsIgnoreCase("")) {
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        String fullCmd = event.getMessage().substring(1);
        String cmd = fullCmd.split(" ")[0];

        if (Main.getInstance().getGeneralManager().getGlobalmuteTier() == 2 && !player.hasPermission(
                "potera.globalmute.bypass")) {
            for (String blockedCmd : this.globalmuteBlockedCmds) {
                if (blockedCmd.equalsIgnoreCase(cmd)) {
                    event.setCancelled(true);
                    player.sendMessage(StringDefaults.PREFIX + "§cDer Chat ist im Moment deaktiviert.");
                    return;
                }
            }
        }

        for (Player inSpy : Main.getInstance().getGeneralManager().getPlayersInSpy()) {
            if (inSpy == player) continue;
            User user = Main.getInstance().getUserManager().getUser(player.getUniqueId());
            UserData userData = user.getUserData();

            if (!userData.hasSpyModeActive(SpyMode.SpyModeType.COMMAND)) continue;
            SpyMode spyMode = userData.getSpyMode(SpyMode.SpyModeType.COMMAND);

            if (spyMode.isAll() || spyMode.getPlayers().contains(player.getUniqueId())) {
                inSpy.sendMessage(StringDefaults.SPY_PREFIX + "§c" + player.getName() + "§8: §6/" + fullCmd);
            }
        }

        if (!player.hasPermission("potera.useblockedcmd")) {
            if (cmd.startsWith("bukkit:") || cmd.startsWith("minecraft:") || cmd.startsWith("intave:")) {
                event.setCancelled(true);
                player.sendMessage(StringDefaults.PREFIX + "§7Der Befehl wurde nicht gefunden. §a/help");
                return;
            }

            for (String blocked : this.blockedCmds) {
                if (cmd.equalsIgnoreCase(blocked)) {
                    event.setCancelled(true);
                    player.sendMessage(StringDefaults.PREFIX + "§7Der Befehl wurde nicht gefunden. §a/help");
                    return;
                }
            }
        }

        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(player.getUniqueId())) {
            boolean allowedCmd = false;
            for (String allowed : this.freezeAllowedCmds) {
                if (cmd.equalsIgnoreCase(allowed)) {
                    allowedCmd = true;
                    break;
                }
            }
            if (!allowedCmd) {
                event.setCancelled(true);
                player.sendMessage(
                        StringDefaults.PREFIX + "§7Du bist eingefroren. Melde dich bei einem Teammitglied.");
                return;
            }
        }

        if (!event.isCancelled()) {
            HelpTopic topic = Bukkit.getHelpMap().getHelpTopic("/" + cmd);
            if (topic == null) {
                event.setCancelled(true);
                player.sendMessage(StringDefaults.PREFIX + "§7Der Befehl wurde nicht gefunden. §a/help");
            }
        }

    }

}
