package active.activesearch;

import classifier.BoundedLearner;
import classifier.Classifier;
import classifier.Learner;
import data.LabeledData;
import exceptions.EmptyUnlabeledSetException;
import active.ActiveLearner;

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
     * Tree search pruner
     */
    private UpperBoundCalculator calculator;

    /**
     * @param learner k-Nearest-Neighbors classifier
     * @param lookahead: number of steps to look into the future at every iteration. Usually 1 and 2 work fine.
     */
    public ActiveTreeSearch(BoundedLearner learner, int lookahead) {
        this(learner, lookahead, new BoundedClassifierUpperBoundCalculator(learner));
    }

    public ActiveTreeSearch(Learner learner, int lookahead) {
        this(learner, lookahead, new DummyUpperBoundCalculator());
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
    public Classifier fit(LabeledData data) {
        return learner.fit(data);
    }

    /**
     * Retrieves the unlabeled point which is expected to return the largest number of positive points in average, in
     * the l-next future steps.
     *
     * @param data: labeled data object
     * @return row index of most informative unlabeled point
     */
    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        int steps = Math.min(data.getNumUnlabeledRows(), this.lookahead);

        if (steps == 0){
            throw new EmptyUnlabeledSetException();
        }

        return utility(data, steps).index;
    }

    /**
     * Core function computing the maximum l-steps utility. Refer to [1] and [2] for details.
     * @param data: labeled data
     * @param steps: number of steps to look into the future
     * @return optimal utility index and value
     */
    private UtilityResult utility(LabeledData data, int steps){
        // compute class probabilities
        double[] probas = learner.fit(data).probability(data);

        // get unlabeled point of maximum probability
        int optimalRow = data.retrieveMinimizerOverUnlabeledData((dt, row) -> -probas[row]);
        double optimalUtility = probas[optimalRow];

        // in 1-step case, just return point we are most certain of being positive (greedy approach)
        if (steps <= 1){
            return new UtilityResult(optimalRow, optimalUtility);
        }

        // warm starting: start at most probable point of being positive
        optimalUtility = optimalUtilityGivenPoint(data, steps, optimalRow, probas[optimalRow]);

        calculator.fit(data, steps);

        int count = 0;
        for (int row = 0; row < data.getNumRows(); row++) {
            // skip labeled points and those not meeting the threshold
            if (data.isInLabeledSet(row) || calculator.upperBound(probas[row]) <= optimalUtility){
                count++;
                continue;
            }

            // compute and update optimal utility
            double util = optimalUtilityGivenPoint(data, steps, row, probas[row]);

            if (util > optimalUtility){
                optimalUtility = util;
                optimalRow = row;
            }
        }
        System.out.println(count);
        return new UtilityResult(optimalRow, optimalUtility);
    }

    /**
     * Helper function for computing the expected number of labeled points to be retrieved if we start at a particular point.
     * This function if computed recursively, calling the utility() method twice. Refer to [2] for details.
     * @param data: labeled data so far
     * @param steps: number to future steps remaining
     * @param rowNumber: row number of starting point
     * @param proba: probability of X[i] being 1
     * @return Expected number of positive points to be retrieved if we start at X[rowNumber]
     */
    private double optimalUtilityGivenPoint(LabeledData data, int steps, int rowNumber, double proba){
        if (steps <= 1){
            return proba;
        }

        // store label
        int label = data.getLabel(rowNumber);

        // add rowNumber to labeled set
        data.addLabeledRow(rowNumber);

        // positive label branch
        data.setLabel(rowNumber, 1);
        double positiveUtility = utility(data, steps-1).utility;

        // negative label branch
        data.setLabel(rowNumber, 0);
        double negativeUtility = utility(data, steps-1).utility;

        // restore previous state
        data.setLabel(rowNumber, label);
        data.removeLabeledRow(rowNumber);

        return (positiveUtility + 1) * proba + negativeUtility * (1 - proba);
    }

    /**
     * Helper class used for storing the results of Utility computation: row number / index and utility value.
     */
    private class UtilityResult {
        int index;

        double utility;
        UtilityResult(int index, double utility) {
            this.index = index;
            this.utility = utility;
        }
    }
}
