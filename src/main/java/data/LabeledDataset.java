package data;

import exceptions.EmptyUnlabeledSetException;
import sampling.ReservoirSampler;

import java.util.*;

public class LabeledDataset {
    /**
     * collection of labeled points
     */
    private final Map<Integer, LabeledPoint> labeled;

    /**
     * collection of unlabeled points
     */
    private final Map<Integer, DataPoint> unlabeled;

    /**
     * @param X: data matrix
     * @throws IllegalArgumentException if X is empty, or points are zero-dimensional
     * TODO: throw exception if any two rows have different dimensions
     */
    public LabeledDataset(double[][] X) {
        if (X.length == 0){
            throw new IllegalArgumentException("Dataset cannot be empty.");
        }

        labeled = new LinkedHashMap<>();  // preserve insertion order

        unlabeled = new HashMap<>();  // insertion order is not important
        for (int i = 0; i < X.length; i++) {
            if (X[i].length != X[0].length){
                throw new IllegalArgumentException("Found rows of different lengths: expected " + X[0].length + ", obtained " + X[i].length);
            }
            unlabeled.put(i, new DataPoint(i, X[i]));
        }
    }

    private LabeledDataset(Map<Integer, LabeledPoint> labeled, Map<Integer, DataPoint> unlabeled) {
        this.labeled = labeled;
        this.unlabeled = unlabeled;
    }

    /**
     * @return collection containing all points
     */
    public Collection<DataPoint> getAllPoints() {
        Collection<DataPoint> result = new ArrayList<>(getNumPoints());
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

    /**
     * @return total number of points
     */
    public int getNumPoints() {
        return labeled.size() + unlabeled.size();
    }

    /**
     * @return number of labeled points
     */
    public int getNumLabeledPoints() {
        return labeled.size();
    }

    /**
     * @return number of unlabeled points
     */
    public int getNumUnlabeledPoints() {
        return unlabeled.size();
    }

    /**
     * Add point to labeled points collection and remove it from unlabeled collection.
     *
     * @param point: point to add
     * @throws IllegalArgumentException if point is not in unlabeled set
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public void putOnLabeledSet(DataPoint point, int label) {
        int row = point.getId();
        DataPoint removed = unlabeled.remove(row);

        if (removed == null){
            throw new IllegalArgumentException("Point " + point + " is not in unlabeled set.");
        }

        labeled.put(row, new LabeledPoint(removed, label));
    }

    /**
     * Add all points in collection to labeled points set, removing then from the unlabeled set.
     *
     * @param points: collection of points to add
     * @param labels: collection of the respective labels for each row number
     * @throws IllegalArgumentException if any point is not in unlabeled set
     * @throws IllegalArgumentException if points and labels have different sizes
     * @throws IllegalArgumentException if any label is invalid (i.e. different from 0 or 1)
     */
    public void putOnLabeledSet(Collection<DataPoint> points, int[] labels) {
        if (points.size() != labels.length) {
            throw new IllegalArgumentException("Points and labels have incompatible sizes.");
        }

        int i = 0;
        for (DataPoint point : points) {
            putOnLabeledSet(point, labels[i++]);
        }
    }

    /**
     * Removes a data point labeled points collection, putting it back on the unlabeled points set.
     *
     * @param point: index of labeled point to remove.
     * @throws IllegalArgumentException if point is not in labeled set
     */
    public void removeFromLabeledSet(DataPoint point) {
        LabeledPoint removed = labeled.remove(point.getId());

        if (removed == null) {
            throw new IllegalArgumentException("Point " + point + " is not in labeled set.");
        }

        unlabeled.put(point.getId(), removed);
    }

    /**
     * Create a new LabeledDataset instance by subsampling the unlabeled set. If sample size is larger than number of unlabeled
     * points remaining, this own object is returned.
     *
     * @param sampleSize: sample size
     * @return new LabeledDataset object whose unlabeled set is restricted to a sample.
     * @throws IllegalArgumentException is size not positive
     * @throws EmptyUnlabeledSetException if unlabeled set is empty
     */
    public LabeledDataset subsampleUnlabeledSet(int sampleSize) {
        if (sampleSize <= 0) {
            throw new IllegalArgumentException("Size must be positive.");
        }

        if (unlabeled.isEmpty()){
            throw new EmptyUnlabeledSetException();
        }

        if (sampleSize >= getNumUnlabeledPoints()) {
            return this;
        }

        // sample keys
        Collection<Integer> points = ReservoirSampler.sample(unlabeled.keySet(), sampleSize);

        // copy (key, value) pairs to sample
        Map<Integer, DataPoint> sample = new HashMap<>(sampleSize);
        for (Integer row : points){
            sample.put(row, unlabeled.get(row));
        }

        return new LabeledDataset(labeled, sample);
    }
}