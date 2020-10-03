package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHeal implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (!player.hasPermission("potera.heal")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " [Spieler]");
            return true;
        }

        if (args.length == 0) {
            boolean noCooldown = player.hasPermission("potera.heal.nocooldown");

            if (!noCooldown && Main.getInstance().getGeneralManager().getHealCooldowns().containsKey(
                    player.getUniqueId())) {
                long timestamp = Main.getInstance().getGeneralManager().getHealCooldowns().get(player.getUniqueId());
                long diff = timestamp - System.currentTimeMillis();

                if (diff / 1000L > 0L) {
                    player.sendMessage(
                            StringDefaults.PREFIX + "§cWarte noch §6" + TimeUtil.timeToStringApproximately(diff,
                                    false) + " §cbevor du dich heilen kannst.");
                    return true;
                }
            }
            player.setFoodLevel(30);
            player.setHealth(player.getMaxHealth());
            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            player.sendMessage(StringDefaults.PREFIX + "§eDu wurdest geheilt.");

            if (!noCooldown)
                Main.getInstance().getGeneralManager().getHealCooldowns().put(player.getUniqueId(),
                        System.currentTimeMillis() + 3600000L);

            return true;
        }

        if (args.length == 1) {
            if (!player.hasPermission("potera.heal.other")) {
                player.sendMessage(StringDefaults.NO_PERM);
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }

            if (target == player) {
                player.performCommand("/heal");
                return true;
            }

            boolean noCooldown = player.hasPermission("potera.heal.nocooldown");

            if (!noCooldown && Main.getInstance().getGeneralManager().getHealCooldowns().containsKey(
                    player.getUniqueId())) {
                long timestamp = Main.getInstance().getGeneralManager().getHealCooldowns().get(player.getUniqueId());
                long diff = timestamp - System.currentTimeMillis();

                if (diff / 1000L > 0L) {
                    player.sendMessage(
                            StringDefaults.PREFIX + "§cWarte noch §6" + TimeUtil.timeToStringApproximately(diff,
                                    false) + " §cbevor du dich heilen kannst.");
                    return true;
                }
            }
            target.setFoodLevel(30);
            target.setHealth(target.getMaxHealth());
            target.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
            target.sendMessage(StringDefaults.PREFIX + "§eDu wurdest geheilt.");
            player.sendMessage(StringDefaults.PREFIX + "§7" + player.getName() + " §ewurde geheilt.");

            if (!noCooldown)
                Main.getInstance().getGeneralManager().getHealCooldowns().put(player.getUniqueId(),
                        System.currentTimeMillis() + 3600000L);

        }

        return true;
    }
}
