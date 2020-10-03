package de.potera.teamhardcore.others;

import java.util.ArrayList;
import java.util.UUID;

public class Report {

    private final UUID target;
    private final ArrayList<ReportEntry> reportEntries;

    public Report(UUID target) {
        this.target = target;
        this.reportEntries = new ArrayList<>();
    }

    public UUID getTarget() {
        return this.target;
    }

    public ArrayList<ReportEntry> getReportEntries() {
        return this.reportEntries;
    }

    public boolean containsEntry(UUID executor) {
        for (ReportEntry entry : this.reportEntries) {
            if (entry.getUuid().equals(executor))
                return true;
        }
        return false;
    }

    public ReportEntry getEntry(UUID executor) {
        for (ReportEntry entry : this.reportEntries) {
            if (entry.getUuid().equals(executor))
                return entry;
        }
        return null;
    }

    public void addEntry(UUID executor, String reason) {
        if (containsEntry(executor))
            return;
        ReportEntry entry = new ReportEntry(executor, reason);
        this.reportEntries.add(entry);
    }

    public void removeEntry(UUID executor) {
        ReportEntry entry = getEntry(executor);
        if (entry == null)
            return;
        this.reportEntries.remove(entry);
    }

    public static class ReportEntry {

        private final UUID uuid;
        private final long timestamp;
        private final String reason;

        public ReportEntry(UUID uuid, String reason) {
            this.uuid = uuid;
            this.timestamp = System.currentTimeMillis();
            this.reason = reason;
        }

        public UUID getUuid() {
            return uuid;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getReason() {
            return reason;
        }
    }

}
