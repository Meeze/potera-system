package de.potera.teamhardcore.others.mines.enchantments;

import de.potera.teamhardcore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class EnchantmentHandler {

    private static final Random RANDOM = new Random();
    private static final String[] NUMERALS = new String[]{"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};

    public static String toRomanNumeral(int number) {
        return (number > 0 && number <= NUMERALS.length) ? NUMERALS[number - 1] : null;
    }

    public static int romanNumeralToNumber(String romanNumeral) {
        for (int i = 0; i < NUMERALS.length; i++) {
            if (NUMERALS[i].equals(romanNumeral))
                return i + 1;
        }
        return -1;
    }

    public static Object[] enchantRandom(ItemStack toEnchant) {
        List<CustomEnchant> possibleEnchantments = new ArrayList<>(Arrays.asList(CustomEnchant.values()));
        Map<CustomEnchant, Integer> enchantsOnItem = getCustomEnchantsWithLevel(toEnchant);

        enchantsOnItem.forEach((enchantment, power) -> {
            if (power >= enchantment.getMaxLevel())
                possibleEnchantments.remove(enchantment);

            if (enchantment.name().equals(CustomEnchant.LUCKYMINER.name()) && (power + 1) >= 3)
                possibleEnchantments.remove(enchantment);

        });

        if (possibleEnchantments.isEmpty())
            return new Object[0];

        CustomEnchant rndEnchantment = possibleEnchantments.get(RANDOM.nextInt(possibleEnchantments.size()));

        boolean hasEnchantment = enchantsOnItem.containsKey(rndEnchantment);
        int nextPower = hasEnchantment ? (enchantsOnItem.get(rndEnchantment) + 1) : 1;

        if (hasEnchantment) {
            setEnchantmentLevel(toEnchant, rndEnchantment, nextPower);
        } else {
            addEnchantment(toEnchant, rndEnchantment, nextPower);
        }

        return new Object[]{rndEnchantment, nextPower};
    }

    public static int countMaxEnchantmentPower() {
        int count = 0;
        for (CustomEnchant enchant : CustomEnchant.values())
            count += enchant.getMaxLevel();
        return count;
    }

    public static boolean hasCustomEnchants(ItemStack itemStack) {
        Set<CustomEnchant> enchants = getCustomEnchantments(itemStack);
        return !enchants.isEmpty();
    }

    public static boolean isEnchantmentBook(ItemStack itemStack) {
        if (!itemStack.getType().equals(Material.ENCHANTED_BOOK)) return false;

        if (!itemStack.hasItemMeta() || (!itemStack.getItemMeta().hasDisplayName() || (!itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(
                "§eVerzaubertes Buch"))))
            return false;

        return true;
    }

    public static int countCustomEnchants(ItemStack itemStack) {
        Set<CustomEnchant> enchants = getCustomEnchantments(itemStack);
        return enchants.size();
    }

    public static Map<CustomEnchant, Integer> getCustomEnchantsWithLevel(ItemStack item) {
        if (!item.hasItemMeta())
            return new HashMap<>();
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore())
            return new HashMap<>();
        List<String> lore = meta.getLore();
        Map<CustomEnchant, Integer> enchants = new HashMap<>();
        for (String line : lore) {
            for (CustomEnchant en : CustomEnchant.values()) {
                if (line.startsWith("§7" + en.getEnchantName())) {
                    int power = romanNumeralToNumber(line.split(" ")[1]);
                    if (power != -1) {
                        enchants.put(en, power);
                        break;
                    }
                }
            }
        }
        return enchants;
    }

    public static Set<CustomEnchant> getCustomEnchantments(ItemStack itemStack) {
        if (!itemStack.hasItemMeta())
            return new HashSet<>();
        ItemMeta meta = itemStack.getItemMeta();
        if (!meta.hasLore())
            return new HashSet<>();

        List<String> lore = meta.getLore();
        Set<CustomEnchant> enchants = new HashSet<>();
        for (String line : lore) {
            for (CustomEnchant enchant : CustomEnchant.values()) {
                if (line.startsWith("§7" + enchant.getEnchantName()) && romanNumeralToNumber(
                        line.split(" ")[1]) != -1) {
                    enchants.add(enchant);
                    break;
                }
            }
        }
        return enchants;
    }

    public static int getEnchantmentLevel(ItemStack item, CustomEnchant enchant) {
        if (!item.hasItemMeta())
            return 0;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore())
            return 0;
        List<String> lore = meta.getLore();
        for (String line : lore) {
            if (line.startsWith("§7" + enchant.getEnchantName())) {
                String[] spl = line.split(" ");
                if (spl.length != 2)
                    break;
                int power = romanNumeralToNumber(spl[1]);
                return (power == -1) ? 0 : power;
            }
        }
        return 0;
    }

    public static int countCustomEnchantPowersFromItem(ItemStack item) {
        int count = 0;
        Map<CustomEnchant, Integer> enchants = getCustomEnchantsWithLevel(item);
        for (CustomEnchant enchant : enchants.keySet())
            count += enchants.get(enchant);
        return count;
    }

    public static boolean hasCustomEnchant(ItemStack item, CustomEnchant enchantment) {
        return getCustomEnchantments(item).contains(enchantment);
    }

    public static void addEnchantment(ItemStack item, CustomEnchant enchantment, int level) {
        if (hasCustomEnchant(item, enchantment))
            return;
        if (level > enchantment.getMaxLevel() || level < 1)
            return;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
        int newPos = countCustomEnchants(item);
        lore.add(newPos, "§7" + enchantment.getEnchantName() + " " + toRomanNumeral(level));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static void removeEnchantment(ItemStack item, CustomEnchant enchantment) {
        if (!hasCustomEnchant(item, enchantment))
            return;
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.hasLore() ? meta.getLore() : null;
        if (lore == null)
            return;
        for (int i = 0; i < lore.size(); i++) {
            String text = lore.get(i);
            if (text.startsWith("§7" + enchantment.getEnchantName())) {
                lore.remove(i);
                break;
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static void setEnchantmentLevel(ItemStack item, CustomEnchant enchantment, int level) {
        if (!hasCustomEnchant(item, enchantment))
            return;
        ItemMeta meta = item.getItemMeta();
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            for (int i = 0; i < lore.size(); i++) {
                String text = lore.get(i);
                if (text.startsWith("§7" + enchantment.getEnchantName()))
                    lore.set(i, "§7" + enchantment.getEnchantName() + " " + toRomanNumeral(level));
            }
            meta.setLore(lore);
        } else {
            List<String> lore = new ArrayList<>();
            lore.add("§7" + enchantment.getEnchantName() + " " + toRomanNumeral(level));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
    }

    public static ItemStack getEnchantmentBook() {
        return new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("§eVerzaubertes Buch").setLore("",
                "§7Dieses Buch fügt deiner",
                "§7Spitzhacke ein zufälliges",
                "§7MineEnchantment hinzu.").build();
    }

    public static ItemStack getMineTimeBook() {
        return new ItemBuilder(Material.ENCHANTED_BOOK).setDisplayName("§eZeit Gutschein").setLore(
                "§7Dieses Buch erhöht deine", "§7individuelle Zeit zum Minen.").build();
    }

}
