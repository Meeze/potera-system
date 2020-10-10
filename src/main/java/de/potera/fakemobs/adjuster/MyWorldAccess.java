package de.potera.fakemobs.adjuster;

import de.potera.fakemobs.FakeMobsPlugin;
import de.potera.fakemobs.merchant.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class MyWorldAccess implements java.lang.reflect.InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method m, Object[] args) {
        try {
            if (m == null) return null;
            Class entityClass = Class.forName(ReflectionUtils.getNMSPackageName() + ".Entity");

            if (m.getName().equals("a") && args.length == 1 && args[0] != null && classInstance(args[0].getClass(), entityClass)) {
                this.onAddEntity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean classInstance(Class clazz, Class instance) {
        while (clazz != null) {
            if (clazz == instance) {
                return true;
            }

            clazz = clazz.getSuperclass();
        }
        return false;
    }

    public void onAddEntity() {
        FakeMobsPlugin.adjustEntityCount();
    }

    private static List getAccessList(World world) throws Exception {
        Class craftWorldClass = Class.forName(ReflectionUtils.getOBCPackageName() + ".CraftWorld");
        Class worldClass = Class.forName(ReflectionUtils.getNMSPackageName() + ".World");

        Object nmsWorld;
        {
            Method method = craftWorldClass.getDeclaredMethod("getHandle");
            method.setAccessible(true);
            nmsWorld = method.invoke(world);
        }

        List accessList;
        {
            Field field = worldClass.getDeclaredField("u");
            field.setAccessible(true);
            accessList = (List) field.get(nmsWorld);
        }

        return accessList;
    }

    public static void registerWorldAccess(World world) {
        try {
            Class iWorldAccessClass = Class.forName(ReflectionUtils.getNMSPackageName() + ".IWorldAccess");
            List accessList = getAccessList(world);

            Object myAccess = Proxy.newProxyInstance(Bukkit.class.getClassLoader(), new Class[]{iWorldAccessClass}, new MyWorldAccess());
            accessList.add(myAccess);
            FakeMobsPlugin.getInstance().getLogger().log(Level.INFO, "Setted up entity adjuster on world {0}!", world.getName());
        } catch (Exception ex) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.WARNING, "Can't register entity adjuster.", ex);
        }
    }

    public static void unregisterWorldAccess(World world) {
        try {
            Class iWorldAccessClass = Class.forName(ReflectionUtils.getNMSPackageName() + ".IWorldAccess");
            Class proxyClass = Proxy.getProxyClass(Bukkit.class.getClassLoader(), new Class[]{iWorldAccessClass});

            List accessList = getAccessList(world);

            Iterator itr = accessList.iterator();
            while (itr.hasNext()) {
                if (itr.next().getClass() == proxyClass) {
                    itr.remove();
                    FakeMobsPlugin.getInstance().getLogger().log(Level.INFO, "Removed entity adjuster from world {0}", world.getName());
                }
            }
        } catch (Exception ex) {
            FakeMobsPlugin.getInstance().getLogger().log(Level.WARNING, "Can't unregister entity adjuster.", ex);
        }
    }

}
