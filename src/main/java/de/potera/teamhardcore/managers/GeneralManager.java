package de.potera.teamhardcore.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.others.EnumPerk;
import de.potera.teamhardcore.others.KillstreakData;
import de.potera.teamhardcore.others.TPDelay;
import de.potera.teamhardcore.others.TPRequest;
import de.potera.teamhardcore.utils.Reflection;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;

public class GeneralManager {

    private final Map<Player, TPDelay> tpDelays = new HashMap<>();
    private final Map<Player, TPRequest> tpRequests = new HashMap<>();
    private final Map<Player, Long> tpCooldowns = new HashMap<>();
    private final Map<Player, Location> lastPositions = new HashMap<>();
    private final Map<Player, Player> lastMessageContacts = new HashMap<>();
    private final Map<UUID, Long> healCooldowns = new HashMap<>();
    private final Map<String, Object> systemData = new HashMap<>();
    private final Map<EnumPerk, Set<Player>> perkEffects = new HashMap<>();
    private final Map<UUID, KillstreakData> playerKillstreaks = new HashMap<>();
    private final Map<Player, Long> tradeCooldown = new HashMap<>();


    private final List<Player> playersInSpy = new ArrayList<>();
    private final List<Player> playersInGodMode = new ArrayList<>();
    private final List<Player> playersInInvsee = new ArrayList<>();
    private final List<Player> playersInVanish = new ArrayList<>();
    private final List<UUID> playersInBuildmode = new ArrayList<>();
    private final List<UUID> playersFreezed = new ArrayList<>();
    private final List<Player> playersInAfkCheck = new ArrayList<>();

    private int globalmuteTier = 0;

    public GeneralManager() {
        loadSystemData();

        for (EnumPerk perk : EnumPerk.values())
            this.perkEffects.put(perk, new HashSet<>());

        startAutoMessager();
        startTablistTask();
        startPerkEffectUpdateTask();
        injectPotionStackSize();
        setCustomServerInfo();
    }

    private void setCustomServerInfo() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST,
                        Collections.singletonList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        int onlinePlayers = Bukkit.getOnlinePlayers().size();

                        event.getPlayer().getAddress().getAddress();

