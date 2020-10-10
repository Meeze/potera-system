package de.potera.rysefoxx.worldguard;

public enum MovementWay {
    MOVE("MOVE", 0), TELEPORT("TELEPORT", 1), SPAWN("SPAWN", 2), DISCONNECT("DISCONNECT", 3);

    String string;
    int id;


    MovementWay(String string, int id) {
    }

}
