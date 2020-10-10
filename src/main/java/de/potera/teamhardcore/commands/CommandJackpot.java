package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.gamble.jackpot.JackpotGame;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandJackpot implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length != 0 && args.length != 2) {
            if (Main.getInstance().getJackpotManager().getJackpotGame() == null) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cAktuell läuft kein Jackpot.");
                return true;
            }

            JackpotGame jackpotGame = Main.getInstance().getJackpotManager().getJackpotGame();
            jackpotGame.openGUI(player, jackpotGame.getGamePhase());
            return true;
        }

        if (args.length == 0) {
            if (Main.getInstance().getJackpotManager().getJackpotGame() == null) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cAktuell läuft kein Jackpot.");
                return true;
            }

            JackpotGame jackpotGame = Main.getInstance().getJackpotManager().getJackpotGame();
            jackpotGame.openGUI(player, jackpotGame.getGamePhase());
            return true;
        }

        if (args[0].equalsIgnoreCase("start")) {
            if (!player.hasPermission("potera.jackpot.admin")) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cDazu hast du keine Berechtigung!");
                return true;
            }

            if (Main.getInstance().getJackpotManager().getJackpotGame() != null) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cEs läuft bereits ein Jackpot.");
                return true;
            }

            long coins;

            try {
                coins = Long.parseLong(args[1]);

                if (coins <= 0)
                    throw new NumberFormatException();

            } catch (NumberFormatException ex) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cBitte gebe einen richtigen Betrag an.");
                return true;
            }

            Main.getInstance().getJackpotManager().startJackpotGame(coins);
            player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§7Du hast einen neuen Jackpot gestartet!");
            return true;
        }

        if (args[0].equalsIgnoreCase("teilnehmen")) {
            if (Main.getInstance().getJackpotManager().getJackpotGame() == null) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cAktuell läuft kein Jackpot.");
                return true;
            }

            JackpotGame jackpotGame = Main.getInstance().getJackpotManager().getJackpotGame();

            if (jackpotGame.containsEntry(player.getUniqueId())) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cDu nimmst bereits am Jackpot teil.");
                return true;
            }

            if (jackpotGame.getGamePhase() != 0) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cDu kannst dem Jackpot nicht mehr beitreten.");
                return true;
            }

            boolean max = args[1].equalsIgnoreCase("max");
            long coins = (max ? jackpotGame.getMaxBet() : 0);

            if (!max) {
                try {
                    coins = Long.parseLong(args[1]);

                    if (coins <= 0)
                        throw new NumberFormatException();

                } catch (NumberFormatException ex) {
                    player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cBitte gebe einen richtigen Betrag an.");
                    return true;
                }
            }


            UserCurrency uc = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();

            if (uc.getMoney() < coins) {
                player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§cDu besitzt nicht genügend Münzen.");
                return true;
            }

            if (coins > jackpotGame.getMaxBet()) {
                player.sendMessage(
                        StringDefaults.JACKPOT_PREFIX + "§cDer Betrag darf nicht höher als " + Util.formatNumber(
                                jackpotGame.getMaxBet()) + "$ sein.");
                return true;
            }

            uc.removeMoney(coins);
            jackpotGame.addEntry(player.getUniqueId(), coins);
            player.sendMessage(StringDefaults.JACKPOT_PREFIX + "§7Du bist dem Jackpot erfolgreich beigetreten.");
            jackpotGame.openGUI(player, jackpotGame.getGamePhase());
            return true;
        }
        return true;
    }
}
