package de.potera.teamhardcore.users;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.TimedDatabaseUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMine extends TimedDatabaseUpdate {

    private final User user;

    private long minePoints;
    private int level;

    private long availableTime;

    public UserMine(User user) {
        this(user, true, true);
    }

    public UserMine(User user, boolean timedUpdate, boolean asyncLoad) {
        super("Mines", timedUpdate);
        this.user = user;
        this.minePoints = 0L;
        this.level = 1;

        if (asyncLoad) {
            loadDataAsync();
        } else {
            loadData();
        }

        checkTime();
    }

    public void checkTime() {
        if (this.availableTime <= 0L) {
            return;
        }

        setAvailableTime(getAvailableTime() - 1000L);
    }

    public long getAvailableTime() {
        return availableTime;
    }

    public void setAvailableTime(long availableTime) {
        this.availableTime = availableTime;

        if (this.availableTime < 0L)
            this.availableTime = 0L;

        setUpdate(true);
    }

    public void addAvailableTime(long availableTime) {
        setAvailableTime(getAvailableTime() + availableTime);
    }

    public long getMinePoints() {
        return minePoints;
    }

    public void setMinePoints(long minePoints) {
        this.minePoints = minePoints;
        setUpdate(true);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        setUpdate(true);
    }

    @Override
    public void saveData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement stCheck = conn.prepareStatement("SELECT `UUID` FROM `Mines` WHERE `UUID` = ?");
            stCheck.setString(1, this.user.getUuid().toString());
            ResultSet rsCheck = Main.getInstance().getDatabaseManager().executeQuery(stCheck);
            if (!rsCheck.next()) {
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO `Mines` (`UUID`, `Level`, `Minepoints`, `AvailableTime`) VALUES (?, 1, 0, 60000)");
                st.setString(1, this.user.getUuid().toString());
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            } else {
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE `Mines` SET `Level` = ?, `Minepoints` = ?, `AvailableTime` = ? WHERE `UUID` = ?");
                st.setInt(1, this.level);
                st.setLong(2, this.minePoints);
                st.setLong(3, this.availableTime);
                st.setString(4, this.user.getUuid().toString());
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            }
            Main.getInstance().getDatabaseManager().close(stCheck, rsCheck);
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
    public void loadData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `Mines` WHERE `UUID` = ?");
            st.setString(1, this.user.getUuid().toString());
            ResultSet rs = Main.getInstance().getDatabaseManager().executeQuery(st);
            if (!rs.next()) {
                saveData();
            } else {
                this.level = rs.getInt("Level");
                this.minePoints = rs.getLong("Minepoints");
                this.availableTime = rs.getLong("AvailableTime");
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
            PreparedStatement st = conn.prepareStatement("DELETE FROM `Mines` WHERE `UUID` = ?");
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
