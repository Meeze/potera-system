package de.potera.teamhardcore.others;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Support {

    private final Map<Player, SupportRole> players;

    public Support(Player supporter, Player member) {
        this.players = new HashMap<>();
        this.players.put(member, SupportRole.MEMBER);
        this.players.put(supporter, SupportRole.SUPPORTER);
    }

    public List<Player> getPlayers(SupportRole role) {
        List<Player> players = new ArrayList<>();
        for (Map.Entry<Player, SupportRole> entryPlayers : this.players.entrySet()) {
            if (entryPlayers.getValue() == role)
                players.add(entryPlayers.getKey());
        }
        return players;
    }

    public Map<Player, SupportRole> getSupportPlayers() {
        return this.players;
    }

    public enum SupportRole {
        MEMBER,
        SUPPORTER
    }

}
