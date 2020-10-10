package de.potera.teamhardcore.events;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumPerk;
import de.potera.teamhardcore.users.UserData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class FoodLevelChange implements Listener {

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        UserData userData = Main.getInstance().getUserManager().getUser(player.getUniqueId()).getUserData();

        if (userData.getOwnedPerks().contains(EnumPerk.NO_HUNGER) && userData.isPerkToggled(EnumPerk.NO_HUNGER)) {
            event.setCancelled(true);
            if (player.getFoodLevel() < 20)
                player.setFoodLevel(30);
        }
    }

}
