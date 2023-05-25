package net.evilkingdom.prison;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import net.evilkingdom.prison.commands.Commands;
import net.evilkingdom.prison.commands.PluginCommand;
import net.evilkingdom.prison.database.Mongo;
import net.evilkingdom.prison.listeners.KeepChunksLoadedListener;
import net.evilkingdom.prison.modules.users.UsersModule;
import net.evilkingdom.prison.plugin.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class Prison extends JavaPlugin {
    private static final Gson gson;
    private static Prison instance;

    static {
        gson = new GsonBuilder().create();
    }

    private Mongo mongo;
    private MongoDatabase database;
    private List<PluginModule> enabledModules;

    public static Prison getInstance() {
        return instance;
    }

    public static Gson getGson() {
        return gson;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        loadDatabase();
        this.enabledModules = new ArrayList<>();
        loadModules(
                new UsersModule()
        );

        Bukkit.getPluginManager().registerEvents(new KeepChunksLoadedListener(), this);
    }

    @Override
    public void onDisable() {
        for (final PluginModule module : this.enabledModules) {
            module.onDisable();
        }
    }

    private void loadDatabase() {
        final String connectionUri = getConfig().getString("mongo.connection_url");
        final String database = getConfig().getString("mongo.database");
        assert connectionUri != null;
        assert database != null;
        this.mongo = new Mongo();
        this.mongo.connect(connectionUri);
        this.database = this.mongo.getClient().getDatabase(database);
    }

    private void loadModules(PluginModule... modules) {
        for (PluginModule module : modules) {
            getLogger().info(String.format("Loading %s module", module.getName()));
            module.onEnable();
            for (PluginCommand command : module.getCommands()) Commands.register(command, this);
            for (Listener listener : module.getListeners()) Bukkit.getPluginManager().registerEvents(listener, this);
            this.enabledModules.add(module);
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
