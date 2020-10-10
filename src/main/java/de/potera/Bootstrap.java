package de.potera;

import de.potera.klysma.ListenerHook;
import de.potera.teamhardcore.Main;

public class Bootstrap {


    public Bootstrap(Main plugin) {
        ListenerHook listenerHook = new ListenerHook(plugin);
        listenerHook.startPathHock("de.potera", Bootstrap.class.getClassLoader(), plugin);
    }
}
