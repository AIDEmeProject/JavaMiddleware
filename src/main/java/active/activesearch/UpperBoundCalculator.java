package active.activesearch;

import data.LabeledDataset;

/**
 * Utility module used for pruning the tree search in the ActiveTreeSearch algorithm.
 */
interface UpperBoundCalculator {
    /**
     * Compute the internal estimators
     * @param data: labeled data
     * @param steps: number of lookahead steps remaining
     */
    void fit(LabeledDataset data, int steps);

    /**
     * Compute upper bound on maximum utility
     * @param proba: probability of the given point being a target
     * @return maximum utility upper bound
     */
    double upperBound(double proba);
}

