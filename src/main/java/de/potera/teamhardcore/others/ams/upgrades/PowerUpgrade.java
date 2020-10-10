package de.potera.teamhardcore.others.ams.upgrades;

import de.potera.teamhardcore.others.ams.Ams;

public class PowerUpgrade extends AmsUpgradeBase {

    public PowerUpgrade(Ams ams, int level) {
        super(EnumAmsUpgrade.POWER, ams, level);
    }

    public double getMultiplier() {
        return getLevel() * 0.01D + 1.0D + getAms().getPrestigeLevel() * 0.1D;
    }

    @Override
    public void initUpgrade() {

    }
}
