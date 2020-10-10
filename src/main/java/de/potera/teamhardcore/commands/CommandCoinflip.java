package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.gamble.Coinflip;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCoinflip implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length > 1) {
            Main.getInstance().getCoinflipManager().openInventory(player);
            return true;
        }

        if (args.length == 0) {
            Main.getInstance().getCoinflipManager().openInventory(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("abbrechen") || args[0].equalsIgnoreCase("cancel")) {
            if (!Main.getInstance().getCoinflipManager().hasOpenCoinflip(player)) {
                player.sendMessage(StringDefaults.CF_PREFIX + "§cDu hast keinen offenen Raum.");
                return true;
            }

            Coinflip coinflip = Main.getInstance().getCoinflipManager().getOpenCoinflip(player);

            if (coinflip.getGamePhase() != -1) {
                player.sendMessage(StringDefaults.CF_PREFIX + "§cDu kannst diesen Coinflip nicht mehr abbrechen.");
                return true;
            }

            UserCurrency uc = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();
            uc.addMoney(coinflip.getEntryPrice());

            Main.getInstance().getCoinflipManager().stopCoinflip(coinflip);
            player.sendMessage(StringDefaults.CF_PREFIX + "§7Du hast den Raum geschlossen.");
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            Main.getInstance().getCoinflipManager().openInventory(player);
            return true;
        }

        long entryPrice;
        try {
            entryPrice = Long.parseLong(args[0]);

            if (entryPrice <= 0)
                throw new NumberFormatException();

        } catch (NumberFormatException ex) {
            player.sendMessage(StringDefaults.CF_PREFIX + "§cBitte gebe eine richtige Zahl an.");
            return true;
        }

        if (Main.getInstance().getCoinflipManager().hasOpenCoinflip(player)) {
            player.sendMessage(StringDefaults.CF_PREFIX + "§cDu hast bereits ein Raum geöffnet");
            return true;
        }

        UserCurrency currency = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserCurrency();

        if (currency.getMoney() < entryPrice) {
            player.sendMessage(StringDefaults.CF_PREFIX + "§cDu hast zu wenige Münzen um diesen Coinflip beizutreten.");
            return true;
        }

        Main.getInstance().getCoinflipManager().openCoinflip(player, entryPrice);
        player.sendMessage(StringDefaults.CF_PREFIX + "§7Der Raum wurde erfolgreich geöffnet.");


        return true;
    }
}
