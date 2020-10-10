package de.potera.teamhardcore.users;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.CompletionState;
import de.potera.teamhardcore.db.CompletionStateImpl;
import de.potera.teamhardcore.others.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHomes implements CompletionState {

    private User user;
    private Map<String, Home> homes;
    private CompletionState completionState;

    public UserHomes(User user) {
        this(user, true);
    }

    public UserHomes(User user, boolean asyncLoad) {
        this.homes = new HashMap<>();
        this.completionState = new CompletionStateImpl();
        this.user = user;
        loadHomes(asyncLoad);
    }

    private void loadHomes(boolean async) {
        Runnable toExecute = () -> {
            Connection conn = null;
            try {
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement st = conn.prepareStatement("SELECT * FROM `Homes` WHERE `UUID` = ?");
                st.setString(1, this.user.getUuid().toString());
                ResultSet rs = st.executeQuery();
                while (rs.next()) {
                    String name = rs.getString("Name");
                    double x = rs.getDouble("X");
                    double y = rs.getDouble("Y");
                    double z = rs.getDouble("Z");
                    float yaw = rs.getFloat("Yaw");
                    float pitch = rs.getFloat("Pitch");
                    String worldName = rs.getString("World");
                    long creationDate = rs.getLong("CreationDate");
                    long lastTeleportDate = rs.getLong("LastTeleportDate");
                    World world = Bukkit.getWorld(worldName);
                    Home home = new Home(this.user.getUuid(), name, new Location(world, x, y, z, yaw, pitch),
                            creationDate, lastTeleportDate);
                    this.homes.put(name, home);
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
        };
        if (async) {
            Main.getInstance().getDatabaseManager().getExecutor().execute(toExecute);
        } else {
            toExecute.run();
        }
    }

    public void addHome(String name, Location loc, boolean async) {
        if (this.homes.containsKey(name))
            return;
        Home home = new Home(this.user.getUuid(), name, loc, System.currentTimeMillis(), -1L);
        this.homes.put(name, home);
        Runnable toExecute = () -> {
            Connection conn = null;
            try {
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "INSERT INTO `Homes` (`UUID`,`Name`,`X`,`Y`,`Z`,`Yaw`,`Pitch`,`World`,`CreationDate`,`LastTeleportDate`) VALUES (?,?,?,?,?,?,?,?,?,?)");
                st.setString(1, this.user.getUuid().toString());
                st.setString(2, home.getName());
                st.setDouble(3, home.getPosition().getX());
                st.setDouble(4, home.getPosition().getY());
                st.setDouble(5, home.getPosition().getZ());
                st.setFloat(6, home.getPosition().getYaw());
                st.setFloat(7, home.getPosition().getPitch());
                st.setString(8, home.getPosition().getWorld().getName());
                st.setLong(9, home.getCreationDate());
                st.setLong(10, home.getLastTeleportDate());
                Main.getInstance().getDatabaseManager().executeUpdate(st);
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
        };
        if (async) {
            Main.getInstance().getDatabaseManager().getExecutor().execute(toExecute);
        } else {
            toExecute.run();
        }
    }

    public void removeHome(String name, boolean async) {
        if (!this.homes.containsKey(name))
            return;
        this.homes.remove(name);
        Runnable toExecute = () -> {
            Connection conn = null;
            try {
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement st = conn.prepareStatement("DELETE FROM `Homes` WHERE `UUID` = ? AND `Name` = ?");
                st.setString(1, this.user.getUuid().toString());
                st.setString(2, name);
                Main.getInstance().getDatabaseManager().executeUpdate(st);
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
        };
        if (async) {
            Main.getInstance().getDatabaseManager().getExecutor().execute(toExecute);
        } else {
            toExecute.run();
        }
    }

    public void updateLastTeleportTime(String name, boolean async) {
        if (!this.homes.containsKey(name))
            return;
        Home home = this.homes.get(name);
        Runnable toExecute = () -> {
            Connection conn = null;
            try {
                conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE `Homes` SET `LastTeleportDate` = ? WHERE `UUID` = ? AND `Name` = ?");
                st.setLong(1, home.getLastTeleportDate());
                st.setString(2, this.user.getUuid().toString());
                st.setString(3, name);
                Main.getInstance().getDatabaseManager().executeUpdate(st);
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
        };
        if (async) {
            Main.getInstance().getDatabaseManager().getExecutor().execute(toExecute);
        } else {
            toExecute.run();
        }
    }

    public Home getHome(String name) {
        if (this.homes == null || !this.homes.containsKey(name))
            return null;
        return this.homes.get(name);
    }

    public User getUser() {
        return this.user;
    }

    public Map<String, Home> getHomes() {
        return this.homes;
    }

    public List<Runnable> getReadyExecutors() {
        return this.completionState.getReadyExecutors();
    }

    public void addReadyExecutor(Runnable exec) {
        this.completionState.addReadyExecutor(exec);
    }

    public boolean isReady() {
        return this.completionState.isReady();
    }

    public void setReady(boolean ready) {
        this.completionState.setReady(ready);
    }
}
