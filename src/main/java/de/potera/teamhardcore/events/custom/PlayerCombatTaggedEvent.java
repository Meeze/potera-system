package de.potera.teamhardcore.events.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerCombatTaggedEvent extends PlayerEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private boolean tagged;
    private boolean cancelled;

    public PlayerCombatTaggedEvent(Player who, boolean tagged) {
        super(who);
        this.tagged = tagged;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isTagged() {
        return this.tagged;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
