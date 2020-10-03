package de.potera.rysefoxx.bossegg;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter

public class BossEggSerializer implements ConfigurationSerializable {

    private double chance;
    private ItemStack itemStack;
    private String id;
    private String displayName;
    private int amount;

    public BossEggSerializer(double chance, String displayName, ItemStack itemStack) {
        this.chance = chance;
        this.displayName = displayName;
        this.itemStack = itemStack;
        this.id = UUID.randomUUID().toString().substring(0, 5);
        this.amount = 0;
    }

    public BossEggSerializer(Map<String, Object> stringObjectMap) {
        this.chance = (double) stringObjectMap.get("chance");
        this.itemStack = (ItemStack) stringObjectMap.get("itemStack");
        this.id = (String) stringObjectMap.get("id");
        this.displayName = (String) stringObjectMap.get("displayName");
        this.amount = (int) stringObjectMap.get("amount");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("chance", this.chance);
        stringObjectMap.put("itemStack", this.itemStack);
        stringObjectMap.put("id", this.id);
        stringObjectMap.put("displayName", this.displayName);
        stringObjectMap.put("amount", this.amount);
        return stringObjectMap;
    }
}
