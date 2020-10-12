package de.potera.rysefoxx.bossegg;

import de.potera.rysefoxx.menubuilder.manager.InventoryMenuBuilder;
import de.potera.rysefoxx.utils.AnvilGUI;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.Arrays;

public class BossEggCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("potera.bossegg")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        /*

        /BossEgg <Create> <EggName>
        /BossEgg <Delete> <EggName>
        /BossEgg List           // ALLE MÖGLICHEN BOSSEGGS AUFLISTEN
        /BossEgg setMaxHealth <EggName> <Health>
        /BossEgg addItem <EggName> <Prozent> <Broadcast>
        /BossEgg withBroadcast <EggName> <Boolean>
        /BossEgg items <EggName>
        /BossEgg get <BossEgg>
        /BossEgg setType <Bossegg> <EntityType>
        /BossEgg setHelmet <BossEgg>
        /BossEgg setChestPlate <BossEgg>
        /BossEgg setLeggings <BossEgg>
        /BossEgg setBoots <BossEgg>
        /BossEgg setWeapon <BossEgg>
        /BossEgg setHolo <BossEgg> <String>
        /BossEgg setMinDropAmount <Bossegg> <Amount>
        /BossEgg setMaxDropAmount <Bossegg> <Amount>
        /BossEgg setAnnouncement <BossEgg> <Boolean>
        /BossEgg setCollection <BossEgg> <Integer>
        /Bossegg Settings <BossEgg>

        /BossEgg spawn <Bossegg> <X> <Y> <Z>

         */

        if (!Main.getPlugin(Main.class).getBossEggManager().isAccessible()) {
            player.sendMessage(StringDefaults.PREFIX + "§cBitte warte bis alle BossEggs geladen wurden.");
            return true;
        }
        if (args.length >= 3 && args[0].equalsIgnoreCase("setHolo")) {
            if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                return true;
            }
            BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
            bossEgg.setHoloText(ChatColor.translateAlternateColorCodes('&', Util.messageBuilder(2, args)));
            bossEgg.save();
            player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das Hologram für §c" + args[1] + " §7geupdatet.");
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setMinDropAmount")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (!Util.isInt(args[2])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte gib eine valide Zahl ein!");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.setMinDropAmount(Integer.parseInt(args[2]));
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Es dropppen nun mindestens §c" + args[2] + " Items");
            } else if (args[0].equalsIgnoreCase("setMaxDropAmount")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (!Util.isInt(args[2])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte gib eine valide Zahl ein!");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.setMaxDropAmount(Integer.parseInt(args[2]));
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Es dropppen nun maximal §c" + args[2] + " Items");

            } else if (args[0].equalsIgnoreCase("addItem")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte halte ein Item in der Hand.");
                    return true;
                }
                if (!Util.isDouble(args[2])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte gebe eine gültige Prozentzahl an.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                BossEggSerializer bossEggSerializer = new BossEggSerializer(Double.parseDouble(args[2]), Util.getCustomName(player.getItemInHand()), player.getItemInHand());

                bossEgg.getItems().add(bossEggSerializer);
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast erfolgreich das Item in deiner Hand zum BossEgg §c" + args[1] + " §7hinzugefügt.");
            } else if (args[0].equalsIgnoreCase("setType")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (EntityType.fromName(args[2]) == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Ungültiges Entity.");
                    return true;
                }
                EntityType entityType = EntityType.fromName(args[2]);
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.setEntityType(entityType);
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§c" + args[1] + " §7erscheint nun als §c" + entityType.getName());
            } else if (args[0].equalsIgnoreCase("setCollection")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                int collection;
                try {
                    collection = Integer.parseInt(args[2]);
                } catch (NumberFormatException exception) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte gib eine valide Zahl ein!");
                    return true;
                }
                bossEgg.setCollection(collection);
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§c" + args[1] + " §7ist nun aus der §c" + args[2] + " Kollektion.");
            } else if (args[0].equalsIgnoreCase("setAnnouncement")) {

                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                boolean broadcast;
                if (args[2].equalsIgnoreCase("true")) {
                    broadcast = true;
                } else if (args[2].equalsIgnoreCase("false")) {
                    broadcast = false;
                } else {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte gebe einen richtigen Boolean an. (True,False)");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.setBroadcastOnSpawn(broadcast);
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + (broadcast ? "§7Im Chat wird nun angezeigt wo der Boss sich befindet" : "§7Im Chat wird nicht mehr angezeigt wo der Boss ist."));
            } else if (args[0].equalsIgnoreCase("withBroadcast")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                boolean broadcast;
                if (args[2].equalsIgnoreCase("true")) {
                    broadcast = true;
                } else if (args[2].equalsIgnoreCase("false")) {
                    broadcast = false;
                } else {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte gebe einen richtigen Boolean an. (True,False)");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.setBroadcast(broadcast);
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Die Items die droppen werden " + (broadcast ? "§cnun an alle Spieler gesendet" : "§cnun nicht mehr an alle Spieler gesendet") + ".");

            } else if (args[0].equalsIgnoreCase("setMaxHealth")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (!Util.isInt(args[2])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Gebe eine gültige Zahl an.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.setMaxHealth(Integer.parseInt(args[2]));
                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§c" + args[1] + " §7startet nun mit §c" + Util.formatBigNumber(bossEgg.getMaxHealth()) + " Herzen");
            }
        } else if (args.length == 5) {
            if (args[0].equalsIgnoreCase("spawn")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                int x;
                int y;
                int z;
                try {
                    x = Integer.parseInt(args[2]);
                    y = Integer.parseInt(args[3]);
                    z = Integer.parseInt(args[4]);
                } catch (Exception exception) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Gib eine gültige Koordinate an. (Ganze Zahl)");
                    return true;
                }

                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.spawn(player, x, y, z);
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("settings")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 4).withTitle("§7BossEgg Einstellungen");
                Util.fill(inventoryMenuBuilder.getInventory(), 9*4);

                inventoryMenuBuilder.withItem(10, new ItemBuilder(Material.BARRIER).setDisplayName("§7Einstellung§8: §cHologram").setLore(Arrays.asList(
                        "§7Stelle ein, ob dieses BossEgg ein",
                        "§7Hologram am Ende anzeigen soll!",
                        "",
                        (bossEgg.isCanSpawnHologram()) ? "§a§lEs wird ein Hologram kurz nach dem sterben erstellt." : "§c§lEs wird kein Hologram erstellt.",
                        "",
                        "§7Klick §8- §eWechseln")).build(), (player12, action, item) -> {
                    bossEgg.setCanSpawnHologram(!bossEgg.isCanSpawnHologram());
                    bossEgg.save();
                    player12.chat("/bossegg settings " + args[1]);
                }, InventoryMenuBuilder.ALL_CLICK_TYPES);
                inventoryMenuBuilder.withItem(12, new ItemBuilder(Material.PAPER).setDisplayName("§7Einstellung§8: §cNachricht beim Spawnen").setLore(Arrays.asList(
                        "§7Stelle ein, ob dieses BossEgg",
                        "§7eine Nachricht an alle Spieler ausgeben soll,",
                        "§7wenn es gespawnt wird.",
                        "",
                        (bossEgg.isBroadcastOnSpawn() ? "§a§lEs wird eine Nachricht an alle Spieler gesendet." : "§c§lEs wird keine Nachricht an alle Spieler gesendet."),
                        "",
                        "§7Linksklick §8- §eWechseln",
                        "§7Rechtsklick §8- §eVorschau: Nachricht")).build(), (player13, action, item) -> {
                    if (action == ClickType.LEFT) {
                        bossEgg.setBroadcastOnSpawn(!bossEgg.isBroadcastOnSpawn());
                        bossEgg.save();
                        player13.chat("/bossegg settings " + args[1]);
                    } else if (action == ClickType.RIGHT) {
                        player13.closeInventory();
                        player13.sendMessage("§6§l§k---------------------------");
                        player13.sendMessage("");
                        player13.sendMessage("§c" + player13.getName() + " §7hat ein Spawner-Ei aus der §c" + bossEgg.getCollection() + " Kollektion benutzt.");
                        player13.sendMessage("");
                        player13.sendMessage("§7Boss Name§8: §c" + bossEgg.getDisplayName());
                        player13.sendMessage("§7Boss Leben§8: §c" + bossEgg.getMaxHealth() + "❤");
                        player13.sendMessage("§7X§8: §c" + player13.getLocation().getBlockX());
                        player13.sendMessage("§7Y§8: §c" + player13.getLocation().getBlockY());
                        player13.sendMessage("§7Z§8: §c" + player13.getLocation().getBlockZ());
                        player13.sendMessage("");
                        player13.sendMessage("§6§l§k---------------------------");
                    }
                }, ClickType.LEFT, ClickType.RIGHT);

                inventoryMenuBuilder.withItem(14, new ItemBuilder(Material.PAPER).setDisplayName("§7Einstellung§8: §cNachricht beim Töten").setLore(Arrays.asList(
                        "§7Stelle ein, ob dieses BossEgg",
                        "§7eine Nachricht an alle Spieler ausgeben soll,",
                        "§7wie viel Loot er gedroppt hat, wo er gestorben ist",
                        "§7und wie sein Boss Name ist.",
                        "",
                        (bossEgg.isBroadcast() ? "§a§lEs werden alle Informationen beim Tod an die Spieler gesendet." : "§c§lEs werden keine Informationen an Spielern gesendet."),
                        "",
                        "§7Linksklick §8- §eWechseln",
                        "§7Rechtsklick §8- §eVorschau: Nachricht")).build(), (player14, action, item) -> {
                    if (action == ClickType.LEFT) {
                        bossEgg.setBroadcast(!bossEgg.isBroadcast());
                        bossEgg.save();
                        player14.chat("/bossegg settings " + args[1]);
                    } else if (action == ClickType.RIGHT) {
                        player14.closeInventory();
                        player14.sendMessage("§6§l§k---------------------------");
                        player14.sendMessage("");
                        player14.sendMessage("§c§l" + player14.getName() + " §7hat den Boss §c§l" + bossEgg.getDisplayName() + " §7getötet!");
                        player14.sendMessage("");
                        player14.sendMessage("§7Der Boss hat §c§l5 Items §7gedroppt.");
                        player14.sendMessage("§7Der Boss ist an den Folgenden Koordinaten gestorben");
                        player14.sendMessage("§cX§8: §6187 §cY§8: §6187 §cZ§8: §6187");
                        player14.sendMessage("");
                        player14.sendMessage("§6§l§k---------------------------");
                    }
                }, ClickType.LEFT, ClickType.RIGHT);

                inventoryMenuBuilder.withItem(16, new ItemBuilder(Material.PAPER).setDisplayName("§7Einstellung§8: §cKann Fähigkeiten benutzen").setLore(Arrays.asList(
                        "§7Stelle ein, ob dieses BossEgg",
                        "§7zufällige Fähigkeiten ausführen kann.",
                        "",
                        (bossEgg.isCanUseAbilities() ? "§a§lEs werden zufällige Fähigkeiten ausgeführt." : "§c§lEs werden keine zufälligen Fähigkeiten ausgeführt."),
                        "",
                        "§7Klick §8- §eWechseln")).build(), (player15, action, item) -> {
                    bossEgg.setCanUseAbilities(!bossEgg.isCanUseAbilities());
                    bossEgg.save();
                    player15.chat("/bossegg settings " + args[1]);
                }, InventoryMenuBuilder.ALL_CLICK_TYPES);

                inventoryMenuBuilder.withItem(19, new ItemBuilder(Material.PAPER).setDisplayName("§7Einstellung§8 §cFähigkeitschance").setLore(Arrays.asList(
                        "§7Stelle ein, ob dieses BossEgg",
                        "§7eine hohe Chance hat, um zufällige",
                        "§7Fähigkeiten auszuführen oder nicht.",
                        "",
                        "§7Derzeitige Chance§8: §c§l" + bossEgg.getAbilityChance() + "%",
                        "",
                        "§7Klick §8- §eChance verändern")).build(), (player16, action, item) -> {
                    AnvilGUI anvilGUI = new AnvilGUI(player16, event -> {
                        if (event.getSlot() != AnvilGUI.AnvilSlot.OUTPUT) return;
                        String result = event.getName().replace("Prozentzahl", "");
                        if (!Util.isDouble(result)) {
                            player16.sendMessage(StringDefaults.PREFIX+"§7Bitte gebe eine gültige Zahl ein.");
                            return;
                        }
                        bossEgg.setAbilityChance(Double.parseDouble(result));
                        bossEgg.save();
                    });
                    anvilGUI.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, new ItemBuilder(Material.DOUBLE_PLANT).setDisplayName("Prozentzahl").build());
                    anvilGUI.open();


                }, InventoryMenuBuilder.ALL_CLICK_TYPES);

                inventoryMenuBuilder.show(player);

            } else if (args[0].equalsIgnoreCase("setHelmet")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (player.getItemInHand() == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte halte ein Item in der Hand.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                Main.getPlugin(Main.class).getBossEggManager().updateHelmet(bossEgg, player.getItemInHand());
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das Rüstungsteil §cHELM §7erfolgreich verändert.");
            } else if (args[0].equalsIgnoreCase("setChestPlate")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (player.getItemInHand() == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte halte ein Item in der Hand.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                Main.getPlugin(Main.class).getBossEggManager().updateChestPlate(bossEgg, player.getItemInHand());
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das Rüstungsteil §cBRUST §7erfolgreich verändert.");
            } else if (args[0].equalsIgnoreCase("setLeggings")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (player.getItemInHand() == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte halte ein Item in der Hand.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                Main.getPlugin(Main.class).getBossEggManager().updateLeggings(bossEgg, player.getItemInHand());
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das Rüstungsteil §cHOSE §7erfolgreich verändert.");
            } else if (args[0].equalsIgnoreCase("setBoots")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (player.getItemInHand() == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte halte ein Item in der Hand.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                Main.getPlugin(Main.class).getBossEggManager().updateBoots(bossEgg, player.getItemInHand());
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das Rüstungsteil §cSCHUHE §7erfolgreich verändert.");
            } else if (args[0].equalsIgnoreCase("setWeapon")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                if (player.getItemInHand() == null) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Bitte halte ein Item in der Hand.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                Main.getPlugin(Main.class).getBossEggManager().updateWeapon(bossEgg, player.getItemInHand());
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast die Waffe erfolgreich verändert.");
            } else if (args[0].equalsIgnoreCase("get")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                player.getInventory().addItem(bossEgg.getItemStack().clone());
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das BossEgg für den Boss §c" + args[1] + " §7erhalten");
            } else if (args[0].equalsIgnoreCase("items")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 6).withTitle("§7Alle Drops vom BossEgg");

                int index = 0;
                for (BossEggSerializer bossEggSerializer : bossEgg.getItems()) {
                    if (index >= 53) break;
                    inventoryMenuBuilder.withItem(index, new ItemBuilder(bossEggSerializer.getItemStack().clone()).setDisplayName("§c" + bossEggSerializer.getDisplayName()).setLore(Arrays.asList(
                            "§7DisplayName §8➡ §c" + bossEggSerializer.getDisplayName(),
                            "§7ID §8➡ §c" + bossEggSerializer.getId(),
                            "§7Chance §8➡ §c" + bossEggSerializer.getChance() + "%",
                            "",
                            "§7Das Item ist schon §c" + Util.formatBigNumber(bossEggSerializer.getAmount()) + "x §7gedroppt.",
                            "",
                            "§7Linksklick §8- §eItem entfernen")).build(), (player1, action, item) -> {
                        bossEgg.getItems().remove(bossEggSerializer);
                        bossEgg.save();
                        player1.chat("/bossegg items " + args[1]);
                        player1.sendMessage(StringDefaults.BOSSEGG_PREFIX + "Du hast das Item erfolgreich entfernt.");
                    }, ClickType.LEFT);
                    index++;
                }

                inventoryMenuBuilder.show(player);
            } else if (args[0].equalsIgnoreCase("create")) {
                if (Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert bereits.");
                    return true;
                }
                BossEgg bossEgg = new BossEgg(args[1]);
                Main.getPlugin(Main.class).getBossEggManager().getBossEggList().add(bossEgg);


                bossEgg.setItemStack(new ItemBuilder(Material.DRAGON_EGG).setDisplayName("§6§lSpawn-Ei §8- §c§l" + bossEgg.getDisplayName()).setLore(Arrays.asList(
                        "§7Klicke mit dem Spawn-Ei auf ein Block",
                        "§7um den Boss §c§l" + bossEgg.getDisplayName() + " §7zu spawnen.",
                        "",
                        "§7Der Boss wird je nach Schwierigkeit Items droppen.",
                        "§7Dieser Boss droppt zwischen §c" + bossEgg.getMinDropAmount() + "§8-§c" + bossEgg.getMaxDropAmount() + " Items",
                        "§7Die Items haben je nach §c§lBesonderheit §7eine §akleine Dropchance",
                        "§7oder eine §ahohe Dropchance.",
                        "",
                        "§7Dieses Spawn-Ei stammt aus der §c§l" + bossEgg.getCollection() + ". §7Kollektion.",
                        "",
                        "§7Linksklick §8- §eMögliche Items anzeigen",
                        "§7Rechtsklick §8- §eBoss spawnen")).build());

                bossEgg.save();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das BossEgg §c" + args[1] + " §7erfolgreich erstellt.");
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (!Main.getPlugin(Main.class).getBossEggManager().alreadyExists(args[1])) {
                    player.sendMessage(StringDefaults.PREFIX + "§7Dieses BossEgg existiert nicht.");
                    return true;
                }
                BossEgg bossEgg = Main.getPlugin(Main.class).getBossEggManager().forName(args[1]);
                bossEgg.delete();
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Du hast das BossEgg §c" + args[1] + " §7erfolgreich gelöscht,");
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {

                Main.getPlugin(Main.class).getBossEggManager().help(player);
            } else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("liste")) {

                InventoryMenuBuilder inventoryMenuBuilder = new InventoryMenuBuilder().withSize(9 * 6).withTitle("§7Alle BossEggs");

                int index = 0;
                for (BossEgg bossEgg : Main.getPlugin(Main.class).getBossEggManager().getBossEggList()) {
                    if (index >= 53) break;
                    inventoryMenuBuilder.withItem(index, new ItemBuilder(bossEgg.getItemStack().clone()).setDisplayName("§cBossEgg §8- §6" + bossEgg.getDisplayName()).setLore(Arrays.asList(
                            "§7Dieser BossEgg hat §c" + bossEgg.getItems().size() + " §7verfügbare Items",
                            "§7Diese Items können je nach ihrer Wahrscheinlichkeit, beim Tod gedroppt werden.",
                            "§7Dieser BossEgg startet mit §c" + Util.formatBigNumber(bossEgg.getMaxHealth()) + " Herzen.",
                            "§7Es werden mindestens §c" + bossEgg.getMinDropAmount() + " Items gedroppt.",
                            "§7Es werden maximal §c" + bossEgg.getMaxDropAmount() + " Items gedroppt.")).build());
                    index++;
                }
                inventoryMenuBuilder.withEventHandler(event -> event.setCancelled(true));
                inventoryMenuBuilder.show(player);
            }
        } else {
            Main.getPlugin(Main.class).getBossEggManager().help(player);
        }


        return false;
    }

}
