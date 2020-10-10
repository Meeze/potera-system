package de.potera.rysefoxx.manager;

import de.potera.teamhardcore.files.FileBase;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TeamManager {

    private final FileBase fileBase;
    private final List<String> owner;
    private final List<String> admin;
    private final List<String> developer;
    private final List<String> architect;
    private final List<String> mod;
    private final List<String> supporter;
    private final List<String> guide;

    public TeamManager() {
        this.fileBase = new FileBase("", "team");
        this.owner = new ArrayList<>();
        this.admin = new ArrayList<>();
        this.developer = new ArrayList<>();
        this.architect = new ArrayList<>();
        this.mod = new ArrayList<>();
        this.supporter = new ArrayList<>();
        this.guide = new ArrayList<>();
    }

    public boolean isNotValidRank(String rank) {
        TeamRanks teamRanks = TeamRanks.forName(rank);
        return teamRanks == null;
    }

    public boolean inGroup(Player player) {
        if (this.fileBase.getConfig().getKeys(false).isEmpty()) return false;

        for (String ranks : this.fileBase.getConfig().getKeys(false)) {
            for (String playerNames : this.fileBase.getConfig().getStringList(ranks)) {
                return playerNames.equalsIgnoreCase(player.getName());
            }
        }
        return false;
    }

    public void addPlayer(Player target, TeamRanks teamRanks) {
        if (teamRanks.equals(TeamRanks.OWNER)) {
            this.owner.add(target.getName());
        } else if (teamRanks.equals(TeamRanks.ADMIN)) {
            this.admin.add(target.getName());
        } else if (teamRanks.equals(TeamRanks.DEVELOPER)) {
            this.developer.add(target.getName());
        } else if (teamRanks.equals(TeamRanks.ARCHITECT)) {
            this.architect.add(target.getName());
        } else if (teamRanks.equals(TeamRanks.MODERATOR)) {
            this.mod.add(target.getName());
        } else if (teamRanks.equals(TeamRanks.SUPPORTER)) {
            this.supporter.add(target.getName());
        } else if (teamRanks.equals(TeamRanks.GUIDE)) {
            this.guide.add(target.getName());
        } else {
            this.guide.add(target.getName());
        }
        save();
    }

    public void removePlayer(Player target) {
        this.owner.remove(target.getName());
        this.admin.remove(target.getName());
        this.developer.remove(target.getName());
        this.architect.remove(target.getName());
        this.mod.remove(target.getName());
        this.supporter.remove(target.getName());
        this.guide.remove(target.getName());
        this.fileBase.saveConfig();
    }

    public void save() {
        this.fileBase.getConfig().set("owner", this.owner);
        this.fileBase.getConfig().set("admin", this.admin);
        this.fileBase.getConfig().set("developer", this.developer);
        this.fileBase.getConfig().set("architect", this.architect);
        this.fileBase.getConfig().set("mod", this.mod);
        this.fileBase.getConfig().set("supporter", this.supporter);
        this.fileBase.getConfig().set("guide", this.guide);
        this.fileBase.saveConfig();
    }

    public String getOwner() {
        if (this.fileBase.getConfig().getStringList("owner") == null || this.fileBase.getConfig().getStringList("owner").isEmpty())
            return "§c§oNiemand";
        return this.fileBase.getConfig().getStringList("owner").toString().replace("[", "§8- §4").replace("]", "");
    }

    public String getAdmin() {
        if (this.fileBase.getConfig().getStringList("admin") == null || this.fileBase.getConfig().getStringList("admin").isEmpty())
            return "§c§oNiemand";
        return this.fileBase.getConfig().getStringList("admin").toString().replace("[", "§8- §c").replace("]", "");
    }

    public String getDeveloper() {
        if (this.fileBase.getConfig().getStringList("developer") == null || this.fileBase.getConfig().getStringList("developer").isEmpty())
            return "§c§oNiemand";
        return this.fileBase.getConfig().getStringList("developer").toString().replace("[", "§8- §b").replace("]", "");
    }

    public String getArchitect() {
        if (this.fileBase.getConfig().getStringList("architect") == null || this.fileBase.getConfig().getStringList("architect").isEmpty())
            return "§c§oNiemand";
        return this.fileBase.getConfig().getStringList("architect").toString().replace("[", "§8- §6").replace("]", "");
    }

    public String getModerator() {
        if (this.fileBase.getConfig().getStringList("mod") == null || this.fileBase.getConfig().getStringList("mod").isEmpty())
            return "§c§oNiemand";
        return this.fileBase.getConfig().getStringList("mod").toString().replace("[", "§8- §5").replace("]", "");
    }

    public String getSupporter() {
        if (this.fileBase.getConfig().getStringList("supporter") == null || this.fileBase.getConfig().getStringList("supporter").isEmpty())
            return "§c§oNiemand";
        return this.fileBase.getConfig().getStringList("supporter").toString().replace("[", "§8- §a").replace("]", "");
    }

    public String getGuide() {
        if (this.fileBase.getConfig().getStringList("guide") == null || this.fileBase.getConfig().getStringList("guide").isEmpty())
            return "§c§oNiemand";
        return this.fileBase.getConfig().getStringList("guide").toString().replace("[", "§8- §3").replace("]", "");
    }

    @Getter
    public enum TeamRanks {

        OWNER("Owner"),
        ADMIN("Admin"),
        DEVELOPER("Developer"),
        ARCHITECT("Architekt"),
        MODERATOR("Mod"),
        SUPPORTER("Supporter"),
        GUIDE("Guide"),
        ;

        String name;

        TeamRanks(String name) {
            this.name = name;
        }

        public static TeamRanks forName(String name) {
            for (TeamRanks teamRanks : values()) {
                if (!teamRanks.name.equalsIgnoreCase(name)) continue;
                return teamRanks;
            }
            return null;
        }
    }



}
