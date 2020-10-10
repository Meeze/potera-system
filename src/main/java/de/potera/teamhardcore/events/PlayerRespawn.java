package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumPerk;
import de.potera.teamhardcore.others.Warp;
import de.potera.teamhardcore.users.UserData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        UserData userData = Main.getInstance().getUserManager().getUser(event.getPlayer().getUniqueId()).getUserData();

        for (EnumPerk toggledPerks : userData.getToggledPerks()) {
            if (!userData.isPerkToggled(toggledPerks)) continue;
            if (toggledPerks.isPotionEffect()) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(),
                        () -> Main.getInstance().getGeneralManager().refreshPerkEffects(event.getPlayer()), 5L);

                break;
            }
        }

        Warp spawn = Main.getInstance().getFileManager().getWarpFile().getWarp("Spawn");

        if (spawn == null) return;
        event.setRespawnLocation(spawn.getLocation());
    }

}
