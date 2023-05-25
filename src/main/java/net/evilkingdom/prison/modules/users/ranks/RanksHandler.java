package net.evilkingdom.prison.modules.users.ranks;

import net.evilkingdom.prison.modules.users.UsersModule;
import net.evilkingdom.prison.modules.users.ranks.algorithm.RankupAlgorithm;
import net.evilkingdom.prison.modules.users.ranks.algorithm.RankupAlgorithmType;
import net.evilkingdom.prison.plugin.PluginHandler;
import net.evilkingdom.prison.plugin.PluginModule;
import net.evilkingdom.prison.utils.numbers.NumberFormatType;
import net.evilkingdom.prison.utils.numbers.Numbers;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class RanksHandler extends PluginHandler {
    private static RanksHandler instance;

    private RankupAlgorithm rankupAlgorithm;
    private Map<Long, List<String>> rankCommands;
    private Map<Long, String> rankDisplays;

    public static RanksHandler getInstance() {
        return instance;
    }

    @Override
    public @NotNull PluginModule getModule() {
        return UsersModule.getInstance();
    }

    @Override
    public void load() {
        instance = this;
        this.rankupAlgorithm = RankupAlgorithmType.valueOf(getModule().getConfig().getString("ranks.algorithm.type")).get();
        loadRankRewards();
        loadRankDisplays();
    }

    private void loadRankRewards() {
        this.rankCommands = new HashMap<>();
        final ConfigurationSection section = getModule().getConfig().getConfigurationSection("ranks.commands");
        for (final String key : section.getKeys(false)) {
            this.rankCommands.put(Long.parseLong(key), section.getStringList(key));
        }
    }

    private void loadRankDisplays() {
        this.rankDisplays = new TreeMap<>(Collections.reverseOrder());
        final ConfigurationSection section = getModule().getConfig().getConfigurationSection("ranks.displays");
        for (final String key : section.getKeys(false)) {
            this.rankDisplays.put(Long.parseLong(key), section.getString(key));
        }
    }

    public RankupAlgorithm getRankupAlgorithm() {
        return rankupAlgorithm;
    }

    public List<String> getRankCommands(final long start, final long end) {
        final List<String> allCommands = new ArrayList<>();
        for (final long key : this.rankCommands.keySet())
            if (key > start && key <= end)
                allCommands.addAll(this.rankCommands.get(key));
        return allCommands;
    }

    public List<String> getRankCommands(final long rank) {
        return this.rankCommands.getOrDefault(rank, new ArrayList<>());
    }

    public String getRankDisplay(final long rank) {
        for (final long key : this.rankDisplays.keySet())
            if (rank >= key) return this.rankDisplays.get(key)
                    .replace("%rank-raw%", String.valueOf(rank))
                    .replace("%rank-comma%", Numbers.format(rank, NumberFormatType.COMMAS))
                    .replace("%rank-letter%", Numbers.format(rank, NumberFormatType.LETTERS));
        return String.valueOf(rank);
    }
}
