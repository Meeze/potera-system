package de.potera.teamhardcore.others.mines;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.managers.MineManager;
import de.potera.teamhardcore.others.crates.BaseCrate;
import de.potera.teamhardcore.others.mines.enchantments.CustomEnchant;
import de.potera.teamhardcore.others.mines.enchantments.EnchantmentEffects;
import de.potera.teamhardcore.others.mines.enchantments.EnchantmentHandler;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserMine;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.TimeUtil;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MineEvents implements Listener {

    private static final Random RANDOM = new Random();
    private static final Set<Material> FORTUNE_BLOCKS = new HashSet<>(Arrays.asList(
            Material.COAL_ORE, Material.REDSTONE_ORE, Material.EMERALD_ORE, Material.DIAMOND_ORE, Material.LAPIS_ORE,
            Material.QUARTZ_ORE));

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        UserMine userMine = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserMine();

        if (item == null || item.getType() == Material.AIR) return;

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().equalsIgnoreCase(
                "§eZeit Gutschein")) {

            if (!item.getType().equals(Material.ENCHANTED_BOOK)) return;
            List<String> lore = item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : new ArrayList<>();
            if (lore.isEmpty() || lore.size() < 2) return;

            long rndmTime = MineManager.TIME_DROPS[RANDOM.nextInt(MineManager.TIME_DROPS.length)];

            event.setCancelled(true);
            Util.removeItems(player.getInventory(), player.getItemInHand(), 1);

            userMine.addAvailableTime(rndmTime);
            player.sendMessage(
                    StringDefaults.MINES_PREFIX + "§7Dir wurden §e" + TimeUtil.timeToString(rndmTime,
                            false) + " §7gutgeschrieben.");
            return;
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (Mine mine : Main.getInstance().getMinesManager().getMines()) {
            if (mine.isInside(event.getPlayer().getLocation()))
                event.getPlayer().teleport(mine.getSpawn());
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        for (Mine mine : Main.getInstance().getMinesManager().getMines()) {
            if (mine.isInside(event.getBlock().getLocation())) {
                if (!mine.isAccessable()) {
                    event.setCancelled(true);
                    player.sendMessage(StringDefaults.MINES_PREFIX + "§cDiese Mine ist zurzeit gesperrt.");
                    return;
                }

                UserMine userMine = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserMine();

                if (mine.getLevel() > userMine.getLevel()) {
                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§cDiese Mine ist erst ab §7Level " + mine.getLevel() + " §cverfügbar.");
                    return;
                }

                if (userMine.getAvailableTime() <= 0L) {
                    player.sendMessage(StringDefaults.MINES_PREFIX + "§cDeine verfügbare Zeit ist abgelaufen.");
                    player.sendMessage(StringDefaults.MINES_PREFIX + "§cBenutze /vote, für neue Zeit!");
                    return;
                }

                ItemStack itemStack = player.getItemInHand();

                int miningPower = 0;

                if (itemStack != null) {
                    if (EnchantmentHandler.hasCustomEnchant(itemStack, CustomEnchant.VEINMINER)) {
                        int power = EnchantmentHandler.getEnchantmentLevel(itemStack, CustomEnchant.VEINMINER);
                        Set<Location> veinLocations = EnchantmentEffects.getVeinBlocks(event.getBlock().getLocation(),
                                power);
                        veinLocations.stream().filter(mine::isInside).forEach(
                                location -> mineBlock(player, location.getBlock(), mine));
                    }

                    if (EnchantmentHandler.hasCustomEnchant(itemStack, CustomEnchant.EXPLOSION)) {
                        int power = EnchantmentHandler.getEnchantmentLevel(itemStack, CustomEnchant.EXPLOSION);
                        double[] tweaked = EnchantmentEffects.getTweakedExplosionSettings(power);

                        Set<Location> explosion = EnchantmentEffects.createExplosion(event.getBlock().getLocation(),
                                (int) tweaked[0], tweaked[1], tweaked[2]);

                        explosion.stream().filter(mine::isInside).forEach(location -> {
                            mineBlock(player, location.getBlock(), mine);
                        });
                    }

                    if (EnchantmentHandler.hasCustomEnchant(itemStack, CustomEnchant.LUCKYMINER)) {
                        miningPower = EnchantmentHandler.getEnchantmentLevel(itemStack, CustomEnchant.LUCKYMINER);
                    }
                }

                event.setCancelled(true);
                event.setExpToDrop(0);

                mineBlock(player, event.getBlock(), mine);
                giveCrateReward(player, miningPower);
                return;
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        for (Mine mine : Main.getInstance().getMinesManager().getMines()) {
            if (mine.isInside(event.getBlock().getLocation())) {
                event.setCancelled(true);
                return;
            }
        }
    }

    private void mineBlock(Player player, Block block, Mine mine) {
        if (!MineManager.MINE_BLOCKS.contains(block.getType())) return;

        UserMine userMine = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserMine();

        int drop = 1;
        if (player.getItemInHand() != null && player.getItemInHand().containsEnchantment(
                Enchantment.LOOT_BONUS_BLOCKS) && FORTUNE_BLOCKS.contains(block.getType())) {
            int power = player.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            drop = RANDOM.nextInt(power + 2) - power;
            if (drop < 0)
                drop = 0;
            drop++;
        }

        if (block.getType() == Material.GLOWING_REDSTONE_ORE)
            block.setType(Material.REDSTONE_ORE);

        MineBlock mineBlock = MineBlock.getByType(block.getType());
        if (mineBlock == null) return;

        float fullPrice = 0L;

        for (int i = 0; i < drop; i++)
            fullPrice = (fullPrice + mineBlock.getValue());

        userMine.setMinePoints(userMine.getMinePoints() + (long) fullPrice);
        Util.sendActionbarMessage(player, StringDefaults.MINES_PREFIX + "§a§l+ §2§l" + fullPrice + " MinePoints");

        block.setType(Material.AIR);
        mine.setBrokenBlocks(mine.getBrokenBlocks() + 1);
    }

    private void giveCrateReward(Player player, int miningLevel) {
        User user = Main.getInstance().getUserManager().getUser(player.getUniqueId());
        UserMine userMine = user.getUserMine();

        double chance = Main.getInstance().getMinesManager().getCrateDropChance(
                userMine.getLevel()) + (miningLevel * 3);
        boolean shouldReward = (RANDOM.nextDouble() * 100.0D <= chance);

        if (shouldReward) {
            BaseCrate crate = Main.getInstance().getCrateManager().getCrate("TestCrate");
            Util.addItem(player, crate.getAddon().getCrateItem());
            player.sendMessage(
                    StringDefaults.MINES_PREFIX + "§7Du hast eine " + crate.getAddon().getDisplayName() + " §7erhalten.");
        }

    }


}
