package de.potera.teamhardcore.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import io.netty.util.internal.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class Util {

    private static final Random random = new Random();
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
    private static final String[] moneyNames = new String[]{"Mrd.", "Bio.", "Brd.", "Trio."};

    public static String formatNumber(double number) {
        return numberFormat.format(number);
    }

    public static String formatBigNumber(long number) {
        if (number < 1000000000L)
            return formatNumber(number);
        return calculateBigFormat((number / 1000000L), 0);
    }

    public static boolean getChance(double d) {
        return ThreadLocalRandom.current().nextDouble() * 100.0D <= d;
    }

    public static void fill(Inventory inventory, int size) {
        for (int i = 0; i < size; i++) {
            inventory.setItem(i, new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability((short) 15).setDisplayName("§8-/-").build());
        }
    }

    public static boolean isInt(String integer) {
        try {
            Integer.parseInt(integer);
        } catch (NumberFormatException exception) {
            return false;
        }
        return Integer.parseInt(integer) >= 1;
    }

    public static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return Double.parseDouble(s) >= 0.1;
    }

    public static String getCustomName(ItemStack itemStack) {
        if (!itemStack.hasItemMeta()) return CraftItemStack.asNMSCopy(itemStack).getName();
        if (!itemStack.getItemMeta().hasDisplayName()) return CraftItemStack.asNMSCopy(itemStack).getName();
        return itemStack.getItemMeta().getDisplayName();
    }

    public static String messageBuilder(int offset, String[] args) {
        StringBuilder msg = new StringBuilder();
        for (int i = offset; i < args.length; i++) {
            msg.append(args[i]).append(" ");
        }
        return msg.toString();
    }

    public static void sendBigMessage(Player player, String prefix, String... message) {
        for (String msg : message) {
            player.sendMessage(prefix + msg);
        }
    }

    public static int randInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private static String calculateBigFormat(double number, int iteration) {
        double shortened = (int) (number / 1000.0D * 100.0D) / 100.0D;
        boolean round = (shortened * 10.0D % 10.0D == 0.0D);
        return (shortened < 1000.0D) ? ((round ?
                Integer.valueOf((int) shortened) : formatNumber(shortened)) + " " + moneyNames[iteration]) :
                calculateBigFormat(shortened, iteration + 1);
    }

    public static boolean isValidNeutralName(String code) {
        return !code.matches(Pattern.quote("[a-zA-Z0-9_-!\"§$%&/()=?*+~#²³{[]}\\ß,.<>|@^°]*"));
    }

    public static String generateRandomKey(int length) {
        char[] chars = new char[length];

        for (int i = 0; i < length; i++) {
            chars[i] = "aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789".charAt(
                    random.nextInt("aAbBcCdDeEfFgGhHiIjJkKlLmMnNoOpPqQrRsStTuUvVwWxXyYzZ0123456789".length()));
        }
        return new String(chars);
    }

    public static int getAvailableItems(Inventory inv, ItemStack item) {
        int counted = 0;
        for (ItemStack content : inv.getContents()) {
            if (content != null && content.getType() != Material.AIR &&
                    content.getType() == item.getType() && content.getDurability() == item.getDurability()) {
                counted += content.getAmount();
            }
        }
        return counted;
    }

    public static boolean hasEnoughItems(Inventory inv, ItemStack item, int amount) {
        return (getAvailableItems(inv, item) >= amount);
    }

    public static boolean removeItems(Inventory inv, ItemStack item, int amount) {
        if (!Util.hasEnoughItems(inv, item, amount)) {
            return false;
        }
        int toRemove = amount;
        HashMap<Integer, ItemStack> slots = new HashMap<>();
        for (int slot = 0; slot < inv.getSize(); ++slot) {
            ItemStack slotItem = inv.getItem(slot);
            if (slotItem == null || slotItem.getType() == Material.AIR || slotItem.getType() != item.getType() || slotItem.getDurability() != item.getDurability())
                continue;
            slots.put(slot, slotItem);
        }
        for (Map.Entry<Integer, ItemStack> entrySlots : slots.entrySet()) {
            if ((entrySlots.getValue()).getAmount() <= toRemove) {
                inv.setItem(entrySlots.getKey(), new ItemStack(Material.AIR));
                toRemove -= (entrySlots.getValue()).getAmount();
                continue;
            }
            ItemStack invItem = inv.getItem(entrySlots.getKey());
            invItem.setAmount(invItem.getAmount() - toRemove);
            break;
        }
        return true;
    }

    public static void clearInventory(Player player) {
        player.getInventory().setContents(new ItemStack[0]);
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
    }

    public static String locationToString(Location l) {
        if (l == null)
            return "";

        return l.getWorld().getName() + ":" + l.getX() + ":" + l.getY() + ":" + l.getZ() + ":" + l.getYaw() + ":" + l.getPitch();

    }

    public static Location stringToLocation(String s) {
        if (s == null || s.trim().equals(""))
            return null;

        final String[] parts = s.split(":");

        if (parts.length == 6)
            return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3]), Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
        return null;
    }

    public static void addItem(Player player, ItemStack item) {
        if (player.getInventory().firstEmpty() == -1)
            player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
        else
            player.getInventory().addItem(item.clone());
    }

    public static int getPing(Player target) {
        Object craftPlayer = Reflection.getOBCClass("entity.CraftPlayer").cast(target);
        Object entityPlayer = Reflection.invoke(craftPlayer,
                Reflection.getMethod(Reflection.getOBCClass("entity.CraftPlayer"), "getHandle"));
        return (Integer) Reflection.getFromField(Reflection.getField(entityPlayer.getClass(), "ping"),
                entityPlayer);
    }

    public static void sendPlayerlistHeaderFooter(Player player, String header, String footer) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.PLAYER_LIST_HEADER_FOOTER);
        WrappedChatComponent wrapHeader = WrappedChatComponent.fromText(header);
        WrappedChatComponent wrapFooter = WrappedChatComponent.fromText(footer);
        packet.getChatComponents().write(0, wrapHeader);
        packet.getChatComponents().write(1, wrapFooter);
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendActionbarMessage(Player player, String message) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
        packet.getBytes().write(0, (byte) 2);
        packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
