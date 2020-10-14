package de.potera.rysefoxx.bossegg;

import de.potera.rysefoxx.utils.HologramAPI;
import de.potera.rysefoxx.utils.RandomCollection;
import de.potera.rysefoxx.utils.TimeUtils;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.utils.ItemBuilder;
import de.potera.teamhardcore.utils.StringDefaults;
import de.potera.teamhardcore.utils.Util;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
@Setter

public class BossEgg {

    private String bossName;
    private List<BossEggSerializer> items;
    private ItemStack itemStack;
    private int maxHealth;
    private boolean deathBroadcast;
    private EntityType entityType;
    private ItemStack helmet;
    private ItemStack chestPlate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack itemInHand;
    private String holoText;
    private LivingEntity entity;
    private int minDropAmount;
    private int maxDropAmount;
    private int collection;
    private boolean broadcastOnSpawn;
    private boolean canSpawnHologram;
    private boolean canUseAbilities;
    private double abilityChance;
    private String displayName;


    public BossEgg(String bossName) {
        this.bossName = bossName;
        this.items = new ArrayList<>();
        this.itemStack = new ItemBuilder(Material.BARRIER).build();
        this.maxHealth = 100;
        this.deathBroadcast = false;
        this.entityType = EntityType.ZOMBIE;
        this.helmet = new ItemBuilder(Material.AIR).build();
        this.chestPlate = new ItemBuilder(Material.AIR).build();
        this.leggings = new ItemBuilder(Material.AIR).build();
        this.boots = new ItemBuilder(Material.AIR).build();
        this.itemInHand = new ItemBuilder(Material.AIR).build();
        this.entity = null;
        this.minDropAmount = 1;
        this.maxDropAmount = 4;
        this.collection = 1;
        this.broadcastOnSpawn = false;
        this.canSpawnHologram = true;
        this.canUseAbilities = true;
        this.abilityChance = 15;
        this.displayName = "&7" + this.bossName;
        this.holoText = "§c§lBOSS §8- " + this.getColoredName() + " §8(§c%maxHealth%§8/§c%health%§8)";
    }

    public void spawn(Player player, int x, int y, int z) {
        if (this.entityType == null) {
            Main.getInstance().getLogger().warning("Could not spawn Boss! EntityType is null.");
            player.sendMessage(StringDefaults.PREFIX + "§7Es ist ein Fehler aufgetreten.");
            return;
        }
        Location location = new Location(player.getWorld(), x, y, z);
        boolean wait = false;
        if (!location.getChunk().isLoaded()) {
            location.getChunk().load();
            wait = true;
        }
        long waitingTime = System.currentTimeMillis() + 5000L;
        long systemTime = System.currentTimeMillis();

        if (wait) {
            while (waitingTime > systemTime) {
                waitingTime -= 1000;
            }
            if (waitingTime < systemTime) {
                createEntity(player, location);
                player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Boss wurde erfolgreich gespawnt.");
            }
        } else {
            createEntity(player, location);

            player.sendMessage(StringDefaults.BOSSEGG_PREFIX + "§7Boss wurde erfolgreich gespawnt.");

        }


    }

