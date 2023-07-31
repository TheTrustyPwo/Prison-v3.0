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
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
        gsonBuilder.registerTypeAdapter(Currency.class, new CurrencyAdapter());
        gsonBuilder.registerTypeAdapter(PrivateMine.class, new PrivateMineAdapter());
        gson = gsonBuilder.create();
    }

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

        super.onEnable();
        Bukkit.getPluginManager().registerEvents(new KeepChunksLoadedListener(), this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    protected List<PluginModule> initializePluginModules() {
        return List.of(
                new UsersModule(),
                new PrivateMinesModule()
        );
    }

    public MongoDatabase getMongoDatabase() {
        final String databaseName = getConfig().getString("mongo.database-name");
        assert databaseName != null;
        return this.getDatabase().getMongoDB().getClient().getDatabase(databaseName);
    }
}
