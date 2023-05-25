package net.evilkingdom.prison.menu.listeners;

import net.evilkingdom.prison.menu.Menu;
import net.evilkingdom.prison.menu.Menus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

public class MenuListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Menu menu = Menus.getMenus().get(player.getUniqueId());
        if (menu == null || !menu.getButtons().containsKey(event.getSlot())) return;
        menu.getButtons().get(event.getSlot()).onClick(player, event.getClick());
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(@NotNull final InventoryCloseEvent event) {
        final Player player = (Player) event.getPlayer();
        final Menu menu = Menus.getMenus().get(player.getUniqueId());
        if (menu != null) {
            menu.close(true);
        }
    }
}
