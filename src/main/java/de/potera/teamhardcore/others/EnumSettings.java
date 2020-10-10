package de.potera.teamhardcore.others;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum EnumSettings {

    CRATE_ANIMATION(Collections.unmodifiableList(Arrays.asList("§aAn", "§cAus")), 0),
    DEATH_MSG(Collections.unmodifiableList(Arrays.asList("§aAn", "§cAus")), 0),
    TRADE_REQUESTS(Collections.unmodifiableList(Arrays.asList("§aAn", "§cAus")), 0),
    TP_REQUESTS(Collections.unmodifiableList(Arrays.asList("§aAn", "§cAus")), 0),
    PRIVATE_MESSAGE(Collections.unmodifiableList(Arrays.asList("§aAn", "§cAus")), 1),
    ;

    private final List<String> options;
    private final int defaultOption;

    EnumSettings(List<String> options, int defaultOption) {
        this.options = options;
        this.defaultOption = defaultOption;
    }

    public static EnumSettings getByName(String name) {
        for (EnumSettings settings : values()) {
            if (settings.name().equalsIgnoreCase(name))
                return settings;
        }
        return null;
    }

    public List<String> getOptions() {
        return this.options;
    }

    public int getDefaultOption() {
        return this.defaultOption;
    }

    public String getOption(int index) {
        if (this.options.size() < index)
            return null;
        return this.options.get(index);
    }

    public int getOptionIndex(String option) {
        return this.options.indexOf(option);
    }

}
