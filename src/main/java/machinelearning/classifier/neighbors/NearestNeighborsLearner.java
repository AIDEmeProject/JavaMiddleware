package machinelearning.classifier.neighbors;

import data.DataPoint;
import data.LabeledPoint;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Learner;
import smile.neighbor.KDTree;
import smile.neighbor.Neighbor;
import utils.Validator;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class is responsible for training a Nearest Neighbors classifier, as described in the paper "Bayesian Optimal
 * Active Search and Surveying". The trained classifier differs from the usual kNN on two main points:
 *
 *  1) A KD-Tree is first fitted over all data points, and not only labeled data
 *
 *  2) They add a smoothing parameter gamma for predicting class probabilities:
 *
 *     \( P(y = 1 | x, D) = (gamma + \sum_{y \in L-kNN(x)} y) / (1 + \sum_{y \in L-kNN(x)} 1) \)
 *
 *     Where L-kNN(x) is the intersection between the k-nearest neighbors of x (as computed above) and the current
 *     labeled set.
 *
 *  One limitation of this class is each learner is constrained to be used with a single dataset.
 */
//  TODO: can we remove this limitation ?
public class NearestNeighborsLearner implements Learner {

    /**
     * Neighborhood size
     */
    private final int k;

    /**
     * Probability smoothing parameter
     */
    private final double gamma;

    /**
     * indexes[i] contains the indexes of the k-Nearest neighbors of X[i]. No particular ordering can be assumed.
     */
    private int[][] indexes;

    /**
     * @param k: neighborhood size
     * @param gamma: probability smoothing parameter
     * @throws IllegalArgumentException if gamma not in [0,1], or if k is not positive
     */
    public NearestNeighborsLearner(int k, double gamma) {
        Validator.assertPositive(k);
        Validator.assertInRange(gamma, 0, 1);

        this.gamma = gamma;
        this.k = k;
    }

    /**
     * Computes a KD-tree over a collection of data points (for easily finding the k-nearest neighbors)
     * @param points: data points
     */
    public void initialize(Collection<DataPoint> points) {
        // convert do double[][] matrix
        double[][] X = new double[points.size()][];
        for (DataPoint point : points){
            X[point.getRow()] = point.getData();
        }

        indexes = new int[X.length][k];

        KDTree<double[]> tree = new KDTree<>(X, X);

        for (int i=0; i < X.length; i++) {
            // compute neighborhood of X[i]
            Neighbor<double[], double[]>[] neighbors = tree.knn(X[i], k);

            for (int j = 0; j < k; j++) {
                indexes[i][j] = neighbors[j].index;
            }

            // sort indexes so we can perform binary search when looking for labeled neighbors
            Arrays.sort(indexes[i]);
        }
    }

    int[][] getIndexes() {
        return indexes;
    }

    /**
     * Fit kNN classifier over labeled data. We assume X is the same collection of points passed as argument in the
     * constructor.
     * @param labeledPoints: collection of labeled points
     * @return KNN classifier
     * @throws IllegalArgumentException if labeledPoints is empty or initialize() was not called beforehand.
     */
    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        Validator.assertNotEmpty(labeledPoints);
        Validator.assertNotNull(indexes);

        int size = indexes.length;
        int[] sumOfLabelsInLabeledNeighborhood = new int[size];
        int[] labeledNeighborhoodSize = new int[size];

        for (int i = 0; i < size; i++) {
            for(LabeledPoint point : labeledPoints){
                // if labeledIndex is one of the k-closest neighbors, increment counters
                if(Arrays.binarySearch(indexes[i], point.getRow()) >= 0){
                    sumOfLabelsInLabeledNeighborhood[i] += point.getLabel().asBinary();
                    labeledNeighborhoodSize[i]++;
                }
            }
        }

        return new NearestNeighborsClassifier(sumOfLabelsInLabeledNeighborhood, labeledNeighborhoodSize, gamma);
    }
}
