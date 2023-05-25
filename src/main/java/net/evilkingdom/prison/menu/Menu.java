package net.evilkingdom.prison.menu;

import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public abstract class Menu {
    private Player player;
    private Inventory inventory;
    private BukkitTask updateTask;

    public abstract @NotNull String getTitle();

    public abstract int getRows();

    public abstract @NotNull Map<Integer, Button> getButtons();

    public void onOpen() {
    }

    public void onClose(final boolean manual) {
    }

    public void open(@NotNull final Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null!");
        this.inventory = Bukkit.createInventory(null, getRows() * 9, Text.colorize(getTitle()));
        player.openInventory(this.inventory);
        onOpen();
        this.updateTask = Bukkit.getScheduler().runTaskTimer(Prison.getInstance(), this::update, 0L, getRefreshRate());
        Menus.getMenus().put(player.getUniqueId(), this);
    }

    public void close(final boolean manual) {
        this.updateTask.cancel();
        this.player.closeInventory();
        Menus.getMenus().remove(this.player.getUniqueId());
        onClose(manual);
    }

    public void update() {
        final Map<Integer, Button> buttons = getButtons();
        for (int slot = 0; slot < this.inventory.getSize(); slot++) {
            if (buttons.containsKey(slot)) this.inventory.setItem(slot, buttons.get(slot).getItem(this.player));
            else this.inventory.setItem(slot, getPlaceholderItem());
        }
    }

    public @NotNull ItemStack getPlaceholderItem() {
        return new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    }

    public long getRefreshRate() {
        return 20L;
    }
}
