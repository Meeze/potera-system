package de.potera.teamhardcore.others.crates.addons;

import de.potera.teamhardcore.others.crates.ContentPiece;
import de.potera.teamhardcore.others.crates.CrateAddon;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.SkullCreator;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TestCrate extends CrateAddon {

    public TestCrate() {
        super("test", "§a§lTest Crate");

        setDisplayItem(SkullCreator.itemFromBase64(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTJkZDdkODE4Y2JhNjUyYjAxY2YzNTBkMmIyYTFjZWVkZDRmNDZhY2FkMDViMmNlODFjM2Y4NzdlYWI3MTcifX19"));


        addContent(new ContentPiece(100, new ItemBuilder(Material.STONE).setDisplayName("§e1x Stein").build()) {
            @Override
            public void onWin(Player player) {
                Util.addItem(player, new ItemStack(Material.STONE));
                player.sendMessage(StringDefaults.PREFIX + "§7Du hast §e1x Stein §7erhalten.");
            }
        });

        addContent(new ContentPiece(100, new ItemBuilder(Material.PAPER).setDisplayName("§e1x Papier").build()) {
            @Override
            public void onWin(Player player) {
                Util.addItem(player, new ItemStack(Material.PAPER));
                player.sendMessage(StringDefaults.PREFIX + "§7Du hast §e1x Papier §7erhalten.");
            }
        });
    }
}
