package net.evilkingdom.prison.modules.privatemines.objects;

import org.bukkit.util.Vector;

import java.io.File;

public class Theme {
    private final String name;
    private int mineDepth, maxMineSize;
    private File schematicFile;
    private Vector spawnOffset, centerOffset, minPointOffset, maxPointOffset;

    public Theme(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getMineDepth() {
        return mineDepth;
    }

    public void setMineDepth(int mineDepth) {
        this.mineDepth = mineDepth;
    }

    public int getMaxMineSize() {
        return maxMineSize;
    }

    public void setMaxMineSize(int maxMineSize) {
        this.maxMineSize = maxMineSize;
    }

    public File getSchematicFile() {
        return schematicFile;
    }

    public void setSchematicFile(File schematicFile) {
        this.schematicFile = schematicFile;
    }

    public Vector getSpawnOffset() {
        return spawnOffset;
    }

    public void setSpawnOffset(Vector spawnOffset) {
        this.spawnOffset = spawnOffset;
    }

    public Vector getCenterOffset() {
        return centerOffset;
    }

    public void setCenterOffset(Vector centerOffset) {
        this.centerOffset = centerOffset;
    }

    public Vector getMinPointOffset() {
        return minPointOffset;
    }

    public void setMinPointOffset(Vector minPointOffset) {
        this.minPointOffset = minPointOffset;
    }

    public Vector getMaxPointOffset() {
        return maxPointOffset;
    }

    public void setMaxPointOffset(Vector maxPointOffset) {
        this.maxPointOffset = maxPointOffset;
    }
}
