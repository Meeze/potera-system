package de.potera.realmeze.punishment.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Punishment {

    private UUID punishmentId;
    private String reason;
    private Instant issuedAt;
    private Instant expiresAt;
    private UUID punishmentIssuer;
    private UUID punishmentReceiver;
    private PunishmentType punishmentType;

}

