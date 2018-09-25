package machinelearning.classifier.neighbors;

import data.DataPoint;
import data.LabeledDataset;
import machinelearning.classifier.Classifier;
import utils.OptimumFinder;
import utils.Validator;

import java.util.Collection;


/**
 * A variant of the usual Nearest Neighbors classifier, as described in the paper "Bayesian Optimal Active Search and Surveying".
 * This classifier differs from the usual kNN on two main points:
 *
 *  1) A KD-Tree is first fitted over all data points, and not only labeled data
 *
 *  2) They add a smoothing parameter gamma for predicting class probabilities:
 *
 *      P(y = 1 | x, D) = (gamma + \sum_{y \in L-kNN(x)} y) / (1 + \sum_{y \in L-kNN(x)} 1)
 *
 *     Where L-kNN(x) is the intersection between the k-nearest neighbors of x (as computed above) and the current
 *     labeled set.
 *
 *  One limitation of this classifier is it cannot be reused with different datasets X.
 */
public class NearestNeighborsClassifier implements Classifier {

    /**
     * Probability smoothing parameter
     */
    private final double gamma;

    /**
     * sumOfLabelsInLabeledNeighborhood[i] is the sum of all labels in the L-kNN neighborhood of X[i]
     */
    private final int[] sumOfLabelsInLabeledNeighborhood;

    /**
     * labeledNeighborhoodSize[i] is the size of the L-kNN neighborhood of X[i]
     */
    private final int[] labeledNeighborhoodSize;


    public NearestNeighborsClassifier(int[] sumOfLabelsInLabeledNeighborhood, int[] labeledNeighborhoodSize, double gamma) {
        Validator.assertEqualLengths(sumOfLabelsInLabeledNeighborhood, labeledNeighborhoodSize);
        Validator.assertNotEmpty(sumOfLabelsInLabeledNeighborhood);
        Validator.assertInRange(gamma, 0, 1);

        this.sumOfLabelsInLabeledNeighborhood = sumOfLabelsInLabeledNeighborhood;
        this.labeledNeighborhoodSize = labeledNeighborhoodSize;
        this.gamma = gamma;
    }

    @Override
    public double probability(DataPoint point) {
        return futureProbabilityUpperBound(point, 0);
    }

    /**
     * Upper bound on "future probabilities". As described in the paper:
     *      p*(D, n) = \max_{x in unlabeled set} (gamma + n + \sum_{y \in L-kNN(x)} y) / (1 + n + \sum_{y \in L-kNN(x)} 1)
     *
     * @param data: labeled data
     * @param maxPositivePoints: maximum number of positive labeled points
     * @return probability upper bound
     */
    @Override
    public double computeProbabilityUpperBound(Collection<DataPoint> data, int maxPositivePoints){
        Validator.assertNonNegative(maxPositivePoints);
        return OptimumFinder.maximizer(data, pt -> futureProbabilityUpperBound(pt, maxPositivePoints)).getScore();
    }

    private double futureProbabilityUpperBound(DataPoint point, int maxPositivePoints){
        int row = point.getRow();
        return (gamma + sumOfLabelsInLabeledNeighborhood[row] + maxPositivePoints) / (1 + labeledNeighborhoodSize[row] + maxPositivePoints);
    }
}
