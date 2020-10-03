package de.potera.teamhardcore.commands;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.TPDelay;
import de.potera.teamhardcore.others.clan.Clan;
import de.potera.teamhardcore.others.clan.ClanMember;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import de.potera.teamhardcore.utils.chat.JSONMessage;
import org.apache.commons.codec.Charsets;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandClan implements CommandExecutor {

    private final CharsetEncoder encoderLatin = Charsets.ISO_8859_1.newEncoder();
    private final CharsetEncoder encoderUtf8 = Charsets.UTF_8.newEncoder();

    public static void sendClanStats(Player player, Clan clan) {
        ClanMember owner = clan.getMemberList().getMembers(ClanMember.ClanRank.OWNER).get(0);
        List<ClanMember> members = new ArrayList<>();
        members.add(owner);
        members.addAll(clan.getMemberList().getMembers(ClanMember.ClanRank.MOD));
        members.addAll(clan.getMemberList().getMembers(ClanMember.ClanRank.MEMBER));
        StringBuilder sb = new StringBuilder();

        for (ClanMember member : members) {
            Player target = Bukkit.getPlayer(member.getUUID());
            if (member.getRank() == ClanMember.ClanRank.OWNER)
                sb.append("§8(").append(ClanMember.ClanRank.OWNER.getColor()).append("O§8) ");
            if (member.getRank() == ClanMember.ClanRank.MOD)
                sb.append("§8(").append(ClanMember.ClanRank.MOD.getColor()).append("M§8) ");
            boolean online = (target != null);
            if (!online || (Main.getInstance().getGeneralManager().getPlayersInVanish().contains(
                    target) && !player.hasPermission(
                    "potera.vanish.see")))
                online = false;
            sb.append(online ? "§a" : "§7").append(member.getLastSeenName()).append("§7, ");
        }

        String memberList = sb.substring(0, sb.length() - 4);
        int amountMembers = clan.getMemberList().getMembers().size();
        int maxMembers = clan.getMaxMembers();
        player.sendMessage(StringDefaults.HEADER);
        player.sendMessage("§7Clan-Name§8: §e" + clan.getClanColor() + clan.getName());
        player.sendMessage("§7Clan-Kills§8: §e" + clan.getKills() + " §8/ §7Clan-Tode§8: §e" + clan.getDeaths());
        player.sendMessage("§7Ranking§8: §e#" + clan.getRank());
        player.sendMessage(" ");
        player.sendMessage("§7Mitglieder §8(" + ((amountMembers >= maxMembers) ? "§c" : "§a") + clan
                .getMemberList().getMembers().size() + " §7/ §c" + maxMembers + "§8)§8: " + memberList);
        player.sendMessage(StringDefaults.FOOTER);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!(cs instanceof Player))
            return true;

        Player player = (Player) cs;

        if (args.length == 0 || args.length > 3) {
            sendHelp(player, label, 1);
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("1")) {
                sendHelp(player, label, 1);
                return true;
            }
            if (args[0].equalsIgnoreCase("2")) {
                sendHelp(player, label, 2);
                return true;
            }
            if (args[0].equalsIgnoreCase("3")) {
                sendHelp(player, label, 3);
                return true;
            }

            if (args[0].equalsIgnoreCase("löschen") || args[0].equalsIgnoreCase("delete")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());

                if (member.getRank() != ClanMember.ClanRank.OWNER) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst den Clan nicht löschen.");
                    return true;
                }

                Main.getInstance().getClanManager().deleteClan(member.getClan().getName());
                player.sendMessage(StringDefaults.CLAN_PREFIX + "§7Du hast den Clan erfolgreich gelöscht.");
                return true;
            }

            if (args[0].equalsIgnoreCase("setbase")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());

                if (member.getRank() != ClanMember.ClanRank.OWNER) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst die ClanBase nicht setzen.");
                    return true;
                }

                Clan clan = member.getClan();
                clan.setClanBase(player.getLocation());
                player.sendMessage(StringDefaults.CLAN_PREFIX + "§7Du hast die ClanBase gesetzt.");
                clan.sendMessageToClan("§e" + player.getName() + " §7hat die ClanBase gesetzt.", player);
                return true;
            }

            if (args[0].equalsIgnoreCase("base")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());

                if (member.getRank() == ClanMember.ClanRank.MEMBER) {
                    player.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§cDu kannst dich nicht zur ClanBase teleportieren.");
                    return true;
                }

                Clan clan = member.getClan();

                if (clan.getClanBase() == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDein Clan besitzt keine ClanBase.");
                    return true;
                }

                if (player.hasPermission("potera.teleport.nodelay")) {
                    Main.getInstance().getGeneralManager().getLastPositions().put(player, player.getLocation());
                    player.teleport(clan.getClanBase());
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§7Du wurdest zur ClanBase teleportiert.");
                } else {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§7Bereite dich auf die Teleportation vor...");
                    TPDelay tpDelay = new TPDelay(player, 0, 3) {
                        @Override
                        public boolean onTick() {
                            ClanMember cMember = Main.getInstance().getClanManager().getClanMember(
                                    player.getUniqueId());
                            if (cMember == null || cMember.getClan() != member.getClan()) return true;
                            Clan tmpClan = cMember.getClan();
                            return (tmpClan.getClanBase() == null);
                        }

                        @Override
                        public void onEnd() {
                            Clan tmpClan = Main.getInstance().getClanManager().getClan(getPlayer().getUniqueId());
                            Main.getInstance().getGeneralManager().getLastPositions().put(getPlayer(),
                                    getPlayer().getLocation());
                            getPlayer().teleport(tmpClan.getClanBase());
                            getPlayer().sendMessage(
                                    StringDefaults.CLAN_PREFIX + "§7Du wurdest zur ClanBase teleportiert!");
                        }
                    };
                    Main.getInstance().getGeneralManager().getTeleportDelays().put(player, tpDelay);
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("stats")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                Clan clan = Main.getInstance().getClanManager().getClan(player.getUniqueId());
                sendClanStats(player, clan);
                return true;
            }

            if (args[0].equalsIgnoreCase("rang")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }
                player.sendMessage(StringDefaults.HEADER);
                player.sendMessage(StringDefaults.FOOTER);
                player.sendMessage(StringDefaults.PREFIX + "§4§lOwner§8:");
                player.sendMessage(" §8- §eClanBase setzen");
                player.sendMessage(" §8- §eClan löschen");
                player.sendMessage(" §8- §eClan Ränge verwalten");
                player.sendMessage("");
                player.sendMessage(StringDefaults.PREFIX + "§5§lMod§8: ");
                player.sendMessage(" §8- §eZur ClanBase teleportieren");
                player.sendMessage(" §8- §eClan Mitglieder einladen");
                player.sendMessage(" §8- §eClan Mitglieder entfernen");
                player.sendMessage("");
                player.sendMessage(StringDefaults.PREFIX + "§9§lMember§8:");
                player.sendMessage("§8- §eIm ClanChat schreiben");
                player.sendMessage("");
                player.sendMessage(StringDefaults.FOOTER);
                return true;
            }

            if (args[0].equalsIgnoreCase("shop")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                player.sendMessage(StringDefaults.CLAN_PREFIX + "TODO");
                return true;
            }

            if (args[0].equalsIgnoreCase("verlassen")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());

                if (member.getRank() == ClanMember.ClanRank.OWNER) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst den Clan nicht verlassen.");
                    return true;
                }

                player.sendMessage(StringDefaults.CLAN_PREFIX + "§7Du hast den Clan erfolgreich verlassen.");
                member.getClan().sendMessageToClan("§e" + player.getName() + " §7hat den Clan verlassen.", player);
                Main.getInstance().getClanManager().removeClanMember(player.getUniqueId());
                return true;
            }

        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("erstellen") || args[0].equalsIgnoreCase("create")) {
                if (Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist bereits in einem Clan.");
                    return true;
                }

                String clanName = args[1];

                if (clanName.length() > 6) {
                    player.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§cDer Clan Name darf nicht länger als 6 Zeichen sein.");
                    return true;
                }

                if (!Util.isValidNeutralName(clanName) || !this.encoderLatin.canEncode(clanName)) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDer Clan Name enthält ungültige Zeichen.");
                    return true;
                }

                if (!this.encoderUtf8.canEncode(clanName)) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDer Clan Name enthält ungültige Zeichen.");
                    return true;
                }

                if (Main.getInstance().getClanManager().getClan(clanName) != null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Clan existiert bereits.");
                    return true;
                }

                Main.getInstance().getClanManager().createClan(clanName, player.getUniqueId(), player.getName());
                player.sendMessage(
                        StringDefaults.CLAN_PREFIX + "§7Du hast den Clan §e" + clanName + " §7erfolgreich erstellt.");
                return true;
            }

            if (args[0].equalsIgnoreCase("einladen") || args[0].equalsIgnoreCase("invite")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());

                if (member.getRank() == ClanMember.ClanRank.MEMBER) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst keine Spieler einladen.");
                    return true;
                }

                Clan clan = member.getClan();

                if (clan.getMemberList().getMembers().size() >= clan.getMaxMembers()) {
                    player.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§cDer Clan hat bereits die maximale Anzahl an Mitgliedern erreicht.");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Spieler ist nicht online!");
                    return true;
                }

                if (clan.getMemberList().getMember(target.getUniqueId()) != null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Spieler ist bereits in deinem Clan.");
                    return true;
                }

                if (Main.getInstance().getClanManager().getClan(target.getUniqueId()) != null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Spieler ist bereits in einem Clan.");
                    return true;
                }

                Main.getInstance().getClanManager().addRequest(target.getUniqueId(), clan);
                player.sendMessage(
                        StringDefaults.CLAN_PREFIX + "§e" + target.getName() + " §7wurde in den Clan eingeladen.");
                target.sendMessage(
                        StringDefaults.CLAN_PREFIX + "§7Du hast eine Einladung vom Clan §e" + clan.getClanColor() + clan.getName() + " §7erhalten.");
                new JSONMessage(StringDefaults.CLAN_PREFIX + "§6Klicke hier, um die Einladung anzunehmen.").tooltip(
                        "§eEinladung annehmen").runCommand("/clan accept " + clan.getName()).send(target);
                clan.sendMessageToClan(
                        "§e" + player.getName() + " §7hat §e" + target.getName() + " §7in den Clan eingeladen.",
                        player);
                return true;
            }

            if (args[0].equalsIgnoreCase("kick")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());

                if (member.getRank() == ClanMember.ClanRank.MEMBER) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst keine Spieler kicken.");
                    return true;
                }

                Clan clan = member.getClan();
                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Spieler ist nicht online!");
                    return true;
                }

                if (clan.getMemberList().getMember(target.getUniqueId()) == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Spieler ist nicht in deinem Clan.");
                    return true;
                }

                ClanMember targetMember = clan.getMemberList().getMember(target.getUniqueId());

                if (targetMember.getRank().getRankPosition() >= member.getRank().getRankPosition()) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst diesen Spieler nicht kicken.");
                    return true;
                }

                Main.getInstance().getClanManager().removeClanMember(target.getUniqueId());
                player.sendMessage(
                        StringDefaults.CLAN_PREFIX + "§e" + target.getName() + " §7wurde aus dem Clan gekickt.");
                target.sendMessage(
                        StringDefaults.CLAN_PREFIX + "§7Du wurdest aus dem Clan gekickt.");
                clan.sendMessageToClan(
                        "§e" + player.getName() + " §7hat §e" + target.getName() + " §7aus dem Clan gekickt.", player,
                        target);
                return true;
            }

            if (args[0].equalsIgnoreCase("annehmen") || args[0].equalsIgnoreCase("accept")) {
                if (Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist bereits in einem Clan.");
                    return true;
                }

                String clanName = args[1];

                if (Main.getInstance().getClanManager().getClan(clanName) == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Clan existiert nicht.");
                    return true;
                }

                if (!Main.getInstance().getClanManager().hasRequest(player.getUniqueId(), clanName)) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu wurdest von diesem Clan nicht eingeladen.");
                    return true;
                }

                Clan clan = Main.getInstance().getClanManager().getClan(clanName);

                if (clan.getMemberList().getMembers().size() >= clan.getMaxMembers()) {
                    player.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§cDieser Clan hat bereits die maximale Anzahl an Mitgliedern erreicht.");
                    return true;
                }

                Main.getInstance().getClanManager().getClanRequests().remove(player.getUniqueId());
                Main.getInstance().getClanManager().addClanMember(player.getUniqueId(), player.getName(), clan,
                        ClanMember.ClanRank.MEMBER);

                clan.sendMessageToClan("§e" + player.getName() + " §7ist dem Clan beigetreten.", player);
                player.sendMessage(StringDefaults.CLAN_PREFIX + "§7Du bist dem Clan beigetreten.");
                return true;
            }

            if (args[0].equalsIgnoreCase("ablehnen") || args[0].equalsIgnoreCase("deny")) {
                if (Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist bereits in einem Clan.");
                    return true;
                }

                String clanName = args[1];

                if (Main.getInstance().getClanManager().getClan(clanName) == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Clan existiert nicht.");
                    return true;
                }

                if (!Main.getInstance().getClanManager().hasRequest(player.getUniqueId(), clanName)) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu wurdest von diesem Clan nicht eingeladen.");
                    return true;
                }

                Clan clan = Main.getInstance().getClanManager().getClan(clanName);

                Main.getInstance().getClanManager().removeRequest(player.getUniqueId(), clan);
                clan.sendMessageToClan("§e" + player.getName() + " §7hat die Clan Einladung abgelehnt.", player);
                player.sendMessage(StringDefaults.CLAN_PREFIX + "§7Du hast die Einladung abgelehnt.");
                return true;
            }

            if (args[0].equalsIgnoreCase("stats")) {
                String clanName = args[1];

                if (Main.getInstance().getClanManager().getClan(clanName) == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDer Clan existiert nicht.");
                    return true;
                }

                sendClanStats(player, Main.getInstance().getClanManager().getClan(clanName));
                return true;
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setrang")) {
                if (!Main.getInstance().getClanManager().hasClan(player.getUniqueId())) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu bist in keinem Clan.");
                    return true;
                }

                ClanMember member = Main.getInstance().getClanManager().getClanMember(player.getUniqueId());
                Clan clan = member.getClan();

                if (member.getRank() != ClanMember.ClanRank.OWNER) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst die Clan Ränge nicht verwalten.");
                    return true;
                }

                String targetName = args[1];
                ClanMember target = null;

                for (Map.Entry<UUID, ClanMember> entryMembers : clan.getMemberList().getMembers().entrySet()) {
                    if (entryMembers.getValue().getLastSeenName().equalsIgnoreCase(targetName)) {
                        target = entryMembers.getValue();

                        break;
                    }
                }

                if (target == null) {
                    player.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§cDieser Spieler ist nicht in deinem Clan.");
                    return true;
                }

                if (target == member) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDu kannst deinen eigenen Rang nicht ändern.");
                    return true;
                }

                ClanMember.ClanRank rank = ClanMember.ClanRank.getByName(args[2]);

                if (rank == null) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDer Rang existiert nicht.");
                    return true;
                }

                if (rank == ClanMember.ClanRank.OWNER) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Rang kann nicht erteilt werden.");
                    return true;
                }

                if (target.getRank() == rank) {
                    player.sendMessage(StringDefaults.CLAN_PREFIX + "§cDieser Spieler besitzt bereits diesen Rang.");
                    return true;
                }

                if (target.getRank() == ClanMember.ClanRank.OWNER) {
                    player.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§cDu kannst den Rang dieses Spielers nicht anpassen.");
                    return true;
                }

                target.setRank(rank);

                Player targetPlayer = Bukkit.getPlayer(target.getUUID());

                player.sendMessage(
                        StringDefaults.CLAN_PREFIX + "§7Du hast §e" + args[2] + " §7auf " + rank.getColor() + rank.getName() + " §7gesetzt.");

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(
                            StringDefaults.CLAN_PREFIX + "§7Du wurdest auf " + rank.getColor() + rank.getName() + " §7gestuft!");
                    member.getClan().sendMessageToClan(
                            "§e" + args[2] + " §7wurde auf " + rank.getColor() + rank.getName() + " §7gestuft.",
                            player, targetPlayer);
                    return true;
                }

                member.getClan().sendMessageToClan(
                        "§e" + args[2] + " §7wurde auf " + rank.getColor() + rank.getName() + " §7gestuft.", player);
                return true;
            }
        }

        return true;
    }

    private void sendHelp(Player player, String label, int page) {
        player.sendMessage(StringDefaults.HEADER);
        if (page == 1) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " erstellen <Name>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " löschen");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " setbase");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " base");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " stats [Clan]");
            player.sendMessage(" ");
            new JSONMessage(" §7Seite §e1§8/§e3").then(" §c[>]").runCommand("/clan 2").send(player);
        }
        if (page == 2) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " einladen <Spieler>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " kick <Spieler>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " setrang <Spieler> <Rang>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " rang");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " verlassen");
            player.sendMessage(" ");
            new JSONMessage("§c[<] ").runCommand("/clan 1").then(" §7Seite §e2§8/§e3").then(" §c[>]").runCommand(
                    "/clan 3").send(player);
        }
        if (page == 3) {
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " annehmen <Clan>");
            player.sendMessage(StringDefaults.PREFIX + "§cVerwendung§8: §7/" + label + " ablehnen <Clan>");
            player.sendMessage(" ");
            new JSONMessage("§c[<] ").runCommand("/clan 2").then(" §7Seite §e3§8/§e3").send(player);
        }
        player.sendMessage(StringDefaults.FOOTER);
    }
}
