package data;

import machinelearning.classifier.Label;
import utils.Validator;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
     * Reference to the initial collection of points
     */
    private final Collection<DataPoint> allPoints;

    /**
     * @param points collection of data points to be used as initial unlabeled data pool
     */
    public LabeledDataset(Collection<DataPoint> points){
        validateDataPointCollection(points);

        labeled = new LinkedHashSet<>();  // preserve insertion order

        unlabeled = new LinkedHashSet<>();  // insertion order is not important
        unlabeled.addAll(points);

        allPoints = points;
    }

    public LabeledDataset(LabeledDataset labeledDataset){
        this(labeledDataset.labeled, labeledDataset.unlabeled);
    }

    public LabeledDataset(Collection<LabeledPoint> labeled, Collection<DataPoint> unlabeled){
        this.labeled = new LinkedHashSet<>(labeled);
        this.unlabeled = new LinkedHashSet<>(unlabeled);
        this.allPoints = new LinkedHashSet<>(unlabeled);
        this.allPoints.addAll(labeled);
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
        return allPoints;
    }

    /**
     * @return Collection of labeled points
     */
    public Collection<LabeledPoint> getLabeledPoints() {
        return new LinkedHashSet<>(labeled);
    }

    /**
     * @return Collection of unlabeled points
     */
    public Collection<DataPoint> getUnlabeledPoints() {
        return new LinkedHashSet<>(unlabeled);
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

    public boolean hasLabeledPoints() {
        return !labeled.isEmpty();
    }

    public boolean hasUnlabeledPoints() {
        return !unlabeled.isEmpty();
    }

    /**
     * Add point to labeled points collection and remove it from unlabeled collection.
     *
     * @param point: labeled point to put
     * @throws IllegalArgumentException if point is not in unlabeled set
     * @throws IllegalArgumentException if label is different from 0 or 1
     */
    public void putOnLabeledSet(LabeledPoint point) {
        boolean removed = unlabeled.remove(point);

        if (!removed){
            throw new IllegalArgumentException("Point " + point + " is not in unlabeled set.");
        }

        labeled.add(point);
    }

    public void putOnLabeledSet(DataPoint point, Label label) {
        putOnLabeledSet(new LabeledPoint(point, label));
    }

    /**
     * Add all points in collection to labeled points set, removing then from the unlabeled set.
     *
     * @param points: collection of labeled points to put
     * @throws IllegalArgumentException if any point is not in unlabeled set
     * @throws IllegalArgumentException if points and labels have different sizes
     * @throws IllegalArgumentException if any label is invalid (i.e. different from 0 or 1)
     */
    public void putOnLabeledSet(Collection<LabeledPoint> points) {
        for (LabeledPoint point : points) {
            putOnLabeledSet(point);
        }
    }

    public void putOnLabeledSet(Collection<DataPoint> points, Label[] label) {
        Validator.assertEquals(points.size(), label.length);

        int i = 0;
        for (DataPoint point : points){
            putOnLabeledSet(point, label[i++]);
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
}