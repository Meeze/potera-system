package de.potera.rysefoxx.manager;

import de.potera.teamhardcore.files.FileBase;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter

public class ItemManager {

    private final FileBase config;

    public ItemManager() {
        this.config = new FileBase("", "chatItem");
    }

    public boolean canUse(Player player) {
        if (this.config.getConfig().getString(player.getUniqueId().toString()) == null) return true;
        if (this.config.getConfig().getLong(player.getUniqueId().toString() + ".time") < System.currentTimeMillis())
            return true;
        return false;
    }

    public int getCoolDown() {
        return this.config.getConfig().getInt("cooldown");
    }

    public long getPlayerCoolDown(Player player) {
        if (this.config.getConfig().getString(player.getUniqueId().toString()) == null) return -1L;
        return this.config.getConfig().getLong(player.getUniqueId().toString() + ".time");

    }

    public void setPlayerCoolDown(Player player, long time) {
        this.config.getConfig().set(player.getUniqueId().toString() + ".time", (System.currentTimeMillis() + time * 1000L));
        this.config.saveConfig();
    }

    public boolean isActive() {
        return this.config.getConfig().getBoolean("active");
    }

    public void setActive(boolean active) {
        this.config.getConfig().set("active", active);
        this.config.saveConfig();
    }

    public void setCoolDown(long time) {
        this.config.getConfig().set("cooldown", time);
        this.config.saveConfig();
    }

}
