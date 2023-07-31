package net.evilkingdom.prison.modules.privatemines;

import net.evilkingdom.commons.commands.PluginCommand;
import net.evilkingdom.commons.plugin.PluginHandler;
import net.evilkingdom.commons.plugin.PluginModule;
import net.evilkingdom.prison.modules.privatemines.commands.*;
import net.evilkingdom.prison.modules.privatemines.commands.admin.PrivateMineSetSizeCommand;
import net.evilkingdom.prison.modules.privatemines.grid.GridHandler;
import net.evilkingdom.prison.modules.privatemines.listeners.BorderListener;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PrivateMinesModule extends PluginModule {
    private static PrivateMinesModule instance;

    public static PrivateMinesModule getInstance() {
        return instance;
    }

    @Override
    public @NotNull String getName() {
        return "PrivateMines";
    }

    @Override
    public @NotNull String getConfigName() {
        return "privatemines";
    }

    @Override
    public void onEnable() {
        instance = this;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onReload() {
        super.onReload();
    }

    @Override
    protected @NotNull List<PluginCommand> initializeCommands() {
        return List.of(
                new PrivateMineCommand(),
                new PrivateMineCreateCommand(),
                new PrivateMineGoCommand(),
                new PrivateMineResetCommand(),
                new PrivateMineSetSizeCommand(),
                new PrivateMineToggleCommand()
        );
    }

    @Override
    protected @NotNull List<Listener> initializeListeners() {
        return List.of(
                new BorderListener()
        );
    }

    @Override
    protected @NotNull List<PluginHandler> initializePluginHandlers() {
        return List.of(
                new GridHandler(),
                new PrivateMinesHandler()
        );
    }
}
