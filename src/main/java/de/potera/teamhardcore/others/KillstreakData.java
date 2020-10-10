package de.potera.teamhardcore.others;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillstreakData {

    private final UUID uuid;
    private final Map<UUID, KillstreakEntry<UUID>> playerTrack;
    private final Map<String, KillstreakEntry<String>> ipTrack;
    private int streak;

    public KillstreakData(UUID uuid) {
        this.playerTrack = new HashMap<>();
        this.ipTrack = new HashMap<>();


        this.uuid = uuid;
        this.streak = 0;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public boolean addStreak(Player target) {
        UUID targetUuid = target.getUniqueId();
        String ip = target.getAddress().getAddress().getHostAddress();
        if (!this.playerTrack.containsKey(targetUuid) && !this.ipTrack.containsKey(ip)) {
            this.playerTrack.put(targetUuid, new KillstreakEntry<>(targetUuid));
            this.ipTrack.put(ip, new KillstreakEntry<>(ip));
            this.streak++;
            return true;
        }

        boolean decision = true;
        KillstreakEntry<UUID> playerEntry = this.playerTrack.getOrDefault(targetUuid, null);
        KillstreakEntry<String> ipEntry = this.ipTrack.getOrDefault(ip, null);

        if (playerEntry != null) {
            boolean playerDecision = true;
            long firstKillTime = playerEntry.getFirstKillTime();
            int killAmount = playerEntry.getKillAmount() + 1;
            if (killAmount > 3) {
                long diff = System.currentTimeMillis() - firstKillTime;
                playerEntry.setFirstKillTime(System.currentTimeMillis());
                if (diff <= 3600000L) {
                    playerEntry.setKillAmount(killAmount);
                    playerDecision = false;
                    decision = false;
                } else {
                    playerEntry.setKillAmount(1);
                }
            }
            if (playerDecision)
                playerEntry.setKillAmount(killAmount);
        } else {
            this.playerTrack.put(targetUuid, new KillstreakEntry<>(targetUuid));
        }

        if (ipEntry != null) {
            boolean ipDecision = true;
            long firstKillTime = ipEntry.getFirstKillTime();
            int killAmount = ipEntry.getKillAmount() + 1;
            if (killAmount >= 3) {
                long diff = System.currentTimeMillis() - firstKillTime;
                ipEntry.setFirstKillTime(System.currentTimeMillis());
                if (diff <= 3600000L) {
                    ipEntry.setKillAmount(killAmount);
                    ipDecision = false;
                    decision = false;
                } else {
                    ipEntry.setKillAmount(1);
                }
            }
            if (ipDecision) {
                ipEntry.setKillAmount(killAmount);
            }
        }
        if (decision) {
            this.streak++;
        }

        return decision;
    }

    public void removeStreak() {
        this.streak = 0;
    }

    public int getStreak() {
        return this.streak;
    }

    public static class KillstreakEntry<T> {
        private final T identifier;
        private long firstKillTime;
        private int killAmount;

        public KillstreakEntry(T identifier) {
            this.identifier = identifier;
            this.firstKillTime = System.currentTimeMillis();
            this.killAmount = 1;
        }

        public T getIdentifier() {
            return this.identifier;
        }

        public long getFirstKillTime() {
            return this.firstKillTime;
        }

        public void setFirstKillTime(long firstKillTime) {
            this.firstKillTime = firstKillTime;
        }

        public int getKillAmount() {
            return this.killAmount;
        }

        public void setKillAmount(int killAmount) {
            this.killAmount = killAmount;
        }
    }

}
