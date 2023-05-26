package net.evilkingdom.prison.modules.privatemines;

import net.evilkingdom.prison.modules.privatemines.world.VoidGenerator;
import net.evilkingdom.prison.plugin.PluginHandler;
import net.evilkingdom.prison.plugin.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PrivateMinesHandler extends PluginHandler {
    private static PrivateMinesHandler instance;
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
        this.initializeWorld();
    }

    private void initializeWorld() {
        final String worldName = getModule().getConfig().getString("world-name");
        assert worldName != null;
        if (new File(worldName).exists()) {
            getModule().getPlugin().log(getModule().getName(), "&aWorld exists, loading it now...");
        } else {
            getModule().getPlugin().log(getModule().getName(), "&aWorld does not exist, creating and loading it now...");
        }

        final WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.generator(new VoidGenerator());
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

        getModule().getPlugin().log(getModule().getName(), "&aWorld initialized");
    }
}
