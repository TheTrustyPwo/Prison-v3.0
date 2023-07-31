package net.evilkingdom.prison.modules.privatemines.listeners;

import net.evilkingdom.commons.border.Border;
import net.evilkingdom.commons.border.enums.BorderColor;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.modules.privatemines.PrivateMine;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Optional;

public class BorderListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> renderBorder(player), 20L);
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        final Player player = playerTeleportEvent.getPlayer();
        Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> renderBorder(player), 20L);
    }

    @EventHandler
    public void onPlayerWorldChange(final PlayerChangedWorldEvent playerChangedWorldEvent) {
        final Player player = playerChangedWorldEvent.getPlayer();
        Bukkit.getScheduler().runTaskLater(Prison.getInstance(), () -> renderBorder(player), 20L);
    }

    public void renderBorder(final Player player) {
        if (player.getWorld() != PrivateMinesHandler.getInstance().getWorld()) return;
        final Optional<PrivateMine> optionalPrivateMine = PrivateMinesHandler.getInstance().getMineAt(player.getLocation());
        optionalPrivateMine.ifPresent(privateMine -> {
            final WorldBorder border = Prison.getInstance().getServer().createWorldBorder();
            border.setCenter(privateMine.getCenter());
            border.setSize(privateMine.getMaxPoint().getX() - privateMine.getMinPoint().getX() + 2.0D);
            player.setWorldBorder(border);
        });
    }

}
