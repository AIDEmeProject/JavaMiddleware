package active.activesearch;

import classifier.Classifier;
import data.DataPoint;
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
    Classifier fit(LabeledDataset data, int steps);

    /**
     * Compute upper bound on maximum utility
     * @param point: data point
     * @return maximum utility upper bound
     */
    double upperBound(DataPoint point);
}

