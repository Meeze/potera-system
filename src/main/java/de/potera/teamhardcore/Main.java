package de.potera.teamhardcore;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.potera.Bootstrap;
import de.potera.fakemobs.FakeMobsPlugin;
import de.potera.klysma.CooldownAPI;
import de.potera.klysma.kits.commands.*;
import de.potera.realmeze.punishment.command.BanCommand;
import de.potera.realmeze.punishment.command.MuteCommand;
import de.potera.realmeze.punishment.command.UnbanCommand;
import de.potera.realmeze.punishment.command.UnmuteCommand;
import de.potera.realmeze.punishment.controller.PunishmentController;
import de.potera.realmeze.punishment.service.PunishmentService;
import de.potera.rysefoxx.bossegg.BossEggAbilities;
import de.potera.rysefoxx.bossegg.BossEggCommand;
import de.potera.rysefoxx.bossegg.BossEggManager;
import de.potera.rysefoxx.bossegg.BossEggSerializer;
import de.potera.rysefoxx.commands.*;
import de.potera.rysefoxx.manager.*;
import de.potera.rysefoxx.menubuilder.manager.InventoryListener;
import de.potera.rysefoxx.menubuilder.manager.MenuBuilderPlugin;
import de.potera.rysefoxx.trade.TradeCommand;
import de.potera.rysefoxx.trade.TradeManager;
import de.potera.rysefoxx.utils.HologramAPI;
import de.potera.teamhardcore.commands.*;
import de.potera.teamhardcore.managers.*;
import de.potera.teamhardcore.utils.VirtualAnvil;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
@Setter
public class Main extends JavaPlugin {

    private static Main instance;

    private FileManager fileManager;
    private DatabaseManager databaseManager;
    private GeneralManager generalManager;
    private UserManager userManager;
    private WarpManager warpManager;
    private AntilagManager antilagManager;
    private ShopManager shopManager;
    private ScoreboardManager scoreboardManager;
    private FakeEntityManager fakeEntityManager;
    private AmsManager amsManager;
    private MineManager minesManager;
    private SupportManager supportManager;
    private ReportManager reportManager;
    private CrateManager crateManager;
    private RouletteManager rouletteManager;
    private ClanManager clanManager;
    private CoinflipManager coinflipManager;
    private RankingManager rankingManager;
    private JackpotManager jackpotManager;
    private CombatManager combatManager;

    private TradeManager tradeManager;
    private DailyPotManager dailyPotManager;
    private BossEggManager bossEggManager;
    private BossEggAbilities bossEggAbilities;
    private TeamManager teamManager;
    private EnderChestManager enderChestManager;
    private AutoMuteManager autoMuteManager;
    private ItemManager itemManager;

    private PunishmentController punishmentController;

    private boolean shutdownDirectly = false;

    private WorldEditPlugin worldEditPlugin;


    public static Main getInstance() {
        return instance;
    }

