package active.activesearch;

import active.ActiveLearner;
import classifier.BoundedLearner;
import classifier.Classifier;
import classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import exceptions.EmptyUnlabeledSetException;
import utils.OptimumFinder;

import java.util.Collection;

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
 * computing relatively inexpensive bound on the expected number of positive points to be retrieved; allowing for skipping
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
public class ActiveTreeSearch implements ActiveLearner {

    /**
     * Classifier training algorithm
     */
    private final Learner learner;

    /**
     * number of steps to look into the future
     */
    private final int lookahead;

    /**
     * maximum utility upper bound calculator
     */
    private UpperBoundCalculator calculator;

    /**
     * @param learner k-Nearest-Neighbors classifier
     * @param lookahead: number of steps to look ahead at every iteration. Usually 1 and 2 work fine.
     */
    public ActiveTreeSearch(BoundedLearner learner, int lookahead) {
        this(learner, lookahead, new BoundedClassifierUpperBoundCalculator(learner));
    }

    /**
     * @param learner any classifier training algorithm
     * @param lookahead: number of steps to look ahead at every iteration. Usually 1 and 2 work fine.
     */
    public ActiveTreeSearch(Learner learner, int lookahead) {
        this(learner, lookahead, new DummyUpperBoundCalculator(learner));
    }

    private ActiveTreeSearch(Learner learner, int lookahead, UpperBoundCalculator calculator) {
        if (lookahead <= 0){
            throw new IllegalArgumentException("Lookahead must be a positive number.");
        }
        this.learner = learner;
        this.lookahead = lookahead;
        this.calculator = calculator;
    }

    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        return learner.fit(labeledPoints);
    }

    /**
     * Retrieves the unlabeled point which is expected to return the largest number of positive points in average, in
     * the l-next future steps.
     *
     * @param data: labeled data object
     * @return row index of most informative unlabeled point
     */
    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        int steps = Math.min(data.getNumUnlabeledRows(), this.lookahead);

        if (steps == 0){
            throw new EmptyUnlabeledSetException();
        }

        return utility(data, steps).getOptimum();
    }

    /**
     * Core function computing the maximum l-steps utility. Refer to [1] and [2] for details.
     * @param data: labeled data
     * @param steps: number of steps to look into the future
     * @return optimal utility index and value
     */
    private OptimumFinder.OptimumResult<DataPoint> utility(LabeledDataset data, int steps){
        Classifier clf = calculator.fit(data, steps);

        return OptimumFinder.branchAndBoundMaximizer(
                data.getUnlabeledPoints(),
                pt -> optimalUtilityGivenPoint(data, steps, clf, pt),
                calculator::upperBound);
    }

    /**
     * Helper function for computing the expected number of labeled points to be retrieved if we start at a particular point.
     * This function if computed recursively, calling the utility() method twice. Refer to [2] for details.
     * @param data: labeled data so far
     * @param steps: number to future steps remaining
     * @param clf: classifier fit on data
     * @param point: point to compute the utility
     * @return Expected number of positive points to be retrieved if we start at X[rowNumber]
     */
    private double optimalUtilityGivenPoint(LabeledDataset data, int steps, Classifier clf, DataPoint point){
        double proba = clf.probability(point);

        if (steps <= 1){
            return proba;
        }

        // positive label branch
        data.addLabeledRow(point, 1);
        double positiveUtility = utility(data, steps-1).getValue();
        data.removeLabeledRow(point);

        // negative label branch
        data.addLabeledRow(point, 0);
        double negativeUtility = utility(data, steps-1).getValue();
        data.removeLabeledRow(point);

        return (positiveUtility + 1) * proba + negativeUtility * (1 - proba);
    }
}
