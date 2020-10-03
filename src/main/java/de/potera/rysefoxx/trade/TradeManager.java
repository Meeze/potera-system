package de.potera.rysefoxx.trade;

import de.potera.rysefoxx.trade.listener.TradeCloseListener;
import de.potera.rysefoxx.trade.listener.TradeQuitListener;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class TradeManager {

    private final ItemStack READY;
    private final ItemStack NOT_READY;
    private final ItemStack ITEM_FILLER;
    private final List<Integer> PLAYER_SLOTS;
    private final List<Integer> TARGET_SLOTS;
    private final List<Integer> FILLER;
    private final Map<Player, List<Player>> requests;
    private final List<Player> trades;
    private final Map<Player, List<ItemStack>> barter;
    private final HashMap<Player, Player> tradePartner;
    private final List<Player> allTrades;


    public TradeManager() {
        Bukkit.getPluginManager().registerEvents(new TradeCloseListener(), Main.getInstance());
        Bukkit.getPluginManager().registerEvents(new TradeQuitListener(), Main.getInstance());

        requests = new HashMap<>();
        trades = new ArrayList<>();
        barter = new HashMap<>();
        tradePartner = new HashMap<>();
        allTrades = new ArrayList<>();
        FILLER = Arrays.asList(0, 1, 3, 4, 7, 5, 8, 9, 13, 17, 18, 22, 26, 27, 31, 35, 36, 40, 44, 45, 48, 47, 46, 49, 50, 51, 52, 53);
        ITEM_FILLER = new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short) 7).setDisplayName("§8-/-").build();
        PLAYER_SLOTS = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
        TARGET_SLOTS = Arrays.asList(14, 15, 16, 23, 24, 25, 32, 33, 34);
        READY = new ItemBuilder(Material.INK_SACK).setDurability((short) 10).setDisplayName("§aBereit zum Traden").build();
        NOT_READY = new ItemBuilder(Material.INK_SACK).setDurability((short) 1).setDisplayName("§cNicht bereit zum Traden").build();
    }

    public void forceEnd() {
        for (Player player : allTrades) {
            if (!barter.containsKey(player)) continue;
            for (ItemStack itemStack : barter.get(player)) {
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                Util.addItem(player, itemStack);
            }
            player.sendMessage(StringDefaults.PREFIX + "Der Tradevorgang wurde abgebrochen!");
        }
    }

    public void stopTrade(Player player) {
        Player partner = tradePartner.get(player);

        if (barter.containsKey(player)) {
            for (ItemStack itemStack : barter.get(player)) {
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                Util.addItem(player, itemStack);
            }
        }
        if (barter.containsKey(partner)) {
            for (ItemStack itemStack : barter.get(partner)) {
                if (itemStack == null || itemStack.getType() == Material.AIR) continue;
                Util.addItem(player, itemStack);
            }
        }


        barter.remove(player);
        barter.remove(partner);

        tradePartner.remove(player);
        tradePartner.remove(partner);

        trades.remove(player);
        trades.remove(partner);
        if (partner != null) {
            partner.closeInventory();
            partner.updateInventory();
            partner.sendMessage(StringDefaults.PREFIX + "Der Tradevorgang wurde abgebrochen!");
        }
        if (player != null) {
            player.updateInventory();
        }
    }

    public List<Player> getAllTrades() {
        return allTrades;
    }

    public Map<Player, List<ItemStack>> getBarter() {
        return barter;
    }

    public HashMap<Player, Player> getTradePartner() {
        return tradePartner;
    }

    public List<Player> getTrades() {
        return trades;
    }

    public Map<Player, List<Player>> getRequests() {
        return requests;
    }

    public List<Integer> getFILLER() {
        return FILLER;
    }

    public ItemStack getITEM_FILLER() {
        return ITEM_FILLER;
    }

    public List<Integer> getTARGET_SLOTS() {
        return TARGET_SLOTS;
    }

    public List<Integer> getPLAYER_SLOTS() {
        return PLAYER_SLOTS;
    }

    public ItemStack getREADY() {
        return READY;
    }

    public ItemStack getNOT_READY() {
        return NOT_READY;
    }
}
