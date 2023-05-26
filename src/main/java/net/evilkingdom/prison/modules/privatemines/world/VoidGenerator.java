package net.evilkingdom.prison.modules.privatemines.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class VoidGenerator extends ChunkGenerator {
    @NotNull
    public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int int1, int int2, @NotNull ChunkGenerator.BiomeGrid biomeGrid) {
        return createChunkData(world);
    }
}
