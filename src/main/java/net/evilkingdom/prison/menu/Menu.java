package net.evilkingdom.prison.menu;

import net.evilkingdom.commons.utilities.text.Text;
import net.evilkingdom.prison.Prison;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Menu {
    protected Player player;
    protected Inventory inventory;
    protected BukkitTask updateTask;

    public abstract @NotNull String getTitle();

    public abstract int getRows();

    public abstract @NotNull Set<Button> getButtons();

    public Set<Button> getEnabledButtons() {
        return getButtons().stream().filter(button -> button.isEnabled(this.player)).collect(Collectors.toSet());
    }

    public void onOpen() {}

    public void onClose(final boolean manual) {
        this.updateTask.cancel();
        Menus.getMenus().remove(this.player.getUniqueId());
    }

    public void open(@NotNull final Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null!");
        this.inventory = Bukkit.createInventory(null, getRows() * 9, Text.colorize(getTitle()));
        this.updateTask = Bukkit.getScheduler().runTaskTimer(Prison.getInstance(), this::update, 0L, getRefreshRate());
        player.openInventory(this.inventory);
        Menus.getMenus().put(player.getUniqueId(), this);
        onOpen();
    }

    public void update() {
        final ItemStack[] placeholders = new ItemStack[getRows() * 9];
        Arrays.fill(placeholders, getPlaceholderItem());
        this.inventory.setContents(placeholders);
        getEnabledButtons().forEach(button -> this.inventory.setItem(button.getSlot(this.player), button.getItem(this.player)));
    }

    public Optional<Button> getButtonAt(final int slot) {
        return getEnabledButtons().stream().filter(button -> button.getSlot(this.player) == slot).findFirst();
    }

    public @NotNull ItemStack getPlaceholderItem() {
        return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    }

    public long getRefreshRate() {
        return 20L;
    }
}
