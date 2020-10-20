package de.potera.teamhardcore.events;

import de.potera.realmeze.punishment.controller.PunishmentController;
import de.potera.realmeze.punishment.event.PunishListener;
import de.potera.realmeze.punishment.model.Punishment;
import de.potera.realmeze.punishment.model.PunishmentType;
import de.potera.rysefoxx.fanciful.FancyMessage;
import de.potera.rysefoxx.utils.Enchants;
import de.potera.rysefoxx.utils.RomanNumber;
import de.potera.rysefoxx.utils.StringSimilarity;
import de.potera.rysefoxx.utils.TimeUtils;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Support;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.others.clan.ClanMember;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

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

        String[] messageArray = event.getMessage().split(" ");

        Optional<Punishment> punishment = punishmentController.getPunishment(player.getUniqueId(), PunishmentType.MUTE);
        if (punishment.isPresent()) {
            if (getPunishmentController().isPunishmentExpired(punishment.get())) {
                getPunishmentController().unmute(event.getPlayer());
            } else {
                player.sendMessage("du bist muted lulw");
                event.setCancelled(true);
                return;
            }
        }

        for (String msg : messageArray) {
            double result;
            for (String badWords : Main.getInstance().getAutoMuteManager().getDisallowedWords()) {
                result = StringSimilarity.similarity(msg, badWords);

                //ToDo: Spieler muten
                if (result >= 1) {
                    event.setCancelled(true);

                    //30 Min mute

                    break;
                } else if (result >= 0.9) {
                    event.setCancelled(true);

                    //25 min mute

                    break;
                } else if (result >= 0.8) {
                    event.setCancelled(true);

                    //20 min mute


                    break;
                } else if (result >= 0.7) {
                    event.setCancelled(true);

                    //15 min mute

                    break;
                }
            }


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


        if (ChatColor.stripColor(message).startsWith("[item]") || ChatColor.stripColor(message).startsWith("[i]") && player.hasPermission("potera.item.post")) {
            if (!Main.getPlugin(Main.class).getItemManager().isActive()) {
                event.setCancelled(true);
                player.sendMessage(StringDefaults.PREFIX + "§cDu kannst derzeit kein Item verlinken.");
                return;
            }
            if (!Main.getPlugin(Main.class).getItemManager().canUse(player)) {
                event.setCancelled(true);
                player.sendMessage(StringDefaults.PREFIX + "§7Du musst noch §c" + TimeUtils.getTime(Main.getPlugin(Main.class).getItemManager().getPlayerCoolDown(player)) + " §7warten.");
                return;
            }
            Main.getPlugin(Main.class).getItemManager().setPlayerCoolDown(player, Main.getPlugin(Main.class).getItemManager().getCoolDown());


            if (Util.hasItemInHand(player)) {
                event.setCancelled(true);
                ItemStack itemInHand = player.getItemInHand();
                String displayName;
                List<String> lore = new ArrayList<>();
                if (itemInHand.hasItemMeta() && itemInHand.getItemMeta().hasDisplayName()) {
                    displayName = itemInHand.getItemMeta().getDisplayName();
                } else {
                    displayName = CraftItemStack.asNMSCopy(itemInHand).getName();
                }
                if (itemInHand.getItemMeta().hasLore()) {
                    for (int i = 0; i < itemInHand.getItemMeta().getLore().size(); i++) {
                        lore.add(i, itemInHand.getItemMeta().getLore().get(i));
                    }
                    lore.add(0, "§6§l" + displayName);
                }
                if (itemInHand.getItemMeta().hasEnchants()) {
                    lore.add(0, "§b" + displayName);
                    Map<Enchantment, Integer> enchant = itemInHand.getItemMeta().getEnchants();
                    for (Map.Entry<Enchantment, Integer> enchantment : enchant.entrySet()) {
                        lore.add("§7" + Objects.requireNonNull(Enchants.forName(enchantment.getKey().getName())).getGoodName() + " " + RomanNumber.toRoman(enchantment.getValue()));
                    }
                }

                //Todo: CHANGE FANCYMESSAGE FORMAT WITH END FORMAT. (event.getFormat());


                FancyMessage fancyMessage = new FancyMessage().text("<" + player.getName() + "> §8[§a" + displayName + "§8] §r" + message.replace("[item]", "")).tooltip(lore);
                for (Player all : Bukkit.getOnlinePlayers()) {
                    fancyMessage.send(all);
                }

            } else {
                event.setCancelled(true);
                player.sendMessage(StringDefaults.PREFIX + "§7Bitte halte ein Item in der Hand.");
                return;
            }


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
