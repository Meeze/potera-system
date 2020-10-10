package de.potera.teamhardcore.others.ams.upgrades;

import de.potera.teamhardcore.others.ams.Ams;

public abstract class AmsUpgradeBase {

    private EnumAmsUpgrade upgrade;
    private Ams ams;
    private int level;

    public AmsUpgradeBase(EnumAmsUpgrade upgrade, Ams ams, int level) {
        this.upgrade = upgrade;
        this.ams = ams;
        this.level = level;
        initUpgrade();
    }

    public EnumAmsUpgrade getUpgrade() {
        return upgrade;
    }

    public Ams getAms() {
        return ams;
    }

    public int getLevel() {
        return level;
    }

    public abstract void initUpgrade();
}
