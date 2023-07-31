package net.evilkingdom.prison.menu.buttons;

import net.evilkingdom.prison.menu.Button;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ConfigButton extends Button {
    public abstract @NotNull ConfigurationSection getConfigSection(@NotNull final Player player);

    @Override
    public @NotNull Material getMaterial(@NotNull final Player player) {
        final String materialName = getConfigSection(player).getString("material");
        assert materialName != null;
        final Material material = Material.getMaterial(materialName);
        assert material != null;
        return material;
    }

    @Override
    public @NotNull String getName(@NotNull final Player player) {
        return getConfigSection(player).getString("name", "");
    }

    @Override
    public int getSlot(@NotNull final Player player) {
        return getConfigSection(player).getInt("slot", 0);
    }

    @Override
    public boolean isEnabled(@NotNull final Player player) {
        return getConfigSection(player).getBoolean("enabled", true);
    }

    @Override
    public @NotNull List<String> getLore(@NotNull final Player player) {
        return getConfigSection(player).getStringList("lore");
    }

    @Override
    public int getAmount(@NotNull final Player player) {
        return getConfigSection(player).getInt("amount", 1);
    }

    @Override
    public boolean isGlowing(@NotNull final Player player) {
        return getConfigSection(player).getBoolean("glowing", false);
    }

    @Override
    public @Nullable ItemMeta applyMeta(@NotNull final Player player) {
        return null;
    }

    @Override
    public void onClick(@NotNull final Player player, @NotNull final ClickType clickType) {};
}
