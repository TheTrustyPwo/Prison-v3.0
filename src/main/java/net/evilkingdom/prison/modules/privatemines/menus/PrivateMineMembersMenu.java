package net.evilkingdom.prison.modules.privatemines.menus;

import net.evilkingdom.prison.menu.Button;
import net.evilkingdom.prison.menu.Menu;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class PrivateMineMembersMenu extends Menu {
    @Override
    public @NotNull String getTitle() {
        return "&8&nMine Members";
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public @NotNull Set<Button> getButtons() {
        return null;
    }
}
