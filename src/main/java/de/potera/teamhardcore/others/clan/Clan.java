package de.potera.teamhardcore.others.clan;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.TimedDatabaseUpdate;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Clan extends TimedDatabaseUpdate {

    private String name;

    private byte maxMembers;

    private int kills;
    private int deaths;
    private int coins;
    private int rank;
    private boolean pvpAllowed;

    private ClanMemberList memberList;

    private Location clanBase;

    public Clan(String name, boolean async) {
        super("Clan", true);
        this.name = name;
        this.kills = 0;
        this.coins = 0;
        this.deaths = 0;
        this.rank = -1;
        this.pvpAllowed = false;
        this.clanBase = null;

        this.memberList = new ClanMemberList(this);

        this.maxMembers = 10;

        if (async) {
            saveDataAsync();
        } else saveData();

        setReady(true);
    }

    public Clan(String name, boolean timedUpdate, boolean asyncLoad) {
        super("Clan", timedUpdate);
        this.name = name;
        if (asyncLoad)
            loadDataAsync();
        else loadData();
    }

    public String getClanColor() {
        if (this.name.equals("Team"))
            return "§4§l";
        int kills = this.kills;

        if (kills >= 3000)
            return "§c";
        if (kills >= 2000)
            return "§8";
        if (kills >= 1000)
            return "§d";
        if (kills >= 800)
            return "§5";
        if (kills >= 500)
            return "§6";
        if (kills >= 400)
            return "§e";
        if (kills >= 300)
            return "§a";
        if (kills >= 200)
            return "§2";
        if (kills >= 100)
            return "§9";
        if (kills >= 50)
            return "§3";
        return "§b";
    }

    public String getName() {
        return this.name;
    }

    public byte getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(byte maxMembers) {
        if (this.maxMembers == maxMembers)
            return;
        this.maxMembers = maxMembers;
        setUpdate(true);
    }

    public Location getClanBase() {
        return clanBase;
    }

    public void setClanBase(Location clanBase) {
        this.clanBase = clanBase;
        setUpdate(true);
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
        setUpdate(true);
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
        setUpdate(true);
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
        setUpdate(true);
    }

    public boolean isPvpAllowed() {
        return pvpAllowed;
    }

    public void setPvpAllowed(boolean pvpAllowed) {
        this.pvpAllowed = pvpAllowed;
        setUpdate(true);
    }

    public ClanMemberList getMemberList() {
        return memberList;
    }

    public void sendMessageToClan(String message, Player... sender) {

        List<Player> ignored = Arrays.asList(sender);

        for (UUID clanMembers : getMemberList().getMembers().keySet()) {
            Player member = Bukkit.getPlayer(clanMembers);
            if (member == null || ignored.contains(member)) continue;
            member.sendMessage(StringDefaults.CLAN_PREFIX + " " + message);
        }
    }

    @Override
    public void saveData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement stCheck = conn.prepareStatement("SELECT `ClanName` FROM `Clan` WHERE `ClanName` = ?");
            stCheck.setString(1, this.name);
            ResultSet rsCheck = Main.getInstance().getDatabaseManager().executeQuery(stCheck);
            if (!rsCheck.next()) {
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO `Clan` (`ClanName`,`ClanBase`,`Kills`,`Deaths`,`Coins`,`MaxMembers`,`PvPAllowed`) VALUES (?,?,?,?,?,?,?)");

                st.setString(1, this.name);
                st.setString(2, (this.clanBase == null ? null : Util.locationToString(this.clanBase)));
                st.setInt(3, this.kills);
                st.setInt(4, this.deaths);
                st.setInt(5, this.coins);
                st.setByte(6, this.maxMembers);
                st.setBoolean(7, this.pvpAllowed);
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            } else {
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE `Clan` SET `ClanBase` = ?, `Kills` = ?, `Deaths` = ?, `Coins` = ?, `MaxMembers` = ?, `PvPAllowed` = ? WHERE `ClanName` = ?");

                st.setString(1, (this.clanBase == null ? null : Util.locationToString(this.clanBase)));
                st.setInt(2, this.kills);
                st.setInt(3, this.deaths);
                st.setInt(4, this.coins);
                st.setByte(5, this.maxMembers);
                st.setBoolean(6, this.pvpAllowed);
                st.setString(7, this.name);
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            }
            Main.getInstance().getDatabaseManager().close(stCheck, rsCheck);
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
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `Clan` WHERE `ClanName` = ?");
            st.setString(1, this.name);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                this.kills = rs.getInt("Kills");
                this.deaths = rs.getInt("Deaths");
                this.coins = rs.getInt("Coins");
                this.maxMembers = rs.getByte("MaxMembers");
                this.pvpAllowed = rs.getBoolean("PvPAllowed");

                if (rs.getString("ClanBase") != null)
                    this.clanBase = Util.stringToLocation(rs.getString("ClanBase"));

                this.memberList = new ClanMemberList(this);
                //    this.clanChest = new ClanChest(this, false);
            }
            Main.getInstance().getDatabaseManager().close(st, rs);
            setReady(true);
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

    @Override
    public void deleteData() {
        getHandlerGroup().removeHandler(this);
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("DELETE FROM `Clan` WHERE `ClanName` = ?");
            st.setString(1, this.name);
            Main.getInstance().getDatabaseManager().executeUpdate(st);
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
}
