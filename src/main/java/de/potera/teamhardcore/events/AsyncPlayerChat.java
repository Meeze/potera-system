package de.potera.teamhardcore.events;

import de.potera.realmeze.punishment.controller.PunishmentController;
import de.potera.realmeze.punishment.event.PunishListener;
import de.potera.realmeze.punishment.model.PunishmentType;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Support;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.others.clan.ClanMember;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class AsyncPlayerChat implements PunishListener {

    private PunishmentController punishmentController;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        String message = event.getMessage();


        if(punishmentController.getPunishment(player.getUniqueId(), PunishmentType.MUTE).isPresent()){
            player.sendMessage("du bist muted lulw");
            event.setCancelled(true);
            return;
        }

        if (message.contains("̇") || message.equalsIgnoreCase("")) {
            event.setCancelled(true);
            return;
        }

        if (Main.getInstance().getGeneralManager().getPlayersFreezed().contains(player.getUniqueId())) {
            message = message.trim();
            event.setMessage(message);

            List<Player> toRemove = new ArrayList<>();

            Bukkit.getOnlinePlayers().forEach(all -> {
                if (!all.hasPermission("potera.freeze.seechat")) {
                    toRemove.add(all);
                }
            });

            event.getRecipients().removeAll(toRemove);
            event.setFormat(StringDefaults.FREEZE_PREFIX + "§7%1$s§8: §c%2$s");
            return;
        }

        if (message.startsWith("#") && Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
            message = message.substring(1);
            message = message.trim();
            event.setMessage(message);

            ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());
            Clan clan = member.getClan();
            List<Player> toRemove = new ArrayList<>();

            for (Player rec : event.getRecipients())
                if (!clan.getMemberList().getMembers().containsKey(rec.getUniqueId()))
                    toRemove.add(rec);

            event.getRecipients().removeAll(toRemove);
            event.setFormat(
                    "§6§lClanChat " + StringDefaults.PREFIX + member.getRank().getColor() + "%1$s§8: §7%2$s");
            return;
        }

        if (Main.getInstance().getSupportManager().getSupport(player) != null) {
            event.getRecipients().clear();

            Support support = Main.getInstance().getSupportManager().getSupport(player);
            Support.SupportRole role = support.getSupportPlayers().get(player);

            for (Player inSupport : support.getSupportPlayers().keySet())
                event.getRecipients().add(inSupport);

            if (role == Support.SupportRole.SUPPORTER)
                event.setFormat("§c%1$s: §7%2$s");
            else
                event.setFormat("§7%1$s: §7%2$s");
            return;
        }

        for (Map.Entry<Player, Support> supports : Main.getInstance().getSupportManager().getSupports().entrySet()) {
            Player inSupport = supports.getKey();
            Support support = supports.getValue();

            if (support.getSupportPlayers().get(inSupport) == Support.SupportRole.MEMBER)
                event.getRecipients().remove(inSupport);
        }

        if (Main.getInstance().getGeneralManager().getGlobalmuteTier() != 0 && !player.hasPermission(
                "potera.globalmute.bypass")) {
            event.setCancelled(true);
            player.sendMessage(StringDefaults.PREFIX + "§cDer Chat ist momentan deaktiviert.");
            return;
        }



        if (!player.hasPermission("potera.settings.bypass")) {
            Set<Player> toRemove = null;

            for (Player receiver : event.getRecipients()) {
                if (receiver == player) continue;

                UserData udReceiver = Main.getInstance().getUserManager().getUser(receiver.getUniqueId()).getUserData();
                if (udReceiver.getIgnoredPlayers().contains(player.getUniqueId())) {
                    if (toRemove == null)
                        toRemove = new HashSet<>();
                    toRemove.add(receiver);
                }
            }

            if (toRemove != null)
                event.getRecipients().removeAll(toRemove);

        }

    }

}