    public void createEntity(Player player, Location location) {
        LivingEntity livingEntity = (LivingEntity) player.getWorld().spawnEntity(location, this.entityType);
        this.entity = livingEntity;

        Entity entity = player.getWorld().spawnEntity(livingEntity.getLocation(), EntityType.ARMOR_STAND);

        ArmorStand as = (ArmorStand) entity;

        as.setGravity(false);
        as.setVisible(false);
        as.setSmall(true);

        String id;

        id = UUID.randomUUID().toString().replace("-", "");
        List<String> allIds = new ArrayList<>();
        if (Main.getPlugin(Main.class).getBossEggManager().getLivingBosses().containsKey(this)) {
            allIds = Main.getPlugin(Main.class).getBossEggManager().getLivingBosses().get(this);
        }
        allIds.add(id);

        Main.getPlugin(Main.class).getBossEggManager().getLivingBosses().put(this, allIds);
        Main.getPlugin(Main.class).getBossEggManager().getAliveTimer().put(id, 0);
        Main.getPlugin(Main.class).getBossEggManager().getDamageDone().put(id, 0.0D);


        livingEntity.setMetadata("BOSS", new FixedMetadataValue(Main.getPlugin(Main.class), id));
        livingEntity.setMaxHealth(this.maxHealth);
        livingEntity.setHealth(this.maxHealth);
        livingEntity.getEquipment().setHelmet(this.helmet);
        livingEntity.getEquipment().setChestplate(this.chestPlate);
        livingEntity.getEquipment().setLeggings(this.leggings);
        livingEntity.getEquipment().setBoots(this.boots);
        livingEntity.getEquipment().setItemInHand(this.itemInHand);

        livingEntity.getEquipment().setBootsDropChance(0);
        livingEntity.getEquipment().setChestplateDropChance(0);
        livingEntity.getEquipment().setHelmetDropChance(0);
        livingEntity.getEquipment().setLeggingsDropChance(0);
        livingEntity.getEquipment().setItemInHandDropChance(0);

        as.setCustomName(holoText.replace("%health%", String.valueOf(livingEntity.getHealth())).replace("%maxHealth%", String.valueOf(livingEntity.getMaxHealth())));
        as.setCustomNameVisible(true);
        as.setMetadata("BOSSPART", new FixedMetadataValue(Main.getInstance(), "nothing tbh"));

        livingEntity.setPassenger(as);

        new BukkitRunnable() {
            int timer = Main.getPlugin(Main.class).getBossEggManager().getAliveTimer().get(id);

            @Override
            public void run() {
                if (entity.isDead()) {
                    cancel();
                    return;
                }
                timer++;
                Main.getPlugin(Main.class).getBossEggManager().getAliveTimer().put(id, timer);

            }
        }.runTaskTimer(Main.getPlugin(Main.class), 20L, 20L);

    }

    public void dropReward(LivingEntity livingEntity, int rewardAmount) {

        if (this.getItems().isEmpty()) {
            Main.getInstance().getLogger().warning("Boss " + this.getBossName() + " has no rewards");
            return;
        }

        RandomCollection<BossEggSerializer> reward = new RandomCollection<>();
        for (BossEggSerializer bossEggSerializer : this.getItems()) {
            reward.add(bossEggSerializer.getChance(), bossEggSerializer);
        }

        List<BossEggSerializer> items = new ArrayList<>();
        for (int i = 0; i < reward.map.size() * 150; ++i) {
            items.add(reward.next().entry);
        }

        Collections.shuffle(items);

        for (int i = 0; i < rewardAmount; i++) {
            BossEggSerializer bossEggSerializer = items.get(new Random().nextInt(items.size()));
            livingEntity.getLocation().getWorld().dropItem(livingEntity.getLocation(), bossEggSerializer.getItemStack());
            bossEggSerializer.setAmount(bossEggSerializer.getAmount() + 1);
        }

    }

