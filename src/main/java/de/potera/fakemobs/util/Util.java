package de.potera.fakemobs.util;

import org.bukkit.entity.EntityType;
import org.bukkit.scoreboard.NameTagVisibility;

public class Util {

    public static int getIdForEntity(EntityType type) {
        switch (type) {
            case BOAT:
                return 1;
            case MINECART:
                return 10;
            case ENDER_CRYSTAL:
                return 51;
            case FIREBALL:
                return 63;
            case SMALL_FIREBALL:
                return 64;
            case WITHER_SKULL:
                return 66;
            case ARMOR_STAND:
                return 78;
        }
        return -1;
    }

    public static String getNameForNametagVisibility(NameTagVisibility visibility) {
        switch (visibility) {
            case NEVER:
                return "never";
            case ALWAYS:
                return "always";
            case HIDE_FOR_OWN_TEAM:
                return "hideForOwnTeam";
            case HIDE_FOR_OTHER_TEAMS:
                return "hideForOtherTeams";
        }
        return null;
    }

}
