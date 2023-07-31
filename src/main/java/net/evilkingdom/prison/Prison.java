package net.evilkingdom.prison;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoDatabase;
import net.evilkingdom.commons.plugin.PluginFramework;
import net.evilkingdom.commons.plugin.PluginModule;
import net.evilkingdom.prison.internal.serializers.LocationAdapter;
import net.evilkingdom.prison.listeners.KeepChunksLoadedListener;
import net.evilkingdom.prison.modules.privatemines.PrivateMine;
import net.evilkingdom.prison.modules.privatemines.PrivateMinesModule;
import net.evilkingdom.prison.modules.privatemines.serializers.PrivateMineAdapter;
import net.evilkingdom.prison.modules.users.UsersModule;
import net.evilkingdom.prison.modules.users.currency.serializers.CurrencyAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Currency;
import java.util.List;

public final class Prison extends PluginFramework {
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
                new UsersModule(),
                new PrivateMinesModule()
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

    public void log(final String... messages) {
        final StringBuilder stringBuilder = new StringBuilder("&6[Prison");
        for (int i = 0; i < messages.length - 1; i++) {
            stringBuilder.append(" » ").append("&6").append(messages[i]);
        }
        stringBuilder.append("]&r ").append(messages[messages.length - 1]);
        Bukkit.getConsoleSender().sendMessage(Text.colorize(stringBuilder.toString()));
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
