package classifier.nearest_neighbors;

import classifier.BoundedClassifier;
import data.DataPoint;
import data.LabeledDataset;


/**
 * A variant of the usual Nearest Neighbors classifier, as described in the paper "Bayesian Optimal Active Search and Surveying".
 * This classifier differs from the usual kNN on two main points:
 *
 *  1) A KD-Tree is first fitted over all unlabeled points, and not only labeled data
 *
 *  2) They add a smoothing parameter gamma for predicting class probabilities:
 *
 *      P(y = 1 | x, D) = (gamma + \sum_{y \in L-kNN(x)} y) / (1 + \sum_{y \in L-kNN(x)} 1)
 *
 *     Where L-kNN(x) is the intersection between the k-nearest neighbors of x (as computed above) and the current
 *     labeled set.
 *
 *  One limitation of this classifier is it cannot be reused with different datasets X; you must create a new instance.
 *  TODO: can we remove this limitation ?
 */
public class NearestNeighborsClassifier implements BoundedClassifier {

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
        if (sumOfLabelsInLabeledNeighborhood.length != labeledNeighborhoodSize.length){
            throw new IllegalArgumentException("Arrays have incompatible sizes.");
        }

        if (sumOfLabelsInLabeledNeighborhood.length == 0){
            throw new IllegalArgumentException("Received empty arrays as input.");
        }

        if (gamma < 0 || gamma > 1){
            throw new IllegalArgumentException("gamma must be between 0 and 1.");
        }
        this.sumOfLabelsInLabeledNeighborhood = sumOfLabelsInLabeledNeighborhood;
        this.labeledNeighborhoodSize = labeledNeighborhoodSize;
        this.gamma = gamma;
    }

    @Override
    public double probability(LabeledDataset data, int row){
        return (gamma + sumOfLabelsInLabeledNeighborhood[row]) / (1 + labeledNeighborhoodSize[row]);
    }

    @Override
    public double probability(DataPoint point) {
        int row = point.getId();
        return (gamma + sumOfLabelsInLabeledNeighborhood[row]) / (1 + labeledNeighborhoodSize[row]);
    }

    /**
     * Upper bound on "future probabilities". As described in the paper:
     *      p*(D, n) = (gamma + n + \sum_{y \in L-kNN(x)} y) / (1 + n + \sum_{y \in L-kNN(x)} 1)
     *
     * @param data: labeled data
     * @param maxPositivePoints: maximum number of positive labeled points
     * @return probability upper bound
     */
    public double computeProbabilityUpperBound(LabeledDataset data, int maxPositivePoints){
        double maxValue = Double.NEGATIVE_INFINITY;
        double value;

        for (DataPoint point : data.getUnlabeledPoints()) {
            int i = point.getId();
            value = (gamma + sumOfLabelsInLabeledNeighborhood[i] + maxPositivePoints) / (1 + labeledNeighborhoodSize[i] + maxPositivePoints);

            if (value > maxValue){
                maxValue = value;
            }
        }
        return maxValue;
    }
}
