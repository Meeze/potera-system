package de.potera.teamhardcore.others;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public enum EnumPerk {

    HASTE("Eile", "Eile Perk", "Erhalte dauerhaft Eile II", PotionEffectType.FAST_DIGGING, 1,
            (short) 0, Material.GOLD_PICKAXE),
    NO_HUNGER("Kein Hunger", "Kein Hunger Perk", "Verliere dauerhaft keinen Hunger", null, 0, (short) 0,
            Material.GOLDEN_CARROT),
    ANTI_FIRE("Feuerresistenz", "Feuerresistenz Perk", "Erhalte dauerhaft Feuerresistenz",
            PotionEffectType.FIRE_RESISTANCE, 0, (short) 0, Material.MAGMA_CREAM),
    SPEED("Speed", "Speed Perk", "Erhalte dauerhaft Speed II", PotionEffectType.SPEED, 1, (short) 0, Material.SUGAR),
    STRENGTH("Stärke", "Stärke Perk", "Erhalte dauerhaft Stärke II", PotionEffectType.INCREASE_DAMAGE, 1, (short) 0,
            Material.DIAMOND_SWORD),
    NIGHT_VISION("Nachtsicht", "Nachtsicht Perk", "Erhalte dauerhaft Nachtsicht", PotionEffectType.NIGHT_VISION, 0,
            (short) 8262, Material.POTION),
    ;

    private final String name;
    private final String displayName;
    private final String description;
    private final PotionEffectType type;
    private final int amplifier;
    private final short durability;
    private final Material material;

    EnumPerk(String name, String displayName, String description, PotionEffectType type, int amplifier, short durability, Material material) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.type = type;
        this.amplifier = amplifier;
        this.durability = durability;
        this.material = material;
    }

    public static EnumPerk getByName(String name) {
        for (EnumPerk perk : values()) {
            if (perk.name().equalsIgnoreCase(name))
                return perk;
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public PotionEffectType getType() {
        return type;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public short getDurability() {
        return durability;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isPotionEffect() {
        return (this.type != null);
    }
}
