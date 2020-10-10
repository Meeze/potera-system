package de.potera.rysefoxx.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AfkCheckCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("potera.afkcheck")) {
            player.sendMessage(StringDefaults.NO_PERM);
            return true;
        }

        //AfkCheck <Spieler>

        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(StringDefaults.NOT_ONLINE);
                return true;
            }
            if (Main.getInstance().getGeneralManager().getPlayersInAfkCheck().contains(target)) {
                player.sendMessage(StringDefaults.PREFIX + "§7Der Spieler wird bereits überprüft.");
                return true;
            }
            Main.getInstance().getGeneralManager().getPlayersInAfkCheck().add(target);
            player.sendMessage(StringDefaults.AFKCHECK_PREFIX + "§c" + target.getName() + " §7wird nun auf Aktivität geprüft.");

            new BukkitRunnable() {
                int tickedSeconds = 0;

                int oldX = target.getLocation().getBlockX();
                int oldZ = target.getLocation().getBlockZ();
                int success = 0;
                int denied = 0;

                @Override
                public void run() {
                    tickedSeconds++;

                    if (tickedSeconds >= 9) {
                        Main.getInstance().getGeneralManager().getPlayersInAfkCheck().remove(target);
                        player.sendMessage(StringDefaults.AFKCHECK_PREFIX + "§7Bei mehreren Überprüfungen, war §c" + target.getName() + " §a§l" + success + " aktiv §7und §c§l" + denied + " §7nicht aktiv.");
                        cancel();
                        return;
                    }
                    if (target.getLocation().getBlockX() != oldX || target.getLocation().getBlockZ() != oldZ) {
                        player.sendMessage(StringDefaults.AFKCHECK_PREFIX + "§7Es wurden Aktivitäten beim Spieler §c" + target.getName() + " §7gefunden.");
                        success++;
                    } else {
                        player.sendMessage(StringDefaults.AFKCHECK_PREFIX + "§7Es wurde keine Aktivität gefunden.");
                        denied++;
                    }
                    oldX = target.getLocation().getBlockX();
                    oldZ = target.getLocation().getBlockZ();
                }
            }.runTaskTimer(Main.getInstance(), 10L, 40L);


        }


        return false;
    }

}
