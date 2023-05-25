package net.evilkingdom.prison.modules.users;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.plugin.PluginHandler;
import net.evilkingdom.prison.plugin.PluginModule;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class UsersHandler extends PluginHandler {
    private static final JsonWriterSettings JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
    private static UsersHandler instance;

    private final ConcurrentHashMap<UUID, User> users;
    private final MongoCollection<Document> usersCollection;

    public UsersHandler() {
        instance = this;
        this.users = new ConcurrentHashMap<>();
        this.usersCollection = Prison.getInstance().getDatabase().getCollection("users");
        this.usersCollection.createIndex(new BasicDBObject("_id", 1));
    }

    public static UsersHandler getInstance() {
        return instance;
    }

    @Override
    public @NotNull PluginModule getModule() {
        return UsersModule.getInstance();
    }

    @Override
    public void load() {
        Bukkit.getScheduler().runTaskLater(Prison.getInstance(), this::loadOnlinePlayers, 10L);
    }

    @Override
    public void unload() {
        super.unload();
        for (final User user : getUsers()) {
            saveUser(user);
        }
    }

    public void loadOnlinePlayers() {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            final User user = loadUser(player.getUniqueId());
            cacheUser(user);
        }
    }

    @NotNull
    public User loadUser(@NotNull final UUID uuid) {
        assert !Bukkit.isPrimaryThread() : "Cannot load user on primary thread";

        final Bson query = Filters.eq("_id", uuid.toString());
        final Document document = this.usersCollection.find(query).first();
        if (document == null) return new User(uuid);

        return Prison.getGson().fromJson(document.toJson(JSON_WRITER_SETTINGS), User.class);
    }

    public void saveUser(@NotNull final User user) {
        assert !Bukkit.isPrimaryThread() : "Cannot save user on primary thread";

        final Bson query = Filters.eq("_id", user.getUuid().toString());
        final Document document = Document.parse(Prison.getGson().toJson(user));
        final ReplaceOptions options = new ReplaceOptions().upsert(true);

        this.usersCollection.replaceOne(query, document, options);
    }

    @NotNull
    public User getOrLoadAndCacheUser(@NotNull final UUID uuid) {
        assert !Bukkit.isPrimaryThread() : "Cannot load user on primary thread";

        if (this.users.containsKey(uuid)) {
            return getUser(uuid);
        } else {
            final User user = loadUser(uuid);
            user.setCacheExpiry(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30L));
            this.users.put(uuid, user);
            return user;
        }
    }

    @NotNull
    public Collection<User> getUsers() {
        return this.users.values();
    }

    @NotNull
    public User getUser(@NotNull final UUID uuid) {
        if (!this.users.containsKey(uuid)) throw new IllegalStateException("User is not cached in memory!");
        return this.users.get(uuid);
    }

    public boolean isUserLoaded(@NotNull final UUID uuid) {
        return this.users.containsKey(uuid);
    }

    public void cacheUser(@NotNull final User user) {
        this.users.put(user.getUuid(), user);
    }

    @Nullable
    public User forgetUser(@NotNull final UUID uuid) {
        return this.users.remove(uuid);
    }

    public MongoCollection<Document> getUsersCollection() {
        return usersCollection;
    }
}
