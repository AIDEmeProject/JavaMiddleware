package data;

import exceptions.EmptyUnlabeledSetException;
import sampling.ReservoirSampler;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.BiFunction;

/**
 * This module is responsible for storing the data points X, the "unknown" labels y, and the collection of labeled
 * rows. It also controls data access, updating labels, and adding / removing labeled rows.
 *
 * Some observations on the behavior of adding / removing labeled rows:
 *
 *  1) If the same labeled row is added twice, the second insertion will be ignored.
 *  2) If attempting to remove a labeled row which was not actually labeled, nothing happens.
 *
 * @author luciano
 */
public class LabeledData {

    /**
     * collection of data points
     */
    private final double[][] X;

    /**
     * collection of labels
     */
    private final int[] y;

    /**
     * collection of labeled rows
     */
    private final LinkedHashSet<Integer> labeledRows;

    /**
     * @param X: features matrix
     * @param y: labels array
     * @throws IllegalArgumentException if either X.length or X[0].length is 0, or if X and y have different sizes
     */
    public LabeledData(double[][] X, int[] y) {
        this(X, y, new LinkedHashSet<>());
    }

    private LabeledData(double[][] X, int[] y, LinkedHashSet<Integer> labeledRows) {
        if (X.length != y.length){
            throw new IllegalArgumentException("X and y must have the same number of elements.");
        }

        if (X.length == 0){
            throw new IllegalArgumentException("Data must contain at least one data point.");
        }

        if (X[0].length == 0){
            throw new IllegalArgumentException("Data points have no dimension.");
        }

        validateLabels(y);

        this.X = X;
        this.y = y;
        this.labeledRows = labeledRows;
    }

    public double[][] getX() {
        return X;
    }

    public int[] getY() {
        return y;
    }

    private static void validateLabels(int[] y){
        for (int label : y) {
            if (label < 0 || label > 1) {
                throw new IllegalArgumentException("Labels must be either 0 or 1.");
            }
        }
    }

    private void validateRowIndex(int row){
        if (row < 0 || row >= getNumRows()){
            throw new IndexOutOfBoundsException("Row index " + row + " out of bounds.");
        }
    }

    /**
     * Retrieve the selected row from the data matrix X.
     * @param row: row to retrieve
     * @return data point at specified index
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     */
    public double[] getRow(int row) {
        return X[row];
    }

    /**
     * Retrieve the selected label from y.
     * @param row: row to retrieve
     * @return label at specified index
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     */
    public int getLabel(int row){
        return y[row];
    }

    /**
     * Sets a new label to the given position
     * @param row: row to retrieve
     * @param label: label to set
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     * @throws IllegalArgumentException if label is not 0 or 1
     */
    public void setLabel(int row, int label){
        if (label < 0 || label > 1){
            throw new IllegalArgumentException("Only 0 or 1 labels supported.");
        }
        y[row] = label;
    }

    /**
     * Returns the collection of labeled rows so far
     */
    public Collection<Integer> getLabeledRows() {
        return labeledRows;
    }

    /**
     * @return Number of rows in data matrix X
     */
    public int getNumRows(){
        return X.length;
    }

    /**
     * @return Number of labeled rows
     */
    public int getNumLabeledRows(){
        return labeledRows.size();
    }

    /**
     * @return Number of unlabeled rows
     */
    public int getNumUnlabeledRows(){
        return getNumRows() - getNumLabeledRows();
    }

    /**
     * @return Dimension of data points
     */
    public int getDim(){
        return X[0].length;
    }

    /**
     * @param row: index to check
     * @return whether row is in labeled set or not
     */
    public boolean isInLabeledSet(int row){
        validateRowIndex(row);
        return labeledRows.contains(row);
    }

    /**
     * Add new index to labeled rows collection. If element is already in set, nothing happens (as if value was discarded).
     * @param row: index of labeled point to add
     */
    public void addLabeledRow(int row){
        validateRowIndex(row);
        labeledRows.add(row);
    }

    /**
     * Add all rows in array to labeled rows. If any element is already in set, nothing happens (as if value was discarded).
     * @param rows: collection of indexes to add
     */
    public void addLabeledRow(int[] rows){
        for (int row : rows) {
            addLabeledRow(row);
        }
    }

    /**
     * Removes an index from the labeled row collection. If not in set, nothing happens.
     * @param row: index of labeled point to remove.
     */
    public void removeLabeledRow(int row){
        validateRowIndex(row);
        labeledRows.remove(row);
    }

    /**
     * @param scoreFunction: computes the score of data[row]
     * @return unlabeled row minimizing the score function
     */
    public int retrieveMinimizerOverUnlabeledData(BiFunction<LabeledData, Integer, Double> scoreFunction){
        if (getNumUnlabeledRows() == 0){
            throw new EmptyUnlabeledSetException();
        }

        double minScore = Double.POSITIVE_INFINITY;
        int minRow = -1;

        // TODO: maybe its better to create a single iterator over unlabeled points (index, x[index], y[index]) ?
        for(int i=0; i < getNumRows(); i++){
            if(isInLabeledSet(i)){
                continue;
            }

            double score = scoreFunction.apply(this, i);
            if(score < minScore){
                minScore = score;
                minRow = i;
            }
        }

        return minRow;
    }

    /**
     * Create a new LabeledData instance by subsampling the unlabeled set. If sample size is larger than number of unlabeled
     * points remaining, this own object is returned.
     * @param size: sample size
     * @return new LabeledData object whose unlabeled set is restricted to a sample.
     * @throws IllegalArgumentException is size not positive
     */
    public LabeledData sample(int size){
        if (size <= 0){
            throw new IllegalArgumentException("Size must be positive.");
        }

        if (size >= getNumUnlabeledRows()){
            return this;
        }

        // sample indexes
        int sampleSize = getNumLabeledRows() + size;
        double[][] sampleX = new double[sampleSize][getDim()];
        int[] sampleY = new int[sampleSize];
        LinkedHashSet<Integer> rows = new LinkedHashSet<>();

        int i = 0;
        for (int row : labeledRows){
            sampleX[i] = X[row];
            sampleY[i] = y[row];
            rows.add(i);
            i++;
        }

        int[] indexes = ReservoirSampler.sample(getNumRows(), size, this::isInLabeledSet);
        for (int row : indexes){
            sampleX[i] = X[row];
            sampleY[i] = y[row];
            i++;
        }

        return new LabeledData(sampleX, sampleY, rows);
    }
}
