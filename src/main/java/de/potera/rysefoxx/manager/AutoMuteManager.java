package de.potera.rysefoxx.manager;

import de.potera.teamhardcore.files.FileBase;
import de.potera.teamhardcore.utils.StringDefaults;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@Getter
public class AutoMuteManager {

    private final FileBase config;
    private final List<String> disallowedWords;


    public AutoMuteManager() {
        this.config = new FileBase("", "automute");
        this.disallowedWords = new ArrayList<>();


        this.load();
    }


    public void addWord(Player player, String word) {
        if (wordInList(word)) {
            player.sendMessage(StringDefaults.PREFIX + "§c" + word + " §7befindet sich bereits in der Liste.");
            return;
        }
        this.disallowedWords.add(word);
        player.sendMessage(StringDefaults.PREFIX + "§7Du hast erfolgreich das Wort §c" + word + " §7hinzugefügt.");
    }

    public void removeWord(Player player, String word) {
        if (!wordInList(word)) {
            player.sendMessage(StringDefaults.PREFIX + "§c" + word + " §7befindet sich nicht in der Liste.");
            return;
        }
        this.disallowedWords.remove(word);
        player.sendMessage(StringDefaults.PREFIX + "§7Du hast erfolgreich das Wort §C" + word + " §7hinzugefügt.");
    }

    public boolean wordInList(String word) {
        for (String words : this.disallowedWords) {
            if (!word.toLowerCase().equals(words.toLowerCase())) continue;
            return true;
        }
        return false;
    }

    public String getDisallowedWord(String message) {
        for (String words : this.disallowedWords) {
            if (!words.toLowerCase().equalsIgnoreCase(message.toLowerCase())) continue;
            return words;
        }
        return "";
    }

    private void load() {
        if (this.config.getConfig().getStringList("words").isEmpty()) return;

        this.disallowedWords.addAll(this.config.getConfig().getStringList("words"));
    }

    public void save() {
        this.config.getConfig().set("words", this.disallowedWords);
        this.config.saveConfig();
    }

}
