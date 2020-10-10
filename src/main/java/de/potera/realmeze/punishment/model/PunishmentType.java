package de.potera.realmeze.punishment.model;

import lombok.Getter;

@Getter
public enum PunishmentType {

    KICK("KICK"),
    MUTE("MUTE"),
    BAN("BAN");

    private final String name;

    PunishmentType(String name) {
        this.name = name;
    }
}