    public void createHologram(LivingEntity livingEntity, int rewardAmount, String metaDataValueId) {

        HologramAPI hologramAPI = new HologramAPI(livingEntity.getLocation(), Arrays.asList(
                "§6§k---------§r §c§lTodesinformationen §6§k---------",
                "§r",
                "§7Hier wurde die Leiche von §c§l" + this.getColoredName() + " §7gefunden.",
                "§r",
                "§6➥ §7Verursachter Schaden§8: §c" + Main.getPlugin(Main.class).getBossEggManager().getDamageDone().get(metaDataValueId),
                "§6➥ §7Überlebte Zeit§8: §c" + TimeUtils.shortInteger(Main.getPlugin(Main.class).getBossEggManager().getAliveTimer().get(metaDataValueId)),
                "§6➥ §7Gedroppte Items§8: §c" + Util.formatBigNumber(rewardAmount),
                "§r",
                "§6§k---------§r §c§lTodesinformationen §6§k---------"));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), hologramAPI::remove, 20 * 60L);
    }

    public void sendInformation(Player killer, int rewardAmount) {
        if (killer == null) return;

        Bukkit.broadcastMessage("§6§l§k---------------------------");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§c§l" + killer.getName() + " §7hat den Boss §c§l" + this.getColoredName() + " §7getötet!");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§7Der Boss hat §c§l" + rewardAmount + " Items §7gedroppt.");
        Bukkit.broadcastMessage("§7Der Boss ist an den Folgenden Koordinaten gestorben");
        Bukkit.broadcastMessage("§cX§8: §6" + killer.getLocation().getBlockX() + " §cY§8: §6" + killer.getLocation().getBlockY() + " §cZ§8: §6" + killer.getLocation().getBlockZ());
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§6§l§k---------------------------");
    }

    public void onDespawn(String meteDataValueId, LivingEntity livingEntity) {

        livingEntity.getLocation().getWorld().playEffect(livingEntity.getLocation(), Effect.EXPLOSION_HUGE, 0, 5);
        livingEntity.getLocation().getWorld().playSound(livingEntity.getLocation(), Sound.EXPLODE, 5, 5);
        if (livingEntity.getPassenger() != null) {
            livingEntity.getPassenger().remove();
        }
        List<String> ids = Main.getPlugin(Main.class).getBossEggManager().getLivingBosses().get(this);
        ids.remove(meteDataValueId);
        Main.getPlugin(Main.class).getBossEggManager().getLivingBosses().put(this, ids);
        Main.getPlugin(Main.class).getBossEggManager().getAliveTimer().remove(meteDataValueId);
        Main.getPlugin(Main.class).getBossEggManager().getDamageDone().remove(meteDataValueId);
    }

    public void useRandomAbility(LivingEntity livingEntity) {
        int randomNumber = Util.randInt(1, 6);
        if (randomNumber == 1) {
            Main.getPlugin(Main.class).getBossEggAbilities().throwPlayer(getRandomPlayerInNearFromBoss(livingEntity), livingEntity.getEyeLocation());
        } else if (randomNumber == 2) {
            Main.getPlugin(Main.class).getBossEggAbilities().throwPlayerUp(getRandomPlayerInNearFromBoss(livingEntity), livingEntity.getEyeLocation());
        } else if (randomNumber == 3) {
            Main.getPlugin(Main.class).getBossEggAbilities().throwNearbyPlayers(livingEntity, 6, 6, 6);
        } else if (randomNumber == 4) {
            Main.getPlugin(Main.class).getBossEggAbilities().throwNearbyPlayersUp(livingEntity, 6, 6, 6);
        } else if (randomNumber == 5) {
            Main.getPlugin(Main.class).getBossEggAbilities().teleportPlayer(getRandomPlayerInNearFromBoss(livingEntity), livingEntity.getEyeLocation(), 5, 6, 5);
        } else if (randomNumber == 6) {
            Main.getPlugin(Main.class).getBossEggAbilities().teleportNearbyPlayers(livingEntity, 6, 6, 6, livingEntity.getEyeLocation(), 5, 6, 5);
        } else {
            Main.getInstance().getLogger().warning("BossEgg Line 243 - Trying to get a nonexistent ability");
        }
    }

    public Player getRandomPlayerInNearFromBoss(LivingEntity livingEntity) {
        List<Player> playerList = new ArrayList<>();
        for (Entity entity : livingEntity.getNearbyEntities(6, 6, 6)) {
            if (!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            if (playerList.contains(player)) continue;
            playerList.add(player);
        }
        Collections.shuffle(playerList);
        return playerList.get(0);
    }

    public String getColoredName() {
        return ChatColor.translateAlternateColorCodes('&', this.getDisplayName());
    }

    public void save() {
        for (BossEgg bossEgg : Main.getPlugin(Main.class).getBossEggManager().getBossEggList()) {
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".items", bossEgg.getItems());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".itemStack", bossEgg.getItemStack());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".maxHealth", bossEgg.getMaxHealth());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".deathBroadcast", bossEgg.isDeathBroadcast());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".entityType", bossEgg.getEntityType().getName());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".helmet", bossEgg.getHelmet());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".chestPlate", bossEgg.getChestPlate());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".leggings", bossEgg.getLeggings());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".boots", bossEgg.getBoots());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".itemInHand", bossEgg.getItemInHand());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".holoText", bossEgg.getHoloText());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".minDropAmount", bossEgg.getMinDropAmount());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".maxDropAmount", bossEgg.getMaxDropAmount());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".collection", bossEgg.getCollection());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".broadcastOnSpawn", bossEgg.isBroadcastOnSpawn());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".canUseAbilities", bossEgg.isCanUseAbilities());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".abilityChance", bossEgg.getAbilityChance());
            Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(bossEgg.getBossName() + ".displayName", bossEgg.getDisplayName());
        }
        Main.getPlugin(Main.class).getBossEggManager().getConfig().saveConfig();
    }

    public void delete() {
        Main.getPlugin(Main.class).getBossEggManager().getBossEggList().remove(this);
        Main.getPlugin(Main.class).getBossEggManager().getConfig().getConfig().set(this.bossName, null);
        Main.getPlugin(Main.class).getBossEggManager().getConfig().saveConfig();
    }


}
