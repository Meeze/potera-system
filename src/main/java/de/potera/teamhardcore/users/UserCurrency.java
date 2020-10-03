package de.potera.teamhardcore.users;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.TimedDatabaseUpdate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserCurrency extends TimedDatabaseUpdate {

    private User user;

    private long money;

    public UserCurrency(User user) {
        this(user, true, true);
    }

    public UserCurrency(User user, boolean async, boolean timedUpdate) {
        super("Currency", timedUpdate);

        this.user = user;
        this.money = 0L;

        if (async)
            loadDataAsync();
        else loadData();
    }

    public void setMoney(long money) {
        this.money = money;
        setUpdate(true);
    }

    public void addMoney(long money) {
        if (money <= 0L) return;
        setMoney(this.money + money);
    }

    public void removeMoney(long money) {
        if (money <= 0L) return;
        if (this.money - money <= 0L)
            setMoney(0L);
        else setMoney(this.money - money);
    }

    public long getMoney() {
        return money;
    }

    @Override
    public void saveData() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement stCheck = conn.prepareStatement("SELECT `UUID` FROM `Currency` WHERE `UUID` = ?");
            stCheck.setString(1, this.user.getUuid().toString());
            ResultSet rsCheck = Main.getInstance().getDatabaseManager().executeQuery(stCheck);
            if (!rsCheck.next()) {
                PreparedStatement st = conn.prepareStatement("INSERT INTO `Currency` (`UUID`, `Money`) VALUES (?, 0)");
                st.setString(1, this.user.getUuid().toString());
                Main.getInstance().getDatabaseManager().executeUpdate(st);
            } else {
                PreparedStatement st = conn.prepareStatement("UPDATE `Currency` SET `Money` = ? WHERE `UUID` = ?");
                st.setLong(1, this.money);
                st.setString(2, this.user.getUuid().toString());
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
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `Currency` WHERE `UUID` = ?");
            st.setString(1, this.user.getUuid().toString());
            ResultSet rs = Main.getInstance().getDatabaseManager().executeQuery(st);
            if (!rs.next()) {
                saveData();
            } else {
                this.money = rs.getLong("Money");
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
            PreparedStatement st = conn.prepareStatement("DELETE FROM `Currency` WHERE `UUID` = ?");
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
