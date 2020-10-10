package de.potera.fakemobs.util;

import de.potera.fakemobs.FakeMobsPlugin;
import org.bukkit.entity.Player;

import java.util.List;

public class LookUpdate implements Runnable {

    @Override
    public void run() {
        try {
            for (FakeMob mob : FakeMobsPlugin.getMobs()) {
                if (!mob.isPlayerLook()) continue;
                List<Player> players = mob.getNearbyPlayers(5D);
                for (Player p : players)
                    mob.sendHeadRotationPacket(p, p.getLocation());
            }
        } catch (Exception e) {
        }
    }

}
