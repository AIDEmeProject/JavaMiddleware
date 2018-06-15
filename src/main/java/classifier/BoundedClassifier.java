package classifier;

import data.LabeledDataset;

/**
 * In [1], they introduce the concept of p* function, which is used for bounding the probability of any point being a
 * target if more labeled data is provided, but only a limited number of target points. With such a bound, the Active Tree
 * Search algorithm can perform pruning optimization.
 *
 * Reference:
 *  [1]   Garnett, R., Krishnamurthy, Y., Xiong, X., Schneider, J.
 *        Bayesian Optimal Active Search and Surveying
 *        ICML, 2012
 */
public interface BoundedClassifier extends Classifier {
    /**
     * Compute an upper bound on the probability of class probability being 1, if we added as many new labeled
     * points as we want, but no more than maxPositivePoints positive points.
     * @param data: labeled data
     * @param maxPositivePoints: maximum number of positive labeled points
     * @return upper bound
     */
    double computeProbabilityUpperBound(LabeledDataset data, int maxPositivePoints);
}
