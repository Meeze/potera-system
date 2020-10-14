package de.potera.realmeze.voucher.model.content;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Setter
@Getter
public class SpawnerContent extends Content {

    private long amount;
    @Enumerated(EnumType.STRING)
    private EntityType type;

    @Override
    public void reward(Player player) {

    }
}
