package learner;

import classifier.BoundedClassifier;
import data.LabeledData;

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
public class ActiveTreeSearch implements Learner {

    /**
     * kNN classifier  TODO: add an interface for "implements computeProbabilityUpperBound function"?
     */
    private BoundedClassifier classifier;

    /**
     * number of steps to look into the future
     */
    private int lookahead;

    /**
     * @param classifier k-Nearest-Neighbors classifier
     * @param lookahead: number of steps to look into the future at every iteration. Usually 1 and 2 work fine.
     */
    public ActiveTreeSearch(BoundedClassifier classifier, int lookahead) {
        if (lookahead < 1){
            throw new IllegalArgumentException("Lookahead must be a positive number.");
        }
        this.classifier = classifier;
        this.lookahead = lookahead;
    }

    @Override
    public void fit(LabeledData data) {
        classifier.fit(data);
    }

    @Override
    public double probability(LabeledData data, int row) {
        return classifier.probability(data, row);
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
     * Helper function for computing the upper bound on the utility. It is also a recursive function, but it only needs to
     * be computed once, and not for every point in the unlabeled set. Refer to [2] for details.
     * @param steps: remaining steps to run
     * @param maxLabeledPoints: maximum number of positive points that can be added to current labeled set
     * @return upper bound on optimal utility
     */
    private double optimalUtilityUpperBound(LabeledData data, int steps, int maxLabeledPoints){
        double pStar = classifier.computeProbabilityUpperBound(data, maxLabeledPoints);

        if (steps <= 1){
            return pStar;
        }

        double positiveUpperBound = optimalUtilityUpperBound(data, steps - 1, maxLabeledPoints + 1);
        double negativeUpperBound = optimalUtilityUpperBound(data,steps - 1, maxLabeledPoints);

        return (positiveUpperBound + 1) * pStar + negativeUpperBound * (1 - pStar);
    }

    /**
     * Core function computing the maximum l-steps utility. Refer to [1] and [2] for details.
     * @param data: labeled data
     * @param steps: number of steps to look into the future
     * @return optimal utility index and value
     */
    private UtilityResult utility(LabeledData data, int steps){
        // compute class probabilities
        classifier.fit(data);
        double[] probas = classifier.probability(data);

        // get unlabeled point of maximum probability
        double optimalUtility = Double.NEGATIVE_INFINITY;
        int optimalRow = -1;

        for (int i = 0; i < data.getNumRows(); i++) {
            if (data.isInLabeledSet(i)) {
                continue;
            }

            if (probas[i] > optimalUtility){
                optimalUtility = probas[i];
                optimalRow = i;
            }
        }

        // in 1-step case, just return point we are most certain of being positive (greedy approach)
        if (steps <= 1){
            return new UtilityResult(optimalRow, optimalUtility);
        }

        // warm starting: start at most probable point of being positive
        optimalUtility = optimalUtilityGivenPoint(data, steps, optimalRow, probas[optimalRow]);

        double u0 = optimalUtilityUpperBound(data,steps-1, 0);
        double u1 = optimalUtilityUpperBound(data,steps-1, 1);

        for (int row = 0; row < data.getNumRows(); row++) {
            // skip labeled points and those not meeting the threshold
            if (data.isInLabeledSet(row) || (u1 + 1) * probas[row] + u0 * (1 - probas[row]) <= optimalUtility){
                continue;
            }

            // compute and update optimal utility
            double util = optimalUtilityGivenPoint(data, steps, row, probas[row]);

            if (util > optimalUtility){
                optimalUtility = util;
                optimalRow = row;
            }
        }

        return new UtilityResult(optimalRow, optimalUtility);
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
            throw new RuntimeException("Entire dataset was labeled, cannot get next point!");
        }

        return utility(data, steps).index;
    }
}
