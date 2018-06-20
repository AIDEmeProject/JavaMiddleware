package data;

import sampling.ReservoirSampler;
import utils.Validator;

import java.util.*;

public class LabeledDataset {
    /**
     * collection of labeled points
     */
    private final Set<LabeledPoint> labeled;

    /**
     * collection of unlabeled points
     */
    private final Set<DataPoint> unlabeled;

    /**
     * @param points collection of data points to be used as initial unlabeled data pool
     */
    public LabeledDataset(Collection<DataPoint> points){
        validateDataPointCollection(points);

        labeled = new LinkedHashSet<>();  // preserve insertion order

        unlabeled = new HashSet<>();  // insertion order is not important
        unlabeled.addAll(points);
    }

    private LabeledDataset(Set<LabeledPoint> labeled, Set<DataPoint> unlabeled) {
        this.labeled = labeled;
        this.unlabeled = unlabeled;
    }

    /**
     * Asserts a collection of data points is non-empty, and that all elements have the same dimension.
     * @param points: collection of data points
     */
    private static void validateDataPointCollection(Collection<DataPoint> points){
        Validator.assertNotEmpty(points);

        Iterator<DataPoint> it = points.iterator();

        int dim = it.next().getDim();
        it.forEachRemaining(pt -> Validator.assertEquals(dim, pt.getDim()));
    }

    /**
     * @return collection containing all points
     */
    public Collection<DataPoint> getAllPoints() {
        Collection<DataPoint> result = new HashSet<>(unlabeled);
        result.addAll(labeled);
        return result;
    }

    /**
     * @return Collection of labeled points
     */
    public Collection<LabeledPoint> getLabeledPoints() {
        return new HashSet<>(labeled);
    }

    /**
     * @return Collection of unlabeled points
     */
    public Collection<DataPoint> getUnlabeledPoints() {
        return new HashSet<>(unlabeled);
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
        boolean removed = unlabeled.remove(point);

        if (!removed){
            throw new IllegalArgumentException("Point " + point + " is not in unlabeled set.");
        }

        labeled.add(new LabeledPoint(point, label));
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
        if (points.size() != labels.length){
            throw new IllegalArgumentException("Points and labels have incompatible dimensions.");
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
        boolean removed = labeled.remove(point);

        if (!removed) {
            throw new IllegalArgumentException("Point " + point + " is not in labeled set.");
        }

        unlabeled.add(point);
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
        Validator.assertPositive(sampleSize);
        Validator.assertNotEmpty(unlabeled);

        if (sampleSize >= getNumUnlabeledPoints()) {
            return this;
        }

        // sample keys
        Collection<DataPoint> points = ReservoirSampler.sample(unlabeled, sampleSize);

        // copy sample to set
        Set<DataPoint> sample = new HashSet<>(sampleSize);
        sample.addAll(points);

        return new LabeledDataset(labeled, sample);
    }
}