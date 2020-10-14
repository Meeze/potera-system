package de.potera.realmeze.voucher.model.content;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
public class MoneyContent extends Content {
    private long amount;

    @Override
    public void reward(Player player) {

    }
}
