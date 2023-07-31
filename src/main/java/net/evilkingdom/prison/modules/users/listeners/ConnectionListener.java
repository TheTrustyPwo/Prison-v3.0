package net.evilkingdom.prison.modules.users.listeners;

import net.evilkingdom.prison.modules.users.User;
import net.evilkingdom.prison.modules.users.UsersHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConnectionListener implements Listener {

    @EventHandler
    public void onPlayerPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
        UsersHandler.getInstance().getOrLoadAndCacheUserAsync(event.getUniqueId())
                .thenAcceptAsync(user -> UsersHandler.getInstance().cacheUser(user));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        if (UsersHandler.getInstance().isUserLoaded(uuid)) {
            final User user = UsersHandler.getInstance().getUser(uuid);
            UsersHandler.getInstance().saveUser(user);
        }
    }

}
