package de.potera.teamhardcore.events;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EntityDeath implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.getType() == EntityType.PIG_ZOMBIE) {
            List<ItemStack> toRemove = new ArrayList<>();
            for (ItemStack itemStack : event.getDrops()) {
                if (itemStack.getType() == Material.ROTTEN_FLESH)
                    toRemove.add(itemStack);
            }
            event.getDrops().removeAll(toRemove);
        }
    }

}
