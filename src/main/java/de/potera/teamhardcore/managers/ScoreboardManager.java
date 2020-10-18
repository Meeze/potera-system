package de.potera.teamhardcore.managers;

import de.potera.rysefoxx.utils.TimeUtils;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumLiga;
import de.potera.teamhardcore.users.User;
import de.potera.teamhardcore.users.UserCurrency;
import de.potera.teamhardcore.users.UserMine;
import de.potera.teamhardcore.users.UserStats;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    private final Map<Player, Scoreboard> playerScoreboards;
    private final Map<Player, ScoreboardType> activatedBoards;
    private final Map<Player, String> playerTeams;

    public ScoreboardManager() {
        this.playerScoreboards = new HashMap<>();
        this.activatedBoards = new HashMap<>();
        this.playerTeams = new HashMap<>();

        startSidebarUpdater();
        createScoreboardForOnlines();
        updateAllScoreboards(true, true);
    }

    private void createScoreboardForOnlines() {
        for (Player all : Bukkit.getOnlinePlayers())
            createNewScoreboard(all);
    }

    public void onDisable() {
        for (Player all : Bukkit.getOnlinePlayers())
            removePlayerScoreboard(all);
    }

    public void activateScoreboard(Player p, ScoreboardType type) {
        if (!this.playerScoreboards.containsKey(p)) {
            createNewScoreboard(p);
        }
        if (this.activatedBoards.containsKey(p)) {
            deactivateScoreboard(p);
        }
        Scoreboard board = this.playerScoreboards.get(p);
        this.activatedBoards.put(p, type);

        if (type == ScoreboardType.MAIN) {
            Objective sidebar = board.registerNewObjective(type.getSidebarName(), "dummy");
            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
            sidebar.setDisplayName("§8§l► §a§lpotera");

            Team teamPlayer = board.registerNewTeam("Internal_Player");
            teamPlayer.setSuffix(p.getName());
            Team teamTrophies = board.registerNewTeam("Internal_Trophy");
            Team teamMoney = board.registerNewTeam("Internal_Money");

            teamPlayer.addEntry("§7");
            teamTrophies.addEntry("§7§7");
            teamMoney.addEntry("§7§7§7");

            sidebar.getScore(" ").setScore(8);
            sidebar.getScore("§a§lName").setScore(7);
            sidebar.getScore("§7").setScore(6);
            sidebar.getScore("  ").setScore(5);
            sidebar.getScore("§3§lLiga").setScore(4);
            sidebar.getScore("§7§7").setScore(3);
            sidebar.getScore("   ").setScore(2);
            sidebar.getScore("§6§lGeld").setScore(1);
            sidebar.getScore("§7§7§7").setScore(0);
        }

        if (type == ScoreboardType.MINES) {
            Objective sidebar = board.registerNewObjective(type.getSidebarName(), "dummy");
            sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
            sidebar.setDisplayName("§8§l► §a§lpotera");

            Team teamLevel = board.registerNewTeam("Internal_Level");
            Team teamPoints = board.registerNewTeam("Internal_Points");
            Team teamNextLevel = board.registerNewTeam("Internal_NL");
            Team teamNLText = board.registerNewTeam("Internal_NLText");
            Team teamTime = board.registerNewTeam("Internal_Time");

            teamLevel.addEntry("§7");
            teamPoints.addEntry("§7§7");
            teamNextLevel.addEntry("§7§7§7§7");
            teamNLText.addEntry("§c§lNächstes ");
            teamNLText.setSuffix("Level");
            teamTime.addEntry("§7§7§7§7§7");

            sidebar.getScore(" ").setScore(11);
            sidebar.getScore("§3§lMine-Level").setScore(10);
            sidebar.getScore("§7").setScore(9);
            sidebar.getScore("  ").setScore(8);
            sidebar.getScore("§a§lMinePunkte").setScore(7);
            sidebar.getScore("§7§7").setScore(6);
            sidebar.getScore("   ").setScore(5);
            sidebar.getScore("§c§lNächstes ").setScore(4);
            sidebar.getScore("§7§7§7§7").setScore(3);
            sidebar.getScore("").setScore(2);
            sidebar.getScore("§5§lTime").setScore(1);
            sidebar.getScore("§7§7§7§7§7").setScore(0);
        }

        updateSidebar(p);
    }

    public void deactivateScoreboard(Player p) {
        if (!this.activatedBoards.containsKey(p) || !this.playerScoreboards.containsKey(p)) {
            return;
        }
        Scoreboard board = this.playerScoreboards.get(p);
        ScoreboardType type = this.activatedBoards.get(p);
        this.activatedBoards.remove(p);

        if (type == ScoreboardType.MAIN) {
            board.getObjective(type.getSidebarName()).unregister();
            board.getTeam("Internal_Player").unregister();
            board.getTeam("Internal_Trophy").unregister();
            board.getTeam("Internal_Money").unregister();
        }

        if (type == ScoreboardType.MINES) {
            board.getObjective(type.getSidebarName()).unregister();
            board.getTeam("Internal_Level").unregister();
            board.getTeam("Internal_Points").unregister();
            board.getTeam("Internal_NL").unregister();
            board.getTeam("Internal_NLText").unregister();
            board.getTeam("Internal_Time").unregister();
        }
    }

    public void createNewScoreboard(Player p) {
        if (this.playerScoreboards.containsKey(p)) {
            return;
        }
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.playerTeams.put(p, getNewTeamName(p));

        Objective dmgIndicator = board.registerNewObjective("DamageIndicator", "health");
        dmgIndicator.setDisplaySlot(DisplaySlot.BELOW_NAME);
        dmgIndicator.setDisplayName("§c❤");

        p.setScoreboard(board);
        this.playerScoreboards.put(p, board);
        updateTeamListAllPlayers(p);
        activateScoreboard(p, ScoreboardType.MAIN);
        updateSidebar(p);
    }

    private Team createPlayerTeam(Scoreboard board, Player who) {
        String tName = this.playerTeams.get(who);
        if (board.getTeam(tName) != null)
            return board.getTeam(tName);
        Team team = board.registerNewTeam(tName);
        team.setPrefix(getTabPrefix(who));
        team.addEntry(who.getName());
        return team;
    }

    private String getNewTeamName(Player forWhom) {
        return getTabSortChar(forWhom) + Util.generateRandomKey(15);
    }

    public String getPlayerTeamName(Player p) {
        return this.playerTeams.get(p);
    }

    public void removePlayerScoreboard(Player p) {
        if (!this.playerScoreboards.containsKey(p))
            return;
        deactivateScoreboard(p);
        this.activatedBoards.remove(p);
        this.playerScoreboards.remove(p);
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void updateTeamListAllPlayers(Player forWhom) {
        for (Player all : this.playerScoreboards.keySet())
            updateTeamList(forWhom, all, all.isOnline());
    }

    public void updateTeamListsSinglePlayer(Player who, boolean online) {
        for (Player all : this.playerScoreboards.keySet())
            updateTeamList(all, who, online);
        if (!online)
            this.playerTeams.remove(who);
    }

    public void updateTeamList(Player forWhom, Player who, boolean online) {
        if (!this.playerScoreboards.containsKey(forWhom)) {
            createNewScoreboard(forWhom);
        }
        Scoreboard board = this.playerScoreboards.get(forWhom);

        if (online) {
            String tName = this.playerTeams.get(who);
            if (board.getTeam(tName) == null)
                createPlayerTeam(board, who);
        } else {
            String tName = this.playerTeams.get(who);
            if (tName != null && board.getTeam(tName) != null)
                board.getTeam(tName).unregister();
        }
    }

    public void updateSidebar(Player forWhom) {
        if (!this.playerScoreboards.containsKey(forWhom)) {
            createNewScoreboard(forWhom);
        }
        if (!this.activatedBoards.containsKey(forWhom)) {
            return;
        }
        Scoreboard board = this.playerScoreboards.get(forWhom);
        ScoreboardType type = this.activatedBoards.get(forWhom);

        if (type == ScoreboardType.MAIN) {
            Team teamTrophies = board.getTeam("Internal_Trophy");
            Team teamMoney = board.getTeam("Internal_Money");

            User user = Main.getInstance().getUserManager().getUser(forWhom.getUniqueId());
            UserCurrency uc = user.getUserCurrency();
            UserStats us = user.getUserStats();

            String money = Util.formatBigNumber(uc.getMoney()) + " $";

            if (money.length() > 16)
                money = "∞";

            teamTrophies.setPrefix(EnumLiga.getLiga(us.getElo()).getDisplayName() + " ");
            teamTrophies.setSuffix("§7(§6" + Util.formatNumber(us.getElo()) + "§7)");
            teamMoney.setSuffix(money);
        }

        if (type == ScoreboardType.MINES) {
            Team teamLevel = board.getTeam("Internal_Level");
            Team teamPoints = board.getTeam("Internal_Points");
            Team teamNextLevel = board.getTeam("Internal_NL");
            Team teamTime = board.getTeam("Internal_Time");


            UserMine userMine = Main.getInstance().getUserManager().getUser(forWhom.getUniqueId()).getUserMine();

            long pointsNextLevel = Main.getInstance().getMinesManager().getMinePointsToNextLevel(userMine.getLevel());
            String points = Util.formatBigNumber(userMine.getMinePoints());

            String nextLevel = (userMine.getMinePoints() >= pointsNextLevel) ? null : Util.formatBigNumber(
                    pointsNextLevel);

            teamTime.setSuffix(TimeUtils.getTimeShort(userMine.getAvailableTime()));

            teamLevel.setSuffix(userMine.getLevel() + "");
            teamPoints.setSuffix(points);

            if (userMine.getMinePoints() >= pointsNextLevel) {
                teamNextLevel.setPrefix("§8§l►§7§l► ");
                teamNextLevel.setSuffix("§6/rankup");
            } else {
                teamNextLevel.setSuffix(nextLevel);
            }

        }
    }

    public void updateAllScoreboards(boolean teamList, boolean sidebar) {
        for (Player all : this.playerScoreboards.keySet()) {
            if (teamList)
                updateTeamListAllPlayers(all);
            if (sidebar) {
                updateSidebar(all);
            }
        }
    }

    private Team getTeamForPlayer(Scoreboard board, Player forWhom) {
        return board.getTeam(this.playerTeams.get(forWhom));
    }

    private char getTabSortChar(Player forWhom) {
        if (forWhom.hasPermission("tab.color.owner"))
            return 'A';
        if (forWhom.hasPermission("tab.color.admin"))
            return 'B';
        if (forWhom.hasPermission("tab.color.dev"))
            return 'C';
        if (forWhom.hasPermission("tab.color.mod"))
            return 'D';
        if (forWhom.hasPermission("tab.color.sup"))
            return 'E';
        if (forWhom.hasPermission("tab.color.staff"))
            return 'F';
        if (forWhom.hasPermission("tab.color.screenshare"))
            return 'G';
        if (forWhom.hasPermission("tab.color.architekt"))
            return 'H';
        if (forWhom.hasPermission("tab.color.yt+"))
            return 'I';
        if (forWhom.hasPermission("tab.color.yt"))
            return 'J';
        if (forWhom.hasPermission("tab.color.hardcore"))
            return 'K';
        if (forWhom.hasPermission("tab.color.lord"))
            return 'L';
        if (forWhom.hasPermission("tab.color.master"))
            return 'M';
        if (forWhom.hasPermission("tab.color.prime"))
            return 'N';
        return 'O';
    }

    private String getTabPrefix(Player forWhom) {
        if (forWhom.hasPermission("tab.color.owner"))
            return "§4§lO §r§8× §4";
        if (forWhom.hasPermission("tab.color.admin"))
            return "§c§lA §r§8× §c";
        if (forWhom.hasPermission("tab.color.dev"))
            return "§3§lD §r§8× §b";
        if (forWhom.hasPermission("tab.color.mod"))
            return "§5§lM §r§8× §5";
        if (forWhom.hasPermission("tab.color.sup"))
            return "§a§lS §r§8× §a";
        if (forWhom.hasPermission("tab.color.staff"))
            return "§2§lST §r§8× §2";
        if (forWhom.hasPermission("tab.color.screenshare"))
            return "§8§lSH §r§8× §8";
        if (forWhom.hasPermission("tab.color.architekt"))
            return "§7AR §r§8× §7";
        if (forWhom.hasPermission("tab.color.yt+"))
            return "§cY§fT §r§8× §f";
        if (forWhom.hasPermission("tab.color.yt"))
            return "§dYT §r§8× §d";
        if (forWhom.hasPermission("tab.color.hardcore"))
            return "§6♚ §r§8× §c";
        if (forWhom.hasPermission("tab.color.lord"))
            return "§5";
        if (forWhom.hasPermission("tab.color.master"))
            return "§a";
        if (forWhom.hasPermission("tab.color.prime"))
            return "§6";
        return "§7";
    }


    private void startSidebarUpdater() {
        new BukkitRunnable() {
            public void run() {
                ScoreboardManager.this.updateAllScoreboards(false, true);
            }
        }.runTaskTimer(Main.getInstance(), 20L, 20L);
    }


    public enum ScoreboardType {
        MAIN("MainSidebar"),
        FIGHT("FightSidebar"),
        MINES("MineSidebar");

        private final String sidebarName;


        ScoreboardType(String sidebarName) {
            this.sidebarName = sidebarName;
        }


        public String getSidebarName() {
            return this.sidebarName;
        }
    }

}