    public static File FILE_COOLDOWNS = new File("plugins" + File.separator + "PoteraPvP" + File.separator + "cooldowns.dat");

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onDisable() {
        CooldownAPI.saveCooldowns(FILE_COOLDOWNS);
        if (shutdownDirectly) return;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getOpenInventory().getTopInventory() == null) continue;
            player.closeInventory();
        }

        this.databaseManager.terminate();
        this.amsManager.onDisable();
        this.fakeEntityManager.onDisable();
        this.scoreboardManager.onDisable();
        this.clanManager.onDisable();
        //this.userManager.onDisable();
        this.rankingManager.onDisable();
        this.combatManager.onDisable();
        this.dailyPotManager.onDisable();
        this.bossEggManager.forceEnd();
        this.enderChestManager.onDisable();
        this.autoMuteManager.save();
        FakeMobsPlugin.onDisable(this);


        HologramAPI.holograms.forEach((hologramAPI, armorStands) -> armorStands.forEach(Entity::remove));
    }

    @Override
    public void onEnable() {
        FakeMobsPlugin.onEnable(this);
        CooldownAPI.loadCooldowns(FILE_COOLDOWNS);
        worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        VirtualAnvil.onEnable();
        MenuBuilderPlugin.instance = instance;
        Bukkit.getPluginManager().registerEvents(MenuBuilderPlugin.inventoryListener = new InventoryListener(this), this);
        registerAll();
        Bootstrap bootstrap = new Bootstrap(getInstance());
    }

    private void registerAll() {
        this.fileManager = new FileManager();
        this.databaseManager = new DatabaseManager();

        if (!this.databaseManager.init()) {
            LogManager.getLogger(Main.class).warn("Datenbankverbindung konnte nicht aufgebaut werden!");
            shutdownDirectly();
            return;
        }

        ConfigurationSerialization.registerClass(BossEggSerializer.class);
        this.userManager = new UserManager();
        this.generalManager = new GeneralManager();
        this.warpManager = new WarpManager();
        this.antilagManager = new AntilagManager();
        this.shopManager = new ShopManager();
        this.scoreboardManager = new ScoreboardManager();
        this.fakeEntityManager = new FakeEntityManager();
        this.amsManager = new AmsManager();
        this.minesManager = new MineManager();
        this.supportManager = new SupportManager();
        this.reportManager = new ReportManager();
        this.crateManager = new CrateManager();
        this.rouletteManager = new RouletteManager();
        this.clanManager = new ClanManager();
        this.coinflipManager = new CoinflipManager();
        this.rankingManager = new RankingManager();
        this.jackpotManager = new JackpotManager();
        this.combatManager = new CombatManager();
        this.punishmentController = new PunishmentController();
        punishmentController.setPunishmentService(new PunishmentService());
        this.punishmentController.init();
        this.dailyPotManager = new DailyPotManager();
        this.tradeManager = new TradeManager();
        this.bossEggManager = new BossEggManager();
        this.bossEggAbilities = new BossEggAbilities();
        this.teamManager = new TeamManager();
        this.enderChestManager = new EnderChestManager();
        this.autoMuteManager = new AutoMuteManager();
        this.itemManager = new ItemManager();

        PluginManager pm = Bukkit.getPluginManager();

        getCommand("item").setExecutor(new ItemCommand());
        getCommand("crate").setExecutor(new CrateCommand());
        getCommand("automute").setExecutor(new AutoMuteCommand());
        getCommand("teleport").setExecutor(new TeleportCommand());
        getCommand("enderchest").setExecutor(new EnderChestCommand());
        getCommand("team").setExecutor(new TeamCommand());
        getCommand("randomteleport").setExecutor(new RandomTeleportCommand());
        getCommand("trash").setExecutor(new TrashCommand());
        getCommand("clear").setExecutor(new CommandClear());
        getCommand("broadcast").setExecutor(new CommandBroadcast());
        getCommand("chatclear").setExecutor(new CommandChatclear());
        getCommand("rename").setExecutor(new CommandRename());
        getCommand("relore").setExecutor(new CommandRelore());
        getCommand("bodysee").setExecutor(new CommandBodysee());
        getCommand("fakemessage").setExecutor(new CommandFakemessage());
        getCommand("feed").setExecutor(new CommandFeed());
        getCommand("fly").setExecutor(new CommandFly());
        getCommand("gamemode").setExecutor(new CommandGamemode());
        getCommand("stack").setExecutor(new CommandStack());
        getCommand("list").setExecutor(new CommandList());
        getCommand("more").setExecutor(new CommandMore());
        getCommand("teamchat").setExecutor(new CommandTeamchat());
        getCommand("tpa").setExecutor(new CommandTpa());
        getCommand("tpall").setExecutor(new CommandTpa());
        getCommand("tphere").setExecutor(new CommandTpa());
        getCommand("tptop").setExecutor(new CommandTpa());
        getCommand("warp").setExecutor(new CommandWarp());
        getCommand("spawn").setExecutor(new CommandSpawn());
        getCommand("fix").setExecutor(new CommandFix());
        getCommand("clearlag").setExecutor(new CommandClearlag());
        getCommand("god").setExecutor(new CommandGod());
        getCommand("gc").setExecutor(new CommandGc());
        getCommand("workbench").setExecutor(new CommandWorkbench());
        getCommand("sudo").setExecutor(new CommandSudo());
        getCommand("giveall").setExecutor(new CommandGiveall());
        getCommand("messagespy").setExecutor(new CommandMessagespy());
        getCommand("kill").setExecutor(new CommandKill());
        getCommand("random").setExecutor(new CommandRandom());
        getCommand("time").setExecutor(new CommandTime());
        getCommand("msg").setExecutor(new CommandMsg());
        getCommand("back").setExecutor(new CommandBack());
        getCommand("commandspy").setExecutor(new CommandCommandspy());
        getCommand("countdown").setExecutor(new CommandCountdown());
        getCommand("globalmute").setExecutor(new CommandGlobalmute());
        getCommand("head").setExecutor(new CommandHead());
        getCommand("invsee").setExecutor(new CommandInvsee());
        getCommand("stats").setExecutor(new CommandStats());
        getCommand("shop").setExecutor(new CommandShop());
        getCommand("settings").setExecutor(new CommandSettings());
        getCommand("regeln").setExecutor(new CommandRegeln());
        getCommand("vanish").setExecutor(new CommandVanish());
        getCommand("vote").setExecutor(new CommandVote());
        getCommand("ping").setExecutor(new CommandPing());
        getCommand("money").setExecutor(new CommandMoney());
        getCommand("build").setExecutor(new CommandBuild());
        getCommand("fakeentity").setExecutor(new CommandFakeentity());
        getCommand("home").setExecutor(new CommandHome());
        getCommand("ams").setExecutor(new CommandAms());
        getCommand("perks").setExecutor(new CommandPerks());
        getCommand("mines").setExecutor(new CommandMines());
        getCommand("support").setExecutor(new CommandSupport());
        getCommand("report").setExecutor(new CommandReport());
        getCommand("roulette").setExecutor(new CommandRoulette());
        getCommand("kit").setExecutor(new KitCommand());
        getCommand("kits").setExecutor(new KitsCommand());
        getCommand("kitmgr").setExecutor(new KitManagerCommand());
        getCommand("kitinv").setExecutor(new KitInvCommand());
        getCommand("kitpreview").setExecutor(new KitPreviewCommand());
        getCommand("clan").setExecutor(new CommandClan());
        getCommand("coinflip").setExecutor(new CommandCoinflip());
        getCommand("ranking").setExecutor(new CommandRanking());
        getCommand("jackpot").setExecutor(new CommandJackpot());
        getCommand("freeze").setExecutor(new CommandFreeze());
        getCommand("combatwall").setExecutor(new CommandCombatwall());
        getCommand("ignore").setExecutor(new CommandIgnore());
        getCommand("pvpshop").setExecutor(new CommandPvPShop());
        getCommand("reward").setExecutor(new CommandReward());
        getCommand("kopfgeld").setExecutor(new CommandKopfgeld());
        getCommand("ban").setExecutor(new BanCommand(getPunishmentController()));
        getCommand("pvp").setExecutor(new PvPCommand());
        getCommand("dailypot").setExecutor(new DailyPotCommand());
        getCommand("trade").setExecutor(new TradeCommand());
        getCommand("bossegg").setExecutor(new BossEggCommand());
        getCommand("warn").setExecutor(new WarnCommand());
        getCommand("delwarn").setExecutor(new DelWarnCommand());
        getCommand("mute").setExecutor(new MuteCommand(getPunishmentController()));
        getCommand("unban").setExecutor(new UnbanCommand(getPunishmentController()));
        getCommand("unmute").setExecutor(new UnmuteCommand(getPunishmentController()));
    }

    public void shutdownDirectly() {
        this.shutdownDirectly = true;
        getServer().shutdown();
    }

    public BossEggManager getBossEggManager() {
        return bossEggManager;
    }

    public MineManager getMinesManager() {
        return minesManager;
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public FakeEntityManager getFakeEntityManager() {
        return fakeEntityManager;
    }

    public ShopManager getShopManager() {
        return shopManager;
    }

    public GeneralManager getGeneralManager() {
        return generalManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public AmsManager getAmsManager() {
        return amsManager;
    }

    public AntilagManager getAntilagManager() {
        return antilagManager;
    }

    public SupportManager getSupportManager() {
        return supportManager;
    }

    public ReportManager getReportManager() {
        return reportManager;
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public RouletteManager getRouletteManager() {
        return rouletteManager;
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public CoinflipManager getCoinflipManager() {
        return coinflipManager;
    }

    public RankingManager getRankingManager() {
        return rankingManager;
    }

    public JackpotManager getJackpotManager() {
        return jackpotManager;
    }

    public CombatManager getCombatManager() {
        return combatManager;
    }

    public PunishmentController getPunishmentController() {
        return punishmentController;
    }
}
