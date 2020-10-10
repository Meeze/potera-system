package de.potera.teamhardcore.others;

import org.bukkit.entity.Player;

public class TPRequest {
    private final Player player;
    private final Player to;
    private final boolean here;
    private final long sent;

    public TPRequest(Player player, Player to, boolean here) {
        this.player = player;
        this.to = to;
        this.here = here;
        this.sent = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public Player getTo() {
        return to;
    }

    public boolean isHere() {
        return here;
    }

    public long getSent() {
        return sent;
    }

}
