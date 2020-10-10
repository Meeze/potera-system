package de.potera.rysefoxx.manager;

import de.potera.teamhardcore.files.FileBase;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
public class EnderChestManager {

    private boolean accessible;
    private final HashMap<UUID, ItemStack[]> contents;
    private final FileBase fileBase;

    public EnderChestManager() {
        this.fileBase = new FileBase("", "enderchest");
        this.contents = new HashMap<>();
        this.accessible = false;
        this.load();

    }

    private void load() {
        if (this.fileBase.getConfig().getKeys(false).isEmpty()) return;
        for (String data : this.fileBase.getConfig().getKeys(false)) {
            ItemStack[] contents = new ItemStack[this.fileBase.getConfig().getList(data + ".enderchest").size()];
            int amount = 0;
            for (ItemStack itemStack : (List<ItemStack>) this.fileBase.getConfig().getList(data + ".enderchest")) {
                contents[amount] = itemStack;
                amount++;
            }
            this.contents.put(UUID.fromString(data), contents);
        }
        this.accessible = true;
    }

    public void onDisable() {
        for (Map.Entry<UUID, ItemStack[]> data : this.contents.entrySet()) {
            List<ItemStack> contents = new ArrayList<>(Arrays.asList(data.getValue()));
            this.fileBase.getConfig().set(data.getKey().toString(), contents);
        }
        this.fileBase.saveConfig();
    }


}
