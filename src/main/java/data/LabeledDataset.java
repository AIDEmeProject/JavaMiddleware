package data;

import exceptions.EmptyUnlabeledSetException;
import sampling.ReservoirSampler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class LabeledDataset {
    private final int size;
    private final Map<Integer, LabeledPoint> labeled;
    private final Map<Integer, DataPoint> unlabeled;

    public LabeledDataset(double[][] X) {
        if (X.length == 0){
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }

        size = X.length;
        labeled = new HashMap<>();

        unlabeled = new HashMap<>();
        for (int i = 0; i < X.length; i++) {
            unlabeled.put(i, new DataPoint(i, X[i]));
        }
    }

    private LabeledDataset(int size, Map<Integer, LabeledPoint> labeled, Map<Integer, DataPoint> unlabeled) {
        this.size = size;
        this.labeled = labeled;
        this.unlabeled = unlabeled;
    }

    /**
     * @return Collection of labeled points
     */
    public Collection<LabeledPoint> getLabeledPoints() {
        return labeled.values();
    }

    /**
     * @return Collection of unlabeled points
     */
    public Collection<DataPoint> getUnlabeledPoints() {
        return unlabeled.values();
    }

    public DataPoint getRow(int i){
        if (unlabeled.containsKey(i)){
            return unlabeled.get(i);
        }

        if (labeled.containsKey(i)){
            return labeled.get(i);
        }

        throw new IllegalArgumentException();
    }

    /**
     * Sets a new label for a given labeled row
     *
     * @param row:   labeled row of interest
     * @param label: new label to set
     */
    public void setLabel(int row, int label) {
        LabeledPoint point = labeled.get(row);
        if (point == null){
            throw new IllegalArgumentException();
        }
        point.setLabel(label);
    }

    /**
     * @return Number of rows in data matrix X
     */
    public int getNumRows() {
        return size;
    }

    /**
     * @return Number of labeled rows
     */
    public int getNumLabeledRows() {
        return labeled.size();
    }

    /**
     * @return Number of unlabeled rows
     */
    public int getNumUnlabeledRows() {
        return unlabeled.size();
    }

    /**
     * Add specified row to labeled rows collection and remove it from unlabeled collection. If element is already in set,
     * nothing happens (as if value were discarded).
     *
     * @param row: index of labeled point to add
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public void addLabeledRow(int row, int label) {
        DataPoint point = unlabeled.remove(row);

        if (point == null){
            throw new IllegalArgumentException();
        }

        labeled.put(row, new LabeledPoint(point, label));
    }

    /**
     * Add all rows in array to labeled rows collection, and remove then from unlabeled collection. If any element is
     * already in set, nothing happens (as if value were discarded).
     *
     * @param rows:   collection of row numbers to add
     * @param labels: collection of the respective labels for each row number
     * @throws IllegalArgumentException  if arrays have incompatible sizes, or if any label if different from 0 or 1
     */
    public void addLabeledRow(int[] rows, int[] labels) {
        if (rows.length != labels.length) {
            throw new IllegalArgumentException("rows and labels have incompatible sizes.");
        }

        for (int i = 0; i < rows.length; i++) {
            addLabeledRow(rows[i], labels[i]);
        }
    }

    /**
     * Removes a (row, label) pair from the labeled row collection, putting it back on unlabeled set. If row is not in
     * labeled set, nothing happens (as if this method were never called).
     *
     * @param row: index of labeled point to remove.
     */
    public void removeLabeledRow(int row) {
        LabeledPoint point = labeled.remove(row);

        if (point == null) {
            throw new IllegalArgumentException();
        }

        unlabeled.put(row, point);
    }

    /**
     * @param scoreFunction: computes the score of data[row]
     * @return unlabeled row minimizing the score function
     */
    public int retrieveMinimizerOverUnlabeledData(Function<DataPoint, Double> scoreFunction) {
        if (getNumUnlabeledRows() == 0) {
            throw new EmptyUnlabeledSetException();
        }

        double minScore = Double.POSITIVE_INFINITY;
        int minRow = -1;

        for (Map.Entry<Integer, DataPoint> entry : unlabeled.entrySet()) {
            double score = scoreFunction.apply(entry.getValue());
            if (score < minScore) {
                minScore = score;
                minRow = entry.getKey();
            }
        }

        return minRow;
    }

    /**
     * Create a new LabeledDataset instance by subsampling the unlabeled set. If sample size is larger than number of unlabeled
     * points remaining, this own object is returned.
     *
     * @param sampleSize: sample size
     * @return new LabeledDataset object whose unlabeled set is restricted to a sample.
     * @throws IllegalArgumentException is size not positive
     */
    public LabeledDataset subsampleUnlabeledSet(int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Size must be positive.");
        }

        if (sampleSize >= getNumUnlabeledRows()) {
            return this;
        }

        Map<Integer, DataPoint> sample = new HashMap<>(sampleSize);

        int[] indexes = ReservoirSampler.sample(getNumRows(), sampleSize, labeled.keySet()::contains);
        for (int row : indexes) {
            sample.put(row, unlabeled.get(row));
        }

        return new LabeledDataset(getNumLabeledRows() + sampleSize, labeled, sample);
    }
}