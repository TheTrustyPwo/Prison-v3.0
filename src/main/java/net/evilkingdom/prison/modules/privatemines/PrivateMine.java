package net.evilkingdom.prison.modules.privatemines;

import net.evilkingdom.commons.constructor.ConstructorRegion;
import net.evilkingdom.commons.cooldown.Cooldown;
import net.evilkingdom.prison.modules.privatemines.objects.Theme;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class PrivateMine {

    private final UUID owner;
    private Theme theme;
    private List<Cooldown> cooldowns;
    private Set<UUID> banned, whitelisted;
    private Location location;
    private int size;
    private boolean isPublic;
    private double tax;

    /**
     * Allows you to create initialize a Private Mine.
     *
     * @param owner ~ The UUID of the owner of the mine.
     */
    public PrivateMine(final UUID owner) {
        this.owner = owner;
        this.theme = null;
        this.size = 61;
        this.isPublic = false;
        this.tax = 0.0;
        this.location = new Location(PrivateMinesHandler.getInstance().getWorld(), 0, 175, 0);
        this.banned = new HashSet<>();
        this.whitelisted = new HashSet<>();
        this.cooldowns = new ArrayList<>();
    }

    public CompletableFuture<Boolean> reset() {
        final int radius = (this.size - 1) / 2;
        return getMineRegion().fill(Material.STONE);
    }

    public CompletableFuture<Boolean> updateSize(final int size) {
        if (size < 3 || size > this.theme.getMaxMineSize() || size % 2 == 0) return CompletableFuture.completedFuture(false);
        setSize(size);
        final int radius = (this.theme.getMaxMineSize() + 1) / 2;
        final ConstructorRegion topLayer = new ConstructorRegion(getCenter().add(radius, 0, radius),
                getCenter().subtract(radius, 0, radius));
        final ConstructorRegion cuboid = new ConstructorRegion(getCenter().add(radius, -1, radius),
                getCenter().subtract(radius, this.theme.getMineDepth(), radius));
        return topLayer.fill(Material.SMOOTH_STONE).thenCompose(fillTopLayerSuccessful -> cuboid.fill(Material.BEDROCK)
                .thenCompose(fillCuboidSuccessful -> reset().thenApply(resetSuccessful ->
                        resetSuccessful && fillCuboidSuccessful && fillTopLayerSuccessful)));
    }

    public void whitelistPlayer(final UUID uuid) {
        this.whitelisted.add(uuid);
    }

    public void unwhitelistPlayer(final UUID uuid) {
        this.whitelisted.remove(uuid);
    }

    public boolean isWhitelisted(final UUID uuid) {
        return this.whitelisted.contains(uuid);
    }

    public ConstructorRegion getRegion() {
        return new ConstructorRegion(getMinPoint(), getMaxPoint());
    }

    public ConstructorRegion getMineRegion() {
        return new ConstructorRegion(getMinMineCorner(), getMaxMineCorner());
    }

    public Location getMinMineCorner() {
        final int radius = (this.size - 1) / 2;
        return getCenter().subtract(radius, this.theme.getMineDepth(), radius);
    }

    public Location getMaxMineCorner() {
        final int radius = (this.size - 1) / 2;
        return getCenter().add(radius, 0, radius);
    }

    public UUID getOwner() {
        return owner;
    }

    public List<Cooldown> getCooldowns() {
        return cooldowns;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Set<UUID> getBanned() {
        return banned;
    }

    public void setBanned(Set<UUID> banned) {
        this.banned = banned;
    }

    public Set<UUID> getWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(Set<UUID> whitelisted) {
        this.whitelisted = whitelisted;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getCenter() {
        return this.location.clone().add(this.theme.getCenterOffset());
    }

    public Location getSpawn() {
        return this.location.clone().add(this.theme.getSpawnOffset());
    }

    public Location getMinPoint() {
        return this.location.clone().add(this.theme.getMinPointOffset());
    }

    public Location getMaxPoint() {
        return this.location.clone().add(this.theme.getMaxPointOffset());
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
