package net.evilkingdom.prison.plugin;

import org.jetbrains.annotations.NotNull;

public abstract class PluginHandler {
    private boolean loaded;

    @NotNull
    public abstract PluginModule getModule();

    public abstract void load();

    public void unload() {
        if (!this.loaded) throw new IllegalStateException("Unable to save unloaded handlers!");
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
