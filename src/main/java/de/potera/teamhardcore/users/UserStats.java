package de.potera.teamhardcore.users;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.TimedDatabaseUpdate;
import de.potera.teamhardcore.others.StatsPeriod;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStats extends TimedDatabaseUpdate {

    private final User user;
    private final int[] kills = new int[3];
    private final int[] deaths = new int[3];
    private final long[] playtime = new long[3];
    private long timeCreated = System.currentTimeMillis();
    private long elo = 250;
    private long kopfgeld = 0;
    private int killstreak = 0;
    private int pvpCoins;

    public UserStats(User user) {
        this(user, true, true);
    }

    public UserStats(User user, boolean timedUpdate, boolean asyncLoad) {
        super("UserStats", timedUpdate, 30000L);
        setForceUpdate(true);
        this.user = user;
        if (asyncLoad) {
            loadDataAsync();
        } else {
            loadData();
        }
    }

    public int getPvPCoins() {
        return pvpCoins;
    }

    public void setPvPCoins(int pvpCoins) {
        this.pvpCoins = pvpCoins;
        setUpdate(true);
    }

    public void addPvPCoins(int pvpCoins) {
        setPvPCoins(getPvPCoins() + pvpCoins);
    }

    public int getKills(StatsPeriod period) {
        return this.kills[period.getIndex()];
    }

    public int getDeaths(StatsPeriod period) {
        return this.deaths[period.getIndex()];
    }

    public int getKillstreak() {
        return this.killstreak;
    }

    public void setKillstreak(int killstreak) {
        this.killstreak = killstreak;
        setUpdate(true);
    }

    public long getKopfgeld() {
        return this.kopfgeld;
    }

    public void setKopfgeld(long kopfgeld) {
        this.kopfgeld = kopfgeld;
        setUpdate(true);
    }

    public long getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
        if (elo < 0)
            this.elo = 0;
        setUpdate(true);
    }

    public long getPlaytime(StatsPeriod period) {
        return this.playtime[period.getIndex()];
    }

    public double getKD(StatsPeriod period) {
        if (this.kills[period.getIndex()] <= 0)
            return 0.0D;
        if (this.deaths[period.getIndex()] <= 0)
            return getKills(period);
        BigDecimal dec = new BigDecimal(this.kills[period.getIndex()] / this.deaths[period.getIndex()]);
        dec = dec.setScale(2, 4);
        return dec.doubleValue();
    }

    public void setKills(int kills, StatsPeriod period) {
        this.kills[period.getIndex()] = kills;
        setUpdate(true);
    }

    public void addKills(int kills) {
        this.kills[0] = this.kills[0] + kills;
        this.kills[1] = this.kills[1] + kills;
        this.kills[2] = this.kills[2] + kills;
        setUpdate(true);
    }

    public void setDeaths(int deaths, StatsPeriod period) {
        this.deaths[period.getIndex()] = deaths;
        setUpdate(true);
    }

    public void addDeaths(int deaths) {
        this.deaths[0] = this.deaths[0] + deaths;
        this.deaths[1] = this.deaths[1] + deaths;
        this.deaths[2] = this.deaths[2] + deaths;
        setUpdate(true);
    }

    public void setPlaytime(long time, StatsPeriod period) {
        this.playtime[period.getIndex()] = time;
        setUpdate(true);
    }

    public void calculateNewPlaytime() {
        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime - this.timeCreated;
        this.playtime[0] = this.playtime[0] + timeDiff;
        this.playtime[1] = this.playtime[1] + timeDiff;
        this.playtime[2] = this.playtime[2] + timeDiff;
        this.timeCreated = currentTime;
        setUpdate(true);
    }

    @Override
    public void saveData() {
        if (this.user.getPlayer() != null)
            calculateNewPlaytime();
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement stCheck = conn.prepareStatement("SELECT * FROM `Stats` WHERE `UUID` = ?");
            stCheck.setString(1, this.user.getUuid().toString());
            ResultSet rsCheck = Main.getInstance().getDatabaseManager().executeQuery(stCheck);
            if (!rsCheck.next()) {
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO `Stats` (UUID, Kills, DKills, WKills, Deaths, DDeaths, WDeaths, Killstreak, Kopfgeld, PvPCoins, Playtime, DPlaytime, WPlaytime, Elo) VALUES (?,0,0,0,0,0,0,0,0,0,0,0,0,?)");

                st.setString(1, this.user.getUuid().toString());
                st.setLong(2, this.elo);
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            } else {
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE `Stats` SET `Kills` = ?, `DKills` = ?, `WKills` = ?, `Deaths` = ?, `DDeaths` = ?, `WDeaths` = ?, `Killstreak` = ?, `PvPCoins` = ?, `Kopfgeld` = ?, `Playtime` = ?, `DPlaytime` = ?, `WPlaytime` = ?, `Elo` = ? WHERE `UUID` = ?");

                st.setInt(1, this.kills[0]);
                st.setInt(2, this.kills[1]);
                st.setInt(3, this.kills[2]);
                st.setInt(4, this.deaths[0]);
                st.setInt(5, this.deaths[1]);
                st.setInt(6, this.deaths[2]);
                st.setInt(7, this.killstreak);
                st.setInt(8, this.pvpCoins);
                st.setLong(9, this.kopfgeld);
                st.setLong(10, this.playtime[0]);
                st.setLong(11, this.playtime[1]);
                st.setLong(12, this.playtime[2]);
                st.setLong(13, this.elo);
                st.setString(14, this.user.getUuid().toString());
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
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `Stats` WHERE `UUID` = ?");
            st.setString(1, this.user.getUuid().toString());
            ResultSet rs = Main.getInstance().getDatabaseManager().executeQuery(st);

            if (!rs.next()) {
                saveData();
            } else {
                this.kills[0] = rs.getInt("Kills");
                this.kills[1] = rs.getInt("DKills");
                this.kills[2] = rs.getInt("WKills");
                this.deaths[0] = rs.getInt("Deaths");
                this.deaths[1] = rs.getInt("DDeaths");
                this.deaths[2] = rs.getInt("WDeaths");
                this.killstreak = rs.getInt("Killstreak");
                this.kopfgeld = rs.getLong("Kopfgeld");
                this.playtime[0] = rs.getLong("Playtime");
                this.playtime[1] = rs.getLong("DPlaytime");
                this.playtime[2] = rs.getLong("WPlaytime");
                this.pvpCoins = rs.getInt("PvPCoins");
                this.elo = rs.getInt("Elo");
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
            PreparedStatement st = conn.prepareStatement("DELETE FROM `Stats` WHERE `UUID` = ?");
            st.setString(1, this.user.getUuid().toString());
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
