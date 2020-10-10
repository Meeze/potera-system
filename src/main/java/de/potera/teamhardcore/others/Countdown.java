package de.potera.teamhardcore.others;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Countdown {
    private static BukkitTask task;
    private static int time;

    public static void start() {
        if (isCurrentlyRunning()) return;

        task = new BukkitRunnable() {
            public void run() {
                if (Countdown.getTime() >= 600) {
                    if (Countdown.getTime() % 600 == 0) {
                        int timeToShow = Countdown.getTime() / 60;
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            all.sendMessage(
                                    StringDefaults.COUNTDOWN_PREFIX + "§aNoch " + timeToShow + " " + "Minuten" + "!");
                            all.playSound(all.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                        }
                    }
                } else if (Countdown.getTime() >= 60) {
                    if (Countdown.getTime() % 60 == 0) {
                        int timeToShow = Countdown.getTime() / 60;
                        for (Player all : Bukkit.getOnlinePlayers()) {
                            all.sendMessage(
                                    StringDefaults.COUNTDOWN_PREFIX + "§aNoch " + timeToShow + " " + ((timeToShow == 1) ? "Minute" : "Minuten") + "!");
                            all.playSound(all.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                        }
                    }
                } else if ((Countdown.getTime() % 10 == 0 && Countdown.getTime() > 10) || (Countdown.getTime() <= 10 && Countdown.getTime() >= 1)) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        all.sendMessage(
                                StringDefaults.COUNTDOWN_PREFIX + "§aNoch " + Countdown.getTime() + " " + ((Countdown.getTime() == 1) ? "Sekunde" : "Sekunden") + "!");
                        all.playSound(all.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                    }
                }
                if (Countdown.getTime() <= 0) {
                    Countdown.stop();
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        all.sendMessage(StringDefaults.COUNTDOWN_PREFIX + "§aDie Zeit ist abgelaufen!");
                        all.playSound(all.getLocation(), Sound.NOTE_PLING, 1.0F, 2.0F);
                    }
                    return;
                }
                Countdown.setTime(Countdown.getTime() - 1);
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }

    public static void stop() {
        setTime(0);
        if (!isCurrentlyRunning())
            return;
        task.cancel();
        task = null;
    }


    public static boolean isCurrentlyRunning() {
        return (task != null);
    }


    public static int getTime() {
        return time;
    }


    public static void setTime(int time) {
        Countdown.time = time;
    }
}
