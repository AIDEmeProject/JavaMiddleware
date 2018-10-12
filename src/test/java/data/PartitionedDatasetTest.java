package data;

import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.ExtendedLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import utils.linalg.Vector;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PartitionedDatasetTest {
    private IndexedDataset dataPoints;
    private PartitionedDataset dataset;

    @BeforeEach
    void setUp() {
        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0, new double[]{0});
        builder.add(10, new double[]{1});
        builder.add(20, new double[]{2});
        builder.add(30, new double[]{3});
        builder.add(40, new double[]{4});

        dataPoints = builder.build();
        dataset = new PartitionedDataset(dataPoints);
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
        dataset.update(new LabeledPoint(dataPoints.get(2), Label.NEGATIVE));
        assertEquals(dataPoints.getRows(2, 1, 0, 3, 4), dataset.getAllPoints());
    }

    @Test
    void getLabeledPoints_noUpdates_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getLabeledPoints());
    }

    @Test
    void getLabeledPoints_singleUpdate_returnsListContainingALabeledPointWrappingTheUpdatedPoint() {
        DataPoint dataPoint = dataPoints.get(0);
        Label label = Label.NEGATIVE;
        dataset.update(new LabeledPoint(dataPoint, label));
        assertEquals(new LabeledPoint(dataPoint, label), dataset.getLabeledPoints().get(0));
    }

    @Test
    void hasLabeledPoints_noUpdates_returnsFalse() {
        assertFalse(dataset.hasLabeledPoints());
    }

    @Test
    void hasLabeledPoints_singleUpdate_returnsTrue() {
        dataset.update(new LabeledPoint(dataPoints.get(0), Label.NEGATIVE));
        assertTrue(dataset.hasLabeledPoints());
    }

    @Test
    void getKnownPoints_noUpdates_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getKnownPoints());
    }

    @Test
    void getKnownPoints_singleUpdateAndNoClassificationModel_returnsListContainingTheUpdatedPoint() {
        DataPoint dataPoint = dataPoints.get(1);
        dataset.update(new LabeledPoint(dataPoint, Label.NEGATIVE));
        assertEquals(dataPoints.getRows(1), dataset.getKnownPoints());
    }

    @Test
    void getKnownPoints_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        ExtendedClassifier classifier = getExtendedClassifierStub(dataPoints.get(2).getId());
        dataset = new PartitionedDataset(dataPoints, classifier);

        dataset.update(new LabeledPoint(dataPoints.get(4), Label.NEGATIVE));

        assertEquals(dataset.getKnownPoints(), dataPoints.getRows(4, 2));
    }

    private ExtendedClassifier getExtendedClassifierStub(long index) {
        ExtendedClassifier classifier = mock(ExtendedClassifier.class);
        doNothing().when(classifier).update(isA(Vector.class), isA(Label.class));
        when(classifier.predict((DataPoint) any())).thenAnswer((Answer<ExtendedLabel>) invocationOnMock -> {
            DataPoint dataPoint1 = invocationOnMock.getArgument(0);
            return dataPoint1.getId() == index ? ExtendedLabel.POSITIVE : ExtendedLabel.UNKNOWN;
        });
        when(classifier.predict((IndexedDataset) any())).thenCallRealMethod();
        return classifier;
    }

    @Test
    void hasUnknownPoints_noUpdates_returnsTrue() {
        assertTrue(dataset.hasUnknownPoints());
    }

    @Test
    void hasUnknownPoints_updatedAllPoints_returnsFalse() {
        for (DataPoint dataPoint : dataPoints) {
            dataset.update(new LabeledPoint(dataPoint, Label.NEGATIVE));
        }
        assertFalse(dataset.hasUnknownPoints());
    }

    @Test
    void getUnlabeledPoints_noUpdates_returnsAllDataPoints() {
        assertEquals(dataPoints, dataset.getUnlabeledPoints());
    }

    @Test
    void getUnlabeledPoints_singleUpdate_returnsListContainingAllDataPointsExceptTheUpdatedOne() {
        dataset.update(new LabeledPoint(dataPoints.get(2), Label.NEGATIVE));
        assertEquals(dataPoints.getRows(1, 0, 3, 4), dataset.getUnlabeledPoints());
    }

    @Test
    void getUnknownPoints_noUpdates_returnsAllDataPoints() {
        assertEquals(dataPoints, dataset.getUnknownPoints());
    }

    @Test
    void getUnknownPoints_singleUpdateAndNoClassificationModel_returnsListContainingAllDataPointsExceptTheUpdatedOne() {
        dataset.update(new LabeledPoint(dataPoints.get(2), Label.NEGATIVE));
        assertEquals(dataPoints.getRows(1, 0, 3, 4), dataset.getUnknownPoints());
    }

    @Test
    void getUncertainPoints_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        ExtendedClassifier classifier = getExtendedClassifierStub(dataPoints.get(2).getId());
        dataset = new PartitionedDataset(dataPoints, classifier);

        dataset.update(new LabeledPoint(dataPoints.get(4), Label.NEGATIVE));

        assertEquals(dataPoints.getRows(1, 3, 0), dataset.getUnknownPoints());
    }

    @Test
    void getLabel_noUpdates_returnsUnknownForAllPoints() {
        ExtendedLabel[] labels = new ExtendedLabel[dataPoints.length()];
        Arrays.fill(labels, ExtendedLabel.UNKNOWN);
        assertArrayEquals(labels, dataset.getLabel(dataPoints));
    }

    @Test
    void getLabel_dataPointsIndexNotInInitialCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.getLabel(new DataPoint(-10, Vector.FACTORY.make(1))));
    }

    @Test
    void getLabel_singleUpdateAndNoClassificationModel_returnExpectedLabelForUpdatedPointAndUnknownForTheRest() {
        DataPoint dataPoint = dataPoints.get(2);
        Label label = Label.NEGATIVE;
        dataset.update(new LabeledPoint(dataPoint, label));

        assertEquals(label, dataset.getLabel(dataPoint).toLabel());

        ExtendedLabel[] labels = new ExtendedLabel[dataPoints.length()-1];
        Arrays.fill(labels, ExtendedLabel.UNKNOWN);
        assertArrayEquals(labels, dataset.getLabel(dataPoints.getRows(1, 0, 3, 4)));
    }

    @Test
    void getLabel_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        ExtendedClassifier classifier = getExtendedClassifierStub(dataPoints.get(2).getId());
        dataset = new PartitionedDataset(dataPoints, classifier);

        dataset.update(new LabeledPoint(dataPoints.get(4), Label.NEGATIVE));

        ExtendedLabel[] expected = new ExtendedLabel[dataPoints.length()];
        Arrays.fill(expected, ExtendedLabel.UNKNOWN);
        expected[2] = ExtendedLabel.POSITIVE;
        expected[4] = ExtendedLabel.NEGATIVE;

        assertArrayEquals(expected, dataset.getLabel(dataPoints));
    }

    @Test
    void update_dataPointsIndexNotInInitialCollection_throwsException() {
        DataPoint dataPoint = new DataPoint(-10, Vector.FACTORY.make(1));
        assertThrows(IllegalArgumentException.class, () -> dataset.update(new LabeledPoint(dataPoint, Label.NEGATIVE)));
    }
}