package de.potera.rysefoxx.bossegg.listener;

import de.potera.rysefoxx.bossegg.BossEgg;
import de.potera.rysefoxx.bossegg.BossEggSerializer;
import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import de.potera.rysefoxx.worldguard.WorldGuardAPI;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class BossInteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR)
            return;
        if (player.getItemInHand().getType() != Material.DRAGON_EGG || player.getItemInHand().getItemMeta() == null || player.getItemInHand().getItemMeta().getDisplayName() == null) {
            return;
        }
        if (!ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName()).contains("Spawn-Ei")) {
            return;
        }
        e.setUseItemInHand(Event.Result.DENY);
        e.setCancelled(true);
        if (!Main.getPlugin(Main.class).getBossEggManager().isAccessible()) {
            return;
        }

        BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName().replace("Spawn-Ei", "").replace("-", "").replace(" ", "")));

        if (bossEgg.getItems().isEmpty() || bossEgg.getItems() == null) {
            player.sendMessage(StringDefaults.PREFIX + "§7Es wurde kein Inhalt in dem Spawn-Ei gefunden.");
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!WorldGuardAPI.WorldGuard.canBuild(player, player.getLocation()) && !player.isOp()) {
                player.sendMessage(StringDefaults.PREFIX + "§7Du kannst das Spawn-Ei hier nicht benutzen.");
                return;
            }


            if (player.getItemInHand().getType() == Material.DRAGON_EGG && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName()).contains("Spawn-Ei")) {
                bossEgg.spawn(player, e.getClickedBlock().getX(), e.getClickedBlock().getY() + 1, e.getClickedBlock().getZ());

                if (player.getItemInHand().getAmount() > 1) {
                    player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
                } else {
                    player.getInventory().removeItem(player.getItemInHand());
                }

                if (bossEgg.isBroadcastOnSpawn()) {
                    Bukkit.broadcastMessage("§6§l§k---------------------------");
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§c" + player.getName() + " §7hat ein Spawner-Ei aus der §c" + bossEgg.getCollection() + " Kollektion benutzt.");
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§7Boss Name§8: §c" + bossEgg.getDisplayName());
                    Bukkit.broadcastMessage("§7Boss Leben§8: §c" + bossEgg.getMaxHealth());
                    Bukkit.broadcastMessage("§7X§8: §c" + e.getClickedBlock().getX());
                    Bukkit.broadcastMessage("§7Y§8: §c" + e.getClickedBlock().getY());
                    Bukkit.broadcastMessage("§7Z§8: §c" + e.getClickedBlock().getZ());
                    Bukkit.broadcastMessage("");
                    Bukkit.broadcastMessage("§6§l§k---------------------------");
                }

            }
        } else if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

            if (player.getItemInHand().getType() == Material.DRAGON_EGG && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName()).contains("Spawn-Ei")) {
                InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 6).withTitle("§7BossEgg Informationen");


                int index = 0;
                for (BossEggSerializer bossEggSerializer : bossEgg.getItems()) {
                    inventoryMenuBuilder.withItem(index, new ItemBuilder(bossEggSerializer.getItemStack().clone()).setDisplayName("§c" + bossEggSerializer.getDisplayName()).setLore(Arrays.asList(
                            "§7DisplayName §8➡ §c" + bossEggSerializer.getDisplayName(),
                            "§7Chance §8➡ §c" + bossEggSerializer.getChance() + "%",
                            "",
                            "§7Das Item ist schon §c" + Util.formatBigNumber(bossEggSerializer.getAmount()) + "x §7gedroppt.")).build());
                    index++;
                }

                inventoryMenuBuilder.withEventHandler(event -> event.setCancelled(true));

                inventoryMenuBuilder.show(player);
            }
        }

    }
}
