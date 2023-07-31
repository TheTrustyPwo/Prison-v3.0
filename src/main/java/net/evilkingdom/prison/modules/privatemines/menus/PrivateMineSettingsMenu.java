package net.evilkingdom.prison.modules.privatemines.menus;

import net.evilkingdom.commons.utilities.text.Text;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.menu.Button;
import net.evilkingdom.prison.menu.Menu;
import net.evilkingdom.prison.menu.Menus;
import net.evilkingdom.prison.menu.buttons.ConfigButton;
import net.evilkingdom.prison.modules.privatemines.PrivateMine;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesHandler;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesModule;
import net.evilkingdom.prison.utils.Response;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class PrivateMineSettingsMenu extends Menu {
    private PrivateMine privateMine;

    @Override
    public @NotNull String getTitle() {
        return "&8&nMine Settings";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public @NotNull Set<Button> getButtons() {
        return Set.of(
            new CollectTaxButton(),
            new MineToggleButton()
        );
    }

    public void open(@NotNull final Player player) {
        this.player = Objects.requireNonNull(player, "Player cannot be null!");
        PrivateMinesHandler.getInstance().getPrivateMineAsync(player.getUniqueId())
            .whenComplete((optionalPrivateMine, throwable) -> {
                if (optionalPrivateMine.isPresent()) {
                    super.open(player);
                    this.privateMine = optionalPrivateMine.get();
                } else Response.get("no-privatemine").send(player);
            });
    }

    private final class CollectTaxButton extends ConfigButton {

        @Override
        public @NotNull ConfigurationSection getConfigSection(@NotNull Player player) {
            final YamlConfiguration config = PrivateMinesModule.getInstance().getConfig();
            final ConfigurationSection section = config.getConfigurationSection("menus.settings.collect-tax");
            assert section != null;
            return section;
        }

        @Override
        public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
            player.sendMessage("WIP");
        }

    }

    private final class MineToggleButton extends ConfigButton {

        @Override
        public @NotNull ConfigurationSection getConfigSection(@NotNull final Player player) {
            final YamlConfiguration config = PrivateMinesModule.getInstance().getConfig();
            final String configPath = privateMine.isPublic() ? "menus.settings.toggle-mine.public" : "menus.settings.toggle-mine.private";
            final ConfigurationSection section = config.getConfigurationSection(configPath);
            assert section != null;
            return section;
        }

        @Override
        public void onClick(@NotNull final Player player, @NotNull final ClickType clickType) {
            player.performCommand("privatemines toggle");
            update();
        }

    }
}
