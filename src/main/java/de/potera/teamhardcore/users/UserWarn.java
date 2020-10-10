package de.potera.teamhardcore.users;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.TimedDatabaseUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserWarn extends TimedDatabaseUpdate {

    private final User user;
    private int warns;

    public UserWarn(User user, boolean timedUpdate, boolean asyncLoad) {
        super("UserWarn", timedUpdate, 30000L);
        this.user = user;

        if (asyncLoad) {
            loadDataAsync();
        } else {
            loadData();
        }

    }


    @Override
    public void saveData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement stCheck = conn.prepareStatement("SELECT * FROM `Warn` WHERE `UUID` = ?");
            stCheck.setString(1, this.user.getUuid().toString());
            ResultSet rsCheck = Main.getInstance().getDatabaseManager().executeQuery(stCheck);
            if (!rsCheck.next()) {
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INFO `Warn` (UUID, Warns) VALIES (?,0)");
                st.setString(1, this.user.getUuid().toString());
                st.setInt(2, this.warns);
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            } else {
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE `Warn` SET `Warns` = ? WHERE `UUID` = ?");
                st.setInt(1, this.warns);
                st.setString(2, this.user.getUuid().toString());
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
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `Warn` WHERE `UUID` = ?");
            st.setString(1, this.user.getUuid().toString());
            ResultSet rs = Main.getInstance().getDatabaseManager().executeQuery(st);
            if (!rs.next()) {
                saveData();
            } else {
                this.warns = rs.getInt("Warns");
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
            PreparedStatement st = conn.prepareStatement("DELETE FROM `Warn` WHERE `UUID` = ?");
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

    public void addWarn(int warns) {
        this.warns += warns;
        setUpdate(true);
    }

    public void deleteWarn(int warns) {
        if ((this.warns -= warns) < 0) {
            this.warns = 0;
            setUpdate(true);
            return;
        }
        this.warns -= warns;
        setUpdate(true);
    }

    public int getWarns() {
        return warns;
    }
}
