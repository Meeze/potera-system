package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CommandBodysee implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.bodysee")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/bodysee <Spieler>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(StringDefaults.NOT_ONLINE);
            return true;
        }

        if (target == player) {
            player.sendMessage(StringDefaults.PREFIX + "§cÖffne dein Inventar um deine Rüstung zu sehen.");
            return true;
        }

        Inventory inventory = Bukkit.createInventory(null, 9, StringDefaults.INVENTORY_PREFIX + "Bodysee");
        player.openInventory(inventory);

        (new BukkitRunnable() {

            public void run() {
                if (player.isDead() || !player.isOnline()) {
                    cancel();

                    return;
                }
                if (!player.getOpenInventory().getTopInventory().equals(inventory)) {
                    cancel();

                    return;
                }
                if (target.isDead() || !target.isOnline()) {
                    if (player.getOpenInventory().getTopInventory().equals(inventory))
                        player.closeInventory();
                    cancel();

                    return;
                }
                ItemStack[] armorContents = target.getInventory().getArmorContents().clone();

                for (int i = 0; i < 4; i++) {
                    inventory.setItem(i, new ItemStack(Material.AIR));
                }

                for (int i = armorContents.length - 1; i >= 0; i--) {
                    ItemStack armor = armorContents[i];
                    if (armor != null && armor.getType() != Material.AIR) {
                        inventory.setItem(inventory.firstEmpty(), armor);
                    }
                }
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack invItem = inventory.getItem(i);
                    if (invItem == null || invItem.getType() == Material.AIR) {
                        inventory.setItem(i,
                                new ItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).setDisplayName(
                                        " ").build());
                    }
                }

            }
        }).runTaskTimer(Main.getInstance(), 0L, 2L);

        return true;
    }
}
