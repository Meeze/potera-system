package de.potera.klysma;

import de.potera.realmeze.punishment.event.PunishListener;
import de.potera.rysefoxx.utils.AnvilGUI;
import de.potera.teamhardcore.Main;
import de.potera.teamhardcore.listener.CombatListener;
import de.potera.teamhardcore.utils.VirtualAnvil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Method;
import java.util.Set;

@AllArgsConstructor
@Getter
public class ListenerHook {

    private final Main main;

    public void startPathHock(String url, ClassLoader loader, JavaPlugin plugin) {
        try{
            ScanEnvironment.getBuilder().getUrls().clear();
            ScanEnvironment.getBuilder().addUrls(ClasspathHelper.forPackage(url, loader));
            Set<Method> methods = startPreProcessor();
            if(methods.isEmpty())
                plugin.getLogger().warning("Could not find any Listeners");
            methods.stream().map(Method::getDeclaringClass).distinct().forEach(method -> processAction(method, plugin));
        }catch (Exception e){
            plugin.getLogger().warning("Could not find any Listeners");
        }
    }

    private Set<Method> startPreProcessor() {
        Reflections reflections = new Reflections(ScanEnvironment.getBuilder());
        return reflections.getMethodsAnnotatedWith(EventHandler.class);
    }

    private void processAction(Class<?> classprocess, JavaPlugin plugin) {
        if(classprocess == VirtualAnvil.AnvilEventHandler.class || classprocess == de.potera.rysefoxx.menubuilder.manager.InventoryListener.class || classprocess == AnvilGUI.AnvilClickEventHandler.class){
            return;
        }
        try {
            Object instance = classprocess.getConstructor().newInstance();
            Bukkit.getLogger().info("Adding " + classprocess.getName() + " as Listener");
            if(instance instanceof PunishListener) {
                ((PunishListener) instance).setPunishmentController(getMain().getPunishmentController());
            }
            if(instance instanceof CombatListener) {
                ((CombatListener) instance).setCombatManager(getMain().getCombatManager());
            }
            Bukkit.getPluginManager().registerEvents((org.bukkit.event.Listener) instance, plugin);
        }catch (Exception e){
            Bukkit.getLogger().severe("Could not register Listener: " + classprocess.getName());
            e.printStackTrace();
        }
    }
}
