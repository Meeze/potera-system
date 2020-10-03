package de.potera.teamhardcore.others.ams;

import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.TimeUtil;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AmsHandler {

    public static ItemStack createBoostItem(double boost, long boostTime) {
        if (boost <= 0.0D) return null;
        if (boostTime <= 0L) return null;

        List<String> lore = new ArrayList<>();
        lore.add("§7Dieses Item fügt deiner AMS");
        lore.add("§7einen Boost in Höhe von §e" + boost + "%");
        lore.add("§7für §e" + TimeUtil.timeToString(boostTime, false) + " §7hinzu.");
        lore.add("");
        lore.add("§7[Linksklick] : §eBoost aktivieren");

        ItemStack itemStack = new ItemBuilder(Material.EXP_BOTTLE).setDisplayName("§aAMS Boost").setLore(lore).build();

        NBTItem item = new NBTItem(itemStack);
        item.setDouble("boost", boost);
        item.setLong("time", boostTime);
        itemStack = item.getItem();
        return itemStack;
    }

    public static ItemStack createBoostItem(double boost, String boostTime) {
        return createBoostItem(boost, TimeUtil.parseTime(boostTime));
    }

}
