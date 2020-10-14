package de.potera.rysefoxx.bossegg;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.files.FileBase;
import de.potera.teamhardcore.utils.StringDefaults;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter

public class BossEggManager {

    private final List<BossEgg> bossEggList;
    private final FileBase config;
    private boolean accessible;
    private final HashMap<BossEgg, List<String>> livingBosses;
    private final HashMap<String, Integer> aliveTimer;
    private final HashMap<String, Double> damageDone;

    public BossEggManager() {
        config = new FileBase("", "bossegg");
        bossEggList = new ArrayList<>();
        livingBosses = new HashMap<>();
        aliveTimer = new HashMap<>();
        damageDone = new HashMap<>();
        accessible = false;
        new BukkitRunnable() {
            @Override
            public void run() {
                load();
            }
        }.runTaskLater(Main.getPlugin(Main.class), 20L);
    }

    public boolean alreadyExists(String name) {
        BossEgg bossEgg = forName(name);
        return bossEgg != null;
    }

    public BossEgg forName(String name) {
        for (BossEgg bossEgg : bossEggList) {
            if (bossEgg.getBossName().equalsIgnoreCase(name)) {
                return bossEgg;
            }
        }
        return null;
    }

    public BossEgg forID(String id) {
        for (Map.Entry<BossEgg, List<String>> data : livingBosses.entrySet()) {
            for (String string : data.getValue()) {
                if (!string.equals(id)) continue;
                return data.getKey();
            }
        }
        return null;
    }

    private void load() {
        accessible = true;
        if (config.getConfig().getKeys(false).isEmpty()) return;
        for (String string : config.getConfig().getKeys(false)) {
            BossEgg bossEgg = new BossEgg(string);
            bossEgg.setItems((List<BossEggSerializer>) config.getConfig().getList(string + ".items"));
            bossEgg.setMaxHealth(config.getConfig().getInt(string + ".maxHealth"));
            bossEgg.setDeathBroadcast(config.getConfig().getBoolean(string + ".deathBroadcast"));
            bossEgg.setItemStack(config.getConfig().getItemStack(string + ".itemStack"));
            bossEgg.setHelmet(config.getConfig().getItemStack(string + ".helmet"));
            bossEgg.setChestPlate(config.getConfig().getItemStack(string + ".chestPlate"));
            bossEgg.setLeggings(config.getConfig().getItemStack(string + ".leggings"));
            bossEgg.setBoots(config.getConfig().getItemStack(string + ".boots"));
            bossEgg.setItemInHand(config.getConfig().getItemStack(string + ".itemInHand"));
            bossEgg.setHoloText(config.getConfig().getString(string + ".holoText"));
            bossEgg.setMinDropAmount(config.getConfig().getInt(string + ".minDropAmount"));
            bossEgg.setMaxDropAmount(config.getConfig().getInt(string + ".maxDropAmount"));
            bossEgg.setBroadcastOnSpawn(config.getConfig().getBoolean(string + ".broadcastOnSpawn"));
            bossEgg.setCanUseAbilities(config.getConfig().getBoolean(string + ".canUseAbilities"));
            bossEgg.setAbilityChance(config.getConfig().getDouble(string + ".abilityChance"));
            bossEgg.setDisplayName(config.getConfig().getString(string + ".displayName"));
            if (EntityType.fromName(config.getConfig().getString(string + ".entityType")) != null) {
                bossEgg.setEntityType(EntityType.fromName(config.getConfig().getString(string + ".entityType")));
            } else {
                Main.getInstance().getLogger().warning("Trying to get a nonexistent entity [ BOSSEGG " + string + " ]");
                Main.getInstance().getLogger().warning("TEntityType automatically set to ZOMBIE");
                bossEgg.setEntityType(EntityType.ZOMBIE);
            }
            bossEggList.add(bossEgg);
        }

    }

    public void updateHelmet(BossEgg bossEgg, ItemStack itemStack) {
        bossEgg.setHelmet(itemStack);
        bossEgg.save();
    }

    public void updateChestPlate(BossEgg bossEgg, ItemStack itemStack) {
        bossEgg.setChestPlate(itemStack);
        bossEgg.save();
    }

    public void updateLeggings(BossEgg bossEgg, ItemStack itemStack) {
        bossEgg.setLeggings(itemStack);
        bossEgg.save();
    }

    public void updateBoots(BossEgg bossEgg, ItemStack itemStack) {
        bossEgg.setBoots(itemStack);
        bossEgg.save();
    }

    public void updateWeapon(BossEgg bossEgg, ItemStack itemStack) {
        bossEgg.setItemInHand(itemStack);
        bossEgg.save();
    }

    public void forceEnd() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity.hasMetadata("BOSS")) {

                    if (entity.getPassenger() != null) {
                        entity.getPassenger().remove();
                    }
                    entity.remove();
                }
            }
        }
    }

    public void help(Player player) {

        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg create <Name> §8- §6BossEgg erstellen");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg delete <Name> §8- §6BossEgg löschen");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setDisplayName <Boss> <Name> §8- §6BossEgg umbenennen");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg list §8- §6Alle BossEggs auflisten");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setMaxHealth <EggName> <Health> §8- §6Max Health setzen");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg addItem <EggName> <Prozent> §8- §6Item hinzufügen");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg withBroadcast <EggName> <Boolean> §8- §6Bei true wird sein Tod gebroadcastet");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setAnnouncement <EggName> <Boolean> §8- §6Bei true wird beim Platzieren des Ei´s die Koordinaten gepostet");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg items <EggName> §8- §6Alle verfügbaren Items ansehen / entfernen");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg get <EggName> §8- §6Ei getten");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setType <EggName> <EntityType> §8- §6EntityType verändern");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg spawn <BossEgg> <X> <Y> <Z> §8- §6Boss spawnen");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setHelmet <BossEgg>");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setChestPlate <BossEgg>");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setLeggings <BossEgg>");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setBoots <BossEgg>");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setWeapon <BossEgg>");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setHolo <BossEgg> <Text> §8- §6Information überm Kopf");
        player.sendMessage(StringDefaults.PREFIX + "§7BossEgg setCollection <BossEgg> <Collection> §8- §6Verändert die Ei Collection");
    }

}
