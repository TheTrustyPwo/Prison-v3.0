package net.evilkingdom.prison.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

public class KeepChunksLoadedListener implements Listener {
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        event.getChunk().setForceLoaded(true);
    }
}
