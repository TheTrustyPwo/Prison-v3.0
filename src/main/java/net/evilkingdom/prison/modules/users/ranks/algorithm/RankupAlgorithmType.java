package net.evilkingdom.prison.modules.users.ranks.algorithm;

import net.evilkingdom.prison.modules.users.ranks.algorithm.impl.ExponentialRankupAlgorithm;
import net.evilkingdom.prison.modules.users.ranks.algorithm.impl.LinearRankupAlgorithm;

public enum RankupAlgorithmType {
    LINEAR(new LinearRankupAlgorithm()),
    EXPONENTIAL(new ExponentialRankupAlgorithm());

    private final RankupAlgorithm rankupAlgorithm;

    RankupAlgorithmType(final RankupAlgorithm rankupAlgorithm) {
        this.rankupAlgorithm = rankupAlgorithm;
    }

    public RankupAlgorithm get() {
        return rankupAlgorithm;
    }
}
