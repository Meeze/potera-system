package de.potera.teamhardcore.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.mines.Mine;
import de.potera.teamhardcore.users.UserMine;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandMines implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (label.equalsIgnoreCase("mines") || label.equalsIgnoreCase("mine")) {

            if (args.length > 3) {
                sendHelp(player, label);
                return true;
            }

            if (args.length == 0 || !player.hasPermission("potera.mines.admin")) {
                sendToPlayerMine(player);
                return true;
            }

            if (args.length == 1) {

                if (!player.hasPermission("potera.mines.admin")) {
                    sendToPlayerMine(player);
                    return true;
                }

                String name = args[0];
                if (Main.getInstance().getMinesManager().getMine(name) == null) {
                    player.sendMessage(StringDefaults.MINES_PREFIX + "§cDie Mine existiert nicht.");
                    return true;
                }

                Mine mine = Main.getInstance().getMinesManager().getMine(name);

                if (mine.getSpawn() == null) {
                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§cDieser Mine wurde noch keine Spawn-Position gesetzt.");
                    return true;
                }

                player.teleport(mine.getSpawn());
                player.sendMessage(
                        StringDefaults.MINES_PREFIX + "§eDu wurdest erfolgreich zur Mine §7" + mine.getName() + " §eteleportiert.");
                return true;
            }

            if (args.length == 2) {
                if (!player.hasPermission("potera.mines.admin")) {
                    //todo: teleport to mine
                    return true;
                }

                if (args[0].equalsIgnoreCase("setmineregion")) {
                    String name = args[1];

                    Selection selection = Main.getInstance().getWorldEditPlugin().getSelection(player);

                    if (selection == null || selection.getMinimumPoint() == null || selection.getMaximumPoint() == null) {
                        player.sendMessage(
                                StringDefaults.MINES_PREFIX + "§cDu musst die Region vorher mit WorldEdit auswählen.");
                        return true;
                    }

                    Mine mine = Main.getInstance().getMinesManager().getMine(name);

                    if (mine == null) {
                        player.sendMessage(StringDefaults.MINES_PREFIX + "§cDie Mine existiert nicht.");
                        return true;
                    }

                    mine.setMinPos(selection.getMinimumPoint());
                    mine.setMaxPos(selection.getMaximumPoint());
                    mine.checkMaxBlocks();
                    mine.saveData();

                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§eDie Position der Mine §7" + name + " §ewurde gesetzt!");
                    return true;
                } else if (args[0].equalsIgnoreCase("setspawn")) {
                    String name = args[1];

                    Mine mine = Main.getInstance().getMinesManager().getMine(name);

                    if (mine == null) {
                        player.sendMessage(StringDefaults.MINES_PREFIX + "§cDie Mine existiert nicht.");
                        return true;
                    }

                    mine.setSpawn(player.getLocation());
                    mine.saveData();

                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§eDer Spawn der Mine §7" + name + " §ewurde gesetzt!");
                    return true;
                } else if (args[0].equalsIgnoreCase("create")) {
                    String name = args[1];

                    if (Main.getInstance().getMinesManager().getMine(name) != null) {
                        player.sendMessage(
                                StringDefaults.MINES_PREFIX + "§cEs existiert bereits eine Mine mit diesem Namen.");
                        return true;
                    }

                    Main.getInstance().getMinesManager().createMine(name, player.getLocation());
                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§eDu hast erfolgreich die Mine §7" + name + " §eerstellt.");
                    return true;
                } else if (args[0].equalsIgnoreCase("delete")) {
                    String name = args[1];

                    if (Main.getInstance().getMinesManager().getMine(name) == null) {
                        player.sendMessage(StringDefaults.MINES_PREFIX + "§cEs existiert keine Mine mit diesem Namen.");
                        return true;
                    }

                    Main.getInstance().getMinesManager().removeMine(name);
                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§eDu hast erfolgreich die Mine §7" + name + " §egelöscht.");
                    return true;
                } else if (args[0].equalsIgnoreCase("reset")) {
                    String name = args[1];


                    Mine mine = Main.getInstance().getMinesManager().getMine(name);
                    if (mine == null) {
                        player.sendMessage(StringDefaults.MINES_PREFIX + "§cEs existiert keine Mine mit diesem Namen.");
                        return true;
                    }

                    Main.getInstance().getMinesManager().scheduledMineReset(mine, false);
                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§eDu hast erfolgreich die Mine §7" + name + " §egelöscht.");
                    return true;
                } else {
                    sendHelp(player, label);
                    return true;
                }
            }

            if (!player.hasPermission("potera.mines.admin")) {
                sendToPlayerMine(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("setregion")) {
                String name = args[1];

                Mine mine = Main.getInstance().getMinesManager().getMine(name);

                if (mine == null) {
                    player.sendMessage(StringDefaults.MINES_PREFIX + "§cDiese Mine existiert nicht.");
                    return true;
                }

                mine.setRegion(args[2]);
                mine.saveData();

                player.sendMessage(
                        StringDefaults.MINES_PREFIX + "§eDie Region der Mine §7" + name + " §ewurde auf §a" + args[2] + " §egesetzt!");
            } else {
                sendHelp(player, label);
                return true;
            }

        }

        if (label.equalsIgnoreCase("rankup")) {
            UserMine userMine = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserMine();

            if (userMine.getLevel() >= 100) {
                player.sendMessage(StringDefaults.MINES_PREFIX + "§cDu hast bereits das maximale Level erreicht.");
                return true;
            }

            int nextLevel = Main.getInstance().getMinesManager().getMinePointsToNextLevel(userMine.getLevel());
            long points = userMine.getMinePoints();

            if (points < nextLevel) {
                player.sendMessage(StringDefaults.MINES_PREFIX + "§cDir fehlen noch §7" + Util.formatNumber(
                        (nextLevel - points)) + " MinePunkte §cbis zum nächsten Level.");
                return true;
            }

            boolean newMine = (Main.getInstance().getMinesManager().getAccessableMine(
                    userMine.getLevel()) != Main.getInstance().getMinesManager().getAccessableMine(
                    userMine.getLevel() + 1));

            userMine.setMinePoints(userMine.getMinePoints() - nextLevel);
            userMine.setLevel(userMine.getLevel() + 1);

            player.sendMessage(
                    StringDefaults.MINES_PREFIX + "§aDu bist zum Level §3§l" + userMine.getLevel() + " §aaufgestiegen!");

            if (newMine) {
                Mine mine = Main.getInstance().getMinesManager().getMine(userMine.getLevel());

                if (mine == null) {
                    player.sendMessage(
                            StringDefaults.MINES_PREFIX + "§cFür dein Level wurde keine Mine gefunden.");
                    return true;
                }

                player.sendMessage(
                        StringDefaults.MINES_PREFIX + "§aDu hast die Mine §3§l" + mine.getName() + " §afreigeschaltet!");
                player.teleport(mine.getSpawn());
            }

            Main.getInstance().getDatabaseManager().getExecutor().execute(new Runnable() {
                int count = 0;

                public void run() {
                    while (this.count < 8) {
                        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 0.6F + this.count * 0.15F);
                        this.count++;
                        try {
                            Thread.sleep(150L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

        return true;
    }

    private void sendHelp(Player player, String label) {
        if (player.hasPermission("potera.mines.admin")) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " setregion <Level> <Name>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " setspawn <Level>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " setmineregion <Level>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " create <Name>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " delete <Name>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " reset <Name>");
        }
    }

    private void sendToPlayerMine(Player player) {
        UserMine userMine = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserMine();
        Mine mine = Main.getInstance().getMinesManager().getMine(userMine.getLevel());

        if (mine == null) {
            player.sendMessage(
                    StringDefaults.MINES_PREFIX + "§cFür dein Level konnte keine Mine gefunden werden.");
            return;
        }

        if (mine.getSpawn() == null) {
            player.sendMessage(
                    StringDefaults.MINES_PREFIX + "§cFür dein Level konnte keine Mine gefunden werden.");
            return;
        }

        player.teleport(mine.getSpawn());
        player.sendMessage(
                StringDefaults.MINES_PREFIX + "§eDu wurdest zur Mine §7" + mine.getName() + " §eteleportiert.");
    }

}
