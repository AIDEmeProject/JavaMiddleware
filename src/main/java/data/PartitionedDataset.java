package data;


import explore.user.UserLabel;

import machinelearning.classifier.Classifier;
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
    private final IndexedDataset points;

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
     * List of user labels
     */
    private List<UserLabel> userLabels = new ArrayList<>();

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
    public PartitionedDataset(IndexedDataset points, ExtendedClassifier classifier) {
        this.points = points.copy();
        this.classifier = Objects.requireNonNull(classifier);

        this.inferredStart = 0;
        this.unknownStart = 0;

        this.labels = new ExtendedLabel[this.points.length()];
        Arrays.fill(this.labels, ExtendedLabel.UNKNOWN);

        this.indexToPosition = new HashMap<>(this.points.length());
        initializeIndexes();
    }


    public ExtendedClassifier getTSMClassifier(){
        return this.classifier;
    }

    /**
     * Add a new point to the dataset. Needed for FakePoint sampling
     * @param point
     */
    public void addLabeledPointToDataset(LabeledPoint point){

        this.points.add(point.getData());

        int nPoints = this.points.length();
        ExtendedLabel[] newLabels = new ExtendedLabel[nPoints + 1];
        for (int i = 0; i < nPoints; i++ ){
            newLabels[i] = this.labels[i];
        }
        newLabels[nPoints] =  ExtendedLabel.fromLabel(point.getLabel());

    }

    /**
     * Through this constructor, no label inference will be done (i.e. the INFERRED LABELS partition is always empty)
     * @param points: data points to build partitions. A copy will be stored internally to avoid unintended changes to input list.
     */
    public PartitionedDataset(IndexedDataset points) {
        this(points, new ExtendedClassifierStub());
    }

    private void initializeIndexes() {
        int i = 0;
        for (DataPoint point : this.points) {
            this.indexToPosition.put(point.getId(), i++);
        }
    }

    public DataPoint get(int i){
        return this.getAllPoints().get(findPosition(i));
    }

    /**
     * @return the entire list of data points. The order of data points MAY CHANGE after every update() call.
     */
    public IndexedDataset getAllPoints() {
        return points;
    }

    /**
     * @return all the data points in the MOST INFORMATIVE partition
     */
    public LabeledDataset getLabeledPoints() {
        return new LabeledDataset(points.getRange(0, inferredStart), userLabels.toArray(new UserLabel[0]));
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
    public IndexedDataset getUnlabeledPoints() {
        return points.getRange(inferredStart, points.length());
    }

    /**
     * @return a list of all points in the UNKNOWN partition
     */
    public IndexedDataset getUnknownPoints() {
        return points.getRange(unknownStart, points.length());
    }

    /**
     * @return whether the UNKNOWN partition is not empty
     */
    public boolean hasUnknownPoints() {
        return unknownStart < points.length();
    }

    /**
     * @return a list of all data points whose labels are known (i.e. MOST INFORMATIVE + INFERRED LABELS)
     */
    public IndexedDataset getKnownPoints() {
        return points.getRange(0, unknownStart);
    }

    /**
     * @param point: a data point
     * @return the current label associated to this data point
     */
    public ExtendedLabel getLabel(DataPoint point) {
        return labels[findPosition(point.getId())];
    }

    /**
     * @param points: a collection of data points
     * @return the current label associated to each data point in the collection
     */
    public ExtendedLabel[] getLabel(IndexedDataset points) {
        return points.stream()
                .map(this::getLabel)
                .toArray(ExtendedLabel[]::new);
    }

    /**
     * @param classifier: classification model
     * @return the predicted labels over the entire data. Points on the known partition have their labels maintained.
     */
    public Label[] predictLabels(Classifier classifier) {
        Label[] predictedLabels = new Label[points.length()];
        for (int i = 0; i < unknownStart; i++) {
            predictedLabels[i] = labels[i].toLabel();
        }

        Label[] classifierLabels = classifier.predict(getUnknownPoints());
        System.arraycopy(classifierLabels, 0, predictedLabels, unknownStart, classifierLabels.length);

        return predictedLabels;
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
        // update user labels
        userLabels.add(labeledPoint.getLabel());

        // update partitions
        ExtendedLabel previousLabel = labels[findPosition(labeledPoint.getId())];

        updateMostInformativePointsPartition(labeledPoint);

        // if our data model is still working and the update point is UNKNOWN, update model and INFERRED + UNKNOWN partitions
        if (classifier.isRunning() && previousLabel.isUnknown()) {
            classifier.update(labeledPoint);

            // when our data model has a change of internal state, it may require a relabeling of points in the INFERRED partition
            if (classifier.triggerRelabeling()) {
                relabelInferredPartition();
            }

            attemptToLabelUnknownPoints();
        }
    }

    /**
     * Perform a sequence of update operations, in the order of the labeledPoints input
     * @param labeledPoints: list of labeled points to update
     */
    public void update(List<LabeledPoint> labeledPoints) {
        labeledPoints.forEach(this::update);
    }

    private void relabelInferredPartition() {
        for (int pos = inferredStart; pos < unknownStart; pos++) {
            ExtendedLabel prediction = classifier.predict(points.get(pos));

            // label has changed
            if (prediction != labels[pos]) {
                // update label
                labels[pos] = prediction;

                // if new prediction is UNKNOWN, put point on UNKNOWN partition
                if (prediction.isUnknown()) {
                    unknownStart--;
                    swap(pos, unknownStart);
                }
            }
        }
    }

    private void updateMostInformativePointsPartition(LabeledPoint labeledPoint) {
        int pos = findPosition(labeledPoint.getId());
        labels[pos] = ExtendedLabel.fromLabel(labeledPoint.getLabel());

        if(pos >= unknownStart) {
            swap(pos, unknownStart);
            swap(unknownStart++, inferredStart++);
        }
        else if (pos >= inferredStart) {
            swap(pos, inferredStart++);
        }
    }


    private void attemptToLabelUnknownPoints() {
        for (int position = unknownStart; position < labels.length; position++) {
            ExtendedLabel prediction = classifier.predict(points.get(position));
            if (!prediction.isUnknown()) {
                labels[position] = prediction;
                swap(position, unknownStart++);
            }
        }
    }

    private void swap(int i, int j) {
            swapMapKeys(indexToPosition, points.getIndexes().get(i), points.getIndexes().get(j));
            swapArrayElements(labels, i, j);
            points.swap(i, j);
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

    private int findPosition(long id) {
        Integer position = indexToPosition.get(id);
        if (position == null) {
            throw new IllegalArgumentException("ID " + id + " not found.");
        }
        return position;
    }
}
