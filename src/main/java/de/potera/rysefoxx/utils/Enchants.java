package de.potera.rysefoxx.utils;

public enum Enchants {

    PROTECTION_ENVIRONMENTAL("PROTECTION_ENVIRONMENTAL", "Protection"),
    WATER_WORKER("WATER_WORKER", "Aqua Affinity"),
    THORNS("THORNS", "Thorns"),
    OXYGEN("OXYGEN", "Respiration"),
    DURABILITY("DURABILITY", "Unbreaking"),
    ARROW_DAMAGE("ARROW_DAMAGE", "Power"),
    ARROW_KNOCKBACK("ARROW_KNOCKBACK", "Punch"),
    ARROW_INFINITE("ARROW_KNOCKBACK", "Infinity"),
    ARROW_FIRE("ARROW_FIRE", "Flame"),
    DEPTH_STRIDER("DEPTH_STRIDER", "Depth Strider"),
    DAMAGE_ALL("DAMAGE_ALL", "Sharpness"),
    LURE("LURE", "Lure"),
    LUCK("LUCK", "Luck of the Sea"),
    SILK_TOUCH("SILK_TOUCH", "Silk Touch"),
    DIG_SPEED("DIG_SPEED", "Efficiency"),
    PROTECTION_FIRE("PROTECTION_FIRE", "Fire Protection"),
    PROTECTION_PROJECTILE("PROTECTION_PROJECTILE", "Projectile Protection"),
    FIRE_ASPECT("FIRE_ASPECT", "Fire Aspect"),
    KNOCKBACK("KNOCKBACK", "Knockback"),
    DAMAGE_ARTHROPODS("DAMAGE_ARTHROPODS", "Bane of Arthropods"),
    LOOT_BONUS_MOBS("LOOT_BONUS_MOBS", "Looting"),
    LOOT_BONUS_BLOCKS("LOOT_BONUS_BLOCKS", "Fortune"),
    DAMAGE_UNDEAD("DAMAGE_UNDEAD", "Smite"),
    PROTECTION_EXPLOSIONS("PROTECTION_EXPLOSIONS", "Blast Protection");

    String displayName;
    String goodName;

    Enchants(String displayName, String goodName) {
        this.displayName = displayName;
        this.goodName = goodName;
    }

    public static Enchants forName(String name) {
        for (Enchants enchants : values()) {
            if (enchants.displayName.equalsIgnoreCase(name)) {
                return enchants;
            }
        }
        return null;
    }

    public String getGoodName() {
        return goodName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
