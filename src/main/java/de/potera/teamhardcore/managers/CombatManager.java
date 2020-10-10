package de.potera.teamhardcore.managers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.events.custom.PlayerCombatTaggedEvent;
import de.potera.teamhardcore.others.CombatWall;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CombatManager {

    public static final String[] CRATE_DROPS = new String[]{"TestCrate"};
    public static final Long[] MONEY_DROPS = new Long[]{200000L, 400000L, 600000L, 800000L, 1000000L};
    private static final int COMBAT_TIME = 10;

    private final ConcurrentHashMap<Player, Long> taggedPlayers;
    private final Map<Player, List<Location>> wallBlocks;

    private CombatWall combatWall;

    public CombatManager() {
        this.taggedPlayers = new ConcurrentHashMap<>();
        this.wallBlocks = new HashMap<>();

        startTimer();
        loadCombatWall();
        registerPacketListener();
    }

    private void startTimer() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Map.Entry<Player, Long> entry : CombatManager.this.taggedPlayers.entrySet()) {
                    long diff = entry.getValue() - CombatManager.this.getSystemTime();

                    if (diff <= 0L) {
                        PlayerCombatTaggedEvent event = new PlayerCombatTaggedEvent(entry.getKey(), false);
                        Bukkit.getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            setTagged(entry.getKey(), false);
                            entry.getKey().sendMessage(
                                    StringDefaults.PVP_PREFIX + "§aDu bist nicht mehr im Kampf! Du kannst dich sicher ausloggen.");
                        }
                    }

                }
            }
        }.runTaskTimer(Main.getInstance(), 10L, 10L);
    }

    private void registerPacketListener() {
        ProtocolManager m = ProtocolLibrary.getProtocolManager();
        m.addPacketListener(
                new PacketAdapter(Main.getInstance(), ListenerPriority.HIGHEST, PacketType.Play.Client.BLOCK_PLACE,
                        PacketType.Play.Client.BLOCK_DIG) {

                    public void onPacketReceiving(PacketEvent event) {
                        Player p = event.getPlayer();
                        PacketContainer packet = event.getPacket();

                        if (!CombatManager.this.wallBlocks.containsKey(p)) return;

                        List<Location> visibleBlocks = CombatManager.this.wallBlocks.get(p);
                        BlockPosition pos = packet.getBlockPositionModifier().read(0);
                        Location loc = new Location(p.getWorld(), pos.getX(), pos.getY(), pos.getZ());

                        if (CombatManager.this.combatWall.contains(loc) && visibleBlocks.contains(loc)) {
                            event.setCancelled(true);
                            if (packet.getType() == PacketType.Play.Client.BLOCK_DIG)
                                CombatManager.this.sendBlockChange(p, loc, Material.STAINED_GLASS, 14);
                        }
                    }
                });
    }

    private void loadCombatWall() {
        FileConfiguration cfg = Main.getInstance().getFileManager().getConfigFile().getConfig();
        if (cfg.get("WallRegion") == null) return;

        Location minPos = Util.stringToLocation(cfg.getString("WallRegion.MinPos"));
        Location maxPos = Util.stringToLocation(cfg.getString("WallRegion.MaxPos"));
        this.combatWall = new CombatWall(minPos, maxPos);
    }

    private void removeWall(Player p) {
        if (!this.wallBlocks.containsKey(p)) return;

        for (Location loc : this.wallBlocks.get(p)) {
            Block b = loc.getBlock();
            sendBlockChange(p, loc, b.getType(), b.getData());
        }

        this.wallBlocks.remove(p);
    }

    public void updateWall(Player p) {
        if (this.combatWall == null || this.combatWall.getWorld() != p.getWorld()) return;

        if (!this.wallBlocks.containsKey(p))
            this.wallBlocks.put(p, new CopyOnWriteArrayList<>());

        List<Location> visibleBlocks = this.wallBlocks.get(p);
        List<Location> locsInRange = getLocationsInRange(p.getLocation(), 4, 6);

        for (Location visible : visibleBlocks) {
            if (!locsInRange.contains(visible)) {
                Block b = visible.getBlock();
                visibleBlocks.remove(visible);
                sendBlockChange(p, visible, b.getType(), b.getData());
            }
        }

        for (Location loc : locsInRange) {
            if (!this.combatWall.contains(loc) || visibleBlocks.contains(loc))
                continue;
            visibleBlocks.add(loc);
            sendBlockChange(p, loc, Material.STAINED_GLASS, 14);
        }
    }

    private void sendBlockChange(Player p, Location loc, Material mat, int data) {
        ProtocolManager m = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = m.createPacket(PacketType.Play.Server.BLOCK_CHANGE);
        packet.getBlockPositionModifier().write(0,
                new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
        packet.getBlockData().write(0, WrappedBlockData.createData(mat, data));
        try {
            m.sendServerPacket(p, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private List<Location> getLocationsInRange(Location loc, int height, int width) {
        List<Location> locs = new ArrayList<>();
        World w = loc.getWorld();
        int startX = loc.getBlockX();
        int startY = loc.getBlockY();
        int startZ = loc.getBlockZ();
        for (int x = startX - width; x <= startX + width; x++) {
            for (int y = startY - height; y <= startY + height; y++) {
                for (int z = startZ - width; z <= startZ + width; z++) {
                    Block current = w.getBlockAt(x, y, z);
                    if (current != null && current.getType() == Material.AIR) {
                        locs.add(current.getLocation());
                    }
                }
            }
        }
        return locs;
    }

    public boolean locationChanged(Location from, Location to) {
        return (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ());
    }

    private long getSystemTime() {
        return System.currentTimeMillis() / 1000L;
    }

    private void dropItems(Player player) {
        ItemStack[] contents = player.getInventory().getContents().clone();
        ItemStack[] armorContents = player.getInventory().getArmorContents().clone();
        player.getInventory().setContents(new ItemStack[0]);
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        for (ItemStack content : contents) {
            if (content != null && content.getType() != Material.AIR)
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), content);
        }
        for (ItemStack content : armorContents) {
            if (content != null && content.getType() != Material.AIR)
                player.getLocation().getWorld().dropItemNaturally(player.getLocation(), content);
        }
    }

    public void onDisable() {
        for (Player player : this.taggedPlayers.keySet())
            setTagged(player, false);
        for (Player player : this.wallBlocks.keySet())
            removeWall(player);
    }

    public void setTagged(Player player, boolean tagged) {
        if (!tagged) {
            this.taggedPlayers.remove(player);
            removeWall(player);
            return;
        }

        if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            player.removePotionEffect(PotionEffectType.INVISIBILITY);

        if (player.getAllowFlight() && !player.hasPermission(
                "potera.combat.admin") && player.getGameMode() != GameMode.CREATIVE)
            player.setAllowFlight(false);
        this.taggedPlayers.put(player, getSystemTime() + COMBAT_TIME);
        updateWall(player);
    }

    public boolean isTagged(Player player) {
        return this.taggedPlayers.containsKey(player);
    }

    public void updateTime(Player player) {
        if (!isTagged(player)) return;
        this.taggedPlayers.put(player, getSystemTime() + COMBAT_TIME);
    }

    public long getRemainingTime(Player player) {
        if (!isTagged(player)) return 0L;
        return (this.taggedPlayers.get(player) - getSystemTime()) * 1000L;
    }

    public CombatWall getCombatWall() {
        return combatWall;
    }

    public void setCombatWall(CombatWall combatWall) {
        List<Player> hasWall = new ArrayList<>(this.wallBlocks.keySet());
        for (Player all : hasWall) removeWall(all);
        FileConfiguration cfg = Main.getInstance().getFileManager().getConfigFile().getConfig();

        if (combatWall == null)
            cfg.set("WallRegion", null);
        else {
            cfg.set("WallRegion.MinPos", Util.locationToString(combatWall.getMinPos()));
            cfg.set("WallRegion.MaxPos", Util.locationToString(combatWall.getMaxPos()));
        }

        Main.getInstance().getFileManager().getConfigFile().saveConfig();
        this.combatWall = combatWall;
        if (combatWall != null)
            for (Player all : hasWall)
                updateWall(all);
    }

}
