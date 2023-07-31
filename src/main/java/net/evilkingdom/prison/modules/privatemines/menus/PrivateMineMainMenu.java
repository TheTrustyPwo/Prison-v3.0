package net.evilkingdom.prison.modules.privatemines.menus;

import net.evilkingdom.prison.menu.Button;
import net.evilkingdom.prison.menu.Menu;
import net.evilkingdom.prison.menu.buttons.ConfigButton;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesModule;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PrivateMineMainMenu extends Menu {
    @Override
    public @NotNull String getTitle() {
        return "&8&nPersonal Mine";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public @NotNull Set<Button> getButtons() {
        return Set.of(
                new TeleportButton(),
                new MembersButton(),
                new SettingsButton(),
                new PublicMinesButton()
        );
    }

    private static class TeleportButton extends ConfigButton {

        @Override
        public @NotNull ConfigurationSection getConfigSection(@NotNull Player player) {
            final YamlConfiguration config = PrivateMinesModule.getInstance().getConfig();
            final ConfigurationSection section = config.getConfigurationSection("menus.main-menu.teleport");
            assert section != null;
            return section;
        }

        @Override
        public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
            player.performCommand("privatemines go");
        }

    }

    private static class MembersButton extends ConfigButton {

        @Override
        public @NotNull ConfigurationSection getConfigSection(@NotNull Player player) {
            final YamlConfiguration config = PrivateMinesModule.getInstance().getConfig();
            final ConfigurationSection section = config.getConfigurationSection("menus.main-menu.members");
            assert section != null;
            return section;
        }

        @Override
        public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
            player.sendMessage("WIP");
        }
    }

    private static class SettingsButton extends ConfigButton {

        @Override
        public @NotNull ConfigurationSection getConfigSection(@NotNull Player player) {
            final YamlConfiguration config = PrivateMinesModule.getInstance().getConfig();
            final ConfigurationSection section = config.getConfigurationSection("menus.main-menu.settings");
            assert section != null;
            return section;
        }

        @Override
        public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
            new PrivateMineSettingsMenu().open(player);
        }
    }

    private static class PublicMinesButton extends ConfigButton {

        @Override
        public @NotNull ConfigurationSection getConfigSection(@NotNull Player player) {
            final YamlConfiguration config = PrivateMinesModule.getInstance().getConfig();
            final ConfigurationSection section = config.getConfigurationSection("menus.main-menu.public-mines");
            assert section != null;
            return section;
        }

        @Override
        public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
            player.sendMessage("WIP");
        }
    }
}
