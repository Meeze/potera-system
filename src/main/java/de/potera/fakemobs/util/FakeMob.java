package de.potera.fakemobs.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import de.potera.fakemobs.FakeMobsPlugin;
import de.potera.fakemobs.merchant.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.logging.Level;

public class FakeMob {
    private final int id;
    private final UUID uniqueId;
    private final List<Player> loadedPlayers;
    private String name;
    private Location loc;
    private EntityType type;
    private WrappedDataWatcher dataWatcher;
    private boolean sitting;
    private boolean invisibility;
    private boolean baby;
    private boolean playerLook;
    private boolean playerNametagVisible;
    private boolean playerHalfVisible;
    private MobInventory inventory;
    private MobShop shop;
    private Multimap<String, WrappedSignedProperty> playerSkin;

    public FakeMob(int id, Location loc, EntityType type) {
        this.name = null;


        this.dataWatcher = null;
        this.loadedPlayers = new ArrayList<>();

        this.sitting = false;
        this.invisibility = false;
        this.baby = false;
        this.playerLook = false;
        this.playerNametagVisible = true;
        this.playerHalfVisible = false;

        this.inventory = new MobInventory();
        this.shop = null;


        this.id = id;
        this.loc = loc;
        this.type = type;
        this.uniqueId = UUID.nameUUIDFromBytes(("FakeMob-" + id).getBytes(Charsets.UTF_8));
        this.dataWatcher = DataWatchCreator.createDefaultWatcher(this);
    }

    public UUID getUniqueID() {
        return this.uniqueId;
    }


    public MobInventory getInventory() {
        return this.inventory;
    }


    public void setInventory(MobInventory inv) {
        this.inventory = inv;
        if (this.inventory == null) {
            this.inventory = new MobInventory();
        }
    }

    public boolean haveShop() {
        return (this.shop != null);
    }


    public MobShop getShop() {
        return this.shop;
    }


    public void setShop(MobShop shop) {
        this.shop = shop;
    }


    public int getEntityId() {
        return 2300 + this.id;
    }


    public Multimap<String, WrappedSignedProperty> getPlayerSkin() {
        return this.playerSkin;
    }


    public void setPlayerSkin(Multimap<String, WrappedSignedProperty> skin) {
        this.playerSkin = skin;
    }


    public void setPlayerSkin(Player player) {
        this.playerSkin = WrappedGameProfile.fromPlayer(player).getProperties();
    }


    public List<Player> getNearbyPlayers() {
        return getNearbyPlayers(3.0D);
    }


    public List<Player> getNearbyPlayers(double radius) {
        List<Player> players = new ArrayList<>();

        for (Player player : getWorld().getPlayers()) {
            if (this.loc.distance(player.getLocation()) <= radius) {
                players.add(player);
            }
        }

        return players;
    }

    public void updateInventory() {
        for (Player p : this.loadedPlayers) {
            sendInventoryPacket(p);
        }
    }

    public boolean isPlayerLoaded(Player player) {
        return this.loadedPlayers.contains(player);
    }


    public void loadPlayer(Player player) {
        if (isPlayerLoaded(player))
            return;
        this.loadedPlayers.add(player);
        sendSpawnPacket(player);
    }

    public void unloadPlayer(Player player) {
        if (!isPlayerLoaded(player))
            return;
        this.loadedPlayers.remove(player);
        sendDestroyPacket(player);
    }


    public List<Player> getLoadedPlayers() {
        return this.loadedPlayers;
    }


    public boolean isInRange(Player player) {
        return (this.loc.getWorld() == player.getLocation().getWorld() && this.loc.distance(
                player.getLocation()) <= 48.0D);
    }


    public int getId() {
        return this.id;
    }


    public String getCustomName() {
        return this.name;
    }

    public void setCustomName(String name) {
        if (name != null && name.length() > 32) name = name.substring(0, 32);
        this.name = name;

        if (this.name != null && this.name.isEmpty()) {
            this.name = null;
        }

        if (this.type == EntityType.PLAYER) {
            return;
        }


        if (this.name == null) {
            this.dataWatcher.setObject(3, Byte.valueOf((byte) 0));
            this.dataWatcher.setObject(2, "");
        } else {
            this.dataWatcher.setObject(3, Byte.valueOf((byte) 1));
            this.dataWatcher.setObject(2, this.name);
        }
    }

    public Location getLocation() {
        return this.loc;
    }

