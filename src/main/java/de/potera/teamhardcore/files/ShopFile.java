package de.potera.teamhardcore.files;

import de.potera.teamhardcore.others.ShopItem;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class ShopFile extends FileBase {

    public ShopFile() {
        super("", "shop");
        writeDefaults();
    }

    private void writeDefaults() {
        FileConfiguration cfg = getConfig();
        for (ShopItem.Category category : ShopItem.Category.values())
            cfg.addDefault(category.name(), new ArrayList<>());
        cfg.options().copyDefaults(true);
        cfg.options().header(
                "Itemformat: ItemTyp (buy/sell) @ Materialname @ SubID @ Preis (pro Item) @ Anzeigename (MSG) @ Anzeigename (im Inventar)");
        cfg.options().copyHeader(true);
        saveConfig();
    }

}
