package machinelearning.active.search;

import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.MaximumUtilityRanker;
import machinelearning.classifier.Learner;
import utils.Validator;

import java.util.Objects;

/**
 * Active Search is a domain of research very close to Active Learning. They differ by the quantity they try to optimize:
 * AL tries to train an accurate classifier with as few labeled points as possible, while AS optimizes to retrieving as
 * many positive points it can within a fixed budget.
 *
 * ActiveTreeSearch implements an optimized version of the Optimal Bayesian Tree Search algorithm (see [1]). In [1], a
 * Dynamic Programming algorithm is proposed for computing the expected number of positive points to be retrieved l-steps
 * in the future. Although the optimality guarantees, its complexity is O((2 * card(X)) ^ l) for retrieving the most informative
 * point, thus impractical in any reasonable scenario.
 *
 * Thus, se also implement an optimization described in [2]. The optimization consists of pruning the tree search by
 * computing relatively inexpensive bound on the expected number of positive points to be retrieved; this allows for skipping
 * points which do not meet the necessary thresholds.
 *
 * References:
 *  [1]   Garnett, R., Krishnamurthy, Y., Wang, D., Schneider, J., and Mann, R.
 *        Bayesian optimal active search on graphs
 *        In Proceedings of the Ninth Workshop on Mining and Learning with Graphs, 2011
 *
 *  [2]   Garnett, R., Krishnamurthy, Y., Xiong, X., Schneider, J.
 *        Bayesian Optimal Active Search and Surveying
 *        ICML, 2012
 */
public class ActiveTreeSearch extends ActiveLearner {
    /**
     * number of steps to look into the future
     */
    private final int lookahead;

    private final Learner learner;

    /**
     * @param learner k-Nearest-Neighbors classifier
     * @param lookahead: number of steps to look ahead at every iteration. Usually 1 and 2 work fine.
     */
    public ActiveTreeSearch(Learner learner, int lookahead) {
        Validator.assertPositive(lookahead);

        this.learner = Objects.requireNonNull(learner);
        this.lookahead = lookahead;
    }

    @Override
    protected Ranker computeRanker() {
        return new MaximumUtilityRanker(learner, labeledSet, lookahead);
    }
}
