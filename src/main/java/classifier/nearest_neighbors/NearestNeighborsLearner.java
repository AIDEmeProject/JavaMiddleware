package classifier.nearest_neighbors;

import classifier.BoundedClassifier;
import classifier.BoundedLearner;
import data.LabeledDataset;
import data.LabeledPoint;
import exceptions.EmptyLabeledSetException;
import smile.neighbor.KDTree;
import smile.neighbor.Neighbor;

import java.util.Arrays;

public class NearestNeighborsLearner implements BoundedLearner {

    /**
     * Probability smoothing parameter
     */
    private final double gamma;

    /**
     * indexes[i] contains the indexes of the k-Nearest neighbors of X[i]. No particular ordering can be assumed.
     */
    private final int[][] indexes;

    /**
     * @param X: collection of all unlabeled data points. KD-tree will be built over this collection.
     * @param k: neighborhood size
     * @throws IllegalArgumentException if gamma not in [0,1], or if k is not positive
     */
    public NearestNeighborsLearner(double[][] X, int k, double gamma) {
        if (k <= 0){
            throw new IllegalArgumentException("Number of neighbors must be positive.");
        }

        if (gamma < 0 || gamma > 1){
            throw new IllegalArgumentException("gamma must be between 0 and 1.");
        }

        this.gamma = gamma;
        this.indexes = computeNeighbors(X, k);
    }

    private int[][] computeNeighbors(double[][] X, int k){
        int [][] indexes = new int[X.length][k];

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

        return indexes;
    }

    /**
     * Fit kNN classifier over labeled data. We assume data.X is the same collection of points passed as argument in the
     * constructor.
     * @param data: collection of labeled points
     */
    @Override
    public BoundedClassifier fit(LabeledDataset data) {
        if (data.getNumLabeledRows() == 0){
            throw new EmptyLabeledSetException();
        }

        int[] sumOfLabelsInLabeledNeighborhood = new int[data.getNumRows()];
        int[] labeledNeighborhoodSize = new int[data.getNumRows()];

        for (int i = 0; i < data.getNumRows(); i++) {
            for(LabeledPoint point : data.getLabeledPoints()){
                // if labeledIndex is one of the k-closest neighbors, increment counters
                if(Arrays.binarySearch(indexes[i], point.getId()) >= 0){
                    sumOfLabelsInLabeledNeighborhood[i] += point.getLabel();
                    labeledNeighborhoodSize[i]++;
                }
            }
        }

        return new NearestNeighborsClassifier(sumOfLabelsInLabeledNeighborhood, labeledNeighborhoodSize, gamma);
    }
}
