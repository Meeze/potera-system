package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.Report;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.LinkedHashMap;
import java.util.UUID;

public class ReportManager {

    private final LinkedHashMap<UUID, Report> reports;

    public ReportManager() {
        this.reports = new LinkedHashMap<>();
    }

    private void startInformationTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                if (ReportManager.this.reports.isEmpty()) return;
                int waiting = ReportManager.this.reports.size();

                String broadcast = StringDefaults.REPORT_PREFIX + "§7" + waiting + " §cSpieler wurde" + (waiting == 1 ? "" : "n") + " reportet.";

                for (Player all : Bukkit.getOnlinePlayers()) {
                    if (!all.hasPermission("potera.report.mod"))
                        continue;
                    all.sendMessage(broadcast);
                    all.playSound(all.getLocation(), Sound.NOTE_PIANO, 1.0F, 1.0F);
                }
            }
        }.runTaskTimerAsynchronously(Main.getInstance(), 20 * 60, 20 * 60);
    }

    public Report getReport(UUID uuid) {
        if (!this.reports.containsKey(uuid))
            return null;
        return this.reports.get(uuid);
    }

    public boolean isReported(UUID uuid) {
        return this.reports.containsKey(uuid);
    }

    public boolean hasPlayerReported(UUID uuid, UUID target) {
        if (!this.reports.containsKey(target))
            return false;
        Report report = this.reports.get(target);
        return report.containsEntry(uuid);
    }

    public void addReport(UUID target, UUID executor, String reason) {
        Report report = (this.reports.containsKey(target) ? this.reports.get(target) : new Report(target));
        if (report.containsEntry(executor)) return;
        report.addEntry(executor, reason);
        this.reports.put(target, report);
    }

    public void closeReport(UUID target) {
        if (!this.reports.containsKey(target)) return;
        this.reports.remove(target);
    }

    public LinkedHashMap<UUID, Report> getReports() {
        return reports;
    }
}
