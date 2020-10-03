package de.potera.teamhardcore.others.ams.upgrades;

import de.potera.teamhardcore.others.ams.Ams;

public class MoneyCapacityUpgrade extends AmsUpgradeBase {

    private static final int BASE_MAXCOINS = 5000000;

    public MoneyCapacityUpgrade(Ams ams, int level) {
        super(EnumAmsUpgrade.MONEY_CAPACITY, ams, level);
    }

    @Override
    public void initUpgrade() {
        if (getLevel() > 100)
            return;
        getAms().setMaxCoins(BASE_MAXCOINS * (getLevel() / 100.0D + 1.0D));
    }
}
