package net.evilkingdom.prison.plugin;

import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.commands.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public abstract class PluginModule {
    private @NotNull YamlConfiguration config = loadConfig();
    private boolean loaded;
    private List<PluginCommand> commands;
    private List<Listener> listeners;
    private List<PluginHandler> pluginHandlers;

    @NotNull
    public abstract String getName();

    @NotNull
    public abstract String getConfigName();

    public void onEnable() {
        this.commands = initializeCommands();
        this.listeners = initializeListeners();
        this.pluginHandlers = initializePluginHandlers();
        for (PluginHandler handler : this.pluginHandlers) {
            handler.load();
            handler.setLoaded(true);
        }
    }

    public void onDisable() {
        for (PluginHandler handler : this.pluginHandlers) {
            if (handler.isLoaded()) handler.unload();
            handler.setLoaded(false);
        }
    }

    public void onReload() {
        this.config = loadConfig();
    }

    @NotNull
    public List<PluginCommand> getCommands() {
        return this.commands;
    }

    @NotNull
    public List<Listener> getListeners() {
        return this.listeners;
    }

    @NotNull
    public List<PluginHandler> getPluginHandlers() {
        return this.pluginHandlers;
    }

    private File getConfigFile() {
        return new File(Prison.getInstance().getDataFolder(), getConfigName() + ".yml");
    }

    private YamlConfiguration loadConfig() {
        File configFile = getConfigFile();
        if (!configFile.exists())
            JavaPlugin.getProvidingPlugin(getClass()).saveResource(getConfigName() + ".yml", false);
        return YamlConfiguration.loadConfiguration(configFile);
    }

    public final void saveConfig() {
        try {
            this.config.save(getConfigFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    public Prison getPlugin() {
        return Prison.getInstance();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public @NotNull YamlConfiguration getConfig() {
        return config;
    }

    // Override these methods in module classes to provide the desired instances

    protected List<PluginCommand> initializeCommands() {
        return Collections.emptyList();
    }

    protected List<Listener> initializeListeners() {
        return Collections.emptyList();
    }

    protected List<PluginHandler> initializePluginHandlers() {
        return Collections.emptyList();
    }
}
