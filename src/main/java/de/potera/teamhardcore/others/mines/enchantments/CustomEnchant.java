package de.potera.teamhardcore.others.mines.enchantments;

public enum CustomEnchant {

    EXPLOSION("Explosion", 4),
    VEINMINER("Veinmining", 4),
    LUCKYMINER("Luckymining", 3),
    ;

    private String enchantName;
    private int maxLevel;

    CustomEnchant(String enchantName, int maxLevel) {
        this.enchantName = enchantName;
        this.maxLevel = maxLevel;
    }


    public String getEnchantName() {
        return this.enchantName;
    }


    public int getMaxLevel() {
        return this.maxLevel;
    }

}
