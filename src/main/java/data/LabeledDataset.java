package data;

import exceptions.EmptyUnlabeledSetException;
import sampling.ReservoirSampler;

import java.util.*;
import java.util.function.Function;

public class LabeledDataset {
    private final Map<Integer, LabeledPoint> labeled;
    private final Map<Integer, DataPoint> unlabeled;

    public LabeledDataset(double[][] X) {
        if (X.length == 0){
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }

        labeled = new LinkedHashMap<>();  // preserve insertion order

        unlabeled = new HashMap<>();
        for (int i = 0; i < X.length; i++) {
            unlabeled.put(i, new DataPoint(i, X[i]));
        }
    }

    private LabeledDataset(Map<Integer, LabeledPoint> labeled, Map<Integer, DataPoint> unlabeled) {
        this.labeled = labeled;
        this.unlabeled = unlabeled;
    }

    public Collection<DataPoint> getAllPoints() {
        Collection<DataPoint> result = new ArrayList<>(getNumRows());
        result.addAll(unlabeled.values());
        result.addAll(labeled.values());
        return result;
    }

    /**
     * @return Collection of labeled points
     */
    public Collection<LabeledPoint> getLabeledPoints() {
        return new HashMap<>(labeled).values();
    }

    /**
     * @return Collection of unlabeled points
     */
    public Collection<DataPoint> getUnlabeledPoints() {
        return new HashMap<>(unlabeled).values();
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
        return labeled.size() + unlabeled.size();
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
     * @param point: point to add
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public void addLabeledRow(DataPoint point, int label) {
        int row = point.getId();
        DataPoint pop = unlabeled.remove(row);

        if (pop == null){
            throw new IllegalArgumentException();
        }

        labeled.put(row, new LabeledPoint(pop, label));
    }

    /**
     * Add all rows in array to labeled rows collection, and remove then from unlabeled collection. If any element is
     * already in set, nothing happens (as if value were discarded).
     *
     * @param points:   collection of points to add
     * @param labels: collection of the respective labels for each row number
     * @throws IllegalArgumentException  if arrays have incompatible sizes, or if any label if different from 0 or 1
     */
    public void addLabeledRow(Collection<DataPoint> points, int[] labels) {
        if (points.size() != labels.length) {
            throw new IllegalArgumentException("rows and labels have incompatible sizes.");
        }

        int i = 0;
        for (DataPoint point : points) {
            addLabeledRow(point, labels[i++]);
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

        // sample keys
        Collection<Integer> rows = ReservoirSampler.sample(unlabeled.keySet(), sampleSize);

        // copy (key, value) pairs to sample
        Map<Integer, DataPoint> sample = new HashMap<>(sampleSize);
        for (Integer row : rows){
            sample.put(row, unlabeled.get(row));
        }


        return new LabeledDataset(labeled, sample);
    }
}