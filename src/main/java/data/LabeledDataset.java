package data;

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
     * Reference to the initial collection of points
     */
    private final List<DataPoint> allPoints;

    /**
     * @param points collection of data points to be used as initial unlabeled data pool
     */
    public LabeledDataset(List<DataPoint> points){
        validateDataPointCollection(points);

        labeled = new LinkedHashSet<>();
        unlabeled = new LinkedHashSet<>(points);
        allPoints = points;
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
    public List<DataPoint> getAllPoints() {
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
}