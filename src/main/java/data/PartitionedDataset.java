package data;

import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.ExtendedClassifierStub;
import machinelearning.threesetmetric.ExtendedLabel;

import java.util.*;

/**
 * This modules maintains a partition of the data points into three sets:
 *
 *   - MOST INFORMATIVE partition: this partition will contain all the points requested for labeling by the Active Learning
 *   algorithm. These points also have their true labels provided, either by the user or by a data model.
 *
 *   - INFERRED LABELS partition: a model of the data can be provided, which we assume is capable of predicting the correct
 *   user labels from given labeled data. Points which have not been asked for labeling by the Active Learning algorithm,
 *   but whose true label could be correctly inferred by the data model, will be put in this partition.
 *
 *   - UNKNOWN LABELS partition: all the remaining points, for which the correct label is currently unknown, are put in this partition.
 */
public final class PartitionedDataset {
    /**
     * List of all data points
     */
    private final List<DataPoint> points;

    /**
     * A extended classifier for inferring labels
     */
    private final ExtendedClassifier classifier;

    /**
     * Map of data point's index to its row position in points
     */
    private final Map<Long, Integer> indexToPosition;

    /**
     * The current known / inferred label for each data point
     */
    private final ExtendedLabel[] labels;

    /**
     * Starting position of INFERRED LABELS partition
     */
    private int inferredStart;

    /**
     * Starting position of UNKNOWN partition
     */
    private int unknownStart;

    /**
     * Create an initial partition data structure, with all points put in the UNKNOWN partition.
     * @param points: data points to build partitions. A copy will be stored internally to avoid unintended changes to input list.
     * @param classifier: {@link ExtendedClassifier} used to build the inferred labels partition
     */
    public PartitionedDataset(List<DataPoint> points, ExtendedClassifier classifier) {
        this.points = new ArrayList<>(points);
        this.classifier = Objects.requireNonNull(classifier);

        this.inferredStart = 0;
        this.unknownStart = 0;

        this.labels = new ExtendedLabel[this.points.size()];
        Arrays.fill(this.labels, ExtendedLabel.UNKNOWN);

        this.indexToPosition = new HashMap<>(this.points.size());
        initializeIndexes();
    }

    /**
     * Through this constructor, no label inference will be done (i.e. the INFERRED LABELS partition is always empty)
     * @param points: data points to build partitions. A copy will be stored internally to avoid unintended changes to input list.
     */
    public PartitionedDataset(List<DataPoint> points) {
        this(points, new ExtendedClassifierStub());
    }

    private void initializeIndexes() {
        int i = 0;
        for (DataPoint point : this.points) {
            this.indexToPosition.put(point.getId(), i++);
        }
    }

    /**
     * @return the entire list of data points. The order of data points MAY CHANGE after every update() call.
     */
    public List<DataPoint> getAllPoints() {
        return points;
    }

    /**
     * @return all the data points in the MOST INFORMATIVE partition
     */
    public List<LabeledPoint> getLabeledPoints() {
        List<LabeledPoint> labeledPoints = new ArrayList<>();
        for (int i = 0; i < inferredStart; i++) {
            labeledPoints.add(new LabeledPoint(points.get(i), labels[i].toLabel()));
        }
        return labeledPoints;
    }

    /**
     * @return whether there is at least one point in the MOST INFORMATIVE partition
     */
    public boolean hasLabeledPoints() {
        return inferredStart > 0;
    }

    /**
     * @return a list of data points outside of the MOST INFORMATIVE partition (i.e. INFERRED LABELS + UNKNOWN partitions)
     */
    public List<DataPoint> getUnlabeledPoints() {
        return points.subList(inferredStart, points.size());
    }

    /**
     * @return a list of all points in the UNKNOWN partition
     */
    public List<DataPoint> getUnknownPoints() {
        return points.subList(unknownStart, points.size());
    }

    /**
     * @return whether the UNKNOWN partition is not empty
     */
    public boolean hasUnknownPoints() {
        return unknownStart < points.size();
    }

    /**
     * @return a list of all data points whose labels are known (i.e. MOST INFORMATIVE + INFERRED LABELS)
     */
    public List<DataPoint> getKnownPoints() {
        return points.subList(0, unknownStart);
    }

    /**
     * @param point: a data point
     * @return the current label associated to this data point
     */
    public ExtendedLabel getLabel(DataPoint point) {
        return labels[findPosition(point)];
    }

    /**
     * @param points: a collection of data points
     * @return the current label associated to each data point in the collection
     */
    public ExtendedLabel[] getLabel(Collection<DataPoint> points) {
        return points.stream()
                .map(this::getLabel)
                .toArray(ExtendedLabel[]::new);
    }

    /**
     * Given a new pair (point, label) obtained through a Active Learning algorithm, we update the current partitioning by
     * performing the following operations:
     *
     *   1) this new pair is put on the MOST INFORMATIVE partition
     *   2) the current data model will be updated with this new data
     *   3) points in the UNKNOWN partition will have their labels recomputed, and put on INFERRED LABELS partition if possible
     *
     * @param labeledPoint: a new labeled point provided by an Active Learning exploration routine
     */
    public void update(LabeledPoint labeledPoint) {
        updateMostInformativePointsPartition(labeledPoint);
        classifier.update(labeledPoint);
        updateInferredLabelsPartition();
    }

    /**
     * Perform a sequence of update operations, in the order of the labeledPoints input
     * @param labeledPoints: list of labeled points to update
     */
    public void update(List<LabeledPoint> labeledPoints) {
        labeledPoints.forEach(this::update);
    }

    private void updateMostInformativePointsPartition(LabeledPoint labeledPoint) {
        int pos = findPosition(labeledPoint);
        labels[pos] = ExtendedLabel.fromLabel(labeledPoint.getLabel());

        if(pos >= unknownStart) {
            swap(pos, unknownStart);
            swap(unknownStart++, inferredStart++);
        }
        else if (pos >= inferredStart) {
            swap(pos, inferredStart++);
        }
    }

    private void updateInferredLabelsPartition() {
        int position = unknownStart;
        for (ExtendedLabel prediction : classifier.predict(getUnknownPoints())) {
            if (prediction != ExtendedLabel.UNKNOWN) {
                labels[position] = prediction;
                swap(position, unknownStart++);
            }
            position++;
        }
    }

    private void swap(int i, int j) {
        swapMapKeys(indexToPosition, points.get(i).getId(), points.get(j).getId());
        swapArrayElements(labels, i, j);
        Collections.swap(points, i, j);
    }

    private static <K, V> void swapMapKeys(Map<K, V> map, K key1, K key2) {
        V temp = map.get(key1);
        map.put(key1, map.get(key2));
        map.put(key2, temp);
    }

    private static <T> void swapArrayElements(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private int findPosition(DataPoint point) {
        Integer position = indexToPosition.get(point.getId());
        if (position == null) {
            throw new IllegalArgumentException("Point " + point + " not found.");
        }
        return position;
    }
}
