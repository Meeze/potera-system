package de.potera.teamhardcore.others.clan;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.TimedDatabaseUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ClanMember extends TimedDatabaseUpdate {
    private UUID uuid;
    private String lastSeenName;
    private Clan clan;
    private ClanRank rank;

    public ClanMember(UUID uuid, String lastSeenName, Clan clan, ClanRank rank) {
        super("ClanMember", true);
        this.uuid = uuid;
        this.lastSeenName = lastSeenName;
        this.clan = clan;
        this.rank = rank;
        setReady(true);
    }

    public ClanMember(UUID uuid, boolean timedUpdate, boolean loadAsync) {
        super("ClanMember", timedUpdate);
        this.uuid = uuid;
        if (loadAsync) {
            loadDataAsync();
        } else {
            loadData();
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getLastSeenName() {
        return this.lastSeenName;
    }

    public void setLastSeenName(String lastSeenName) {
        this.lastSeenName = lastSeenName;
        setUpdate(true);
    }

    public Clan getClan() {
        return this.clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
        setUpdate(true);
    }

    public ClanRank getRank() {
        return this.rank;
    }

    public void setRank(ClanRank rank) {
        this.rank = rank;
        setUpdate(true);
    }

    @Override
    public void saveData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement stCheck = conn.prepareStatement("SELECT `ClanName` FROM `ClanMember` WHERE `UUID` = ?");
            stCheck.setString(1, this.uuid.toString());
            ResultSet rs = Main.getInstance().getDatabaseManager().executeQuery(stCheck);

            if (!rs.next()) {
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO `ClanMember` (`ClanName`,`UUID`,`LastSeenName`,`Rank`) VALUES (?,?,?,?)");
                st.setString(1, this.clan.getName());
                st.setString(2, this.uuid.toString());
                st.setString(3, this.lastSeenName);
                st.setString(4, this.rank.name());
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            } else {
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE `ClanMember` SET `ClanName` = ?, `LastSeenName` = ?, `Rank` = ? WHERE `UUID` = ?");
                st.setString(1, this.clan.getName());
                st.setString(2, this.lastSeenName);
                st.setString(3, this.rank.name());
                st.setString(4, this.uuid.toString());
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            }

            Main.getInstance().getDatabaseManager().close(stCheck, rs);
        } catch (Exception e) {
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

    @Override
    public void loadData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `ClanMember` WHERE `UUID` = ?");
            st.setString(1, this.uuid.toString());
            ResultSet rs = st.executeQuery();
            if (!rs.next()) {
                this.lastSeenName = "Unknown";
                this.clan = null;
                this.rank = null;
            } else {
                this.clan = Main.getInstance().getClanManager().getClan(rs.getString("ClanName"));
                this.lastSeenName = rs.getString("LastSeenName");
                this.rank = (rs.getString("Rank") != null) ? ClanRank.getByName(rs.getString("Rank")) : null;
            }
            Main.getInstance().getDatabaseManager().close(st, rs);
            setReady(true);
        } catch (Exception e) {
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

    @Override
    public void deleteData() {
        getHandlerGroup().removeHandler(this);
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM `ClanMember` WHERE `UUID` = ?");
            st.setString(1, this.uuid.toString());
            Main.getInstance().getDatabaseManager().executeUpdate(st);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum ClanRank {
        MEMBER(0, "Member", "§e"),
        MOD(1, "Mod", "§5"),
        OWNER(2, "Owner", "§4");

        private int rankPosition;
        private String name;
        private String color;

        ClanRank(int rankPosition, String name, String color) {
            this.rankPosition = rankPosition;
            this.name = name;
            this.color = color;
        }

        public static ClanRank getByName(String name) {
            for (ClanRank ranks : values()) {
                if (ranks.name().equalsIgnoreCase(name))
                    return ranks;
            }
            return null;
        }

        public int getRankPosition() {
            return this.rankPosition;
        }

        public String getName() {
            return this.name;
        }

        public String getColor() {
            return this.color;
        }
    }

}
