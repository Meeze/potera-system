package de.potera.teamhardcore.events;

import de.potera.klysma.kits.Kit;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.commands.CommandHome;
import de.potera.teamhardcore.inventories.HomeInventory;
import de.potera.teamhardcore.inventories.PerkInventory;
import de.potera.teamhardcore.inventories.SettingsInventory;
import de.potera.teamhardcore.others.EnumPerk;
import de.potera.teamhardcore.others.EnumSettings;
import de.potera.teamhardcore.others.Home;
import de.potera.teamhardcore.others.ShopItem;
import de.potera.teamhardcore.others.ams.Ams;
import de.potera.teamhardcore.others.ams.AmsFriend;
import de.potera.teamhardcore.others.ams.AmsHandler;
import de.potera.teamhardcore.others.ams.upgrades.AmsUpgradeBase;
import de.potera.teamhardcore.others.ams.upgrades.EnumAmsUpgrade;
import de.potera.teamhardcore.others.gamble.Coinflip;
import de.potera.teamhardcore.others.gamble.jackpot.JackpotGame;
import de.potera.teamhardcore.others.gamble.roulette.RouletteEntry;
import de.potera.teamhardcore.others.gamble.roulette.RouletteGame;
import de.potera.teamhardcore.others.gamble.roulette.RouletteSetup;
import de.potera.teamhardcore.others.mines.enchantments.CustomEnchant;
import de.potera.teamhardcore.others.mines.enchantments.EnchantmentHandler;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.users.UserData;
import de.potera.teamhardcore.users.UserHomes;
import de.potera.teamhardcore.users.UserStats;
import de.potera.teamhardcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryClick implements Listener {

    public static Random RANDOM = new Random();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;

        Player player = (Player) event.getWhoClicked();
        ItemStack itemStack = event.getCurrentItem();
        Inventory inventory = event.getInventory();
        int slot = event.getRawSlot();

        if (event.getHotbarButton() != -1) {
            itemStack = event.getView().getBottomInventory().getItem(event.getHotbarButton());
            if (itemStack == null || itemStack.getType() == Material.AIR)
                itemStack = event.getCurrentItem();
        }

        if ((itemStack == null || itemStack.getType() == Material.AIR) && event.getCursor() != null && event.getCursor().getType() != Material.AIR)
            itemStack = event.getCursor();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        if (event.getClickedInventory() == null) return;

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
            if (event.getCursor() == null || event.getCursor().getType() == Material.AIR) {
                return;
            }

            ItemStack cursor = event.getCursor();

            if (cursor == null || cursor.getType() != Material.ENCHANTED_BOOK) {
                return;
            }


            if ((event.getCursor() != null && event.getCursor().getType() != Material.AIR && event.getCursor().getAmount() > 1) || (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().getAmount() > 1)) {
                return;
            }


            if (!EnchantmentHandler.isEnchantmentBook(cursor)) {
                return;
            }

            if (itemStack.getType() != Material.DIAMOND_PICKAXE) {
                return;
            }

            Object[] enchantment = EnchantmentHandler.enchantRandom(itemStack);

            if (enchantment.length == 0) {
                return;
            }

            CustomEnchant customEnchant = (CustomEnchant) enchantment[0];
            int power = (Integer) enchantment[1];

            player.sendMessage(
                    StringDefaults.MINES_PREFIX + "§7Die Spitzhacke wurde auf §e" + customEnchant.getEnchantName() + " " +
                            EnchantmentHandler.toRomanNumeral(power) + " §7geupgradet.");
            player.setItemOnCursor(new ItemStack(Material.AIR));
            player.updateInventory();

            event.setCancelled(true);
            return;
        }

        if (event.getView().getTopInventory().getType() == InventoryType.ANVIL) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && itemStack.getType().getMaxStackSize() <= 1 && itemStack.getAmount() > 1) {
                event.setCancelled(true);
                if (slot <= 2) {
                    player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items entnommen werden.");
                } else {
                    player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items reingelegt werden.");
                }
                return;
            }
            if ((slot == 0 || slot == 1) && itemStack.getType().getMaxStackSize() <= 1 && itemStack.getAmount() > 1) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() != Material.AIR) {
                    player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items entnommen werden.");
                } else {
                    player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items reingelegt werden.");
                }
                return;
            }
            if (slot == 2 && itemStack.getType().getMaxStackSize() <= 1 && itemStack.getAmount() > 1) {
                event.setCancelled(true);
                player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items entnommen werden.");
                return;
            }
        }

        if (event.getView().getTopInventory().getType() == InventoryType.ENCHANTING) {
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && itemStack.getType().getMaxStackSize() <= 1 && itemStack.getAmount() > 1) {
                if (slot == 0) {
                    event.setCancelled(true);
                    player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items entnommen werden.");
                }

                return;
            }
            if (event.getAction() == InventoryAction.HOTBAR_SWAP &&
                    itemStack.getType().getMaxStackSize() <= 1 && itemStack.getAmount() > 1) {
                event.setCancelled(true);
                if (event.getCurrentItem().getType() != Material.AIR) {
                    player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items entnommen werden.");
                } else {
                    player.sendMessage(StringDefaults.PREFIX + "§cEs dürfen nur ungestackte Items reingelegt werden.");
                }
                return;
            }
        }
        if (Main.getInstance().getGeneralManager().getPlayersInInvsee().contains(player) && !player.hasPermission(
                "potera.invsee.edit")) {
            event.setCancelled(true);

            return;
        }

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() && itemStack.getItemMeta().getDisplayName().equalsIgnoreCase(
                "§eVerzauberungs Buch")) {
            if (!EnchantmentHandler.isEnchantmentBook(itemStack)) {
                return;
            }


        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Bodysee")) {
            event.setCancelled(true);
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Reward")) {
            event.setCancelled(true);

            if (slot == 13) {
                UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();
                if (!userData.hasDailyReward()) {
                    player.sendMessage(
                            StringDefaults.REWARD_PREFIX + "§cDu hast die Belohnung heute breits abgeholt.");
                    return;
                }

                event.getView().close();
                userData.setDailyReward(false);
                giveRandomDailyReward(player);
            }

        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "PvP Shop")) {
            event.setCancelled(true);

            UserStats us = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserStats();

            if (slot == 20) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player,
                        new ItemBuilder(Material.BOOK).setDisplayName("§6Rangupgrade").setLore(
                                "§7Dieses Buch benötigst du,", "§7um deinen Rang aufzuwerten", "",
                                "§7[Linksklick] : §eRang aufwerten").build());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast ein §6Rangupgrade Buch §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 21) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player,
                        new ItemBuilder(Material.PAPER).setDisplayName("§6Perk Gutschein").setLore(
                                "§7Durch dieses Item, erhältst", "§7ein zufälliges Perk", "",
                                "§7[Linksklick] : §eGutschein verwenden").build());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast ein §6Perk Gutschein §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 22) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player,
                        Main.getInstance().getCrateManager().getCrate("TestCrate").getAddon().getCrateItem());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast eine §6Test Crate §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 23) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player,
                        Main.getInstance().getCrateManager().getCrate("TestCrate").getAddon().getCrateItem());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast eine §6Test Crate §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 24) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player,
                        Main.getInstance().getCrateManager().getCrate("TestCrate").getAddon().getCrateItem());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast eine §6Test Crate §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 29) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player, EnchantmentHandler.getMineTimeBook());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast ein §eZeit Gutschein §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 30) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player, EnchantmentHandler.getEnchantmentBook());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast ein §eVerzaubertes Buch §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 31) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player, AmsHandler.createBoostItem(50, "120m"));
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast ein §aAMS Boost §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 32) {
                if (us.getPvPCoins() < 250) {
                    player.sendMessage(
                            StringDefaults.PVP_PREFIX + "§cDir fehlen noch §7" + 250 + " PvP Coins §cdazu.");
                    return;
                }
                us.setPvPCoins(us.getPvPCoins() - 250);

                Util.addItem(player,
                        new ItemBuilder(Material.DIAMOND_SWORD).setLore("§7Zuletzt getötet§8: §cniemanden",
                                "§7Gesamte Kills§8: §e0 Kills").build());
                player.sendMessage(StringDefaults.PVP_PREFIX + "§7Du hast ein §cStatTrak Schwert §7erhalten.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Ranking")) {
            event.setCancelled(true);

            if (slot == 28 && itemStack.getType().equals(Material.DOUBLE_PLANT)) {
                Main.getInstance().getRankingManager().openRankingInventory(player, 1);
                return;
            }

            if (slot == 29 && itemStack.getType().equals(Material.FURNACE)) {
                Main.getInstance().getRankingManager().openRankingInventory(player, 2);
                return;
            }

            if (slot == 31 && itemStack.getType().equals(Material.WATCH)) {
                Main.getInstance().getRankingManager().openRankingInventory(player, 3);
                return;
            }

            if (slot == 33 && itemStack.getType().equals(Material.DIAMOND_SWORD)) {
                Main.getInstance().getRankingManager().openRankingInventory(player, 4);
                return;
            }

            if (slot == 34 && itemStack.getType().equals(Material.BANNER)) {
                Main.getInstance().getRankingManager().openRankingInventory(player, 5);
                return;
            }

            if (slot == 45 && itemStack.getType().equals(Material.WOOD_DOOR)) {
                Main.getInstance().getRankingManager().openRankingInventory(player, 0);
                return;
            }

        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Jackpot")) {
            event.setCancelled(true);

            if (slot == 13 && itemStack.getType().equals(Material.SIGN)) {
                JackpotGame game = Main.getInstance().getJackpotManager().getJackpotGame();

                if (game == null) {
                    event.getView().close();
                    return;
                }

                if (game.getGamePhase() != 0) {
                    Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(),
                            () -> game.openGUI(player, game.getGamePhase()), 1L);
                    return;
                }

                if (game.containsEntry(player.getUniqueId())) {
                    return;
                }

                new VirtualAnvil(player, "Einsatz: ") {
                    @Override
                    public void onConfirm(String text) {
                        if (text == null) {
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§cBitte gebe einen gültigen Wert an.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                            return;
                        }

                        long coins;

                        try {
                            coins = Long.parseLong(text.startsWith("Einsatz: ") ? text.substring(9) : text);
                            if (coins <= 0)
                                throw new NumberFormatException();
                        } catch (NumberFormatException ex) {
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§cBitte gebe einen gültigen Wert an.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                            return;
                        }

                        if (Main.getInstance().getJackpotManager().getJackpotGame() == null) {
                            event.getView().close();
                            return;
                        }

                        if (game.getGamePhase() != 0) {
                            event.getView().close();
                            return;
                        }

                        UserCurrency uc = Main.getInstance().getUserManager().getUser(player.getUniqueId())
                                .getUserCurrency();

                        if (uc.getMoney() < coins) {
                            player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cDu besitzt nicht genügend Münzen.");
                            return;
                        }

                        if (coins > game.getMaxBet()) {
                            player.sendMessage(
                                    StringDefaults.JACKPOT_PREFIX + "§cDer Betrag darf nicht höher als " + Util.formatNumber(
                                            game.getMaxBet()) + "$ sein.");
                            return;
                        }

                        setConfirmedSuccessfully(true);

                        uc.removeMoney(coins);
                        game.addEntry(player.getUniqueId(), coins);
                        player.sendMessage(
                                StringDefaults.JACKPOT_PREFIX + "§7Du bist dem Jackpot erfolgreich beigetreten.");

                        Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                                () -> game.openGUI(player, game.getGamePhase()), 1L);
                    }

                    @Override
                    public void onCancel() {
                        if (!isConfirmedSuccessfully()) {
                            if (Main.getInstance().getJackpotManager().getJackpotGame() == null) {
                                event.getView().close();
                                return;
                            }
                            Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(),
                                    () -> game.openGUI(player, game.getGamePhase()), 1L);
                        }
                    }
                };
                return;
            }

        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Coinflip")) {
            event.setCancelled(true);
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Coinflips")) {
            event.setCancelled(true);

            if (itemStack.getType().equals(Material.IRON_FENCE)) {
                Coinflip coinflip = Main.getInstance().getCoinflipManager().getCoinflipBySlot(slot);
                player.openInventory(coinflip.getInventory());
                return;
            }

            Coinflip coinflip = Main.getInstance().getCoinflipManager().getCoinflipBySlot(slot);

            if (coinflip.getEntries().get(0) == player) {
                player.sendMessage(StringDefaults.CF_PREFIX + "§cDu kannst deinem eigenen Coinflip nicht beitreten.");
                return;
            }

            UserCurrency userCurrency = Main.getInstance().getUserManager().getUser(
                    player.getUniqueId()).getUserCurrency();

            if (userCurrency.getMoney() < coinflip.getEntryPrice()) {
                player.sendMessage(
                        StringDefaults.CF_PREFIX + "§cDu hast zu wenige Münzen um diesen Coinflip beizutreten.");
                return;
            }

            Main.getInstance().getCoinflipManager().startCoinflip(coinflip, player);
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Kit Vorschau")) {
            event.setCancelled(true);

            if (slot == inventory.getSize() - 9) {
                player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 1.0F, 1.0F);
                return;
            }
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Kits")) {
            event.setCancelled(true);

            if (slot == 28) {
                if (event.isLeftClick()) {
                    Kit kit = Kit.getKit("member");

                    if (kit == null) {
                        player.sendMessage(
                                StringDefaults.PREFIX + "§cEin Fehler beim Laden des Kits ist aufgetreten...");
                        return;
                    }

                    boolean success = kit.giveKit(player, false);

                    if (success)
                        event.getView().close();
                    return;
                }

                if (event.isRightClick()) {
                    Kit kit = Kit.getKit("member");
                    kit.previewItems(player);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    return;
                }
            }

            if (slot == 30) {
                if (event.isLeftClick()) {
                    Kit kit = Kit.getKit("elite");

                    if (kit == null) {
                        player.sendMessage(
                                StringDefaults.PREFIX + "§cEin Fehler beim Laden des Kits ist aufgetreten...");
                        return;
                    }

                    boolean success = kit.giveKit(player, false);

                    if (success)
                        event.getView().close();
                    return;
                }

                if (event.isRightClick()) {
                    Kit kit = Kit.getKit("elite");
                    kit.previewItems(player);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    return;
                }
            }

            if (slot == 32) {
                if (event.isLeftClick()) {

                    Kit kit = Kit.getKit("hero");

                    if (kit == null) {
                        player.sendMessage(
                                StringDefaults.PREFIX + "§cEin Fehler beim Laden des Kits ist aufgetreten...");
                        return;
                    }

                    boolean success = kit.giveKit(player, false);

                    if (success)
                        event.getView().close();
                    return;
                }

                if (event.isRightClick()) {
                    Kit kit = Kit.getKit("hero");
                    kit.previewItems(player);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    return;
                }
            }

            if (slot == 34) {
                if (event.isLeftClick()) {
                    Kit kit = Kit.getKit("titan");

                    if (kit == null) {
                        player.sendMessage(
                                StringDefaults.PREFIX + "§cEin Fehler beim Laden des Kits ist aufgetreten...");
                        return;
                    }

                    boolean success = kit.giveKit(player, false);

                    if (success)
                        event.getView().close();
                    return;
                }

                if (event.isRightClick()) {
                    Kit kit = Kit.getKit("titan");
                    kit.previewItems(player);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    return;
                }
            }

        }

        if (inventory.getTitle().startsWith(StringDefaults.INVENTORY_PREFIX + "Crate-")) {
            event.setCancelled(true);
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "X-Roulette")) {
            event.setCancelled(true);
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Roulette Einsatz")) {
            event.setCancelled(true);

            if (Main.getInstance().getRouletteManager().getRouletteGame() == null) {
                event.getView().close();
                return;
            }

            if (!Main.getInstance().getRouletteManager().getBuildingRoulettes().containsKey(player)) {
                event.getView().close();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Main.getInstance().getRouletteManager().openGUI(player, false);
                    }
                }.runTaskLater(Main.getInstance(), 1L);
                return;
            }

            RouletteSetup setup = Main.getInstance().getRouletteManager().getBuildingRoulettes().get(player);

            if (slot == 21) {
                if (setup.getEntry() - RouletteSetup.ENTRY_VALUE < RouletteSetup.MIN_ENTRY) {
                    player.sendMessage(
                            StringDefaults.PREFIX + "§cDer Einsatz kann nicht niedriger als " + RouletteSetup.MIN_ENTRY + "$ betragen.");
                    return;
                }

                setup.setEntry(setup.getEntry() - RouletteSetup.ENTRY_VALUE);
                Main.getInstance().getRouletteManager().updateRouletteBuilder(player, inventory);
                return;
            }

            if (slot == 22) {
                new VirtualAnvil(player, "Einsatz: ") {
                    @Override
                    public void onConfirm(String text) {
                        if (text == null) {
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§cBitte gebe einen gültigen Wert an.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                            return;
                        }

                        long coins;

                        try {
                            coins = Long.parseLong(text.startsWith("Einsatz: ") ? text.substring(9) : text);
                            if (coins < RouletteSetup.MIN_ENTRY)
                                throw new NumberFormatException();
                        } catch (NumberFormatException ex) {
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§cBitte gebe einen gültigen Wert an.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                            return;
                        }

                        setup.setEntry(coins);

                        setConfirmedSuccessfully(true);
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                                () -> Main.getInstance().getRouletteManager().openGUI(player, true), 1L);
                    }

                    @Override
                    public void onCancel() {
                        if (!isConfirmedSuccessfully()) {
                            Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                                    () -> Main.getInstance().getRouletteManager().openGUI(player, false), 1L);
                        }
                    }
                };
                return;
            }

            if (slot == 23) {
                if (setup.getEntry() == RouletteSetup.MIN_ENTRY)
                    setup.setEntry(RouletteSetup.ENTRY_VALUE);
                else setup.setEntry(setup.getEntry() + RouletteSetup.ENTRY_VALUE);

                Main.getInstance().getRouletteManager().updateRouletteBuilder(player, inventory);
                return;
            }

            if (slot == 30) {
                if (setup.getMultiplier() - RouletteSetup.MULTIPLIER_VALUE < RouletteSetup.MIN_MULTIPLIER) {
                    player.sendMessage(
                            StringDefaults.PREFIX + "§cDer Multiplier kann nicht niedriger als x" + RouletteSetup.MIN_MULTIPLIER + " sein.");
                    return;
                }

                setup.setMultiplier(setup.getMultiplier() - RouletteSetup.MULTIPLIER_VALUE);
                Main.getInstance().getRouletteManager().updateRouletteBuilder(player, inventory);
                return;
            }

            if (slot == 31) {
                new VirtualAnvil(player, "Multiplier: ") {
                    @Override
                    public void onConfirm(String text) {
                        if (text == null) {
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§cBitte gebe einen gültigen Wert an.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                            return;
                        }

                        double multiplier;

                        try {
                            multiplier = Double.parseDouble(
                                    text.startsWith("Multiplier: ") ? text.substring(12) : text);
                            if (multiplier < RouletteSetup.MIN_MULTIPLIER || multiplier > RouletteSetup.MAX_MULTIPLIER)
                                throw new NumberFormatException();
                        } catch (NumberFormatException ex) {
                            player.sendMessage(StringDefaults.ROLL_PREFIX + "§cBitte gebe einen gültigen Wert an.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                            return;
                        }

                        setup.setMultiplier(multiplier);

                        setConfirmedSuccessfully(true);
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                                () -> Main.getInstance().getRouletteManager().openGUI(player, true), 1L);
                    }

                    @Override
                    public void onCancel() {
                        if (!isConfirmedSuccessfully()) {
                            Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                                    () -> Main.getInstance().getRouletteManager().openGUI(player, false), 1L);
                        }
                    }
                };
                return;
            }

            if (slot == 32) {
                if (setup.getMultiplier() + RouletteSetup.MULTIPLIER_VALUE > RouletteSetup.MAX_MULTIPLIER) {
                    player.sendMessage(
                            StringDefaults.PREFIX + "§cDer Multiplier kann nicht höher als x" + RouletteSetup.MAX_MULTIPLIER + " sein.");
                    return;
                }

                if (setup.getMultiplier() == RouletteSetup.MIN_MULTIPLIER)
                    setup.setMultiplier(1.5);
                else
                    setup.setMultiplier(setup.getMultiplier() + RouletteSetup.MULTIPLIER_VALUE);


                Main.getInstance().getRouletteManager().updateRouletteBuilder(player, inventory);
                return;
            }

            if (slot == 43) {
                RouletteGame rouletteGame = Main.getInstance().getRouletteManager().getRouletteGame();

                if (rouletteGame == null) {
                    event.getView().close();
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cAktuell läuft keine Roulette Runde.");
                    return;
                }

                if (rouletteGame.getPlayers().containsKey(player)) {
                    event.getView().close();
                    player.sendMessage(
                            StringDefaults.ROLL_PREFIX + "§cDu nimmst bereits an dieser Roulette Runde tei.");
                    return;
                }

                if (rouletteGame.getGamePhase() != 0) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cDu kannst dieser Runde nicht mehr beitreten.");
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> rouletteGame.openGUI(player, 0), 1L);
                    return;
                }

                UserCurrency uc = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();

                long toRemove = setup.getEntry();

                if (toRemove > uc.getMoney()) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cDu besitzt nicht soviele Münzen");
                    return;
                }

                uc.removeMoney(toRemove);

                RouletteEntry entry = new RouletteEntry(player.getUniqueId(), setup.getMultiplier(), setup.getEntry());
                player.sendMessage(StringDefaults.ROLL_PREFIX + "§7Du hast erfolgreich teilgenommen.");

                rouletteGame.getPlayers().put(player, entry);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> rouletteGame.openGUI(player, 0), 1L);
                return;
            }

        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Perks")) {
            event.setCancelled(true);

            UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();

            if (slot == 10) {
                if (!userData.getOwnedPerks().contains(EnumPerk.HASTE)) return;
                userData.togglePerk(EnumPerk.HASTE);
                PerkInventory.updatePerkInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                return;
            }
            if (slot == 11) {
                if (!userData.getOwnedPerks().contains(EnumPerk.NO_HUNGER)) return;
                userData.togglePerk(EnumPerk.NO_HUNGER);
                PerkInventory.updatePerkInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                return;
            }

            if (slot == 12) {
                if (!userData.getOwnedPerks().contains(EnumPerk.ANTI_FIRE)) return;
                userData.togglePerk(EnumPerk.ANTI_FIRE);
                PerkInventory.updatePerkInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                return;
            }

            if (slot == 14) {
                if (!userData.getOwnedPerks().contains(EnumPerk.SPEED)) return;
                userData.togglePerk(EnumPerk.SPEED);
                PerkInventory.updatePerkInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                return;
            }

            if (slot == 15) {
                if (!userData.getOwnedPerks().contains(EnumPerk.STRENGTH)) return;
                userData.togglePerk(EnumPerk.STRENGTH);
                PerkInventory.updatePerkInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                return;
            }

            if (slot == 16) {
                if (!userData.getOwnedPerks().contains(EnumPerk.NIGHT_VISION)) return;
                userData.togglePerk(EnumPerk.NIGHT_VISION);
                PerkInventory.updatePerkInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                return;
            }
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Homes")) {
            event.setCancelled(true);

            UserHomes userHomes = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserHomes();
            List<Home> homes = new ArrayList<>(userHomes.getHomes().values());

            if (itemStack.getType() == Material.STAINED_CLAY && itemStack.getDurability() == 5) {
                int index = HomeInventory.getHomeIndexBySlot(slot);
                if (slot == -1) return;

                Home home = homes.get(index);

                if (home == null) {
                    player.sendMessage(
                            StringDefaults.PREFIX + "§cEin Fehler ist aufgetreten, bitte kontaktiere einen Admin.");
                    return;
                }
                CommandHome.teleportToHome(player, player.getName(), home.getName(), userHomes);
            }
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "Warps")) {
            event.setCancelled(true);

            if (slot == 10) {
                player.performCommand("/warp fps");
                return;
            }

            if (slot == 12) {
                player.performCommand("/warp nether");
                return;
            }

            if (slot == 14) {
                player.performCommand("/warp casino");
                return;
            }

            if (slot == 15) {
                player.performCommand("/warp enchanter");
                return;
            }
        }

        if (inventory.getTitle().equalsIgnoreCase("Rüstung von")) event.setCancelled(true);

        if (inventory.getTitle().equals(StringDefaults.INVENTORY_PREFIX + "Settings")) {
            event.setCancelled(true);

            UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();

            if (slot == 11) {
                int currentOption = userData.getSettingsOption(EnumSettings.CRATE_ANIMATION);
                int newOption = (currentOption + 1 >= EnumSettings.CRATE_ANIMATION.getOptions().size() ? 0 : (currentOption + 1));
                userData.setSettingsOption(EnumSettings.CRATE_ANIMATION, newOption);
                SettingsInventory.updateSettingsInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 12) {
                int currentOption = userData.getSettingsOption(EnumSettings.DEATH_MSG);
                int newOption = (currentOption + 1 >= EnumSettings.DEATH_MSG.getOptions().size() ? 0 : (currentOption + 1));
                userData.setSettingsOption(EnumSettings.DEATH_MSG, newOption);
                SettingsInventory.updateSettingsInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 13) {
                int currentOption = userData.getSettingsOption(EnumSettings.TRADE_REQUESTS);
                int newOption = (currentOption + 1 >= EnumSettings.TRADE_REQUESTS.getOptions().size() ? 0 : (currentOption + 1));
                userData.setSettingsOption(EnumSettings.TRADE_REQUESTS, newOption);
                SettingsInventory.updateSettingsInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 14) {
                int currentOption = userData.getSettingsOption(EnumSettings.TP_REQUESTS);
                int newOption = (currentOption + 1 >= EnumSettings.TP_REQUESTS.getOptions().size() ? 0 : (currentOption + 1));
                userData.setSettingsOption(EnumSettings.TP_REQUESTS, newOption);
                SettingsInventory.updateSettingsInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 15) {
                int currentOption = userData.getSettingsOption(EnumSettings.PRIVATE_MESSAGE);
                int newOption = (currentOption + 1 >= EnumSettings.PRIVATE_MESSAGE.getOptions().size() ? 0 : (currentOption + 1));
                userData.setSettingsOption(EnumSettings.PRIVATE_MESSAGE, newOption);
                SettingsInventory.updateSettingsInventory(player, inventory);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }
        }

        if (inventory.getTitle().equals("§c§lShop")) {
            event.setCancelled(true);

            if (slot == 11) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.BLOCKS);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 13) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.MISC);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 15) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.REDSTONE);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 19) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.TOOLS);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 25) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.DECORATION);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 29) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.FOOD);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 31) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.SELL);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }

            if (slot == 33) {
                Main.getInstance().getShopManager().openCategoryInventory(player, ShopItem.Category.BREWING);
                player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
            }
        }

        if (inventory.getTitle().startsWith("§c§lShop (")) {
            event.setCancelled(true);

            String display = inventory.getTitle().substring(10, inventory.getTitle().length() - 1);
            ShopItem.Category category = ShopItem.Category.getByDisplayname(display);

            if (slot == inventory.getSize() - 9) {
                Main.getInstance().getShopManager().openMainInventory(player);
                player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 1.0F, 2.0F);
                return;
            }

            if (slot % 9 >= 2 && slot % 9 <= 8) {
                if (category == null)
                    return;

                List<ShopItem> shopItems = Main.getInstance().getShopManager().getShopItems().get(category);
                if (shopItems == null)
                    return;

                Material mat = itemStack.getType();
                short data = itemStack.getDurability();


                Optional<ShopItem> foundItem = shopItems.stream().filter(
                        shopItem -> (shopItem.getMaterial() == mat && shopItem.getData() == data)).findFirst();
                if (!foundItem.isPresent()) return;
                ShopItem shopItem = foundItem.get();
                int amount = event.isShiftClick() ? 64 : (event.isRightClick() ? 16 : 1);
                shopItem.handleBuy(player, amount);
            }

        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "AMS")) {
            event.setCancelled(true);

            if (!Main.getInstance().getAmsManager().getAmsGuiCache().containsKey(player)) {
                event.getView().close();
                player.sendMessage(StringDefaults.AMS_PREFIX + "§cEin Fehler ist aufgetreten.");
                return;
            }

            Ams ams = Main.getInstance().getAmsManager().getAmsGuiCache().get(player);

            if (!ams.hasPermission(player)) {
                event.getView().close();
                player.sendMessage(StringDefaults.AMS_PREFIX + "§cDu hast keine Berechtigung auf diese Ams.");
            }

            if (slot == 20) {
                if (ams.getCoins() < 50.0D) {
                    player.sendMessage(StringDefaults.AMS_PREFIX + "§cEs müssen mindestens 50$ vorhanden sein.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                    return;
                }

                long money = ams.getCoins();
                ams.setCoins(ams.getCoins() - money);

                UserCurrency userCurrency = Main.getInstance().getUserManager().getUser(
                        player.getUniqueId()).getUserCurrency();
                userCurrency.addMoney(money);

                player.sendMessage(
                        StringDefaults.AMS_PREFIX + "§7Du hast §e" + Util.formatNumber(money) + "$ §7abgehoben.");
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                return;
            }

            if (slot == 21) {
                if (event.isLeftClick()) {
                    int spawner = Util.getAvailableItems(player.getInventory(),
                            new ItemBuilder(Material.MOB_SPAWNER).build());

                    if (spawner == 0) {
                        player.sendMessage(StringDefaults.AMS_PREFIX + "§cDu besitzt keine Spawner im Inventar.");
                        return;
                    }

                    ams.setSpawner(ams.getSpawner() + spawner);
                    Util.removeItems(player.getInventory(), new ItemBuilder(Material.MOB_SPAWNER).build(), spawner);
                    player.sendMessage(StringDefaults.AMS_PREFIX + "§eDu hast §7" + Util.formatNumber(
                            spawner) + " Spawner §ehinzugefügt.");
                    return;
                }

                if (event.isRightClick()) {
                    if (ams.getSpawner() == 0) {
                        player.sendMessage(StringDefaults.AMS_PREFIX + "§cDie AMS enthält keine Spawner.");
                        return;
                    }

                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(StringDefaults.AMS_PREFIX + "§cDein Inventar ist voll.");
                        return;
                    }

                    int toRemove = (ams.getSpawner() > 16) ? 16 : (int) ams.getSpawner();
                    ams.setSpawner(ams.getSpawner() - toRemove);

                    Util.addItem(player, new ItemStack(Material.MOB_SPAWNER, toRemove));
                    player.sendMessage(StringDefaults.AMS_PREFIX + "§eDu hast §7" + Util.formatNumber(
                            toRemove) + " Spawner §eaus der AMS entnommen.");
                    player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0F, 1.0F);
                }

            }

            if (slot == 23) {
                Main.getInstance().getAmsManager().openGui(player, ams, 3);
                player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
            }

            if (slot == 24) {
                if (!ams.getOwner().equals(player.getUniqueId())) {
                    player.sendMessage(
                            StringDefaults.AMS_PREFIX + "§cDu hast keinen Zugriff auf die Freunde dieser AMS.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                    return;
                }

                Main.getInstance().getAmsManager().openGui(player, ams, 2);
                player.playSound(player.getLocation(), Sound.CLICK, 1.0F, 1.0F);
                return;
            }
        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "AMS Upgrades")) {
            event.setCancelled(true);

            if (!Main.getInstance().getAmsManager().getAmsGuiCache().containsKey(
                    player) || !Main.getInstance().getAmsManager().getAmsStateCache().containsKey(player)) {
                player.sendMessage(StringDefaults.AMS_PREFIX + "§cEs gab einen unerwarteten Fehler.");
                return;
            }

            Ams ams = Main.getInstance().getAmsManager().getAmsGuiCache().get(player);

            if (!ams.hasPermission(player)) {
                event.getView().close();
                player.sendMessage(StringDefaults.AMS_PREFIX + "§cDu hast keine Berechtigung auf diese Ams.");
            }

            if (slot == 10) {
                if (ams.getUpgradeAmount() >= 400) {
                    player.sendMessage(StringDefaults.AMS_PREFIX + "§cDu besitzt bereits alle Upgrades.");
                    return;
                }

                long price = Main.getInstance().getAmsManager().getUpgradePrice(ams);
                UserCurrency userCurrency = Main.getInstance().getUserManager().getUser(
                        player.getUniqueId()).getUserCurrency();

                if (userCurrency.getMoney() < price) {
                    player.sendMessage(
                            StringDefaults.AMS_PREFIX + "§cDu besitzt zu wenig Geld für das nächste Upgrade.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                    return;
                }

                List<EnumAmsUpgrade> possibleUpgrades = new ArrayList<>(Arrays.asList(EnumAmsUpgrade.values()));
                for (EnumAmsUpgrade upgrade : ams.getUpgrades().keySet()) {
                    AmsUpgradeBase base = ams.getUpgrades().get(upgrade);
                    if (base.getLevel() >= 100)
                        possibleUpgrades.remove(upgrade);
                }

                EnumAmsUpgrade randomUpgrade = possibleUpgrades.get(RANDOM.nextInt(possibleUpgrades.size()));

                AmsUpgradeBase newUpgrade = ams.getUpgrades().containsKey(randomUpgrade) ? randomUpgrade.create(ams,
                        ams.getUpgrades().get(randomUpgrade).getLevel() + 1) : randomUpgrade.create(ams, 1);
                ams.addUpgrade(newUpgrade);
                userCurrency.removeMoney(price);


                Main.getInstance().getAmsManager().updateGui(player, inventory, new int[]{6});
                player.sendMessage(
                        StringDefaults.AMS_PREFIX + "§7Du hast das Upgrade §e" + randomUpgrade.getDisplayName() + " §7auf Stufe §e" + newUpgrade.getLevel() + " §7erweitert.");
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                return;
            }

            if (slot == 11) {
                if (ams.getUpgradeAmount() < 400) {
                    player.sendMessage(
                            StringDefaults.AMS_PREFIX + "§cDu benötigst alle Upgrades um das Prestige zu erhöhen.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                    return;
                }

                if (ams.getPrestigeLevel() >= 10) {
                    player.sendMessage(
                            StringDefaults.AMS_PREFIX + "§cDein Prestigelevel ist bereits auf maximaler Stufe.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                    return;
                }

                ams.clearUpgrades();
                ams.setPrestigeLevel(ams.getPrestigeLevel() + 1);

                Main.getInstance().getAmsManager().updateGui(player, inventory, new int[]{6});
                player.sendMessage(
                        StringDefaults.AMS_PREFIX + "§7Du bist nun Prestigelevel §e" + ams.getPrestigeLevel() + "§7!");
                player.sendMessage(
                        StringDefaults.AMS_PREFIX + "§7Du erhältst einen dauerhaften §e10% Boost §7auf jedes Upgrade!");
            }

            if (slot == 18) {
                Main.getInstance().getAmsManager().openGui(player, ams, 1);
                player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 1.0F, 1.0F);
                return;
            }

        }

        if (inventory.getTitle().equalsIgnoreCase(StringDefaults.INVENTORY_PREFIX + "AMS Freunde")) {
            event.setCancelled(true);
            if (!Main.getInstance().getAmsManager().getAmsGuiCache().containsKey(
                    player) || !Main.getInstance().getAmsManager().getAmsStateCache().containsKey(player)) {
                player.sendMessage(StringDefaults.AMS_PREFIX + "§cEs gab einen unerwarteten Fehler.");
                return;
            }

            Ams ams = Main.getInstance().getAmsManager().getAmsGuiCache().get(player);

            if (!ams.getOwner().equals(player.getUniqueId())) {
                player.sendMessage(StringDefaults.AMS_PREFIX + "§cDu hast keinen Zugriff auf die Freunde dieser AMS.");
                Main.getInstance().getAmsManager().openGui(player, ams, 1);
                player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 1.0F, 1.0F);
                return;
            }

            if (!ams.hasPermission(player)) {
                event.getView().close();
                player.sendMessage(StringDefaults.AMS_PREFIX + "§cDu hast keine Berechtigung auf diese Ams.");
            }

            if (slot == 10) {
                if (ams.getFriends().size() >= 5) {
                    player.sendMessage(StringDefaults.AMS_PREFIX + "§cDie Freundesliste ist bereits voll.");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                    return;
                }
                Main.getInstance().getAmsManager().getAmsStateCache().put(player, new int[]{5});

                new VirtualAnvil(player, "Spieler: ") {
                    @Override
                    public void onConfirm(String text) {
                        if (text == null) {
                            player.sendMessage(StringDefaults.AMS_PREFIX + "§cBitte gebe einen gültigen Namen an.");
                            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.0F);
                            return;
                        }
                        String pName = text.startsWith("Spieler: ") ? text.substring(9) : text;
                        if (pName.length() > 16) {
                            player.sendMessage(StringDefaults.AMS_PREFIX + "§cBitte gebe einen gültigen Namen an.");
                            return;
                        }

                        Callback<OfflinePlayer> task = opTarget -> Bukkit.getScheduler().runTask(Main.getInstance(),
                                () -> {
                                    ams.addFriend(opTarget.getUniqueId());
                                    player.sendMessage(
                                            StringDefaults.AMS_PREFIX + "§e" + opTarget.getName() + " §7wurde als Freund zu deiner AMS hinzugefügt.");

                                    setConfirmedSuccessfully(true);
                                    event.getView().close();
                                    Bukkit.getScheduler().runTask(Main.getInstance(),
                                            () -> Main.getInstance().getAmsManager().openGui(player, ams, 2));
                                });

                        Player targetOnline = Bukkit.getPlayer(pName);

                        if (targetOnline == player) {
                            player.sendMessage(
                                    StringDefaults.AMS_PREFIX + "§cDu kannst dich nicht selbst als Freund hinzufügen.");
                            return;
                        }

                        if (targetOnline == null) {
                            UUIDFetcher.getUUID(pName, uuid -> {
                                if (uuid == null) {
                                    player.sendMessage(
                                            StringDefaults.AMS_PREFIX + "§cDer Spieler konnte nicht gefunden werden.");
                                    return;
                                }

                                OfflinePlayer opTarget = Bukkit.getOfflinePlayer(uuid);
                                if (!opTarget.hasPlayedBefore()) {
                                    player.sendMessage(
                                            StringDefaults.AMS_PREFIX + "§cDer Spieler war noch nie auf diesem Server.");
                                    return;
                                }

                                if (ams.getFriend(uuid) != null) {
                                    player.sendMessage(
                                            StringDefaults.AMS_PREFIX + "§cDu bist mit diesem Spieler bereits befreundet.");
                                    return;
                                }

                                task.accept(opTarget);
                            });
                        } else {
                            if (ams.getFriend(targetOnline.getUniqueId()) != null) {
                                player.sendMessage(
                                        StringDefaults.AMS_PREFIX + "§cDu bist mit diesem Spieler bereits befreundet.");
                                return;
                            }

                            task.accept(targetOnline);
                        }
                    }

                    @Override
                    public void onCancel() {
                        if (!isConfirmedSuccessfully())
                            Bukkit.getScheduler().runTask(Main.getInstance(),
                                    () -> Main.getInstance().getAmsManager().openGui(player, ams, 2));
                    }
                };
                return;
            }

            if (slot >= 12 & slot <= 16) {
                int index = slot - 12;

                if (ams.getFriends().size() <= index) return;
                AmsFriend friend = ams.getFriends().get(index);

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(friend.getUuid());
                ams.removeFriend(friend.getUuid());
                Main.getInstance().getAmsManager().updateGui(player, inventory, new int[]{3});

                player.sendMessage(
                        StringDefaults.AMS_PREFIX + "§e" + offlinePlayer.getName() + " §7wurde aus der AMS entfernt.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                return;
            }

            if (slot == 18) {
                Main.getInstance().getAmsManager().openGui(player, ams, 1);
                player.playSound(player.getLocation(), Sound.DOOR_CLOSE, 1.0F, 1.0F);
            }

        }
    }

    private void giveRandomDailyReward(Player player) {

        for (int i = 0; i < 128; i++) {
            Util.addItem(player, Main.getInstance().getCrateManager().getCrate("TestCrate").getAddon().getCrateItem());
        }

        player.sendMessage(StringDefaults.REWARD_PREFIX + "§7Du hast deine tägliche Belohnung abgeholt.");
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
    }

}
