package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.ams.AmsHandler;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        Main.getInstance().getUserManager().addToCache(player.getUniqueId());
        User user = Main.getInstance().getUserManager().getUser(player.getUniqueId());
        UserData userData = user.getUserData();

        userData.addReadyExecutor(() -> {
            if (userData.isVanished()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Main.getInstance().getGeneralManager().vanishAll(player);
                        player.sendMessage(StringDefaults.PREFIX + "§eDu befindest dich weiterhin im Vanish.");
                    }
                }.runTask(Main.getInstance());
            }
        });
        Main.getInstance().getGeneralManager().sendCustomTabHeaderFooter(player);
        Main.getInstance().getGeneralManager().updateVanish(player);
        Main.getInstance().getScoreboardManager().createNewScoreboard(player);
        Main.getInstance().getScoreboardManager().updateTeamListsSinglePlayer(player, true);

        Main.getInstance().getClanManager().loadClanMember(player.getUniqueId());

        if (!Main.getInstance().getClanManager().getRequests(player.getUniqueId()).isEmpty()) {
            int requests = Main.getInstance().getClanManager().getRequests(player.getUniqueId()).size();

            player.sendMessage(
                    StringDefaults.CLAN_PREFIX + "§7Du hast noch §e" + requests + " §7offene Anfrage" + (requests == 1 ? "" : "N"));
        }

        if (Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
            Clan clan = Main.getInstance().getClanManager().getClan(player.getName());
            clan.sendMessageToClan("§e" + player.getName() + " §7ist dem Server beigetreten.", player);
        }

        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(player.getUniqueId())) {
            player.sendMessage(
                    StringDefaults.PREFIX + "§7Du bist eingefroren. Melde dich bei einem Teammitglied.");
        }

        for (int i = 0; i < 5; i++) {
            player.getInventory().addItem(AmsHandler.createBoostItem(27.6, "30m"));
        }

        user.getUserStats().setPvPCoins(10000);

    }

}
