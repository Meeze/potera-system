package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Support;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SupportManager {

    private final Map<Player, Support> supports;
    private final Set<Player> waiting;

    public SupportManager() {
        this.supports = new HashMap<>();
        this.waiting = new HashSet<>();

        startNotificationTask();
    }

    private void startNotificationTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (SupportManager.this.waiting.isEmpty()) return;

                int waiting = SupportManager.this.waiting.size();

                String broadcast = StringDefaults.SUPPORT_PREFIX + "§7" + waiting + " §cSpieler warte" + (waiting == 1 ? "t" : "n") + " auf Support.";

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!all.hasPermission("potera.support"))
                        continue;
                    all.sendMessage(broadcast);
                    all.playSound(all.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20 * 60, 20 * 60);
    }

    public void setWaiting(Player player, boolean waiting) {
        if (waiting) {
            if (this.waiting.contains(player)) return;
            this.waiting.add(player);
        } else {
            if (!this.waiting.contains(player)) return;
            this.waiting.remove(player);
        }
    }

    public void createSupport(Player supporter, Player member) {
        if (this.supports.containsKey(supporter) || this.supports.containsKey(member)) return;

        Support support = new Support(supporter, member);
        this.supports.put(member, support);
        this.supports.put(supporter, support);
    }

    public boolean isWaiting(Player player) {
        return this.waiting.contains(player);
    }

    public Support getSupport(Player player) {
        return this.supports.getOrDefault(player, null);
    }

    public void handlePlayerQuit(Player player) {
        if (!this.supports.containsKey(player)) return;
        Support support = getSupport(player);
        for (Player all : support.getSupportPlayers().keySet()) {
            all.sendMessage(StringDefaults.SUPPORT_PREFIX + "§7" + player.getName() + " §chat den Support verlassen.");
            all.sendMessage(StringDefaults.SUPPORT_PREFIX + "§cDer Supportchat wird beendet.");
            getSupports().remove(all);
        }
    }

    public Set<Player> getWaiting() {
        return waiting;
    }

    public Map<Player, Support> getSupports() {
        return supports;
    }
}
