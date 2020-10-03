package de.potera.teamhardcore.others.ams.upgrades;

import de.potera.teamhardcore.others.ams.Ams;

public class DoubleCoinsUpgrade extends AmsUpgradeBase {

    public DoubleCoinsUpgrade(Ams ams, int level) {
        super(EnumAmsUpgrade.DOUBLE_COINS, ams, level);
    }

    public int getChance() {
        return getLevel() * getAms().getPrestigeLevel() * 10;
    }

    @Override
    public void initUpgrade() {

    }
}
