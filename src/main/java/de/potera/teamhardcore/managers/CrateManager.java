package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.others.crates.BaseCrate;
import de.potera.teamhardcore.others.crates.CrateOpening;
import de.potera.teamhardcore.others.crates.addons.TestCrate;
import de.potera.teamhardcore.utils.StringDefaults;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CrateManager {

    private final Map<String, BaseCrate> availableCrates;
    private final Map<Player, CrateOpening> playersInOpening;

    public CrateManager() {
        this.availableCrates = new HashMap<>();
        this.playersInOpening = new HashMap<>();
        loadCrates();
    }

    private void loadCrates() {
        this.availableCrates.clear();
        this.availableCrates.put("TestCrate", new BaseCrate(new TestCrate()));
    }

    public BaseCrate getCrate(String name) {
        if (!this.availableCrates.containsKey(name))
            return null;
        return this.availableCrates.get(name);
    }

    public void onDisable() {
        for (Map.Entry<Player, CrateOpening> entry : this.playersInOpening.entrySet()) {
            Player target = entry.getKey();
            target.closeInventory();

            CrateOpening opening = entry.getValue();
            opening.cancelAllTasks();

            target.sendMessage(StringDefaults.PREFIX + "§cDas Crate-Opening wurde abgebrochen.");
        }
    }

    public Map<Player, CrateOpening> getPlayersInOpening() {
        return playersInOpening;
    }

    public Map<String, BaseCrate> getAvailableCrates() {
        return availableCrates;
    }
}
