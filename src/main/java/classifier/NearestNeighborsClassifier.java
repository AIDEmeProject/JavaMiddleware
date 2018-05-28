package classifier;

import data.LabeledData;
import smile.neighbor.KDTree;
import smile.neighbor.Neighbor;

import java.util.Arrays;

public class NearestNeighborsClassifier implements Classifier {
    private double gamma;
    private int[][] indexes;
    private int[] labelsSum;
    private int[] count;

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
        this.count = new int[X.length];
    }

    private int[][] computeNeighbors(double[][] X, int k){
        int [][] indexes = new int[X.length][k];

        KDTree<double[]> tree = new KDTree<>(X, X);
        for (int i=0; i < X.length; i++) {

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

    @Override
    public void fit(LabeledData data) {
        for (int i = 0; i < data.getNumRows(); i++) {
            for(int labeledIndex : data.getLabeledRows()){
                // if labeledIndex is between the k-closest neighbors, increment labelsSum and count
                if(Arrays.binarySearch(indexes[i], labeledIndex) >= 0){
                    labelsSum[i] += data.getY()[i];
                    count[i]++;
                }
            }
        }
    }

    private double probabilitySingle(int rowNumber){
        return (gamma + labelsSum[rowNumber]) / (1 + count[rowNumber]);
    }

    @Override
    public int[] predict(LabeledData data) {
        int[] labels = new int[data.getNumRows()];

        for (int i = 0; i < labels.length; i++) {
            labels[i] = probabilitySingle(i) > 0.5 ? 1 : 0;
        }

        return labels;
    }

    @Override
    public double[] probability(LabeledData data) {
        double[] probas = new double[data.getNumRows()];

        for (int i = 0; i < indexes.length; i++) {
            probas[i] = probabilitySingle(i);
        }

        return probas;
    }
}
