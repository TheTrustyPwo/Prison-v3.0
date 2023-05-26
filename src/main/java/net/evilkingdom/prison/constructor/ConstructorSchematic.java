package net.evilkingdom.prison.constructor;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.*;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ConstructorSchematic {

    private final JavaPlugin plugin;

    private Location center;
    private Clipboard clipboard;

    /**
     * Allows you to create a schematic for a plugin.
     *
     * @param plugin ~ The plugin the schematic is for.
     */
    public ConstructorSchematic(final JavaPlugin plugin, final Location center) {
        this.plugin = plugin;

        this.center = center;
    }

    /**
     * Allows you to retrieve the schematic's center.
     *
     * @return The schematic's center.
     */
    public Location getCenter() {
        return this.center;
    }

    /**
     * Allows you to set the schematic's center.
     *
     * @param center ~ The center to set.
     */
    public void setCenter(final Location center) {
        this.center = center;
    }

    /**
     * Allows you to retrieve the schematic's plugin.
     *
     * @return The schematic's plugin.
     */
    public JavaPlugin getPlugin() {
        return this.plugin;
    }

    /**
     * Allows you to save the schematic to a file.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param file ~ The file of the schematic will save to.
     * @return True when the task is complete or false if something goes wrong.
     */
    public CompletableFuture<Boolean> save(final File file) {
        return CompletableFuture.supplyAsync(() -> {
            if (file.exists()) {
                file.delete();
            }
            try (final ClipboardWriter writer = BuiltInClipboardFormat.FAST.getWriter(new FileOutputStream(file))) {
                writer.write(this.clipboard);
            } catch (final IOException ioException) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to load the schematic from a file.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param file ~ The file to load from.
     * @return True when the task is complete or false if something goes wrong.
     */
    public CompletableFuture<Boolean> load(final File file) {
        return CompletableFuture.supplyAsync(() -> {
            final ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
            if (clipboardFormat == null) {
                return false;
            }
            try (final ClipboardReader reader = clipboardFormat.getReader(new FileInputStream(file))) {
                final Clipboard clipboard = reader.read();
                clipboard.getRegion().setWorld(BukkitAdapter.adapt(this.center.getWorld()));
                this.clipboard = clipboard;
            } catch (final IOException ioException) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to load the schematic from a region.
     * Uses FastAsyncWorldEdit's API.
     *
     * @param region ~ The region to load from.
     * @return True when the task is complete or false if something goes wrong.
     */
    public CompletableFuture<Boolean> load(final ConstructorRegion region) {
        return CompletableFuture.supplyAsync(() -> {
            final CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.asBlockVector(region.getCornerOne()), BukkitAdapter.asBlockVector(region.getCornerTwo()));
            cuboidRegion.setWorld(BukkitAdapter.adapt(this.center.getWorld()));
            final BlockArrayClipboard clipboard = new BlockArrayClipboard(cuboidRegion);
            final ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(cuboidRegion.getWorld(), cuboidRegion, clipboard, BukkitAdapter.asBlockVector(this.center));
            forwardExtentCopy.setCopyingEntities(true);
            forwardExtentCopy.setCopyingBiomes(false);
            try {
                Operations.complete(forwardExtentCopy);
                clipboard.setOrigin(BukkitAdapter.asBlockVector(this.center));
                this.clipboard = clipboard;
            } catch (final WorldEditException worldEditException) {
                return false;
            }
            return true;
        });
    }

    /**
     * Allows you to paste the schematic.
     * Uses FastAsyncWorldEdit's API.
     *
     * @return True when the task is complete or false if something goes wrong.
     */
    public CompletableFuture<Boolean> paste() {
        return CompletableFuture.supplyAsync(() -> {
            try (final EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(this.clipboard.getRegion().getWorld()).fastMode(true).build()) {
                Operations.complete(new ClipboardHolder(this.clipboard).createPaste(editSession).to(BukkitAdapter.asBlockVector(this.center)).copyBiomes(false).ignoreAirBlocks(true).copyEntities(true).build());
            } catch (final WorldEditException worldEditException) {
                return false;
            }
            return true;
        });
    }

}
