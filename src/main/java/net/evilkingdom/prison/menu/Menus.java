package net.evilkingdom.prison.menu;

import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.menu.listeners.MenuListener;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Menus {
    private static final Map<UUID, Menu> OPENED_MENUS;

    static {
        OPENED_MENUS = new ConcurrentHashMap<>();
        Bukkit.getPluginManager().registerEvents(new MenuListener(), Prison.getInstance());
    }

    public static Map<UUID, Menu> getMenus() {
        return OPENED_MENUS;
    }
}
