package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.others.clan.ClanMember;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClanManager {

    private final Map<String, Clan> clans;
    private final Map<UUID, ClanMember> clanMembers;
    private final Map<UUID, List<Clan>> clanRequests;

    private ScheduledExecutorService executorService;

    public ClanManager() {
        this.clans = new HashMap<>();
        this.clanMembers = new HashMap<>();
        this.clanRequests = new HashMap<>();
        this.executorService = Executors.newSingleThreadScheduledExecutor();

        Main.getInstance().getDatabaseManager().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `Clan` (`ClanName` VARCHAR(16), `ClanBase` TEXT, `Kills` INT, `Deaths` INT, `Coins` INT, `MaxMembers` TINYINT, `PvPAllowed` BOOLEAN NOT NULL DEFAULT FALSE, UNIQUE KEY(`ClanName`)) CHARACTER SET = utf8");


        Main.getInstance().getDatabaseManager().executeUpdate(
                "CREATE TABLE IF NOT EXISTS `ClanMember` (`ClanName` VARCHAR(16), `UUID` VARCHAR(36), `LastSeenName` VARCHAR(16), `Rank` VARCHAR(10), UNIQUE KEY(`UUID`)) CHARACTER SET = utf8");


        loadAllClans();
        loadClanMembersForOnlines();
        startRankingUpdater();
    }

    private void loadAllClans() {
        this.clans.clear();
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT `ClanName` FROM `Clan`");
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                String clanName = rs.getString("ClanName");
                Clan clan = new Clan(clanName, true, false);
                this.clans.put(clanName, clan);
            }

            Main.getInstance().getDatabaseManager().close(st, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed())
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadClanMembersForOnlines() {
        for (Player online : Bukkit.getOnlinePlayers())
            loadClanMember(online.getUniqueId());
    }

    private void startRankingUpdater() {
        this.executorService.scheduleAtFixedRate(() -> {
            Connection conn = null;
            try {
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement st = conn.prepareStatement("SELECT `ClanName` FROM `Clan` ORDER BY `Kills` DESC");
                ResultSet rs = Main.getInstance().getDatabaseManager().executeQuery(st);

                int pointer = 1;

                while (rs.next()) {
                    String clanName = rs.getString("ClanName");
                    Clan clan = getClan(clanName);

                    if (clan == null) {
                        Main.getInstance().getLogger().warning(
                                "Fehler beim Updaten des Clan Rankings! Clan existiert nicht mehr");
                        continue;
                    }
                    clan.setRank(pointer);
                    pointer++;
                }

                Main.getInstance().getDatabaseManager().close(st, rs);
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (conn != null && !conn.isClosed())
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 1L, 30L, TimeUnit.SECONDS);
    }

    public void onDisable() {
        this.executorService.shutdown();
    }

    public void createClan(String name, UUID owner, String ownerName) {
        if (this.clans.containsKey(name) || getClan(name) != null) return;

        Clan clan = new Clan(name, true);
        addClanMember(owner, ownerName, clan, ClanMember.ClanRank.OWNER);
        this.clans.put(name, clan);
    }

    public void deleteClan(String name) {
        Clan clan = getClan(name);
        if (clan == null) return;

        Map<UUID, ClanMember> membersCopy = new HashMap<>(clan.getMemberList().getMembers());
        for (Map.Entry<UUID, ClanMember> entryMembers : membersCopy.entrySet()) {
            if (getClanMember(entryMembers.getKey()) != null) {
                removeClanMember(entryMembers.getKey());
                continue;
            }
            entryMembers.getValue().deleteDataAsync();
            clan.getMemberList().removeMember(entryMembers.getValue());
        }

        this.clans.remove(name);
    }

    public void addClanMember(UUID uuid, String lastSeenName, Clan clan, ClanMember.ClanRank rank) {
        if (this.clanMembers.containsKey(uuid)) return;
        ClanMember member = new ClanMember(uuid, lastSeenName, clan, rank);
        member.saveDataAsync();
        clan.getMemberList().addMember(member);
        this.clanMembers.put(uuid, member);
    }

    public void removeClanMember(UUID uuid) {
        ClanMember member = this.clanMembers.get(uuid);

        if (member == null) return;

        Clan clan = member.getClan();
        member.deleteDataAsync();
        clan.getMemberList().removeMember(member);
        unloadClanMember(uuid);
    }

    public void unloadClanMember(UUID uuid) {
        if (!this.clanMembers.containsKey(uuid))
            return;
        this.clanMembers.remove(uuid);
    }

    public void loadClanMember(UUID uuid) {
        if (this.clanMembers.containsKey(uuid)) return;

        Main.getInstance().getDatabaseManager().getExecutor().execute(() -> {
            Connection conn = null;
            try {
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement st = conn.prepareStatement("SELECT `ClanName` FROM `ClanMember` WHERE `UUID` = ?");
                st.setString(1, uuid.toString());
                ResultSet rs = st.executeQuery();
                if (!rs.next()) {
                    Main.getInstance().getDatabaseManager().close(st, rs);
                    return;
                }
                String clanName = rs.getString("ClanName");
                Clan clan = getClan(clanName);
                if (clan == null) {
                    Main.getInstance().getLogger().warning(
                            "ClanMember konnte nicht geladen werden! (Clan existiert nicht)");
                    Main.getInstance().getDatabaseManager().close(st, rs);
                    return;
                }
                ClanMember member = clan.getMemberList().getMember(uuid);
                if (member == null) {
                    Main.getInstance().getLogger().warning(
                            "ClanMember konnte nicht geladen werden! (ClanMember existiert nicht in Clan)");
                    Main.getInstance().getDatabaseManager().close(st, rs);
                    return;
                }
                Player online = Bukkit.getPlayer(uuid);
                if (online == null) {
                    Main.getInstance().getLogger().warning(
                            "ClanMember konnte nicht geladen werden! (Spieler nicht online)");
                    Main.getInstance().getDatabaseManager().close(st, rs);
                    return;
                }
                member.setLastSeenName(online.getName());
                this.clanMembers.put(uuid, member);
                Main.getInstance().getDatabaseManager().close(st, rs);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (conn != null && !conn.isClosed())
                        conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public boolean hasClan(UUID uuid) {
        return this.clanMembers.containsKey(uuid);
    }

    public void addRequest(UUID uuid, Clan clan) {
        List<Clan> requests = this.clanRequests.getOrDefault(uuid, new ArrayList<>());

        if (requests.contains(clan)) return;
        requests.add(clan);
        this.clanRequests.put(uuid, requests);
    }

    public void removeRequest(UUID uuid, Clan clan) {
        if (getRequests(uuid).isEmpty()) return;
        List<Clan> requests = getRequests(uuid);

        if (!requests.contains(clan)) return;
        requests.remove(clan);
        this.clanRequests.put(uuid, requests);
    }

    public boolean hasRequest(UUID uuid, String name) {
        if (!this.clanRequests.containsKey(uuid)) return false;

        List<Clan> requests = this.clanRequests.get(uuid);

        if (requests.isEmpty()) return false;

        for (Clan clan : requests)
            if (clan.getName().equalsIgnoreCase(name)) return true;
        return false;
    }

    public List<Clan> getRequests(UUID uuid) {
        if (!this.clanRequests.containsKey(uuid)) return new ArrayList<>();
        return this.clanRequests.get(uuid);
    }

    public Clan getClan(String name) {
        for (Map.Entry<String, Clan> entryClans : this.clans.entrySet()) {
            Clan clan = entryClans.getValue();
            if (clan.getName().equalsIgnoreCase(name))
                return clan;
        }
        return null;
    }

    public Clan getClan(UUID clanMember) {
        if (!this.clanMembers.containsKey(clanMember))
            return null;
        ClanMember cMember = this.clanMembers.get(clanMember);
        return cMember.getClan();
    }

    public ClanMember getClanMember(UUID uuid) {
        if (!this.clanMembers.containsKey(uuid))
            return null;
        return this.clanMembers.get(uuid);
    }

    public Map<String, Clan> getClans() {
        return clans;
    }

    public Map<UUID, ClanMember> getClanMembers() {
        return clanMembers;
    }

    public Map<UUID, List<Clan>> getClanRequests() {
        return clanRequests;
    }
}