                        WrappedServerPing serverPing = event.getPacket().getServerPings().read(0);
                        serverPing.setVersionProtocol(-1);
                        serverPing.setVersionName("§8▶ §c" + onlinePlayers + "§8/ §c420");
                        serverPing.setPlayersVisible(false);
                    }
                });
    }

    private void writeDefaultSystemData() {
        this.systemData.put("StatsPeriodTime", System.currentTimeMillis());
    }

    private void loadSystemData() {
        File file = new File(Main.getInstance().getDataFolder(), "systemdata.dat");

        if (!file.exists()) {
            writeDefaultSystemData();
            saveSystemData();
            return;
        }

        try {
            FileInputStream fin = new FileInputStream(file);
            ObjectInputStream oin = new ObjectInputStream(fin);
            List<Object> allData = new ArrayList<>();

            try {
                Object read;
                while ((read = oin.readObject()) != null)
                    allData.add(read);
            } catch (EOFException eOFException) {
                eOFException.printStackTrace();
            }

            int c = 0;
            Object fRead = null;
            for (Object data : allData) {
                if (c == 0) {
                    fRead = data;
                    c = 1;
                    continue;
                }
                this.systemData.put((String) fRead, data);
                c = 0;
            }

            oin.close();
            fin.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void saveSystemData() {
        File file = new File(Main.getInstance().getDataFolder(), "sysdata.dat");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fout = new FileOutputStream(file, false);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            for (Map.Entry<String, Object> entry : this.systemData.entrySet()) {
                if (!(entry.getValue() instanceof java.io.Serializable)) continue;
                oout.writeObject(entry.getKey());
                oout.writeObject(entry.getValue());
            }
            oout.flush();
            oout.close();
            fout.flush();
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void injectPotionStackSize() {
        Class<?> itemClass = Reflection.getNMSClass("Item");
        Object item = Reflection.invoke(null, Reflection.getMethod(itemClass, "getById", int.class), 373);
        Reflection.setForField(Reflection.getField(itemClass, "maxStackSize"), item, 64);
    }

    private void startAutoMessager() {
        if (Main.getInstance().getFileManager().getConfigFile().getAutoMessages().isEmpty()) return;

        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                String msg = Main.getInstance().getFileManager().getConfigFile().getAutoMessages().get(this.index);
                Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(msg));
                this.index = (this.index >= Main.getInstance().getFileManager().getConfigFile().getAutoMessages().size() - 1) ? 0 : (this.index + 1);

            }
        }.runTaskTimer(Main.getInstance(), 3600L, 3600L);
    }

    private void startTablistTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> sendCustomTabHeaderFooter(player));
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private void startPerkEffectUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(Main.getInstance(), () -> {
            for (Set<Player> players : this.perkEffects.values()) {
                for (Player player : players)
                    refreshPerkEffects(player);
            }
        }, 600L, 600L);
    }

    public void addPlayerToPerkEffect(Player player, EnumPerk perk) {
        if (!this.perkEffects.containsKey(perk)) return;
        Set<Player> players = this.perkEffects.get(perk);
        if (players.contains(player))
            return;
        players.add(player);
        refreshPerkEffects(player);
    }

    public void removePlayerFromPerkEffect(Player player, EnumPerk perk) {
        if (!this.perkEffects.containsKey(perk)) return;
        Set<Player> players = this.perkEffects.get(perk);
        if (!players.contains(player))
            return;
        players.remove(player);
    }

    public void refreshPerkEffects(Player player) {
        for (Map.Entry<EnumPerk, Set<Player>> entryPerks : this.perkEffects.entrySet()) {
            if (!(entryPerks.getValue()).contains(player))
                continue;

            if ((entryPerks.getKey()).isPotionEffect()) {
                EnumPerk perk = entryPerks.getKey();
                PotionEffect effect = new PotionEffect(perk.getType(), Integer.MAX_VALUE, perk.getAmplifier(), true);
                player.addPotionEffect(effect, true);
            }
        }
    }

    public void sendCustomTabHeaderFooter(Player player) {
        Util.sendPlayerlistHeaderFooter(player,
                "\n§r    §7§m*-----*-----*§r   §a§lpotera§r   §7§m*-----*-----*§r    \n",
                "\n");
    }

    public int getAdjustedPlayerCount(Player p) {
        int total = Bukkit.getOnlinePlayers().size();
        if (p == null || !p.hasPermission("potera.vanish.see"))
            total -= this.playersInVanish.size();
        return total;
    }

    public Map<String, Object> getSystemData() {
        return systemData;
    }

    public int getGlobalmuteTier() {
        return globalmuteTier;
    }

    public void setGlobalmuteTier(int globalmuteTier) {
        this.globalmuteTier = globalmuteTier;
        if (globalmuteTier > 2)
            this.globalmuteTier = 2;
        if (globalmuteTier < 0) {
            this.globalmuteTier = 0;
        }
    }

    public void updateVanish(Player forWhom) {
        if (forWhom.hasPermission("potera.vanish.see"))
            return;
        for (Player allVanish : playersInVanish) {
            if (forWhom == allVanish)
                continue;
            forWhom.hidePlayer(allVanish);
        }
    }

    public void vanishAll(Player p) {
        if (this.playersInVanish.contains(p))
            return;
        this.playersInVanish.add(p);
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (all == p)
                continue;
            if (all.hasPermission("potera.vanish.see"))
                continue;
            all.hidePlayer(p);
        }
    }

    public void unvanishAll(Player p) {
        if (!this.playersInVanish.contains(p))
            return;
        this.playersInVanish.remove(p);
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.showPlayer(p);
        }
    }

    public List<UUID> getPlayersInBuildmode() {
        return playersInBuildmode;
    }

    public List<Player> getPlayersInVanish() {
        return playersInVanish;
    }

    public Map<Player, Player> getLastMessageContacts() {
        return lastMessageContacts;
    }

    public Map<UUID, Long> getHealCooldowns() {
        return healCooldowns;
    }

    public Map<Player, Location> getLastPositions() {
        return lastPositions;
    }

    public Map<Player, TPDelay> getTeleportDelays() {
        return tpDelays;
    }

    public Map<Player, TPRequest> getTeleportRequests() {
        return tpRequests;
    }

    public Map<Player, Long> getTeleportCooldowns() {
        return tpCooldowns;
    }

    public List<Player> getPlayersInGodMode() {
        return playersInGodMode;
    }

    public List<Player> getPlayersInSpy() {
        return playersInSpy;
    }

    public List<Player> getPlayersInInvsee() {
        return playersInInvsee;
    }

    public List<UUID> getPlayersFreezed() {
        return playersFreezed;
    }

    public Map<UUID, KillstreakData> getPlayerKillstreaks() {
        return playerKillstreaks;
    }

    public List<Player> getPlayersInAfkCheck() {
        return playersInAfkCheck;
    }

    public Map<Player, Long> getTradeCooldown() {
        return tradeCooldown;
    }
}
