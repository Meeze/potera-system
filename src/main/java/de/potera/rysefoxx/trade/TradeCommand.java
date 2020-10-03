package de.potera.rysefoxx.trade;

import de.potera.rysefoxx.menubuilder.interfaces.ItemListener;
import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TradeCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (!Main.getInstance().getTradeManager().getRequests().containsKey(player) || Main.getInstance().getTradeManager().getRequests().get(player).size() <= 0) {
                    player.sendMessage(StringDefaults.PREFIX + "§cDu hast keine offenen Tauschanfragen.");
                    return true;
                }
                InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 3).withTitle("§7Tauschanfragen");

                inventoryMenuBuilder.withItem(26, new ItemBuilder(Material.BARRIER).setLore(Collections.singletonList("§7Klicke, um alle offenen Tauschanfragen zu löschen.")).setDisplayName("§cAlle Tauschanfragen löschen").build(), new ItemListener() {
                    @Override
                    public void onInteract(Player player, ClickType action, ItemStack item) {
                        for (ItemStack itemStack : player.getOpenInventory().getTopInventory().getContents()) {
                            if (itemStack == null) continue;
                            for (int i = 0; i < player.getOpenInventory().getTopInventory().getSize(); i++) {
                                if (i == 26) break;
                                player.getOpenInventory().getTopInventory().setItem(i, null);
                            }
                        }
                        Main.getInstance().getTradeManager().getRequests().remove(player);
                        player.sendMessage(StringDefaults.TRADE_PREFIX + "§7Du hast alle offenen Tauschanfragen gelöscht.");
                    }
                }, InventoryMenuBuilder.ALL_CLICK_TYPES);


                int index = 0;
                for (Player player1 : Main.getInstance().getTradeManager().getRequests().get(player)) {
                    if (index >= 35) break;
                    inventoryMenuBuilder.withItem(index, new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(player1.getName()).setDisplayName("§6Tauschanfrage §7von §6" + player1.getName()).setLore("", "§7Linksklick §8- §eTauschanfrage annehmen", "§7Rechtsklick §8- §eTauschanfrage entfernen").build(), (player2, action, item) -> {

                        if (action == ClickType.LEFT) {
                            if (Main.getInstance().getTradeManager().getTradePartner().containsKey(player1)) {
                                player2.sendMessage(StringDefaults.PREFIX + "§7Die Tauschanfrage konnte nicht angenommen werden, da §c" + player1.getName() + " §7sich in einem Tauschvorgang befindet.");
                                return;
                            }
                            if (!player1.isOnline()) {
                                player2.sendMessage(StringDefaults.PREFIX + "§7Tauschanfrage wurde entfernt, da der Spieler nicht mehr online ist.");
                                player2.closeInventory();
                                Main.getInstance().getTradeManager().getRequests().get(player2).remove(player1);
                                return;
                            }
                            player1.sendMessage(StringDefaults.TRADE_PREFIX + "§c" + player2.getName() + " §7hat deine Tauschanfrage angenommen.");
                            player2.sendMessage(StringDefaults.TRADE_PREFIX + "§c" + player1.getName() + " §7hat deine Tauschanfrage angenommen.");
                            Main.getInstance().getTradeManager().getRequests().get(player2).remove(player1);

                            new TradeStart(player, player1);
                        } else if (action == ClickType.RIGHT) {
                            Main.getInstance().getTradeManager().getRequests().get(player2).remove(player1);
                            player2.closeInventory();
                            player2.sendMessage(StringDefaults.TRADE_PREFIX + "§7Du hast die Tauschanfrage von §c" + player1.getName() + " §7abgelehnt.");
                            player1.sendMessage(StringDefaults.TRADE_PREFIX + "§c" + player2.getName() + " §7hat deine Tauschanfrage abgelehnt.");
                        }
                    }, ClickType.LEFT, ClickType.RIGHT);
                    index++;
                }


                inventoryMenuBuilder.show(player);

            } else {

                Player ta = Bukkit.getPlayer(args[0]);
                if (ta == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieser Spieler ist nicht online.");
                    return true;
                }
                if (ta == player) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Du kannst dir keine Tauschanfrage schicken.");
                    return true;
                }


                if (Main.getInstance().getTradeManager().getRequests().containsKey(ta) && Main.getInstance().getTradeManager().getRequests().get(ta).contains(player)) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Du hast dem Spieler bereits eine Tauschanfrage geschickt.");
                    return true;
                }

                List<Player> requests;
                if (Main.getInstance().getTradeManager().getRequests().containsKey(ta)) {
                    requests = Main.getInstance().getTradeManager().getRequests().get(ta);
                } else {
                    requests = new ArrayList<>();
                }
                requests.add(player);
                Main.getInstance().getTradeManager().getRequests().put(ta, requests);
                player.sendMessage(StringDefaults.TRADE_PREFIX + "§c" + ta.getName() + " §7hat deine Tauschanfrage erhalten.");
                ta.sendMessage(StringDefaults.TRADE_PREFIX + "§c" + player.getName() + " §7hat dir eine Tauschanfrage geschickt.");

                new JSONMessage(StringDefaults.PREFIX + "§8>§7Klicke hier§8>").runCommand("/trade list").then(" §8⚊ ").send(player);

            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("annehmen")) {
                if (!Main.getInstance().getTradeManager().getRequests().containsKey(player)) {
                    player.sendMessage(StringDefaults.PREFIX + "§cDu hast keine offenen Tauschanfragen.");
                    return true;
                }
                Player ta = Bukkit.getPlayer(args[1]);
                if (ta == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieser Spieler ist nicht online.");
                    return true;
                }
                if (Main.getInstance().getTradeManager().getTradePartner().containsKey(ta)) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Die Tauschanfrage konnte nicht angenommen werden, da §c" + ta.getName() + " §7sich in einem Trade befindet.");
                    return true;
                }
                if (!Main.getInstance().getTradeManager().getRequests().get(player).contains(ta)) {
                    player.sendMessage(StringDefaults.TRADE_PREFIX + "§7Du hast keine Tauschanfrage von §c" + ta.getName() + " §7erhalten.");
                    return true;
                }
                ta.sendMessage(StringDefaults.TRADE_PREFIX + "§c" + player.getName() + " §7hat deine Tauschanfrage angenommen.");
                player.sendMessage(StringDefaults.TRADE_PREFIX + "Du hast die Tauschanfrage von §c" + ta.getName() + " §7angenommen.");
                Main.getInstance().getTradeManager().getRequests().get(player).remove(ta);
                new TradeStart(player, ta);
            }
        } else {
            Util.sendBigMessage(player, StringDefaults.TRADE_PREFIX + "§cVerwendung§8: §7/", "Trade list", "Trade <Spieler>", "Trade accept <Spieler>");
        }

        return false;
    }

}
