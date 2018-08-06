package active.search;

import classifier.Classifier;
import data.LabeledDataset;

/**
 * Tree Search optimization method described in [1]. The optimization consists of pruning the tree search by
 * computing relatively inexpensive bound on the expected number of positive points to be retrieved.
 *
 * References:
 *  [1]   Garnett, R., Krishnamurthy, Y., Xiong, X., Schneider, J.
 *        Bayesian Optimal Active Search and Surveying
 *        ICML, 2012
 */
class UtilityUpperBoundCalculator {
    private double u0, u1;

    public void fit(LabeledDataset data, Classifier classifier, int steps){
        u0 = optimalUtilityUpperBound(data, classifier, steps-1, 0);
        u1 = optimalUtilityUpperBound(data, classifier,  steps-1, 1);
    }

    /**
     * Helper function for computing the upper bound on the utility. It is also a recursive function, but it only needs to
     * be computed once, and not for every point in the unlabeled set. Refer to [1] for details.
     * @param steps: remaining steps to run
     * @param maxLabeledPoints: maximum number of positive points that can be added to current labeled set
     * @return upper bound on optimal utility
     */
    private double optimalUtilityUpperBound(LabeledDataset data, Classifier classifier, int steps, int maxLabeledPoints){
        double pStar = classifier.computeProbabilityUpperBound(data, maxLabeledPoints);

        if (steps <= 1 || pStar == Double.POSITIVE_INFINITY){
            return pStar;
        }

        double positiveUpperBound = optimalUtilityUpperBound(data, classifier,steps - 1, maxLabeledPoints+1);
        double negativeUpperBound = optimalUtilityUpperBound(data, classifier,steps - 1, maxLabeledPoints);

        return (positiveUpperBound + 1) * pStar + negativeUpperBound * (1 - pStar);
    }

    public double upperBound(double proba){
        return (u1 + 1) * proba + u0 * (1 - proba);
    }
}
