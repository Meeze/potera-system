package de.potera.rysefoxx.utils;

import java.util.ArrayList;
import java.util.List;

public enum TimeUtils {

    SECOND("Sekunden", 1L, "s"), MINUTE("Minuten", 60L, "m"), HOUR("Stunden", 3600L, "h"), DAY("Tage", 86400L, "d"), WEEK("Wochen", 604800L, "week");

    private final String name;
    private final long toSecond;
    private final String shortcut;

    TimeUtils(String name, long toSecond, String shortcut) {
        this.name = name;
        this.toSecond = toSecond;
        this.shortcut = shortcut;
    }

    public static List<String> getUnitsAsString() {
        List<String> units = new ArrayList<>();
        for (TimeUtils unit : values()) {
            units.add(unit.getShortcut().toLowerCase());
        }
        return units;
    }

    public static TimeUtils getUnitShortCut(String unit) {
        for (TimeUtils units : values()) {
            if (units.getShortcut().toLowerCase().equals(unit.toLowerCase())) {
                return units;
            }
        }
        return null;
    }


    public static TimeUtils getUnitFullName(String unit) {
        for (TimeUtils units : values()) {
            if (units.getName().toLowerCase().equals(unit.toLowerCase())) {
                return units;
            }
        }
        return null;
    }

    public long getToSecond() {
        return this.toSecond;
    }

    public String getName() {
        return this.name;
    }

    public String getShortcut() {
        return this.shortcut;
    }

    public static String getTime(long ends) {
        long current = System.currentTimeMillis();
        if (ends == -1L) {
            return "§4permanent";
        }

        long differenz = ends - current;

        long seconds = 0L;
        long minutes = 0L;
        long hours = 0L;
        long days = 0L;
        long weeks = 0L;

        while (differenz >= 1000L) {
            differenz -= 1000L;
            seconds++;
        }
        while (seconds >= 60L) {
            seconds -= 60L;
            minutes++;
        }
        while (minutes >= 60L) {
            minutes -= 60L;
            hours++;
        }
        while (hours >= 24) {
            hours -= 24L;
            days++;
        }
        while (days >= 7L) {
            days -= 7L;
            weeks++;
        }

        if (weeks > 0L) {
            return weeks + " Wochen " + (days > 0 ? days + " Tage" : "") + (hours > 0 ? hours + " Stunden" : "") + (minutes > 0 ? minutes + " Minuten" : "") + (seconds > 0 ? seconds + " Sekunden" : "");
        }
        if (days > 0L) {
            return days + " Tage " + (hours > 0 ? hours + " Stunden" : "") + (minutes > 0 ? minutes + " Minuten" : "") + (seconds > 0 ? seconds + " Sekunden" : "");
        }
        if (hours > 0L) {
            return hours + " Stunden " + (minutes > 0 ? minutes + " Minuten" : "") + (seconds > 0 ? seconds + " Sekunden" : "");
        }
        if (minutes > 0L) {
            return minutes + " Minuten " + (seconds > 0 ? seconds + " Sekunden" : "");
        }

        return seconds + " Sekunden";

    }

    public static String getTimeShort(long ends) {
        long current = System.currentTimeMillis();
        if (ends == -1L) {
            return "§4∞";
        }

        long differenz = ends - current;

        long seconds = 0L;
        long minutes = 0L;
        long hours = 0L;
        long days = 0L;
        long weeks = 0L;

        while (differenz >= 1000L) {
            differenz -= 1000L;
            seconds++;
        }
        while (seconds >= 60L) {
            seconds -= 60L;
            minutes++;
        }
        while (minutes >= 60L) {
            minutes -= 60L;
            hours++;
        }
        while (hours >= 24) {
            hours -= 24L;
            days++;
        }
        while (days >= 7L) {
            days -= 7L;
            weeks++;
        }

        if (weeks > 0L) {
            return weeks + " W " + (days > 0 ? days + " T" : "") + (hours > 0 ? hours + " S" : "") + (minutes > 0 ? minutes + " M" : "") + (seconds > 0 ? seconds + " S" : "");
        }
        if (days > 0L) {
            return days + " T " + (hours > 0 ? hours + " S" : "") + (minutes > 0 ? minutes + " M" : "") + (seconds > 0 ? seconds + " S" : "");
        }
        if (hours > 0L) {
            return hours + " S " + (minutes > 0 ? minutes + " M" : "") + (seconds > 0 ? seconds + " S" : "");
        }
        if (minutes > 0L) {
            return minutes + " M " + (seconds > 0 ? seconds + " S" : "");
        }

        return seconds + " S";

    }

    public static String shortInteger(int duration) {

        String string = "";

        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (duration / 60 / 60 / 24 >= 1) {
            days = duration / 60 / 60 / 24;
            duration = duration - ((duration / 60 / 60 / 24) * 60 * 60 * 24);
        }

        if (duration / 60 / 60 >= 1) {
            hours = duration / 60 / 60;
            duration = duration - ((duration / 60 / 60) * 60 * 60);
        }

        if (duration / 60 >= 1) {
            minutes = duration / 60;
            duration = duration - ((duration / 60) * 60);
        }

        if (duration >= 1) {
            seconds = duration;
        }

        if (days >= 1) {
            if (days == 1) {
                string = days + " Tag§7, §e";
            } else {
                string = days + " Tage§7, §e";
            }
        }

        if (hours <= 9) {
            string = string + "0" + hours + ":";
        } else {
            string = string + hours + ":";
        }

        if (minutes <= 9) {
            string = string + "0" + minutes + ":";
        } else {
            string = string + minutes + ":";
        }

        if (seconds <= 9) {
            string = string + "0" + seconds;
        } else {
            string = string + seconds;
        }

        return string;

    }

    public static String shortIntegerWithText(int duration) {

        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (duration / 60 / 60 / 24 >= 1) {
            days = duration / 60 / 60 / 24;
            duration = duration - ((duration / 60 / 60 / 24) * 60 * 60 * 24);
        }

        if (duration / 60 / 60 >= 1) {
            hours = duration / 60 / 60;
            duration = duration - ((duration / 60 / 60) * 60 * 60);
        }

        if (duration / 60 >= 1) {
            minutes = duration / 60;
            duration = duration - ((duration / 60) * 60);
        }

        if (duration >= 1) {
            seconds = duration;
        }


        if (days > 0L) {
            return days + (days > 1 ? " Tage" : " Tag") + (hours > 0? hours+ " Stunden" : " Stunde") +  (minutes > 0 ? minutes+ " Minuten" : " Minute") +  (seconds > 0 ? seconds+ " Sekunden" : " Sekunde");
        }
        if (hours > 0L) {
            return hours + (hours > 1 ? " Stunden" : " Stunde")+(minutes > 0 ? minutes+ " Minuten" : " Minute") + (seconds > 0 ? seconds+ " Sekunden" : " Sekunde");
        }
        if (minutes > 0L) {
            return minutes + (minutes > 1 ? " Minuten" : " Minute")+(seconds > 0 ? seconds + " Sekunden" : "");
        }

        return seconds + (seconds > 1 ? " Sekunden" : " Sekunde");


    }

}
