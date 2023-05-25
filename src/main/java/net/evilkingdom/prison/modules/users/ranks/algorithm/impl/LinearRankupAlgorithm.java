package net.evilkingdom.prison.modules.users.ranks.algorithm.impl;

import net.evilkingdom.prison.modules.users.UsersModule;
import net.evilkingdom.prison.modules.users.ranks.algorithm.RankupAlgorithm;

public class LinearRankupAlgorithm implements RankupAlgorithm {
    private final double increment;
    private final double base;

    public LinearRankupAlgorithm() {
        this.increment = UsersModule.getInstance().getConfig().getDouble("ranks.algorithm.linear.increase-per-rank");
        this.base = UsersModule.getInstance().getConfig().getDouble("ranks.algorithm.linear.base-cost");
    }

    /**
     * Gets the next rankup price for the specified rank
     *
     * @param rank ~ Rank: If rank is 1, it will return the price to rankup to rank 2
     * @return The next rankup price
     */
    @Override
    public long getRankupPrice(long rank) {
        return (long) Math.floor(increment * rank + base);
    }

    /**
     * Gets the total rankup price for a range of ranks
     *
     * @param start ~ Start of the range of the rank
     * @param end   ~ End of the range of the rank
     * @return Total rankup price for specified range
     */
    @Override
    public long getTotalRankupPrice(long start, long end) {
        return ((getRankupPrice(start) + getRankupPrice(end - 1)) / 2) * (end - start);
    }

    /**
     * Gets the maximum number of rankups from the player's balance
     *
     * @param start   ~ The player's current rank
     * @param balance ~ The player's balance
     * @return The maximum number of rankups
     */
    @Override
    public long getMaxRankups(long start, long balance) {
        return (long) Math.floor(((-2 * getRankupPrice(start) + this.increment +
                Math.sqrt(Math.pow((2 * getRankupPrice(start) - this.increment), 2) + 8 * this.increment * balance)) / (2 * this.increment)));
    }
}
