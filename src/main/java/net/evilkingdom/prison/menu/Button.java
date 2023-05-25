package net.evilkingdom.prison.menu;

import net.evilkingdom.prison.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class Button {
    public abstract @NotNull Material getMaterial(@NotNull final Player player);

    public abstract @NotNull String getName(@NotNull final Player player);

    public @NotNull List<String> getLore(@NotNull final Player player) {
        return List.of();
    }

    public int getAmount(@NotNull final Player player) {
        return 1;
    }

    public boolean isGlowing(@NotNull final Player player) {
        return false;
    }

    public @Nullable ItemMeta applyMeta(@NotNull final Player player) {
        return null;
    }

    public abstract void onClick(@NotNull Player player, @NotNull ClickType clickType);

    public @NotNull ItemStack getItem(@NotNull final Player player) {
        return ItemBuilder.of(getMaterial(player)).name(getName(player)).lore(getLore(player)).amount(getAmount(player)).glow(isGlowing(player))
                .transform(itemStack -> {
                    ItemMeta itemMeta = applyMeta(player);
                    if (itemMeta != null) itemStack.setItemMeta(itemMeta);
                }).build();
    }
}
