package de.potera.realmeze.voucher.model.content;

import org.bukkit.entity.Player;

import javax.persistence.Embeddable;

@Embeddable
public abstract class Content {
   public abstract void reward(Player player);
}
