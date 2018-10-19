package data;

import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.ExtendedLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static machinelearning.classifier.Label.NEGATIVE;
import static machinelearning.classifier.Label.POSITIVE;
import static org.junit.jupiter.api.Assertions.*;

class PartitionedDatasetTest {
    private List<DataPoint> dataPoints;
    private PartitionedDataset dataset;
    private MockClassifier classifier;

    @BeforeEach
    void setUp() {
        dataPoints = new ArrayList<>();
        dataPoints.add(new DataPoint(0, new double[]{0}));
        dataPoints.add(new DataPoint(10, new double[]{1}));
        dataPoints.add(new DataPoint(20, new double[]{2}));
        dataPoints.add(new DataPoint(30, new double[]{3}));
        dataPoints.add(new DataPoint(40, new double[]{4}));

        classifier = new MockClassifier(Arrays.asList(0L, 10L, 20L, 30L, 40L));
        dataset = new PartitionedDataset(dataPoints, classifier);
    }

    @Test
    void constructor_nullExtendedClassifier_throwsException() {
        assertThrows(NullPointerException.class, () -> new PartitionedDataset(dataPoints, null));
    }

    @Test
    void getAllPoints_noUpdates_outputIsACopyOfOriginalInput() {
        assertNotSame(dataPoints, dataset.getAllPoints());
        assertEquals(dataPoints, dataset.getAllPoints());
    }

    @Test
    void getAllPoints_singleUpdate_outputContainsTheSameElementsAsInputButNotInOriginalOrder() {
        dataset.update(new LabeledPoint(dataPoints.get(2), NEGATIVE));
        assertNotEquals(dataPoints, dataset.getAllPoints());
        assertTrue(dataset.getAllPoints().containsAll(dataPoints));
    }

    @Test
    void getLabeledPoints_noUpdates_returnsEmptyList() {
        assertTrue(dataset.getLabeledPoints().isEmpty());
    }

    @Test
    void getLabeledPoints_singleUpdate_returnsListContainingALabeledPointWrappingTheUpdatedPoint() {
        LabeledPoint labeledPoint = new LabeledPoint(dataPoints.get(0), NEGATIVE);
        dataset.update(labeledPoint);
        assertEquals(Collections.singletonList(labeledPoint), dataset.getLabeledPoints());
    }

    @Test
    void hasLabeledPoints_noUpdates_returnsFalse() {
        assertFalse(dataset.hasLabeledPoints());
    }

    @Test
    void hasLabeledPoints_singleUpdate_returnsTrue() {
        dataset.update(new LabeledPoint(dataPoints.get(0), NEGATIVE));
        assertTrue(dataset.hasLabeledPoints());
    }

    @Test
    void getKnownPoints_noUpdates_returnsEmptyList() {
        assertTrue(dataset.getKnownPoints().isEmpty());
    }

    @Test
    void getKnownPoints_singleUpdateAndNoClassificationModel_returnsListContainingTheUpdatedPoint() {
        DataPoint dataPoint = dataPoints.get(1);
        dataset.update(new LabeledPoint(dataPoint, NEGATIVE));
        assertEquals(Collections.singletonList(dataPoint), dataset.getKnownPoints());
    }

    @Test
    void getKnownPoints_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        classifier.setLabel(20L, ExtendedLabel.NEGATIVE);

        dataset.update(new LabeledPoint(dataPoints.get(4), NEGATIVE));

