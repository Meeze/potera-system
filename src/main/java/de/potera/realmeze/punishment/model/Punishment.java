package de.potera.realmeze.punishment.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Data
@Entity
public class Punishment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID punishmentId;
    private String reason;
    @Temporal(TemporalType.TIMESTAMP)
    private Instant issuedAt;
    @Temporal(TemporalType.TIMESTAMP)
    private Instant expiresAt;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID punishmentIssuer;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID punishmentReceiver;
    @Enumerated(value = EnumType.STRING)
    private PunishmentType punishmentType;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Punishment other = (Punishment) obj;
        return Objects.equals(other.getPunishmentId(), getPunishmentId());
    }

}

