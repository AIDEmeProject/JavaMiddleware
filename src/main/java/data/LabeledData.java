package data;

import exceptions.EmptyUnlabeledSetException;
import sampling.ReservoirSampler;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * This module is responsible for storing the data points X, and the collection of labeled rows so far. It also provides
 * methods for accessing data, updating labels, and adding / removing labeled rows.
 *
 * Some observations on the behavior of adding / removing labeled rows:
 *
 *  1) If the same labeled row is added twice, an exception will be thrown.
 *  3) If attempting to remove a labeled row which was not actually labeled, an exception is also thrown.
 *
 * @author luciano
 */
public class LabeledData {

    /**
     * collection of data points
     */
    private final double[][] X;

    /**
     * collection of labeled rows
     */
    private final LinkedHashMap<Integer, Integer> labeledRows;

    /**
     * @param X: features matrix
     * @throws IllegalArgumentException if either X.length or X[0].length is 0
     */
    public LabeledData(double[][] X) {
        this(X, new LinkedHashMap<>());
    }

    private LabeledData(double[][] X, LinkedHashMap<Integer, Integer> labeledRows) {
        if (X.length == 0){
            throw new IllegalArgumentException("Data must contain at least one data point.");
        }

        if (X[0].length == 0){
            throw new IllegalArgumentException("Data points have no dimension.");
        }

        this.X = X;
        this.labeledRows = labeledRows;
    }

    private void validateRowIndex(int row){
        if (row < 0 || row >= getNumRows()){
            throw new IndexOutOfBoundsException("Row index " + row + " out of bounds.");
        }
    }

    private void validateLabeledRowIndex(int row){
        validateRowIndex(row);
        if (!labeledRows.containsKey(row)){
            throw new IllegalArgumentException("Row number " + row + " is not in labeled set.");
        }
    }

    private void validateLabel(int label){
        if (label < 0 || label > 1){
            throw new IllegalArgumentException("Only 0 or 1 labels supported.");
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
     * Retrieve the label of a given labeled row
     * @param row: labeled row of interest
     * @return label of specified row
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     * @throws IllegalArgumentException if row is not in labeled set
     */
    public int getLabel(int row){
        validateLabeledRowIndex(row);
        return labeledRows.get(row);
    }

    /**
     * Sets a new label for a given labeled row
     * @param row: labeled row of interest
     * @param label: new label to set
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     * @throws IllegalArgumentException if row not in labeled set, or if label is not 0 or 1
     */
    public void setLabel(int row, int label){
        validateLabeledRowIndex(row);
        validateLabel(label);
        labeledRows.put(row, label);
    }

    /**
     * @return The collection of labeled rows so far
     */
    public Collection<Integer> getLabeledRows() {
        return labeledRows.keySet();
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
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     */
    public boolean isInLabeledSet(int row){
        validateRowIndex(row);
        return labeledRows.containsKey(row);
    }

    /**
     * Add new index to labeled rows collection. If element is already in set, nothing happens (as if value was discarded).
     * @param row: index of labeled point to add
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     * @throws IllegalArgumentException row is already in labeled set, or label is different from 0 or 1
     */
    public void addLabeledRow(int row, int label){
        validateRowIndex(row);
        validateLabel(label);

        if (labeledRows.containsKey(row)){
            throw new IllegalArgumentException("Key already in labeled set.");
        }

        labeledRows.put(row, label);
    }

    /**
     * Add all rows in array to labeled rows. If any element is already in set, nothing happens (as if value was discarded).
     * @param rows: collection of row numbers to add
     * @param labels: collection of the respective labels for each row number
     * @throws IndexOutOfBoundsException if any row satisfies row &lt; 0 or row &gt; len(X) - 1
     * @throws IllegalArgumentException if arrays have incompatible sizes, a row is already in labeled set, or if any labels if different from 0 or 1
     */
    public void addLabeledRow(int[] rows, int[] labels){
        if (rows.length != labels.length){
            throw new IllegalArgumentException("rows and labels have incompatible sizes.");
        }

        for (int i = 0; i < rows.length; i++) {
            addLabeledRow(rows[i], labels[i]);
        }
    }

    /**
     * Removes a (row, label) pair from the labeled row collection.
     * @param row: index of labeled point to remove.
     * @throws IndexOutOfBoundsException if row &lt; 0 or row &gt; len(X) - 1
     * @throws IllegalArgumentException if row to remove is not in labeled set
     */
    public void removeLabeledRow(int row){
        validateLabeledRowIndex(row);
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
        LinkedHashMap<Integer, Integer> rows = new LinkedHashMap<>();

        int i = 0;
        for (Map.Entry<Integer, Integer> entry : labeledRows.entrySet()){
            sampleX[i] = X[entry.getKey()];
            rows.put(i, entry.getValue());
            i++;
        }

        int[] indexes = ReservoirSampler.sample(getNumRows(), size, this::isInLabeledSet);
        for (int row : indexes){
            sampleX[i] = X[row];
            i++;
        }

        return new LabeledData(sampleX, rows);
    }
}