        assertListsHaveTheSameElements(dataset.getKnownPoints(), Arrays.asList(dataPoints.get(4), dataPoints.get(2)));
    }

    private <T> void assertListsHaveTheSameElements(List<T> list1, List<T> list2) {
        assertEquals(list1.size(), list2.size());
        assertTrue(list1.containsAll(list2));
    }

    @Test
    void hasUnknownPoints_noUpdates_returnsTrue() {
        assertTrue(dataset.hasUnknownPoints());
    }

    @Test
    void hasUnknownPoints_updatedAllPoints_returnsFalse() {
        for (DataPoint dataPoint : dataPoints) {
            dataset.update(new LabeledPoint(dataPoint, NEGATIVE));
        }
        assertFalse(dataset.hasUnknownPoints());
    }

    @Test
    void getUnlabeledPoints_noUpdates_returnsAllDataPoints() {
        assertEquals(dataPoints, dataset.getUnlabeledPoints());
    }

    @Test
    void getUnlabeledPoints_singleUpdate_returnsListContainingAllDataPointsExceptTheUpdatedOne() {
        dataset.update(new LabeledPoint(dataPoints.remove(2), NEGATIVE));
        assertEquals(dataPoints.size(), dataset.getUnlabeledPoints().size());
        assertTrue(dataset.getUnlabeledPoints().containsAll(dataPoints));
    }

    @Test
    void getUnknownPoints_noUpdates_returnsAllDataPoints() {
        assertEquals(dataPoints, dataset.getUnknownPoints());
    }

    @Test
    void getUnknownPoints_singleUpdateAndNoClassificationModel_returnsListContainingAllDataPointsExceptTheUpdatedOne() {
        dataset.update(new LabeledPoint(dataPoints.remove(2), NEGATIVE));
        assertEquals(dataPoints.size(), dataset.getUnknownPoints().size());
        assertTrue(dataset.getUnknownPoints().containsAll(dataPoints));
    }

    @Test
    void getUnknownPoints_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        classifier.setLabel(20L, ExtendedLabel.NEGATIVE);

        dataset.update(new LabeledPoint(dataPoints.get(4), NEGATIVE));

        assertListsHaveTheSameElements(dataset.getUnknownPoints(), Arrays.asList(dataPoints.get(0), dataPoints.get(1), dataPoints.get(3)));
    }

    @Test
    void getLabel_emptyInputCollection_returnsEmptyArray() {
        assertEquals(0, dataset.getLabel(Collections.EMPTY_LIST).length);
    }

    @Test
    void getLabel_noUpdates_returnsUnknownForAllPoints() {
        ExtendedLabel[] labels = new ExtendedLabel[dataPoints.size()];
        Arrays.fill(labels, ExtendedLabel.UNKNOWN);
        assertArrayEquals(labels, dataset.getLabel(dataPoints));
    }

    @Test
    void getLabel_dataPointsIndexNotInInitialCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getLabel(new DataPoint(-10, new double[]{1})));
    }

    @Test
    void getLabel_singleUpdateAndNoClassificationModel_returnExpectedLabelForUpdatedPointAndUnknownForTheRest() {
        DataPoint dataPoint = dataPoints.remove(2);
        Label label = NEGATIVE;
        dataset.update(new LabeledPoint(dataPoint, label));

        assertEquals(label, dataset.getLabel(dataPoint).toLabel());

        ExtendedLabel[] labels = new ExtendedLabel[dataPoints.size()];
        Arrays.fill(labels, ExtendedLabel.UNKNOWN);
        assertArrayEquals(labels, dataset.getLabel(dataPoints));
    }

    @Test
    void getLabel_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        classifier.setLabel(20L, ExtendedLabel.POSITIVE);
        dataset.update(new LabeledPoint(dataPoints.get(4), NEGATIVE));

        ExtendedLabel[] expected = new ExtendedLabel[dataPoints.size()];
        Arrays.fill(expected, ExtendedLabel.UNKNOWN);
        expected[2] = ExtendedLabel.POSITIVE;
        expected[4] = ExtendedLabel.NEGATIVE;

        assertArrayEquals(expected, dataset.getLabel(dataPoints));
    }

    @Test
    void update_dataPointsIndexNotInInitialCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.update(new LabeledPoint(-10, new double[]{1}, NEGATIVE)));
    }

    @Test
    void update_dataModelIsNotRunning_dataModelIsNotUpdated() {
        classifier.setRunning(false);
        dataset.update(new LabeledPoint(dataPoints.get(2), NEGATIVE));
        assertTrue(classifier.updates.isEmpty());
    }

    @Test
    void update_dataModelIsRunningButPredictedLabelIsKnown_dataModelIsNotUpdated() {
        classifier.setLabel(30L, ExtendedLabel.NEGATIVE);

        LabeledPoint labeledPoint = new LabeledPoint(dataPoints.get(0), POSITIVE);
        dataset.update(labeledPoint);
        dataset.update(new LabeledPoint(dataPoints.get(3), POSITIVE));

        assertEquals(Collections.singletonList(labeledPoint), classifier.updates);
    }

    @Test
    void update_dataModelIsRunningAndPredictedLabelIsUnknown_dataModelIsUpdatedWithInputLabeledPoint() {
        LabeledPoint labeledPoint = new LabeledPoint(dataPoints.get(2), NEGATIVE);
        dataset.update(labeledPoint);
        assertEquals(Collections.singletonList(labeledPoint), classifier.updates);
    }

    @Test
    void update_relabelingTriggeredByDataModel_labelsInInferredPartitionAreRecomputed() {
        // update model: last two labels are negative
        classifier.setLabel(30L, ExtendedLabel.NEGATIVE);
        classifier.setLabel(40L, ExtendedLabel.NEGATIVE);

        // new point
        dataset.update(new LabeledPoint(dataPoints.get(0), POSITIVE));

        assertLabels(
                ExtendedLabel.POSITIVE,  // "user" labeled point
                ExtendedLabel.UNKNOWN,
                ExtendedLabel.UNKNOWN,
                ExtendedLabel.NEGATIVE,  // predicted by model
                ExtendedLabel.NEGATIVE  // predicted by model
        );

        // model update: one new label set to positive, one previous negative label is unknown
        // set trigger to true in order to perform relabeling
        classifier.setLabel(20L, ExtendedLabel.POSITIVE);
        classifier.setLabel(30L, ExtendedLabel.UNKNOWN);
        classifier.setTriggerRelabeling(true);


        dataset.update(new LabeledPoint(dataPoints.get(1), POSITIVE));

        // verify that user labeled points are negative, and remaining points are positive
        assertLabels(
                ExtendedLabel.POSITIVE,
                ExtendedLabel.POSITIVE,
                ExtendedLabel.POSITIVE,  // by model
                ExtendedLabel.UNKNOWN,   // relabeling
                ExtendedLabel.NEGATIVE   // previous classification, remained unchanged
        );
    }

    private class MockClassifier implements ExtendedClassifier {
        private Map<Long, ExtendedLabel> mapping;
        private boolean isRunning = true;
        private boolean triggerRelabeling;

        /**
         * List of labeled points called by update
         */
        List<LabeledPoint> updates = new ArrayList<>();

        public MockClassifier(List<Long> unknownIds) {
            this.mapping = new HashMap<>();
            for (Long id : unknownIds) {
                this.mapping.put(id, ExtendedLabel.UNKNOWN);
            }
            this.triggerRelabeling = false;
        }

        public void setRunning(boolean running) {
            isRunning = running;
        }

        public void setTriggerRelabeling(boolean triggerRelabeling) {
            this.triggerRelabeling = triggerRelabeling;
        }

        public void setLabel(long id, ExtendedLabel label) {
            mapping.put(id, label);
        }

        @Override
        public void update(Collection<LabeledPoint> labeledPoint) {
            for (LabeledPoint point : labeledPoint) {
                setLabel(point.getId(), ExtendedLabel.fromLabel(point.getLabel()));
                updates.add(point);
            }
        }

        @Override
        public ExtendedLabel predict(DataPoint dataPoint) {
            return mapping.get(dataPoint.getId());
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public boolean triggerRelabeling() {
            return triggerRelabeling;
        }
    }

    private void assertLabels(ExtendedLabel... labels) {
        assertEquals(dataset.getLabel(dataPoints.get(0)), labels[0]);
        assertEquals(dataset.getLabel(dataPoints.get(1)), labels[1]);
        assertEquals(dataset.getLabel(dataPoints.get(2)), labels[2]);
        assertEquals(dataset.getLabel(dataPoints.get(3)), labels[3]);
        assertEquals(dataset.getLabel(dataPoints.get(4)), labels[4]);
    }
}