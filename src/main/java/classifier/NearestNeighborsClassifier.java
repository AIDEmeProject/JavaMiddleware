package classifier;

import data.LabeledData;
import smile.neighbor.KDTree;
import smile.neighbor.Neighbor;

import java.util.Arrays;

public class NearestNeighborsClassifier implements Classifier {
    /**
     * A variant of the usual Nearest Neighbors classifier, as described in the Active Search paper. This classifier
     * differs on two main points:
     *
     *  1) A KD-Tree is first fitted on all unlabeled points, and not only labeled data
     *
     *  2) They add a smoothing parameter gamma for predicting class probabilities:
     *
     *      P(y = 1 | x, D) = (gamma + \sum_{y \in L-kNN(x)} y) / (1 + \sum_{y \in L-kNN(x)} 1)
     *
     *     Where L-kNN(x) is the intersection between the k-nearest neighbors of x (as computed above) and the current
     *     labeled set.
     *
     *
     *  One limitation of this classifier is it cannot be reused with different datasets X; you must create a new instance.
     *  TODO: can we remove this limitation ?
     */

    /**
     * Probability smoothing parameter
     */
    private double gamma;

    /**
     * indexes[i] contains the indexes of the k-Nearest neighbors of X[i]. No particular ordering can be assumed.
     */
    private int[][] indexes;

    /**
     * labelsSum[i] is the sum of all labels in the L-kNN neighborhood of X[i]
     */
    private int[] labelsSum;

    /**
     * labeledNeighborhoodSize[i] is the size of the L-kNN neighborhood of X[i]
     */
    private int[] labeledNeighborhoodSize;


    /**
     * @param X: collection of all unlabeled data points. KD-tree will be built over this collection.
     * @param k: neighborhood size
     * @param gamma: smoothing parameter
     * @throws IllegalArgumentException if gamma not in [0,1], or if k is not positive
     */
    public NearestNeighborsClassifier(double[][] X, int k, double gamma) {
        if (gamma < 0 || gamma > 1){
            throw new IllegalArgumentException("Gamma must be between 0 and 1.");
        }

        if (k <= 0){
            throw new IllegalArgumentException("Number of neighbors must be positive.");
        }

        this.gamma = gamma;
        this.indexes = computeNeighbors(X, k);
        this.labelsSum = new int[X.length];
        this.labeledNeighborhoodSize = new int[X.length];
    }

    private int[][] computeNeighbors(double[][] X, int k){
        int [][] indexes = new int[X.length][k];

        KDTree<double[]> tree = new KDTree<>(X, X);
        for (int i=0; i < X.length; i++) {
            // compute neighborhood of X[i]
            Neighbor<double[], double[]>[] neighbors = tree.knn(X[i], k);

            for (int j = 0; j < k; j++) {
                Neighbor<double[], double[]> neighbor = neighbors[j];
                indexes[i][j] = neighbor.index;
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
    public void fit(LabeledData data) {
        for (int i = 0; i < data.getNumRows(); i++) {
            // reset any previous values
            labelsSum[i] = 0;
            labeledNeighborhoodSize[i] = 0;

            for(int labeledIndex : data.getLabeledRows()){
                // if labeledIndex is between the k-closest neighbors, increment labelsSum and labeledNeighborhoodSize
                if(Arrays.binarySearch(indexes[i], labeledIndex) >= 0){
                    labelsSum[i] += data.getLabel(labeledIndex);
                    labeledNeighborhoodSize[i]++;
                }
            }
        }
    }

    @Override
    public double probability(LabeledData data, int row){
        return (gamma + labelsSum[row]) / (1 + labeledNeighborhoodSize[row]);
    }

    /**
     * The pStar function is an upper bound on the probability of class probability being 1, if we added as many new labeled
     * points as we want, but no more than number of positive points. This value is used in ActiveTreeSearch class.
     * @param maxPositivePoints: maximum number of positive labeled points
     * @return upper bound
     * TODO: create interface for this method?
     */
    public double pStar(LabeledData data, int maxPositivePoints){
        double maxValue = Double.NEGATIVE_INFINITY;
        double value;

        for (int i = 0; i < indexes.length; i++) {
            if (data.isInLabeledSet(i)){
                continue;
            }

            value = (gamma + labelsSum[i] + maxPositivePoints) / (1 + labeledNeighborhoodSize[i] + maxPositivePoints);

            if (value > maxValue){
                maxValue = value;
            }
        }
        return maxValue;
    }
}
