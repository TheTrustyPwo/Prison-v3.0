package net.evilkingdom.prison.modules.users.ranks.algorithm.impl;

import net.evilkingdom.prison.modules.users.UsersModule;
import net.evilkingdom.prison.modules.users.ranks.algorithm.RankupAlgorithm;

public class ExponentialRankupAlgorithm implements RankupAlgorithm {
    private final double initial;
    private final double growth;

    public ExponentialRankupAlgorithm() {
        this.initial = UsersModule.getInstance().getConfig().getDouble("ranks.algorithm.exponential.initial-amount");
        this.growth = UsersModule.getInstance().getConfig().getDouble("ranks.algorithm.exponential.growth-factor");
    }

    /**
     * Gets the next rankup price for the specified rank
     *
     * @param rank ~ Rank: If rank is 1, it will return the price to rankup to rank 2
     * @return The next rankup price
     */
    @Override
    public long getRankupPrice(long rank) {
        return (long) Math.floor(initial * Math.pow(this.growth, rank));
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
        return (long) Math.floor(this.initial * (Math.pow(this.growth, end) - Math.pow(this.growth, start)) / (this.growth - 1));
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
        return (long) Math.floor(Math.log(((this.growth * balance) / this.initial) - (balance / this.initial)
                + Math.pow(this.growth, start)) / Math.log(this.growth));
    }
}
