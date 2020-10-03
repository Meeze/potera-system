package de.potera.teamhardcore.others;

public enum EnumLiga {

    UNRANKEND(0, "§7Unplatziert"),
    BRONZE(500, "§cBronze"),
    SILVER(1000, "§7Silber"),
    GOLD(1500, "§eGold"),
    PLATINUM(2000, "§8§lPlatin");

    private final int elo;
    private final String displayName;

    EnumLiga(int elo, String displayName) {
        this.elo = elo;
        this.displayName = displayName;
    }

    public static EnumLiga getLiga(long elo) {
        EnumLiga chosen = null;
        for (EnumLiga liga : values()) {
            if (liga.getElo() <= elo) {
                if (chosen == null || chosen.getElo() < liga.getElo())
                    chosen = liga;
            }
        }
        return chosen;
    }

    public static boolean checkRankswitch(int trophiesBefore, int trophiesAfter) {
        return (getLiga(trophiesBefore) != getLiga(trophiesAfter));
    }

    public int getElo() {
        return elo;
    }

    public String getDisplayName() {
        return this.displayName;
    }

}
