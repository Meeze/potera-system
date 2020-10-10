package de.potera.teamhardcore.others.clan;

import de.potera.teamhardcore.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClanMemberList {

    private final Clan clan;

    private final Map<UUID, ClanMember> members;

    public ClanMemberList(Clan clan) {
        this.clan = clan;
        this.members = new HashMap<>();

        loadAllMembers();
    }

    private void loadAllMembers() {
        Connection conn = null;
        try {
            conn = Main.getInstance().getDatabaseManager().getHikari().getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM `ClanMember` WHERE `ClanName` = ?");
            st.setString(1, this.clan.getName());
            ResultSet rs = Main.getInstance().getDatabaseManager().executeQuery(st);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("UUID"));
                String lastSeenName = rs.getString("LastSeenName");
                ClanMember.ClanRank rank = ClanMember.ClanRank.valueOf(rs.getString("Rank"));
                ClanMember member = new ClanMember(uuid, lastSeenName, getClan(), rank);
                this.members.put(uuid, member);
            }

            Main.getInstance().getDatabaseManager().close(st, rs);
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

    public void addMember(ClanMember member) {
        if (this.members.containsKey(member.getUUID())) return;
        this.members.put(member.getUUID(), member);
    }

    public void removeMember(ClanMember member) {
        if (!this.members.containsKey(member.getUUID()))
            return;
        this.members.remove(member.getUUID());
    }

    public boolean containsMember(ClanMember member) {
        return this.members.containsKey(member.getUUID());
    }

    public ClanMember getMember(UUID uuid) {
        if (!this.members.containsKey(uuid))
            return null;
        return this.members.get(uuid);
    }

    public List<ClanMember> getMembers(ClanMember.ClanRank rank) {
        List<ClanMember> membersByRank = new ArrayList<>();
        for (ClanMember members : this.members.values()) {
            if (members.getRank() == rank)
                membersByRank.add(members);
        }
        return membersByRank;
    }

    public Map<UUID, ClanMember> getMembers() {
        return members;
    }

    public Clan getClan() {
        return clan;
    }

}
