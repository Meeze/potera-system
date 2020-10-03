package de.potera.teamhardcore.others.ams.upgrades;

import de.potera.teamhardcore.others.ams.Ams;

public class OfflineGenUpgrade extends AmsUpgradeBase {

    public OfflineGenUpgrade(Ams ams, int level) {
        super(EnumAmsUpgrade.OFFLINE_GEN, ams, level);
    }

    public double getMultiplier() {
        return getLevel() * 0.01D * getAms().getPrestigeLevel() * 0.1D;
    }

    @Override
    public void initUpgrade() {

    }
}
