package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.gamble.roulette.RouletteEntry;
import de.potera.teamhardcore.others.gamble.roulette.RouletteGame;
import de.potera.teamhardcore.others.gamble.roulette.RouletteSetup;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRoulette implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        /*
        /roll
        /roll start
        /roll teilnehmen <Amount> <Multiplier>
         */

        if (args.length > 1 && args.length != 3) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label);
            player.sendMessage(
                    StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " teilnehmen <Münzen> <Multiplier>");
            if (player.hasPermission("potera.roulette.admin"))
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " start");
            return true;
        }

        if (args.length == 0) {
            if (Main.getInstance().getRouletteManager().getRouletteGame() == null) {
                player.sendMessage(StringDefaults.ROLL_PREFIX + "§cAktuell läuft keine Roulette Runde.");
                return true;
            }

            RouletteGame rouletteGame = Main.getInstance().getRouletteManager().getRouletteGame();

            if (Main.getInstance().getRouletteManager().getRouletteGame().getPlayers().containsKey(player)) {
                rouletteGame.openGUI(player, rouletteGame.getGamePhase());
                return true;
            }

            if (rouletteGame.getGamePhase() == 0) {
                Main.getInstance().getRouletteManager().openGUI(player, false);
                return true;
            }

            player.sendMessage(StringDefaults.ROLL_PREFIX + "§cDu kannst dieser Runde nicht mehr beitreten.");
        }

        if (args.length == 1) {
            if (!player.hasPermission("potera.roulette.admin")) {
                player.performCommand("/roll");
                return true;
            }

            if (args[0].equalsIgnoreCase("start")) {
                RouletteGame rouletteGame = Main.getInstance().getRouletteManager().getRouletteGame();
                if (rouletteGame != null) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cEs läuft bereits eine Roulette Runde.");
                    return true;
                }

                Main.getInstance().getRouletteManager().startRouletteGame();
                player.sendMessage(StringDefaults.ROLL_PREFIX + "§aDu hast eine neue Roulette Runde gestartet!");
            } else {
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label);
                player.sendMessage(
                        StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " teilnehmen <Münzen> <Multiplier>");
                if (player.hasPermission("potera.roulette.admin"))
                    player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " start");
                return true;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("teilnehmen")) {

                long coins;
                double multiplier;

                try {
                    coins = Long.parseLong(args[1]);
                    multiplier = Double.parseDouble(args[2]);

                    if (coins < RouletteSetup.MIN_ENTRY)
                        throw new NumberFormatException();

                    if (multiplier < RouletteSetup.MIN_MULTIPLIER || multiplier > RouletteSetup.MAX_MULTIPLIER)
                        throw new NumberFormatException();

                } catch (NumberFormatException ex) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cBitte gebe einen richtigen Wert an.");
                    return true;
                }

                RouletteGame rouletteGame = Main.getInstance().getRouletteManager().getRouletteGame();
                if (rouletteGame == null) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cAktuell läuft keine Roulette Runde.");
                    return true;
                }

                if (rouletteGame.getPlayers().containsKey(player)) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cDu nimmst bereits an dieser Runde teil.");
                    return true;
                }

                if (rouletteGame.getGamePhase() != 0) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cDu kannst dieser Runde nicht mehr beitreten.");
                    Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> rouletteGame.openGUI(player, 0), 1L);
                    return true;
                }

                UserCurrency uc = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();

                if (coins > uc.getMoney()) {
                    player.sendMessage(StringDefaults.ROLL_PREFIX + "§cDu besitzt nicht soviele Münzen");
                    return true;
                }

                uc.removeMoney(coins);

                RouletteEntry entry = new RouletteEntry(player.getUniqueId(), multiplier, coins);
                player.sendMessage(StringDefaults.ROLL_PREFIX + "§7Du hast erfolgreich teilgenommen.");

                rouletteGame.getPlayers().put(player, entry);
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> rouletteGame.openGUI(player, 0), 1L);

            } else {
                player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label);
                player.sendMessage(
                        StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " teilnehmen <Münzen> <Multiplier>");
                if (player.hasPermission("potera.roulette.admin"))
                    player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " start");
                return true;
            }
        }


        return true;
    }
}
