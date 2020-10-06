package de.potera.teamhardcore.managers;

import de.potera.teamhardcore.files.ConfigFile;
import de.potera.teamhardcore.files.FakeEntityFile;
import de.potera.teamhardcore.files.ShopFile;
import de.potera.teamhardcore.files.WarpFile;

public class FileManager {

    private final ConfigFile configFile;
    private final WarpFile warpFile;
    private final ShopFile shopFile;
    private final FakeEntityFile fakeEntityFile;

    public FileManager() {
        this.configFile = new ConfigFile();
        this.warpFile = new WarpFile();
        this.shopFile = new ShopFile();
        this.fakeEntityFile = new FakeEntityFile();
    }

    public FakeEntityFile getFakeEntityFile() {
        return fakeEntityFile;
    }

    public ShopFile getShopFile() {
        return shopFile;
    }

    public WarpFile getWarpFile() {
        return warpFile;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }
}
