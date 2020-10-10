package de.potera.rysefoxx.trade;


import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TradeStart {

    public TradeStart(Player player, Player ta) {
        Main.getInstance().getTradeManager().getAllTrades().add(player);
        Main.getInstance().getTradeManager().getAllTrades().add(ta);
        Main.getInstance().getTradeManager().getTrades().add(player);
        createInventory(player, ta);
    }


    private void createInventory(Player player, Player ta) {

        InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 6).withTitle("§7Trade");

        for (int i : Main.getInstance().getTradeManager().getFILLER()) {
            inventoryMenuBuilder.withItem(i, Main.getInstance().getTradeManager().getITEM_FILLER());
        }

        inventoryMenuBuilder.withItem(2, new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(player.getName()).setDisplayName("§6" + player.getName()).build());
        inventoryMenuBuilder.withItem(6, new ItemBuilder(Material.SKULL_ITEM).setDurability((short) 3).setSkullOwner(ta.getName()).setDisplayName("§6" + ta.getName()).build());


        inventoryMenuBuilder.withItem(37, Main.getInstance().getTradeManager().getNOT_READY());
        inventoryMenuBuilder.withItem(38, Main.getInstance().getTradeManager().getNOT_READY());
        inventoryMenuBuilder.withItem(39, Main.getInstance().getTradeManager().getNOT_READY());


        inventoryMenuBuilder.withItem(41, Main.getInstance().getTradeManager().getNOT_READY());
        inventoryMenuBuilder.withItem(42, Main.getInstance().getTradeManager().getNOT_READY());
        inventoryMenuBuilder.withItem(43, Main.getInstance().getTradeManager().getNOT_READY());

        player.openInventory(inventoryMenuBuilder.getInventory());
        ta.openInventory(inventoryMenuBuilder.getInventory());

        Main.getInstance().getTradeManager().getTradePartner().put(player, ta);
        Main.getInstance().getTradeManager().getTradePartner().put(ta, player);


        inventoryMenuBuilder.withEventHandler(event -> {
            event.setCancelled(true);
            if (!(event.getWhoClicked() instanceof Player)) return;
            if (event.getCurrentItem() == null) return;

            Player p = (Player) event.getWhoClicked();

            if(Main.getInstance().getGeneralManager().getTradeCooldown().containsKey(player)){
                if(Main.getInstance().getGeneralManager().getTradeCooldown().get(player) > System.currentTimeMillis()){
                    player.sendMessage(StringDefaults.PREFIX+"§7Bitte warte einen Moment.");
                    player.playSound(player.getLocation(), Sound.FIZZ,5,5);
                    return;
                }
            }

            if (Main.getInstance().getTradeManager().getTrades().contains(p)) {
                if (event.getClickedInventory().getTitle().equalsIgnoreCase("§7Trade")) {
                    updateStandby(event, p, 37, 38, 39);
                    updateItems(event, p, Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30));
                } else {
                    updatePlayerInventory(event, p, Main.getInstance().getTradeManager().getPLAYER_SLOTS());

                }
            } else {
                if (event.getClickedInventory().getTitle().equalsIgnoreCase("§7Trade")) {
                    updateStandby(event, p, 41, 42, 43);
                    updateItems(event, p, Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34));
                } else {
                    updatePlayerInventory(event, p, Main.getInstance().getTradeManager().getTARGET_SLOTS());

                }

            }

        });

    }

    private void updateStandby(InventoryClickEvent event, Player player, int slot1, int slot2, int slot3) {
        if (event.getSlot() == slot1 || event.getSlot() == slot2 || event.getSlot() == slot3) {
            Main.getInstance().getGeneralManager().getTradeCooldown().put(player, System.currentTimeMillis()+3000L);
            if (event.getClickedInventory().getItem(event.getSlot()).getData().getData() == 1) {
                for (int i : Arrays.asList(slot1, slot2, slot3)) {
                    event.getClickedInventory().setItem(i, Main.getInstance().getTradeManager().getREADY());
                    checkIfReady(event.getClickedInventory(), player);
                }
            } else {
                for (int i : Arrays.asList(slot1, slot2, slot3)) {
                    event.getClickedInventory().setItem(i, Main.getInstance().getTradeManager().getNOT_READY());
                }
            }
        }
    }


    private void updatePlayerInventory(InventoryClickEvent event, Player player, List<Integer> slots) {
        Main.getInstance().getGeneralManager().getTradeCooldown().put(player, System.currentTimeMillis()+3000L);
        for (int i : slots) {
            if (event.getInventory().getItem(i) != null) continue;
            event.getInventory().setItem(i, event.getCurrentItem());
            List<ItemStack> barter;
            if (Main.getInstance().getTradeManager().getBarter().containsKey(player)) {
                barter = Main.getInstance().getTradeManager().getBarter().get(player);
            } else {
                barter = new ArrayList<>();
            }
            barter.add(event.getCurrentItem());
            Main.getInstance().getTradeManager().getBarter().put(player, barter);
            player.getInventory().setItem(event.getSlot(), null);
            break;
        }
        forceUnready(event.getInventory());

    }


    private void updateItems(InventoryClickEvent event, Player player, List<Integer> slots) {
        for (int i : slots) {
            if (event.getSlot() == i) {
                if (event.getInventory().getItem(event.getSlot()) == null) return;
                player.getInventory().addItem(event.getInventory().getItem(event.getSlot()));
                Main.getInstance().getTradeManager().getBarter().get(player).remove(event.getInventory().getItem(event.getSlot()));
                event.getInventory().setItem(event.getSlot(), null);
                forceUnready(event.getClickedInventory());
                Main.getInstance().getGeneralManager().getTradeCooldown().put(player, System.currentTimeMillis()+3000L);
            }
        }
    }

    private void forceUnready(Inventory inventory) {
        if (inventory.getItem(39).getData().getData() == 2) {
            setUnready(inventory, Arrays.asList(39, 38, 37));
        }
        if (inventory.getItem(41).getData().getData() == 2) {
            setUnready(inventory, Arrays.asList(41, 42, 43));
        }
    }

    private void setUnready(Inventory inventory, List<Integer> slots) {
        for (int i : slots) {
            inventory.setItem(i, Main.getInstance().getTradeManager().getNOT_READY());
        }
    }

    private void checkIfReady(Inventory inventory, Player player) {
        if (inventory.getItem(37).getData().getData() == 2 && inventory.getItem(41).getData().getData() == 2) {
            Player ta = Main.getInstance().getTradeManager().getTradePartner().get(player);

            if (Main.getInstance().getTradeManager().getBarter().containsKey(player)) {
                for (ItemStack itemStack : Main.getInstance().getTradeManager().getBarter().get(player)) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                    Util.addItem(player,itemStack);
                }
            }
            if (Main.getInstance().getTradeManager().getBarter().containsKey(ta)) {
                for (ItemStack itemStack : Main.getInstance().getTradeManager().getBarter().get(ta)) {
                    if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                    Util.addItem(player,itemStack);
                }
            }

            Main.getInstance().getTradeManager().getBarter().remove(player);
            Main.getInstance().getTradeManager().getBarter().remove(ta);

            Main.getInstance().getTradeManager().getTradePartner().remove(player);
            Main.getInstance().getTradeManager().getTradePartner().remove(ta);

            Main.getInstance().getTradeManager().getTrades().remove(player);
            Main.getInstance().getTradeManager().getTrades().remove(ta);

            if (ta != null) {
                ta.closeInventory();
                ta.updateInventory();
            }
            player.closeInventory();
            player.updateInventory();


        }


    }



}
