package net.evilkingdom.prison.modules.privatemines;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import net.evilkingdom.commons.constructor.ConstructorSchematic;
import net.evilkingdom.commons.plugin.PluginHandler;
import net.evilkingdom.commons.plugin.PluginModule;
import net.evilkingdom.commons.utilities.logger.Logger;
import net.evilkingdom.prison.Prison;
import net.evilkingdom.prison.modules.privatemines.grid.GridHandler;
import net.evilkingdom.prison.modules.privatemines.grid.GridPoint;
import net.evilkingdom.prison.modules.privatemines.objects.Theme;
import net.evilkingdom.prison.modules.privatemines.world.VoidGenerator;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PrivateMinesHandler extends PluginHandler {
    private static final JsonWriterSettings JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
    private static PrivateMinesHandler instance;

    private ConcurrentHashMap<UUID, PrivateMine> privateMines;
    private MongoCollection<Document> privateMinesCollection;
    private Map<String, Theme> themes;
    private World world;

    public static PrivateMinesHandler getInstance() {
        return instance;
    }

    @Override
    public @NotNull PluginModule getModule() {
        return PrivateMinesModule.getInstance();
    }

    @Override
    public void load() {
        instance = this;
        this.privateMines = new ConcurrentHashMap<>();
        this.privateMinesCollection = Prison.getInstance().getMongoDatabase().getCollection("privatemines");
        this.privateMinesCollection.createIndex(new BasicDBObject("_id", 1));
        this.initializeWorld();
        this.initializeThemes();
        Bukkit.getScheduler().runTaskLater(Prison.getInstance(), this::loadOnlinePlayers, 10L);
    }

    @Override
    public void unload() {
        super.unload();
        final CompletableFuture<?>[] savePrivateMinesFuture = getPrivateMines().stream()
                .map(this::savePrivateMineAsync)
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(savePrivateMinesFuture).join();
    }

    public void loadOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            final CompletableFuture<Optional<PrivateMine>> loadPrivateMineFuture = loadPrivateMineAsync(player.getUniqueId());
            loadPrivateMineFuture.thenAccept(optionalPrivateMine -> optionalPrivateMine.ifPresent(this::cachePrivateMine));
        });
    }

    public Optional<PrivateMine> loadPrivateMine(@NotNull final UUID uuid) {
        assert !Bukkit.isPrimaryThread() : "Cannot load private mine on primary thread";

        final Bson query = Filters.eq("_id", uuid.toString());
        final Document document = this.privateMinesCollection.find(query).first();
        if (document == null) return Optional.empty();

        return Optional.of(Prison.getGson().fromJson(document.toJson(JSON_WRITER_SETTINGS), PrivateMine.class));
    }

    public CompletableFuture<Optional<PrivateMine>> loadPrivateMineAsync(@NotNull final UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            final Bson query = Filters.eq("_id", uuid.toString());
            final Document document = this.privateMinesCollection.find(query).first();
            if (document == null) return Optional.empty();

            return Optional.of(Prison.getGson().fromJson(document.toJson(JSON_WRITER_SETTINGS), PrivateMine.class));
        });
    }

    public void savePrivateMine(@NotNull final PrivateMine privateMine) {
        assert !Bukkit.isPrimaryThread() : "Cannot save private mine on primary thread";

        final Bson query = Filters.eq("_id", privateMine.getOwner().toString());
        final Document document = Document.parse(Prison.getGson().toJson(privateMine));
        final ReplaceOptions options = new ReplaceOptions().upsert(true);

        this.privateMinesCollection.replaceOne(query, document, options);
    }

    public CompletableFuture<Void> savePrivateMineAsync(@NotNull final PrivateMine privateMine) {
        return CompletableFuture.runAsync(() -> {
            final Bson query = Filters.eq("_id", privateMine.getOwner().toString());
            final Document document = Document.parse(Prison.getGson().toJson(privateMine));
            final ReplaceOptions options = new ReplaceOptions().upsert(true);

            this.privateMinesCollection.replaceOne(query, document, options);
        });
    }

    public Optional<PrivateMine> getPrivateMine(@NotNull final UUID uuid) {
        assert !Bukkit.isPrimaryThread() : "Cannot load private mine on primary thread";

        if (isPrivateMineLoaded(uuid)) {
            return getCachedPrivateMine(uuid);
        } else {
            final Optional<PrivateMine> privateMine = loadPrivateMine(uuid);
            privateMine.ifPresent(value -> this.privateMines.put(uuid, value));
            return privateMine;
        }
    }

    public CompletableFuture<Optional<PrivateMine>> getPrivateMineAsync(@NotNull final UUID uuid) {
        if (isPrivateMineLoaded(uuid)) {
            return CompletableFuture.completedFuture(getCachedPrivateMine(uuid));
        } else {
            CompletableFuture<Optional<PrivateMine>> loadPrivateMineFuture = loadPrivateMineAsync(uuid);
            return loadPrivateMineFuture.thenApplyAsync(optionalPrivateMine -> {
                optionalPrivateMine.ifPresent(privateMine -> this.privateMines.put(uuid, privateMine));
                return optionalPrivateMine;
            });
        }
    }


    @NotNull
    public Collection<PrivateMine> getPrivateMines() {
        return this.privateMines.values();
    }

    public Optional<PrivateMine> getCachedPrivateMine(@NotNull final UUID uuid) {
        return Optional.ofNullable(this.privateMines.get(uuid));
    }

    public boolean isPrivateMineLoaded(@NotNull final UUID uuid) {
        return this.privateMines.containsKey(uuid);
    }

    public void cachePrivateMine(@NotNull final PrivateMine privateMine) {
        this.privateMines.put(privateMine.getOwner(), privateMine);
    }

    public Optional<PrivateMine> forgetPrivateMine(@NotNull final UUID uuid) {
        return Optional.ofNullable(this.privateMines.remove(uuid));
    }

    public MongoCollection<Document> getPrivateMinesCollection() {
        return this.privateMinesCollection;
    }

    public Optional<PrivateMine> getMineAt(final Location location) {
        return this.privateMines.values().stream()
                .filter(privateMine -> privateMine.getRegion().isWithin(location))
                .findFirst();
    }

    public CompletableFuture<Void> createPrivateMine(final UUID uuid, final String themeName) {
        if (!this.themes.containsKey(themeName)) {
            throw new IllegalArgumentException("Theme does not exist!");
        }

        if (getPrivateMine(uuid).isPresent()) {
            throw new IllegalStateException("Player already owns a mine!");
        }

        final long startTime = System.currentTimeMillis();
        final int minesDistance = getModule().getConfig().getInt("mines-distance");
        final GridPoint gridPoint = GridHandler.getInstance().getNextAndIncrementGrid();
        final Location pasteLocation = new Location(this.world, gridPoint.x() * minesDistance, 175, gridPoint.z() * minesDistance);

        final Theme theme = this.themes.get(themeName);
        final ConstructorSchematic constructorSchematic = new ConstructorSchematic(pasteLocation);
        final File schematicFile = theme.getSchematicFile();

        return constructorSchematic.load(schematicFile)
                .thenCompose(loadSuccessful -> {
                    if (!loadSuccessful) throw new IllegalStateException("Failed to load schematic.");

                    return constructorSchematic.paste().thenApply(pasteSuccessful -> {
                        if (!pasteSuccessful) throw new IllegalStateException("Failed to paste schematic.");

                        final PrivateMine privateMine = new PrivateMine(uuid);
                        privateMine.setTheme(theme);
                        privateMine.setLocation(pasteLocation);
                        cachePrivateMine(privateMine);

                        Bukkit.getScheduler().runTaskLater(getModule().getPlugin(), () -> {
                            this.world.getBlockAt(privateMine.getSpawn()).setType(Material.AIR);
                            this.world.getBlockAt(privateMine.getCenter()).setType(Material.AIR);
                        }, 20L);

                        final long endTime = System.currentTimeMillis();
                        Logger.log("PrivateMines", String.format("Created mine for %s at %s in %,dms",
                                uuid, pasteLocation, (endTime - startTime)));
                        return null;
                    });
                });
    }

    private void initializeWorld() {
        final String worldName = getModule().getConfig().getString("world-name");
        assert worldName != null;
        if (new File(worldName).exists()) {
            Logger.log(getModule().getName(), "&aWorld exists, loading it now...");
        } else {
            Logger.log(getModule().getName(), "&aWorld does not exist, creating and loading it now...");
        }

        final WorldCreator worldCreator = new WorldCreator(worldName).generator(new VoidGenerator());
        this.world = Bukkit.createWorld(worldCreator);
        Bukkit.getScheduler().runTaskLater(getModule().getPlugin(), () -> {
            this.world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            this.world.setGameRule(GameRule.KEEP_INVENTORY, true);
            this.world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
            this.world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            this.world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            this.world.setGameRule(GameRule.DO_FIRE_TICK, false);
        }, 20L);

        Logger.log(getModule().getName(), "&aWorld initialized");
    }

    private void initializeThemes() {
        this.themes = new HashMap<>();
        final ConfigurationSection section = getModule().getConfig().getConfigurationSection("themes");
        assert section != null;

        section.getKeys(false).forEach(themeName -> {
            final Theme theme = new Theme(themeName);
            theme.setMineDepth(section.getInt(themeName + ".mine-depth"));
            theme.setMaxMineSize(section.getInt(themeName + ".max-mine-size"));

            final String schematicFileName = section.getString(themeName + ".schematic");
            if (schematicFileName == null) return;
            final File schematicFile = new File(getModule().getPlugin().getDataFolder() + File.separator + "schematics", schematicFileName);
            theme.setSchematicFile(schematicFile);
            final ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);
            if (format == null) return;

            BlockVector3 spawn = null, center = null;
            try (final ClipboardReader reader = format.getReader(new FileInputStream(schematicFile))) {
                final Clipboard clipboard = reader.read();

                for (final BlockVector3 blockVector : clipboard.getRegion()) {
                    final BlockData data = BukkitAdapter.adapt(clipboard.getFullBlock(blockVector));
                    if (data.getMaterial() == Material.CHEST) {
                        spawn = blockVector.subtract(clipboard.getOrigin());
                    } else if (data.getMaterial() == Material.FURNACE) {
                        center = blockVector.subtract(clipboard.getOrigin());
                    }
                }

                if (spawn == null || center == null) return;
                theme.setSpawnOffset(BukkitAdapter.adapt(this.world, spawn).add(0.5D, 1.0D, 0.5D).toVector());
                theme.setCenterOffset(BukkitAdapter.adapt(this.world, center).toVector());
                theme.setMinPointOffset(BukkitAdapter.adapt(this.world, clipboard.getMinimumPoint().subtract(clipboard.getOrigin())).toVector());
                theme.setMaxPointOffset(BukkitAdapter.adapt(this.world, clipboard.getMaximumPoint().subtract(clipboard.getOrigin())).toVector());
                this.themes.put(themeName, theme);

                Logger.log("PrivateMines", String.format("Loaded %s theme", themeName));
            } catch (IOException ignored) {
            }
        });
    }

    public Theme getTheme(final String themeName) {
        return this.themes.get(themeName);
    }

    public World getWorld() {
        return world;
    }
}
