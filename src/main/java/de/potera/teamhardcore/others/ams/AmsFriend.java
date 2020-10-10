package de.potera.teamhardcore.others.ams;

import java.util.UUID;

public class AmsFriend {

    private final UUID uuid;

    public AmsFriend(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
