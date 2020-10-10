package de.potera.teamhardcore.events;

import de.potera.fakemobs.event.PlayerInteractFakeMobEvent;
import de.potera.fakemobs.util.FakeMob;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.fakeentity.FakeEntity;
import de.potera.teamhardcore.others.fakeentity.FakeEntityOptionBase;
import de.potera.teamhardcore.others.fakeentity.FakeEntityOptionImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerInteractFakeMob implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractFakeMobEvent event) {
        Player player = event.getPlayer();
        FakeMob fakeMob = event.getMob();

        FakeEntity entity = Main.getInstance().getFakeEntityManager().getEntityByFakemob(fakeMob);

        if (entity == null) return;

        if (entity.getInteractCooldowns().containsKey(player)) {
            long diff = System.currentTimeMillis() - entity.getInteractCooldowns().get(player);
            if (diff < 800L) return;
        }
        entity.getInteractCooldowns().put(player, System.currentTimeMillis());
        if (!entity.getEntityOptions().isEmpty()) {
            for (FakeEntityOptionImpl option : entity.getEntityOptions()) {
                if (option.getExecutingState() != FakeEntityOptionBase.ExecutingState.CLICK) continue;
                option.executeOnClick(player);
            }
        }
    }

}
