package de.potera.realmeze.voucher.model;

import de.potera.realmeze.voucher.model.content.Content;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;


@Entity
@Data
public class Voucher {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID voucherId;
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID creatorId;
    @Temporal(TemporalType.TIMESTAMP)
    private Instant createdAt;
    @Embedded
    private Content content;


}
