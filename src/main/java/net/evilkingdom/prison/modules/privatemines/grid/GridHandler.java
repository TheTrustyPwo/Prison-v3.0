package net.evilkingdom.prison.modules.privatemines.grid;

import net.evilkingdom.commons.plugin.PluginHandler;
import net.evilkingdom.commons.plugin.PluginModule;
import net.evilkingdom.commons.utilities.grid.Grids;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesModule;
import org.jetbrains.annotations.NotNull;

public class GridHandler extends PluginHandler {
    private static GridHandler instance;
    private int gridCounter;

    public static GridHandler getInstance() {
        return instance;
    }

    @Override
    public @NotNull PluginModule getModule() {
        return PrivateMinesModule.getInstance();
    }

    @Override
    public void load() {
        instance = this;
        this.gridCounter = getModule().getConfig().getInt("grid-counter", 0);
    }

    public GridPoint getNextAndIncrementGrid() {
        GridPoint grid = indexToGrid(this.gridCounter++);
        getModule().getConfig().set("grid-counter", this.gridCounter);
        getModule().saveConfig();
        return grid;
    }

    public GridPoint indexToGrid(final int index) {
        final int[] grid = Grids.getPoint(index);
        return new GridPoint(grid[0], grid[1]);
    }
}
