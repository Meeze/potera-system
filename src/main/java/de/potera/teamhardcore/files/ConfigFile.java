package de.potera.teamhardcore.files;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ConfigFile extends FileBase {

    private final List<Pattern> blockedWordPatterns = new ArrayList<>();
    private final List<String> combatCommands = new ArrayList<>();
    private final List<String> newbieGreetingMessage = new ArrayList<>();
    private final List<String> greetingMessage = new ArrayList<>();
    private final List<String> autoMessages = new ArrayList<>();
    private String newbieJoinMessage;

    public ConfigFile() {
        super("", "config");
        writeDefaults();
        loadBlockedWordPatterns();
        loadCombatCommands();
        loadNewbieGreetingMessage();
        loadGreetingMessage();
        loadNewbieJoinMessage();
        loadAutoMessages();
    }

    private void writeDefaults() {
        FileConfiguration cfg = getConfig();

        cfg.addDefault("MySQL.Host", "host");
        cfg.addDefault("MySQL.Port", "3306");
        cfg.addDefault("MySQL.User", "user");
        cfg.addDefault("MySQL.Pass", "pass");
        cfg.addDefault("MySQL.DB", "db");

        cfg.addDefault("WordBlacklist", new ArrayList<>());
        cfg.addDefault("AllowedCombatCommands", Arrays.asList("cmd1", "cmd2"));

        cfg.addDefault("NewbieJoinMessage", "&c%player% ist neu auf dem Server! (Template)");
        cfg.addDefault("NewbieGreetingMessage", new ArrayList<>());
        cfg.addDefault("GreetingMessage", new ArrayList<>());
        cfg.addDefault("AutoMessages", new ArrayList<>());

        cfg.options().copyDefaults(true);
        saveConfig();
    }

    public void loadBlockedWordPatterns() {
        this.blockedWordPatterns.clear();
        FileConfiguration cfg = getConfig();
        if (cfg.get("WordBlacklist") == null)
            return;
        for (String blacklisted : cfg.getStringList("WordBlacklist")) {
            StringBuilder sb = new StringBuilder();
            for (char ch : blacklisted.toCharArray()) {
                String chst = String.valueOf(ch);
                sb.append("[").append(chst).append("]+");
            }
            String regex = (sb.length() == 0) ? "" : sb.substring(0, sb.length() - 1);
            this.blockedWordPatterns.add(Pattern.compile(regex, 2));
        }
    }

    public List<Pattern> getBlockedWordPatterns() {
        return this.blockedWordPatterns;
    }

    private void loadCombatCommands() {
        FileConfiguration cfg = getConfig();
        if (cfg.get("AllowedCombatCommands") == null)
            return;
        for (String allowed : cfg.getStringList("AllowedCombatCommands")) {
            this.combatCommands.add(allowed.toLowerCase());
        }
    }

    public List<String> getCombatCommands() {
        return this.combatCommands;
    }

    private void loadNewbieGreetingMessage() {
        this.newbieGreetingMessage.clear();
        FileConfiguration cfg = getConfig();
        if (cfg.get("NewbieGreetingMessage") == null) {
            this.newbieGreetingMessage.add("§cNeulingsnachricht nicht gesetzt.");
            return;
        }
        for (String msg : cfg.getStringList("NewbieGreetingMessage")) {
            this.newbieGreetingMessage.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public List<String> getNewbieGreetingMessage() {
        return this.newbieGreetingMessage;
    }

    private void loadGreetingMessage() {
        this.greetingMessage.clear();
        FileConfiguration cfg = getConfig();
        if (cfg.get("GreetingMessage") == null) {
            this.greetingMessage.add("§cBegrüßungsnachricht nicht gesetzt.");
            return;
        }
        for (String msg : cfg.getStringList("GreetingMessage")) {
            this.greetingMessage.add(ChatColor.translateAlternateColorCodes('&', msg));
        }
    }

    public List<String> getGreetingMessage() {
        return this.greetingMessage;
    }

    private void loadNewbieJoinMessage() {
        this.newbieJoinMessage = null;
        FileConfiguration cfg = getConfig();
        if (cfg.get("NewbieJoinMessage") == null) {
            this.newbieJoinMessage = "§cNeulingsankündigung nicht gesetzt.";
            return;
        }
        this.newbieJoinMessage = ChatColor.translateAlternateColorCodes('&', cfg.getString("NewbieJoinMessage"));
    }

    public String getNewbieJoinMessage() {
        return this.newbieJoinMessage;
    }

    private void loadAutoMessages() {
        FileConfiguration cfg = getConfig();
        if (cfg.get("AutoMessages") == null)
            return;
        for (String msg : cfg.getStringList("AutoMessages")) {
            this.autoMessages.add(ChatColor.translateAlternateColorCodes('&', msg).replace("%b%", "\n"));
        }
    }

    public List<String> getAutoMessages() {
        return this.autoMessages;
    }

}
