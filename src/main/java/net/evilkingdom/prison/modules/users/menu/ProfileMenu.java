package net.evilkingdom.prison.modules.users.menu;

import net.evilkingdom.prison.menu.Button;
import net.evilkingdom.prison.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class ProfileMenu extends Menu {
    @Override
    public @NotNull String getTitle() {
        return "&8Profile";
    }

    @Override
    public int getRows() {
        return 6;
    }

    @Override
    public @NotNull Set<Button> getButtons() {
        return Set.of(
                new PlayerButton()
        );
    }

    private static class PlayerButton extends Button {

        @Override
        public @NotNull Material getMaterial(@NotNull Player player) {
            return Material.PLAYER_HEAD;
        }

        @Override
        public @NotNull String getName(@NotNull Player player) {
            return player.getName();
        }

        @Override
        public int getSlot(@NotNull Player player) {
            return 10;
        }

        @Override
        public void onClick(@NotNull Player player, @NotNull ClickType clickType) {
            player.sendMessage("L BOZO " + clickType.name());
        }
    }
}