    public void setLocation(Location loc) {
        this.loc = loc;
    }

    public World getWorld() {
        return this.loc.getWorld();
    }

    public EntityType getType() {
        return this.type;
    }

    public void setType(EntityType type) {
        if (type == null || this.type == type || !type.isAlive())
            return;
        for (Player p : this.loadedPlayers) {
            sendDestroyPacket(p);
        }
        this.type = type;
        this.dataWatcher = DataWatchCreator.createDefaultWatcher(this);

        for (Player p : this.loadedPlayers)
            sendSpawnPacket(p);
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        if (this.type != EntityType.OCELOT && this.type != EntityType.WOLF && this.type != EntityType.PLAYER)
            return;
        if (this.sitting == sitting)
            return;
        this.sitting = sitting;

        if (getType() == EntityType.PLAYER) {
            byte current = this.dataWatcher.getByte(0).byteValue();
            if (sitting) {
                this.dataWatcher.setObject(0, Byte.valueOf((byte) (current | 0x2)));
            } else {
                this.dataWatcher.setObject(0, Byte.valueOf((byte) (current & 0xFFFFFFFD)));
            }
        } else if (sitting) {
            this.dataWatcher.setObject(16, Byte.valueOf((byte) 1));
        } else {
            this.dataWatcher.setObject(16, Byte.valueOf((byte) 0));
        }
    }

    public boolean isInvisibility() {
        return this.invisibility;
    }

    public void setInvisibility(boolean invisibility) {
        if (this.invisibility == invisibility)
            return;
        this.invisibility = invisibility;

        byte current = this.dataWatcher.getByte(0).byteValue();
        if (invisibility) {
            this.dataWatcher.setObject(0, Byte.valueOf((byte) (current | 0x20)));
        } else {
            this.dataWatcher.setObject(0, Byte.valueOf((byte) (current & 0xFFFFFFDF)));
        }

        for (Player player : this.loadedPlayers)
            sendMetaPacket(player);
    }

    public boolean isBaby() {
        return this.baby;
    }

    public void setBaby(boolean baby) {
        if (!Ageable.class.isAssignableFrom(this.type.getEntityClass()) &&
                !Zombie.class.isAssignableFrom(this.type.getEntityClass()))
            return;
        this.baby = baby;

        if (baby) {
            this.dataWatcher.setObject(12, Byte.valueOf((byte) -1));
        } else {
            this.dataWatcher.setObject(12, Byte.valueOf((byte) 0));
        }

        for (Player player : this.loadedPlayers)
            sendMetaPacket(player);
    }

    public int getVillagerProfession() {
        if (this.type != EntityType.VILLAGER)
            return -1;
        return this.dataWatcher.getInteger(16).intValue();
    }

    public void setVillagerProfession(int profession) {
        if (this.type != EntityType.VILLAGER)
            return;
        Villager.Profession professionType = null;
        for (Villager.Profession p : Villager.Profession.values()) {
            if (p.getId() == profession) {
                professionType = p;
                break;
            }
        }
        if (professionType == null)
            return;
        this.dataWatcher.setObject(16, Integer.valueOf(profession));

        for (Player player : this.loadedPlayers)
            sendMetaPacket(player);
    }

    public boolean isPlayerLook() {
        return this.playerLook;
    }

    public void setPlayerLook(boolean look) {
        if (this.playerLook == look)
            return;
        if (!look) {
            for (Player player : this.loadedPlayers) {
                sendHeadRotationPacket(player, getLocation().getYaw());
            }
        }
        this.playerLook = look;
    }

    public boolean isPlayerNametagVisible() {
        return this.playerNametagVisible;
    }

    public void setPlayerNametagVisible(boolean playerNametagVisible) {
        if (this.type != EntityType.PLAYER)
            return;
        if (this.playerNametagVisible == playerNametagVisible)
            return;
        this.playerNametagVisible = playerNametagVisible;
        if (playerNametagVisible) {
            for (Player player : this.loadedPlayers)
                sendShowPlayerNametagPacket(player);
        } else {
            for (Player player : this.loadedPlayers)
                sendRemovePlayerNametagPacket(player);
        }
    }

    public boolean isPlayerHalfVisible() {
        return this.playerHalfVisible;
    }

