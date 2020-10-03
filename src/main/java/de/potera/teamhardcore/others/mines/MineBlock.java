package de.potera.teamhardcore.others.mines;

import org.bukkit.Material;

public enum MineBlock {

    STONE(Material.STONE, "Stein", 1.6F),
    COBBLESTONE(Material.COBBLESTONE, "Bruchstein", 1.0F),
    COAL_ORE(Material.COAL_ORE, "Kohleerz", 2.4F),
    IRON_ORE(Material.IRON_ORE, "Eisenerz", 3.7F),
    GOLD_ORE(Material.GOLD_ORE, "Golderz", 13.9F),
    REDSTONE_ORE(Material.REDSTONE_ORE, "Redstoneerz", 33.3F),
    LAPIS_ORE(Material.LAPIS_ORE, "Lapiserz", 80.0F),
    DIAMOND_ORE(Material.DIAMOND_ORE, "Diamanterz", 192.3F),
    EMERALD_ORE(Material.EMERALD_ORE, "Smaragderz", 462.0F),
    QUARTZ_ORE(Material.QUARTZ_ORE, "Quartzerz", 2666.7F),
    ENDSTONE(Material.ENDER_STONE, "Endstein", 1110.0F),
    REDSTONE_BLOCK(Material.REDSTONE_BLOCK, "Redstoneblock", 51.6F),
    GOLD_BLOCK(Material.GOLD_BLOCK, "Goldblock", 21.5F),
    IRON_BLOCK(Material.IRON_BLOCK, "Eisenblock", 8.9F),
    LAPIS_BLOCK(Material.LAPIS_BLOCK, "Lapisblock", 124.1F),
    COAL_BLOCK(Material.COAL_BLOCK, "Kohleblock", 5.8F),
    OBSIDIAN(Material.OBSIDIAN, "Obsidian", 1720.4F),
    EMERALD_BLOCK(Material.EMERALD_BLOCK, "Smaragdblock", 10.0F),
    DIAMOND_BLOCK(Material.DIAMOND_BLOCK, "Diamantblock", 298.1F);

    private final Material type;
    private final String displayname;
    private final float value;

    MineBlock(Material type, String displayname, float value) {
        this.type = type;
        this.displayname = displayname;
        this.value = value;
    }

    public static MineBlock getByType(Material type) {
        for (MineBlock mineBlock : values()) {
            if (mineBlock.getType() == type)
                return mineBlock;
        }
        return null;
    }

    public Material getType() {
        return this.type;
    }

    public String getDisplayname() {
        return this.displayname;
    }

    public float getValue() {
        return this.value;
    }

}
