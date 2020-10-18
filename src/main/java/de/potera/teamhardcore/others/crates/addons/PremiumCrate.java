package de.potera.teamhardcore.others.crates.addons;

import de.potera.teamhardcore.others.crates.CrateAddon;
import de.potera.teamhardcore.utils.SkullCreator;

public class PremiumCrate extends CrateAddon {


    public PremiumCrate() {
        super("premium", "§6§lPremium Crate");

        setDisplayItem(SkullCreator.itemFromBase64(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmE4YzZlZDk5N2JmYjA3ODQ3NDg3NmI3ZGVlNmZhM2ExYTljYzYxYjZhYTI3OWUxNTc5ODNlZGM5Y2RmMjJmZSJ9fX0="));

    }
}
