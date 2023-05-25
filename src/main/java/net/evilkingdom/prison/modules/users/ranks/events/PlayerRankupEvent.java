package net.evilkingdom.prison.modules.users.ranks.events;

import net.evilkingdom.prison.modules.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerRankupEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final User user;
    private final long previousRank;
    private final long newRank;
    private boolean isCancelled;

    public PlayerRankupEvent(@NotNull final Player player, @NotNull final User user, final long previousRank, final long newRank) {
        this.player = player;
        this.user = user;
        this.previousRank = previousRank;
        this.newRank = newRank;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public User getUser() {
        return user;
    }

    public long getPreviousRank() {
        return previousRank;
    }

    public long getNewRank() {
        return newRank;
    }

    /**
     * Gets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins
     *
     * @return true if this event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Sets the cancellation state of this event. A cancelled event will not
     * be executed in the server, but will still pass to other plugins.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