    public void setPlayerHalfVisible(boolean playerHalfVisible) {
        if (this.type != EntityType.PLAYER)
            return;
        if (this.playerHalfVisible == playerHalfVisible)
            return;
        this.playerHalfVisible = playerHalfVisible;
        if (playerHalfVisible) {
            for (Player player : this.loadedPlayers)
                sendShowHalfVisiblePacket(player);
        } else {
            for (Player player : this.loadedPlayers)
                sendRemoveHalfVisiblePacket(player);
        }
    }

    public void teleport(Location loc) {
        this.loc = loc;

        for (Player player : this.loadedPlayers) {
            sendPositionPacket(player);
            sendHeadRotationPacket(player, loc.getYaw());
        }
    }

    public void updateMetadata() {
        for (Player player : this.loadedPlayers) {
            sendMetaPacket(player);
        }
    }

    public void updateCustomName() {
        for (Player player : this.loadedPlayers) {
            if (getType() == EntityType.PLAYER) {
                sendDestroyPacket(player);
                continue;
            }
            sendMetaPacket(player);
        }


        if (getType() == EntityType.PLAYER) {
            Bukkit.getScheduler().runTaskLater(FakeMobsPlugin.getInstance(), () -> {
                for (Player player : FakeMob.this.loadedPlayers) {
                    FakeMob.this.sendSpawnPacket(player);
                }
            }, 5L);
        }
    }


    //////////////// -- PACKETS -- ////////////////


    public void sendSpawnPacket(Player player) {
        if (getType() == EntityType.PLAYER) {
            sendPlayerSpawnPacket(player);
        } else {
            sendEntitySpawnPacket(player);
        }
    }

    public void sendPlayerSpawnPacket(final Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.NAMED_ENTITY_SPAWN);

        packet.getIntegers().write(0, Integer.valueOf(getEntityId()));
        packet.getIntegers().write(1, Integer.valueOf((int) Math.floor(this.loc.getX() * 32.0D)));
        packet.getIntegers().write(2, Integer.valueOf((int) Math.floor(this.loc.getY() * 32.0D)));
        packet.getIntegers().write(3, Integer.valueOf((int) Math.floor(this.loc.getZ() * 32.0D)));
        packet.getIntegers().write(4, Integer.valueOf(0));

        packet.getBytes().write(0, Byte.valueOf((byte) (int) (this.loc.getYaw() * 256.0F / 360.0F)));
        packet.getBytes().write(1, Byte.valueOf((byte) (int) (this.loc.getPitch() * 256.0F / 360.0F)));

        final WrappedGameProfile profile = new WrappedGameProfile(this.uniqueId,
                (getCustomName() == null) ? "No Name" : getCustomName());
        if (this.playerSkin != null) {
            profile.getProperties().putAll(this.playerSkin);
        }

        final boolean isSpigot18 = (packet.getGameProfiles().size() == 0);
        if (isSpigot18) {
            packet.getSpecificModifier(UUID.class).write(0, profile.getUUID());
        } else {
            packet.getGameProfiles().write(0, profile);
        }
        packet.getDataWatcherModifier().write(0, this.dataWatcher);

