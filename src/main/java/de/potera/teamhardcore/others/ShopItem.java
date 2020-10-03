package de.potera.teamhardcore.others;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShopItem {

    private final Category category;
    private final Type type;
    private final Material material;
    private final short data;
    private final long price;
    private final String displayname;
    private final String inventoryname;

    public ShopItem(Category category, Type type, Material material, short data, long price, String displayname, String inventoryname) {
        this.category = category;
        this.type = type;
        this.material = material;
        this.data = data;
        this.price = price;
        this.displayname = displayname;
        this.inventoryname = inventoryname;
    }

    public int buyItems(Player player, int amount) {
        ItemStack itemStack = new ItemStack(material, amount, getData());
        Map<Integer, ItemStack> leftOver = player.getInventory().addItem(itemStack);

        if (!leftOver.isEmpty()) {
            int lefted = 0;
            for (ItemStack itemLefted : leftOver.values())
                lefted += itemLefted.getAmount();
            amount -= lefted;
            if (amount == 0) {
                player.sendMessage(StringDefaults.PREFIX + "§cDu hast keinen Platz mehr im Inventar.");
                player.playSound(player.getLocation(), Sound.FIZZ, 1.0F, 1.0F);
                return -1;
            }
        }
        return amount;
    }

    public int sellItems(Player p, int amount) {
        ItemStack shopItemStack = new ItemStack(getMaterial(), amount, getData());
        int availItems = Util.getAvailableItems(p.getInventory(), shopItemStack);

        if (availItems == 0) {
            p.sendMessage(StringDefaults.PREFIX + "§cDu besitzt zu wenig Items dieser Art.");
            p.playSound(p.getLocation(), Sound.FIZZ, 1.0F, 1.0F);
            return -1;
        }

        if (availItems < amount) {
            amount = availItems;
        }
        Util.removeItems(p.getInventory(), shopItemStack, amount);
        return amount;
    }

    public void handleBuy(Player p, int amount) {
        long price = getPrice() * amount;
        UserCurrency uc = Main.getInstance().getUserManager().getUser(p.getUniqueId()).getUserCurrency();

        if (getType() == Type.BUY) {
            if (uc.getMoney() < price) {
                p.sendMessage(StringDefaults.PREFIX + "§cDu besitzt zu wenig Geld.");
                p.playSound(p.getLocation(), Sound.FIZZ, 1.0F, 1.0F);

                return;
            }
            amount = buyItems(p, amount);

            if (amount == -1) {
                return;
            }
            price = getPrice() * amount;

            uc.removeMoney(price);
            p.sendMessage(
                    StringDefaults.PREFIX + "§aDu hast §3" + amount + "x §b" + getDisplayname() + " §afür §b" + Util.formatNumber(
                            price) + "$ §agekauft.");

            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
        } else {
            amount = sellItems(p, amount);

            if (amount == -1) {
                return;
            }
            price = getPrice() * amount;

            uc.addMoney(price);
            p.sendMessage(
                    StringDefaults.PREFIX + "§aDu hast §3" + amount + "x §b" + getDisplayname() + " §afür §b" + Util.formatNumber(
                            price) + "$ §averkauft.");
            p.playSound(p.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
        }
    }


    public Category getCategory() {
        return this.category;
    }


    public Material getMaterial() {
        return this.material;
    }


    public short getData() {
        return this.data;
    }


    public long getPrice() {
        return this.price;
    }


    public Type getType() {
        return this.type;
    }


    public String getDisplayname() {
        return this.displayname;
    }


    public String getInventoryname() {
        return this.inventoryname;
    }


    public static enum Category {

        MISC("Verschiedenes", true,
                new ItemBuilder(Material.ENDER_PEARL).setDisplayName("§eVerschiedenes").build()),
        BLOCKS("Blöcke", true,
                new ItemBuilder(Material.COBBLESTONE).setDisplayName("§eBlöcke").build()),
        TOOLS("Tools", true,
                new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName("§eTools").build()),
        DECORATION("Dekoration", true,
                new ItemBuilder(Material.RED_ROSE).setDisplayName("§eFarbstoffe").build()),
        BREWING("Baumaterial", true,
                new ItemBuilder(Material.POTION).setDisplayName("§eBrauzutaten").build()),
        REDSTONE("Redstone", true,
                new ItemBuilder(Material.REDSTONE).setDisplayName("§eRedstone").build()),
        FOOD("Essen", true,
                new ItemBuilder(Material.BAKED_POTATO).setDisplayName("§eEssen").build()),
        SELL("Verkaufbares", true,
                new ItemBuilder(Material.CHEST).setDisplayName("§eVerkaufbares").build()),
        ;

        private final String displayname;
        private final boolean itemShop;
        private final ItemStack displayItem;

        Category(String displayname, boolean itemShop, ItemStack displayItem) {
            this.displayname = displayname;
            this.itemShop = itemShop;
            this.displayItem = displayItem;
        }

        public static Category getByName(String name) {
            for (Category category : values()) {
                if (category.name().equalsIgnoreCase(name))
                    return category;
            }
            return null;
        }

        public static Category getByDisplayname(String displayname) {
            for (Category category : values()) {
                if (category.getDisplayname().equalsIgnoreCase(displayname))
                    return category;
            }
            return null;
        }

        public String getDisplayname() {
            return this.displayname;
        }

        public boolean isItemShop() {
            return this.itemShop;
        }

        public ItemStack getDisplayItem() {
            return this.displayItem;
        }
    }

    public static enum Type {
        BUY, SELL,
        ;

        public static Type getByName(String name) {
            for (Type type : values()) {
                if (type.name().equalsIgnoreCase(name))
                    return type;
            }
            return null;
        }

    }


}
