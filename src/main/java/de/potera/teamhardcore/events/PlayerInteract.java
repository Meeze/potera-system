package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumPerk;
import de.potera.teamhardcore.others.EnumSettings;
import de.potera.teamhardcore.others.ams.Ams;
import de.potera.teamhardcore.others.crates.BaseCrate;
import de.potera.teamhardcore.others.crates.CrateOpening;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.TimeUtil;
import de.potera.teamhardcore.utils.Util;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerInteract implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();

        if (item == null || item.getType() == Material.AIR) return;

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(
                "§ePerk Gutschein")) {

            if (!item.getType().equals(Material.PAPER)) return;
            event.setCancelled(true);


            NBTItem nbtItem = new NBTItem(item);
            String perkString = nbtItem.hasKey("voucher_value") ? nbtItem.getString("voucher_value") : null;

            if (perkString == null) {
                player.sendMessage(
                        StringDefaults.PREFIX + "§cDieser Gutschein ist fehlerhaft, bitte bei einem Admin melden.");
                return;
            }

            UserData ud = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();
            EnumPerk perk = EnumPerk.getByName(perkString);

            if (perk == null) {
                player.sendMessage(
                        StringDefaults.PREFIX + "§cDieser Gutschein ist fehlerhaft, bitte bei einem Admin melden.");
                return;
            }

            if (ud.getOwnedPerks().contains(perk)) {
                player.sendMessage(StringDefaults.PREFIX + "§cDu besitzt dieses Perk bereits.");
                return;
            }

            Util.removeItems(player.getInventory(), player.getItemInHand(), 1);
            player.updateInventory();

            ud.addPerk(perk);
            player.sendMessage(
                    StringDefaults.PREFIX + "§7Du hast das Perk " + perk.getDisplayName() + " §7freigeschaltet.");
            return;
        }

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(
                "§eMünzen Gutschein")) {

            if (!item.getType().equals(Material.PAPER)) return;
            event.setCancelled(true);

            NBTItem nbtItem = new NBTItem(item);
            long value = nbtItem.hasKey("voucher_value") ? nbtItem.getLong("voucher_value") : 0L;

            if (value == 0.0D) {
                player.sendMessage(
                        StringDefaults.PREFIX + "§cDieser Gutschein ist fehlerhaft, bitte bei einem Admin melden.");
                return;
            }

            Util.removeItems(player.getInventory(), player.getItemInHand(), 1);
            player.updateInventory();

            UserCurrency uc = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();
            uc.addMoney(value);
            player.sendMessage(StringDefaults.PREFIX + "§7Dir wurden §e" + Util.formatNumber(
                    value) + "$ 7gutgeschrieben.");
            return;
        }

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(
                "§aAMS Boost")) {

            if (!item.getType().equals(Material.EXP_BOTTLE)) return;
            event.setCancelled(true);


            Ams ams = Main.getInstance().getAmsManager().getAms(player.getUniqueId());

            NBTItem nbtItem = new NBTItem(item);
            double boost = nbtItem.hasKey("boost") ? nbtItem.getDouble("boost") : 0.0D;
            long boostTime = nbtItem.hasKey("time") ? nbtItem.getLong("time") : 0L;

            if (player.isSneaking() && player.hasPermission("potera.ams.admin")) {
                player.sendMessage(StringDefaults.HEADER);
                player.sendMessage("§7Verfügbarer Boost§8: §e" + (boost == 0.0D ? "§cFehlerhaft" : boost));
                player.sendMessage(
                        "§7Verfügbarer Zeit§8: §e" + (boostTime == 0L ? "§cFehlerhaft" : TimeUtil.timeToString(
                                boostTime, false)));
                player.sendMessage(StringDefaults.FOOTER);
                return;
            }

            if (boost == 0.0D) {
                player.sendMessage(
                        StringDefaults.AMS_PREFIX + "§cDieser Boost ist fehlerhaft, bitte bei einem Admin melden.");
                return;
            }

            if (boostTime == 0L) {
                player.sendMessage(
                        StringDefaults.AMS_PREFIX + "§cDieser Boost ist fehlerhaft, bitte melde dich bei einem Admin.");
                return;
            }

            Util.removeItems(player.getInventory(), player.getItemInHand(), 1);
            player.updateInventory();

            ams.setCurrentBoost(ams.getCurrentBoost() + boost);
            ams.setBoostTime(ams.getBoostTime() + boostTime);
            player.sendMessage(
                    StringDefaults.AMS_PREFIX + "§7Du hast erfolgreich einen §e" + boost + "% §7Boost für §e" + TimeUtil.timeToString(
                            boostTime, false) + " §7eingelöst.");
            return;
        }


        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().contains(
                " Crate")) {

            if (!item.getType().equals(Material.SKULL_ITEM)) return;

            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
            if (lore.isEmpty() || lore.size() < 5) return;
            if (!lore.get(3).startsWith("§7[") && !lore.get(4).startsWith("§7[")) return;

            if (Main.getInstance().getCrateManager().getPlayersInOpening().containsKey(player)) return;

            String crateName = item.getItemMeta().getDisplayName().replace(" ", "");
            BaseCrate crate = Main.getInstance().getCrateManager().getCrate(ChatColor.stripColor(crateName));
            if (crate == null || crate.isDisabled()) return;

            if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
                player.openInventory(crate.getInventory());
                return;
            }

            event.setCancelled(true);
            Util.removeItems(player.getInventory(), player.getItemInHand(), 1);
            player.updateInventory();

            boolean skip = (userData.getSettingsOption(EnumSettings.CRATE_ANIMATION) != 0);

            CrateOpening opening = new CrateOpening(player, crate, skip);
            if (!skip) Main.getInstance().getCrateManager().getPlayersInOpening().put(player, opening);
        }

    }

}
