package de.potera.teamhardcore.others;

public enum StatsPeriod {
    ALL(0, new String[]{"Gesamt", "G"}),
    WEEKLY(1, new String[]{"Woche", "W"}),
    DAILY(2, new String[]{"Tag", "T"});

    private int index;
    private String[] aliases;

    StatsPeriod(int index, String[] aliases) {
        this.index = index;
        this.aliases = aliases;
    }

    public static StatsPeriod getPeriodByInput(String input) {
        for (StatsPeriod period : values()) {
            for (String alias : period.getAliases()) {
                if (alias.equalsIgnoreCase(input))
                    return period;
            }
        }
        return null;
    }

    public int getIndex() {
        return this.index;
    }

    public String[] getAliases() {
        return this.aliases;
    }
}