        int protocolVersion = ProtocolLibrary.getProtocolManager().getProtocolVersion(player);
        if (protocolVersion >= 47 || protocolVersion == Integer.MIN_VALUE) {
            PacketContainer infoPacket = ProtocolLibrary.getProtocolManager().createPacket(
                    PacketType.Play.Server.PLAYER_INFO);

            if (isSpigot18) {
                Object playerInfo = ReflectionUtils.createPlayerInfoData(profile.getHandle(), GameMode.SURVIVAL, 0,
                        " ");
                infoPacket.getSpecificModifier(ReflectionUtils.PlayerInfoAction.getNMSClass()).write(0,
                        ReflectionUtils.PlayerInfoAction.ADD_PLAYER);
                infoPacket.getSpecificModifier(List.class).write(0, Arrays.asList(new Object[]{playerInfo}));
            } else {
                infoPacket.getIntegers().write(0, Integer.valueOf(0));
                infoPacket.getIntegers().write(1, Integer.valueOf(0));
                infoPacket.getIntegers().write(2, Integer.valueOf(0));

                infoPacket.getGameProfiles().write(0, profile);
            }

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, infoPacket);
            } catch (Exception e) {
                FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE, "Can''t send player info packet to {0}",
                        player.getName());
                e.printStackTrace();
            }

            Bukkit.getScheduler().runTaskLater(FakeMobsPlugin.getInstance(), (Runnable) () -> {
                if (!FakeMob.this.isPlayerLoaded(player)) {
                    return;
                }
                PacketContainer infoPacket1 = ProtocolLibrary.getProtocolManager().createPacket(
                        PacketType.Play.Server.PLAYER_INFO);

                if (isSpigot18) {
                    Object playerInfo = ReflectionUtils.createPlayerInfoData(profile.getHandle(), GameMode.SURVIVAL,
                            0, "");
                    infoPacket1.getSpecificModifier(ReflectionUtils.PlayerInfoAction.getNMSClass()).write(0,
                            ReflectionUtils.PlayerInfoAction.REMOVE_PLAYER);
                    infoPacket1.getSpecificModifier(List.class).write(0, Arrays.asList(new Object[]{playerInfo}));
                } else {
                    infoPacket1.getIntegers().write(0, Integer.valueOf(4));
                    infoPacket1.getIntegers().write(1, Integer.valueOf(0));
                    infoPacket1.getIntegers().write(2, Integer.valueOf(0));

                    infoPacket1.getGameProfiles().write(0, profile);
                }

                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, infoPacket1);
                } catch (Exception e) {
                    FakeMobsPlugin.getInstance().getLogger().log(Level.WARNING,
                            "Can''t send player info packet to {0}", player.getName());
                    e.printStackTrace();
                }
            }, (this.playerSkin == null) ? 5L : 40L);
        }

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE, "Can''t send spawn packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();

            return;
        }
        sendHeadRotationPacket(player, this.loc.getYaw());
        sendInventoryPacket(player);

        if (!this.playerNametagVisible)
            sendRemovePlayerNametagPacket(player);
        if (this.playerHalfVisible)
            sendShowHalfVisiblePacket(player);
    }

    public void sendEntitySpawnPacket(Player player) {
        if (this.type.isAlive()) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                    PacketType.Play.Server.SPAWN_ENTITY_LIVING);

            packet.getIntegers().write(0, Integer.valueOf(getEntityId()));
            packet.getIntegers().write(1, Integer.valueOf(this.type.getTypeId()));
            packet.getIntegers().write(2, Integer.valueOf((int) Math.floor(this.loc.getX() * 32.0D)));
            packet.getIntegers().write(3, Integer.valueOf((int) Math.floor((this.loc.getY() + 0.001D) * 32.0D)));
            packet.getIntegers().write(4, Integer.valueOf((int) Math.floor(this.loc.getZ() * 32.0D)));

            packet.getBytes().write(0, Byte.valueOf((byte) (int) (this.loc.getYaw() * 256.0F / 360.0F)));
            packet.getBytes().write(1, Byte.valueOf((byte) (int) (this.loc.getPitch() * 256.0F / 360.0F)));
            packet.getBytes().write(2, Byte.valueOf((byte) (int) (this.loc.getYaw() * 256.0F / 360.0F)));

            packet.getDataWatcherModifier().write(0, this.dataWatcher);

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (Exception e) {
                FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                        "Can''t send spawn packet to {0} from mob #{1}",
                        new Object[]{player.getName(), Integer.valueOf(getId())});
                e.printStackTrace();

                return;
            }
            sendInventoryPacket(player);
        } else {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                    PacketType.Play.Server.SPAWN_ENTITY);

            packet.getIntegers().write(0, Integer.valueOf(getEntityId()));
            packet.getIntegers().write(1, Integer.valueOf((int) Math.floor(this.loc.getX() * 32.0D)));
            packet.getIntegers().write(2, Integer.valueOf((int) Math.floor((this.loc.getY() + 0.001D) * 32.0D)));
            packet.getIntegers().write(3, Integer.valueOf((int) Math.floor(this.loc.getZ() * 32.0D)));
            packet.getIntegers().write(4, Integer.valueOf(0));
            packet.getIntegers().write(5, Integer.valueOf(0));
            packet.getIntegers().write(6, Integer.valueOf(0));
            packet.getIntegers().write(7, Integer.valueOf((int) (this.loc.getYaw() * 256.0D / 360.0D)));
            packet.getIntegers().write(8, Integer.valueOf((int) (this.loc.getPitch() * 256.0D / 360.0D)));
            packet.getIntegers().write(9, Integer.valueOf(Util.getIdForEntity(this.type)));
            packet.getIntegers().write(10, Integer.valueOf(0));

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
            } catch (Exception e) {
                FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                        "Can''t send spawn packet to {0} from mob #{1}",
                        new Object[]{player.getName(), Integer.valueOf(getId())});
                e.printStackTrace();
                return;
            }
        }
    }

    public void sendMetaPacket(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.ENTITY_METADATA);

        packet.getIntegers().write(0, Integer.valueOf(getEntityId()));
        packet.getWatchableCollectionModifier().write(0, this.dataWatcher.getWatchableObjects());

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send metadata oacket to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendInventoryPacket(Player player) {
        List<PacketContainer> packets = this.inventory.createPackets(getEntityId());
        if (packets.isEmpty())
            return;
        try {
            for (PacketContainer packet : packets)
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send inventory packets to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendHeadRotationPacket(Player player, Location point) {
        double xDiff = point.getX() - this.loc.getX();

        double zDiff = point.getZ() - this.loc.getZ();
        double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        double newYaw = Math.acos(xDiff / DistanceXZ) * 180.0D / Math.PI;

        if (zDiff < 0.0D)
            newYaw += Math.abs(180.0D - newYaw) * 2.0D;
        double yaw = (float) newYaw - 98.0D;
        sendHeadRotationPacket(player, yaw);
    }

    public void sendHeadRotationPacket(Player player, double yaw) {
        PacketContainer packetHead = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.ENTITY_HEAD_ROTATION);

        packetHead.getIntegers().write(0, Integer.valueOf(getEntityId()));
        packetHead.getBytes().write(0, Byte.valueOf((byte) (int) (yaw * 256.0D / 360.0D)));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetHead);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send head rotation packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendLookPacket(Player player, float yaw, float pitch) {
        PacketContainer packetLook = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.ENTITY_LOOK);

        packetLook.getIntegers().write(0, Integer.valueOf(getEntityId()));
        packetLook.getBytes().write(0, Byte.valueOf((byte) (int) (yaw * 256.0F / 360.0F)));
        packetLook.getBytes().write(1, Byte.valueOf((byte) (int) (pitch * 256.0F / 360.0F)));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packetLook);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE, "Can''t send look packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendRemovePlayerNametagPacket(Player player) {
        PacketContainer teamPacket = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.SCOREBOARD_TEAM);
        teamPacket.getStrings().write(0, "FakeMob-" + this.id + "-NT");
        teamPacket.getStrings().write(1, "FakeMob-" + this.id + "-NT");
        teamPacket.getStrings().write(4, "never");
        teamPacket.getSpecificModifier(Collection.class).write(0, Collections.singletonList(this.name));
        teamPacket.getIntegers().write(1, Integer.valueOf(0));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, teamPacket);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send remove player nametag packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendShowPlayerNametagPacket(Player player) {
        PacketContainer teamPacket = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.SCOREBOARD_TEAM);
        teamPacket.getStrings().write(0, "FakeMob-" + this.id + "-NT");
        teamPacket.getIntegers().write(1, Integer.valueOf(1));
        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, teamPacket);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send remove player nametag packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendShowHalfVisiblePacket(Player player) {
        try {
            PacketContainer teamPacket = ProtocolLibrary.getProtocolManager().createPacket(
                    PacketType.Play.Server.SCOREBOARD_TEAM);
            Team pTeam = player.getScoreboard().getEntryTeam(player.getName());
            teamPacket.getStrings().write(0, pTeam.getName());
            teamPacket.getStrings().write(1, pTeam.getDisplayName());
            teamPacket.getStrings().write(2, pTeam.getPrefix());
            teamPacket.getStrings().write(3, pTeam.getSuffix());
            teamPacket.getStrings().write(4, Util.getNameForNametagVisibility(NameTagVisibility.NEVER));
            int data = 0;
            if (pTeam.allowFriendlyFire())
                data |= 0x1;
            data |= 0x2;
            teamPacket.getIntegers().write(2, data);
            teamPacket.getIntegers().write(1, Integer.valueOf(2));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, teamPacket);

            teamPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            teamPacket.getStrings().write(0, pTeam.getName());
            teamPacket.getIntegers().write(1, Integer.valueOf(3));
            teamPacket.getSpecificModifier(Collection.class).write(0, Collections.singletonList(this.name));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, teamPacket);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send show half visible packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendRemoveHalfVisiblePacket(Player player) {
        try {
            PacketContainer teamPacket = ProtocolLibrary.getProtocolManager().createPacket(
                    PacketType.Play.Server.SCOREBOARD_TEAM);
            Team pTeam = player.getScoreboard().getEntryTeam(player.getName());

            teamPacket.getStrings().write(0, pTeam.getName());
            teamPacket.getIntegers().write(1, Integer.valueOf(4));
            teamPacket.getSpecificModifier(Collection.class).write(0, Arrays.asList(new String[]{this.name}));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, teamPacket);

            teamPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            teamPacket.getStrings().write(0, pTeam.getName());
            teamPacket.getStrings().write(1, pTeam.getDisplayName());
            teamPacket.getStrings().write(2, pTeam.getPrefix());
            teamPacket.getStrings().write(3, pTeam.getSuffix());
            teamPacket.getStrings().write(4, Util.getNameForNametagVisibility(pTeam.getNameTagVisibility()));

            int data = 0;
            if (pTeam.allowFriendlyFire())
                data |= 0x1;
            if (pTeam.canSeeFriendlyInvisibles())
                data |= 0x2;
            teamPacket.getIntegers().write(2, Integer.valueOf(data));
            teamPacket.getIntegers().write(1, Integer.valueOf(2));
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, teamPacket);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send remove half visible packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendPositionPacket(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.ENTITY_TELEPORT);

        packet.getIntegers().write(0, Integer.valueOf(getEntityId()));
        packet.getIntegers().write(1, Integer.valueOf((int) Math.floor(this.loc.getX() * 32.0D)));
        packet.getIntegers().write(2, Integer.valueOf((int) Math.floor((this.loc.getY() + 0.001D) * 32.0D)));
        packet.getIntegers().write(3, Integer.valueOf((int) Math.floor(this.loc.getZ() * 32.0D)));

        packet.getBytes().write(0, Byte.valueOf((byte) (int) (this.loc.getYaw() * 256.0F / 360.0F)));
        packet.getBytes().write(1, Byte.valueOf((byte) (int) (this.loc.getPitch() * 256.0F / 360.0F)));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send position packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendEntityStatusPacket(Player player, byte status) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.ENTITY_STATUS);

        packet.getIntegers().write(0, Integer.valueOf(getEntityId()));
        packet.getBytes().write(0, Byte.valueOf(status));

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send entity status packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();
        }
    }

    public void sendDestroyPacket(Player player) {
        PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(
                PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{getEntityId()});

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
        } catch (Exception e) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                    "Can''t send destroy packet to {0} from mob #{1}",
                    new Object[]{player.getName(), Integer.valueOf(getId())});
            e.printStackTrace();

            return;
        }
        if (ProtocolLibrary.getProtocolManager().getProtocolVersion(player) >= 47 && getType() == EntityType.PLAYER) {
            WrappedGameProfile profile = new WrappedGameProfile(this.uniqueId,
                    (getCustomName() == null) ? "No Name" : getCustomName());
            PacketContainer infoPacket = ProtocolLibrary.getProtocolManager().createPacket(
                    PacketType.Play.Server.PLAYER_INFO);

            boolean spigot18 = (infoPacket.getIntegers().size() == 0);
            if (spigot18) {
                Object playerInfo = ReflectionUtils.createPlayerInfoData(profile.getHandle(), GameMode.SURVIVAL, 0, "");
                infoPacket.getSpecificModifier(ReflectionUtils.PlayerInfoAction.getNMSClass()).write(0,
                        ReflectionUtils.PlayerInfoAction.REMOVE_PLAYER);
                infoPacket.getSpecificModifier(List.class).write(0, Arrays.asList(new Object[]{playerInfo}));
            } else {
                infoPacket.getIntegers().write(0, Integer.valueOf(4));
                infoPacket.getIntegers().write(1, Integer.valueOf(0));
                infoPacket.getIntegers().write(2, Integer.valueOf(0));

                infoPacket.getGameProfiles().write(0, profile);
            }

            try {
                ProtocolLibrary.getProtocolManager().sendServerPacket(player, infoPacket);
            } catch (Exception e) {
                FakeMobsPlugin.getInstance().getLogger().log(Level.SEVERE,
                        "Can''t send player info destroy packet to {0}", player.getName());
                e.printStackTrace();
            }
        }

        if (this.type == EntityType.PLAYER && !this.playerNametagVisible)
            sendShowPlayerNametagPacket(player);
        if (this.type == EntityType.PLAYER && this.playerHalfVisible)
            sendRemoveHalfVisiblePacket(player);
    }
}
