package net.evilkingdom.prison.modules.privatemines;

import net.evilkingdom.prison.commands.PluginCommand;
import net.evilkingdom.prison.plugin.PluginHandler;
import net.evilkingdom.prison.plugin.PluginModule;
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
    public @NotNull List<PluginCommand> initializeCommands() {
        return List.of(

        );
    }

    @Override
    public @NotNull List<Listener> initializeListeners() {
        return List.of(

        );
    }

    @Override
    public @NotNull List<PluginHandler> initializePluginHandlers() {
        return List.of(
            new PrivateMinesHandler()
        );
    }
}
