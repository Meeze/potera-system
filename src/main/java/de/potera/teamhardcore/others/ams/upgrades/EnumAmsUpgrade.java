package de.potera.teamhardcore.others.ams.upgrades;

import de.potera.teamhardcore.others.ams.Ams;
import de.potera.teamhardcore.utils.Reflection;

public enum EnumAmsUpgrade {
    MONEY_CAPACITY("Mehr Speicherplatz", MoneyCapacityUpgrade.class),
    DOUBLE_COINS("Doppelte Coins", DoubleCoinsUpgrade.class),
    POWER("Coinboost", PowerUpgrade.class),
    OFFLINE_GEN("Offline-Generierung", OfflineGenUpgrade.class),
    ;

    private final String displayName;
    private final Class<?> handlingClass;

    EnumAmsUpgrade(String displayName, Class<?> handlingClass) {
        this.displayName = displayName;
        this.handlingClass = handlingClass;
    }

    public static EnumAmsUpgrade getByName(String name) {
        for (EnumAmsUpgrade upgrade : values()) {
            if (upgrade.name().equalsIgnoreCase(name))
                return upgrade;
        }
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<?> getHandlingClass() {
        return handlingClass;
    }

    public AmsUpgradeBase create(Ams ams, int level) {
        return (AmsUpgradeBase) Reflection.newInstance(
                Reflection.getConstructor(this.handlingClass, Ams.class, int.class), new Object[]{ams, level});
    }
}
