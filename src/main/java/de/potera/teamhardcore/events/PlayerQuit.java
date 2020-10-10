package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.utils.StringDefaults;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        LogManager.getLogger(PlayerQuit.class).info("Starting playerquit for player " + player.getName());

        if (Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
            Clan clan = Main.getInstance().getClanManager().getClan(player.getUniqueId());
            clan.sendMessageToClan("§e" + player.getName() + " §7hat den Server verlassen.", player);
        }

        Main.getInstance().getClanManager().unloadClanMember(player.getUniqueId());
        Main.getInstance().getClanManager().getRequests(player.getUniqueId()).clear();

        Main.getInstance().getScoreboardManager().removePlayerScoreboard(player);
        Main.getInstance().getScoreboardManager().updateTeamListsSinglePlayer(player, false);

        Main.getInstance().getGeneralManager().getPlayersInSpy().remove(player);
        Main.getInstance().getGeneralManager().getPlayersInGodMode().remove(player);
        Main.getInstance().getGeneralManager().getTeleportRequests().remove(player);
        Main.getInstance().getRouletteManager().getBuildingRoulettes().remove(player);

        Main.getInstance().getCoinflipManager().handlePlayerQuit(player);

        if (Main.getInstance().getGeneralManager().getPlayersInVanish().contains(player))
            Main.getInstance().getGeneralManager().unvanishAll(player);

        if (Main.getInstance().getGeneralManager().getLastMessageContacts().containsKey(player)) {
            Player target = Main.getInstance().getGeneralManager().getLastMessageContacts().get(player);
            Main.getInstance().getGeneralManager().getLastMessageContacts().remove(player);
            if (Main.getInstance().getGeneralManager().getLastMessageContacts().get(target) == player)
                Main.getInstance().getGeneralManager().getLastMessageContacts().remove(target);
        }

        if (Main.getInstance().getSupportManager().isWaiting(player))
            Main.getInstance().getSupportManager().setWaiting(player, false);

        Main.getInstance().getSupportManager().handlePlayerQuit(player);

        Main.getInstance().getUserManager().removeFromCache(player.getUniqueId());

        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(player.getUniqueId())) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all.hasPermission("potera.freeze")) {
                    all.sendMessage(
                            StringDefaults.FREEZE_PREFIX + "§c§lAchtung§8: §7" + player.getName() + " §chat sich im Freeze ausgeloggt.");
                }
            }
        }

        Main.getInstance().getFakeEntityManager().clearInteractCooldowns(player);
        LogManager.getLogger(PlayerQuit.class).info("PlayerQuit for player " + player.getName() + " finished");

    }

}
