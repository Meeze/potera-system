package de.potera.teamhardcore.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.HandlerGroups;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseManager {

    private HikariDataSource hikari;
    private ExecutorService executor;

    public boolean init() {
        FileConfiguration cfg = Main.getInstance().getFileManager().getConfigFile().getConfig();
        String host = cfg.getString("MySQL.Host");
        String port = cfg.getString("MySQL.Port");
        String user = cfg.getString("MySQL.User");
        String pass = cfg.getString("MySQL.Pass");
        String database = cfg.getString("MySQL.DB");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setMinimumIdle(5);
        hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.addDataSourceProperty("serverName", host);
        hikariConfig.addDataSourceProperty("port", port);
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("user", user);
        hikariConfig.addDataSourceProperty("password", pass);
        hikariConfig.setIdleTimeout(30000L);
        hikariConfig.setLeakDetectionThreshold(60000L);
        this.hikari = new HikariDataSource(hikariConfig);
        this.executor = Executors.newCachedThreadPool();
        return true;
    }

    public void terminate() {
        this.executor.shutdown();
        HandlerGroups.stopAll();
        closeHikari();
    }

    public HikariDataSource getHikari() {
        return this.hikari;
    }

    public void closeHikari() {
        if (this.hikari != null && !this.hikari.isClosed()) {
            this.hikari.close();
        }
        this.executor.shutdown();
    }

    public void close(PreparedStatement st, ResultSet rs) {
        try {
            if (st != null) {
                st.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (Exception exception) {
        }
    }

    public void close(PreparedStatement... statements) {
        for (PreparedStatement st : statements) {
            try {
                st.close();
            } catch (Exception exception) {
            }
        }
    }

    public void close(ResultSet... resultSets) {
        for (ResultSet rs : resultSets) {
            try {
                rs.close();
            } catch (Exception exception) {
            }
        }
    }

    public void executeUpdate(String statement) {
        Connection conn = null;
        try {
            conn = this.hikari.getConnection();
            PreparedStatement st = conn.prepareStatement(statement);
            st.executeUpdate();
            close(st, null);
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("executeUpdate konnte nicht ausgeführt werden: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed())
                    conn.close();
            } catch (SQLException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void executeUpdate(PreparedStatement statement) {
        try {
            statement.executeUpdate();
            close(statement, null);
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("executeUpdate konnte nicht ausgeführt werden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(PreparedStatement statement) {
        try {
            return statement.executeQuery();
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("executeQuery konnte nicht ausgeführt werden: " + e.getMessage());

            return null;
        }
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

}
