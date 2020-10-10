package de.potera.teamhardcore.users;

import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.db.CompletionState;
import de.potera.teamhardcore.db.CompletionStateImpl;
import de.potera.teamhardcore.others.EnumPerk;
import de.potera.teamhardcore.others.EnumSettings;
import de.potera.teamhardcore.others.SpyMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class UserData extends CompletionStateImpl {

    private final User user;
    private final Map<SpyMode.SpyModeType, SpyMode> spyModes;
    private final Map<EnumSettings, Integer> settings;
    private final Map<String, Long> kitCooldowns;

    private final Set<EnumPerk> ownedPerks;
    private final Set<EnumPerk> toggledPerks;
    private final Set<EnumPerk> tempDeniedPerks;
    private final Set<UUID> ignoredPlayers;

    private boolean dailyReward;
    private boolean vanished;

    private long dailyRewardTimestamp;

    private CompletionState completionState;

    public UserData(User user) {
        this(user, true);
    }

    public UserData(User user, boolean async) {
        this.user = user;

        this.spyModes = new HashMap<>();
        this.settings = new HashMap<>();
        this.kitCooldowns = new HashMap<>();

        this.ownedPerks = new HashSet<>();
        this.toggledPerks = new HashSet<>();
        this.tempDeniedPerks = new HashSet<>();
        this.ignoredPlayers = new HashSet<>();

        this.vanished = false;
        this.completionState = new CompletionStateImpl();

        saveDefaults(async);
        loadData(async);
        this.completionState.addReadyExecutor(this::checkState);
    }

    public boolean isVanished() {
        return this.vanished;
    }

    public void setVanished(boolean vanished) {
        if (this.vanished == vanished)
            return;
        this.vanished = vanished;
        saveData((this.user.getPlayer() != null));
    }

    public void changeSpyMode(SpyMode.SpyModeType type, boolean active, boolean all, UUID... players) {
        if (active) {
            if (this.spyModes.containsKey(type))
                return;
            SpyMode spyMode = new SpyMode(all);
            if (players.length > 0)
                spyMode.addPlayers(players);
            this.spyModes.put(type, spyMode);
            if (this.user.getPlayer() != null &&
                    !Main.getInstance().getGeneralManager().getPlayersInSpy().contains(this.user.getPlayer()))
                Main.getInstance().getGeneralManager().getPlayersInSpy().add(this.user.getPlayer());
        } else {
            if (!this.spyModes.containsKey(type))
                return;
            this.spyModes.remove(type);
            if (this.spyModes.isEmpty() &&
                    this.user.getPlayer() != null) {
                Main.getInstance().getGeneralManager().getPlayersInSpy().remove(this.user.getPlayer());
            }
        }
        saveData((this.user.getPlayer() != null));
    }

    public void editSpyList(SpyMode.SpyModeType type, boolean add, UUID uuid) {
        if (this.spyModes == null || !this.spyModes.containsKey(type))
            return;
        SpyMode spyMode = this.spyModes.get(type);
        if (spyMode.isAll())
            return;
        if (add) {
            spyMode.addPlayer(uuid);
        } else {
            spyMode.removePlayer(uuid);
        }
        if (spyMode.getPlayers().isEmpty()) {
            changeSpyMode(type, false, false);
            return;
        }
        saveData((this.user.getPlayer() != null));
    }

    public boolean hasSpyModeActive(SpyMode.SpyModeType type) {
        return (getSpyMode(type) != null);
    }

    public SpyMode getSpyMode(SpyMode.SpyModeType type) {
        return this.spyModes.get(type);
    }

    private void loadSpyModes(String jsonStr) {
        JSONObject mainObject = new JSONObject(jsonStr);
        if (mainObject.has("command")) {
            JSONObject commandObject = mainObject.getJSONObject("command");
            boolean all = commandObject.getBoolean("all");
            SpyMode spyModeCmd = new SpyMode(all);
            if (!all)
                for (Object uuidObj : commandObject.getJSONArray("players")) {
                    spyModeCmd.addPlayer(UUID.fromString((String) uuidObj));
                }
            this.spyModes.put(SpyMode.SpyModeType.COMMAND, spyModeCmd);
        }
        if (mainObject.has("message")) {
            JSONObject messageObject = mainObject.getJSONObject("message");
            boolean all = messageObject.getBoolean("all");
            SpyMode spyModeMsg = new SpyMode(all);
            if (!all)
                for (Object uuidObj : messageObject.getJSONArray("players")) {
                    spyModeMsg.addPlayer(UUID.fromString((String) uuidObj));
                }
            this.spyModes.put(SpyMode.SpyModeType.MESSAGE, spyModeMsg);
        }
        if (!this.spyModes.isEmpty() && this.user.getPlayer() != null)
            Main.getInstance().getGeneralManager().getPlayersInSpy().add(this.user.getPlayer());
    }

    private JSONObject saveSpyModes() {
        JSONObject mainObject = new JSONObject();
        if (!this.spyModes.isEmpty()) {
            if (this.spyModes.containsKey(SpyMode.SpyModeType.COMMAND)) {
                SpyMode spyMode = this.spyModes.get(SpyMode.SpyModeType.COMMAND);
                boolean all = spyMode.isAll();
                JSONObject commandObject = new JSONObject();
                commandObject.put("all", all);
                if (!all) {
                    JSONArray uuidArray = new JSONArray();
                    for (UUID uuid : spyMode.getPlayers())
                        uuidArray.put(uuid.toString());
                    commandObject.put("players", uuidArray);
                }
                mainObject.put("command", commandObject);
            }
            if (this.spyModes.containsKey(SpyMode.SpyModeType.MESSAGE)) {
                SpyMode spyMode = this.spyModes.get(SpyMode.SpyModeType.MESSAGE);
                boolean all = spyMode.isAll();
                JSONObject messageObject = new JSONObject();
                messageObject.put("all", all);
                if (!all) {
                    JSONArray uuidArray = new JSONArray();
                    for (UUID uuid : spyMode.getPlayers())
                        uuidArray.put(uuid.toString());
                    messageObject.put("players", uuidArray);
                }
                mainObject.put("message", messageObject);
            }
        }
        return mainObject;
    }

    public Map<EnumSettings, Integer> getSettings() {
        return this.settings;
    }

    public int getSettingsOption(EnumSettings settings) {
        if (!this.settings.containsKey(settings))
            return settings.getDefaultOption();
        return this.settings.get(settings);
    }

    public void setSettingsOption(EnumSettings settings, int option) {
        if (settings.getDefaultOption() == option) {
            this.settings.remove(settings);
        } else {
            this.settings.put(settings, option);
        }
        saveData((this.user.getPlayer() != null));
    }

    private void loadSettingsData(String jsonStr) {
        JSONObject mainObject = new JSONObject(jsonStr);
        for (String settingsStr : mainObject.keySet()) {
            EnumSettings settings = EnumSettings.getByName(settingsStr);
            if (settings == null)
                continue;
            int option = mainObject.getInt(settingsStr);
            this.settings.put(settings, option);
        }
    }

    private JSONObject saveSettingsData() {
        JSONObject mainObject = new JSONObject();
        for (Map.Entry<EnumSettings, Integer> entrySettings : this.settings.entrySet()) {
            EnumSettings settings = entrySettings.getKey();
            int option = entrySettings.getValue();
            if (settings.getDefaultOption() == option)
                continue;
            mainObject.put(settings.name(), option);
        }
        return mainObject;
    }

    public boolean isPerkToggled(EnumPerk perk) {
        return (this.toggledPerks.contains(perk) && !this.tempDeniedPerks.contains(perk));
    }

    public void addPerk(EnumPerk perk) {
        if (this.ownedPerks.contains(perk)) return;
        this.ownedPerks.add(perk);
        saveData(this.user.getPlayer() != null);
    }

    public void removePerk(EnumPerk perk) {
        if (!this.ownedPerks.contains(perk)) return;
        this.ownedPerks.remove(perk);
        togglePerk(perk, false);
        saveData(this.user.getPlayer() != null);
    }

    public Set<EnumPerk> getOwnedPerks() {
        return ownedPerks;
    }

    public void togglePerk(EnumPerk perk) {
        togglePerk(perk, !this.toggledPerks.contains(perk));
    }

    public void togglePerk(EnumPerk perk, boolean state) {
        if ((state && this.toggledPerks.contains(perk)) || (!state && !this.toggledPerks.contains(perk)))
            return;

        if (state)
            this.toggledPerks.add(perk);
        else this.toggledPerks.remove(perk);
        if (this.user.getPlayer() != null) {
            if (perk == EnumPerk.NO_HUNGER && this.toggledPerks.contains(perk))
                this.user.getPlayer().setFoodLevel(30);
            if (!this.tempDeniedPerks.contains(perk) && perk.isPotionEffect()) {
                if (state) {
                    Main.getInstance().getGeneralManager().addPlayerToPerkEffect(this.user.getPlayer(), perk);
                } else {
                    if (this.user.getPlayer().hasPotionEffect(perk.getType()))
                        this.user.getPlayer().removePotionEffect(perk.getType());
                    Main.getInstance().getGeneralManager().removePlayerFromPerkEffect(this.user.getPlayer(), perk);
                }
            }
        }
        saveData(this.user.getPlayer() != null);
    }

    public void togglePerkTemporarily(EnumPerk perk, boolean state) {
        if (state) {
            this.tempDeniedPerks.remove(perk);
            Main.getInstance().getGeneralManager().addPlayerToPerkEffect(this.user.getPlayer(), perk);
            Main.getInstance().getGeneralManager().refreshPerkEffects(this.user.getPlayer());
        } else {
            this.tempDeniedPerks.add(perk);
            Main.getInstance().getGeneralManager().removePlayerFromPerkEffect(this.user.getPlayer(), perk);
        }
    }

    public Set<EnumPerk> getToggledPerks() {
        return toggledPerks;
    }

    public Set<EnumPerk> getTempDeniedPerks() {
        return tempDeniedPerks;
    }

    private void loadPerkData(String jsonStr) {
        JSONObject mainObject = new JSONObject(jsonStr);

        if (mainObject.has("owned")) {
            JSONArray array = mainObject.getJSONArray("owned");
            for (Object ownedObj : array) {
                EnumPerk perk = EnumPerk.getByName((String) ownedObj);
                if (perk == null)
                    continue;
                this.ownedPerks.add(perk);
            }
        }

        if (mainObject.has("toggled")) {
            JSONArray toggledArray = mainObject.getJSONArray("toggled");
            for (Object perkStrObj : toggledArray) {
                String perkStr = (String) perkStrObj;
                EnumPerk perk = EnumPerk.getByName(perkStr);
                if (perk != null)
                    this.toggledPerks.add(perk);
            }
        }
    }

    private JSONObject savePerkData() {
        JSONObject mainObject = new JSONObject();
        if (!this.ownedPerks.isEmpty()) {
            JSONArray ownedArray = new JSONArray();
            for (EnumPerk perk : this.ownedPerks)
                ownedArray.put(perk.name());
            mainObject.put("owned", ownedArray);
        }
        if (!this.toggledPerks.isEmpty()) {
            JSONArray toggledArray = new JSONArray();
            for (EnumPerk perk : this.toggledPerks)
                toggledArray.put(perk.getName());
            mainObject.put("toggled", toggledArray);
        }
        return mainObject;
    }

    public void addKitCooldown(String name, Long cooldown) {
        this.kitCooldowns.put(name, cooldown);
        saveData(this.user.getPlayer() != null);
    }

    public void removeKitCooldown(String name) {
        if (!this.kitCooldowns.containsKey(name)) return;
        this.kitCooldowns.remove(name);
        saveData(this.user.getPlayer() != null);
    }

    public boolean hasKitCooldown(String name) {
        if (!this.kitCooldowns.containsKey(name)) return false;
        long diff = this.kitCooldowns.get(name) - System.currentTimeMillis();

        if (diff / 1000L > 0L) return true;
        removeKitCooldown(name);
        return false;
    }

    public Map<String, Long> getKitCooldowns() {
        return kitCooldowns;
    }

    private void loadKitData(String jsonStr) {
        JSONObject mainObject = new JSONObject(jsonStr);
        if (mainObject.has("cooldowns")) {
            JSONObject cdObject = mainObject.getJSONObject("cooldowns");
            for (String kitName : cdObject.keySet())
                this.kitCooldowns.put(kitName, cdObject.getLong(kitName));
        }
    }

    private JSONObject saveKitData() {
        JSONObject mainObject = new JSONObject();
        if (!this.kitCooldowns.isEmpty()) {
            JSONObject cdObject = new JSONObject();
            for (Map.Entry<String, Long> entryCooldown : this.kitCooldowns.entrySet())
                cdObject.put(entryCooldown.getKey(), entryCooldown.getValue());
            mainObject.put("cooldowns", cdObject);
        }
        return mainObject;
    }

    public Set<UUID> getIgnoredPlayers() {
        return ignoredPlayers;
    }

    public void addIgnoredPlayer(UUID uuid) {
        if (this.user.getUuid().equals(uuid)) return;
        if (this.ignoredPlayers.contains(uuid)) return;
        this.ignoredPlayers.add(uuid);
        saveData(this.user.getPlayer() != null);
    }

    public void removeIgnoredPlayer(UUID uuid) {
        if (this.user.getUuid().equals(uuid)) return;
        if (!this.ignoredPlayers.contains(uuid)) return;
        this.ignoredPlayers.remove(uuid);
        saveData(this.user.getPlayer() != null);
    }

    private void loadIgnoreData(String jsonStr) {
        JSONArray mainArray = new JSONArray(jsonStr);
        for (Object ignoredObj : mainArray) {
            UUID ignored = UUID.fromString((String) ignoredObj);
            this.ignoredPlayers.add(ignored);
        }
    }

    private JSONArray saveIgnoreData() {
        JSONArray mainArray = new JSONArray();
        for (UUID ignored : this.ignoredPlayers)
            mainArray.put(ignored.toString());
        return mainArray;
    }

    public boolean hasDailyReward() {
        return this.dailyReward;
    }

    public void setDailyReward(boolean dailyReward) {
        if (this.dailyReward == dailyReward)
            return;
        this.dailyReward = dailyReward;
        this.dailyRewardTimestamp = System.currentTimeMillis();
        saveData(this.user.getPlayer() != null);
    }

    private void checkDailyRewardState() {
        if (!this.dailyReward) {
            LocalDate dateNow = LocalDate.now();
            LocalDate dateLastReward = LocalDateTime.ofInstant(Instant.ofEpochMilli(this.dailyRewardTimestamp),
                    TimeZone.getDefault().toZoneId()).toLocalDate();
            if (dateNow.isAfter(dateLastReward))
                setDailyReward(true);
        }
    }

    private void loadRewardData(String jsonStr) {
        JSONObject mainObject = new JSONObject(jsonStr);
        JSONObject dailyObject = mainObject.getJSONObject("daily");
        this.dailyReward = dailyObject.getBoolean("free");
        this.dailyRewardTimestamp = dailyObject.getLong("time");
    }

    private JSONObject saveRewardData() {
        JSONObject mainObject = new JSONObject();
        JSONObject dailyObject = new JSONObject();
        dailyObject.put("free", this.dailyReward);
        dailyObject.put("time", this.dailyRewardTimestamp);
        mainObject.put("daily", dailyObject);
        return mainObject;
    }

    public void checkState() {
        checkDailyRewardState();
    }

    private void loadData(boolean async) {

    }

    private void saveData(boolean async) {

    }

    private void saveDefaults(boolean async) {

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
