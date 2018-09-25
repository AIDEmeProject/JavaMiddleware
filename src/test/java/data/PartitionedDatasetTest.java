package data;

import machinelearning.classifier.Label;
import machinelearning.threesetmetric.ExtendedClassifier;
import machinelearning.threesetmetric.ExtendedLabel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import utils.linalg.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PartitionedDatasetTest {
    private List<DataPoint> dataPoints;
    private PartitionedDataset dataset;

    @BeforeEach
    void setUp() {
        dataPoints = new ArrayList<>();
        dataPoints.add(new DataPoint(0, new double[]{0}));
        dataPoints.add(new DataPoint(10, new double[]{1}));
        dataPoints.add(new DataPoint(20, new double[]{2}));
        dataPoints.add(new DataPoint(30, new double[]{3}));
        dataPoints.add(new DataPoint(40, new double[]{4}));

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
        assertNotEquals(dataPoints, dataset.getAllPoints());
        assertTrue(dataset.getAllPoints().containsAll(dataPoints));
    }

    @Test
    void getLabeledPoints_noUpdates_returnsEmptyList() {
        assertTrue(dataset.getLabeledPoints().isEmpty());
    }

    @Test
    void getLabeledPoints_singleUpdate_returnsListContainingALabeledPointWrappingTheUpdatedPoint() {
        DataPoint dataPoint = dataPoints.get(0);
        Label label = Label.NEGATIVE;
        dataset.update(new LabeledPoint(dataPoint, label));
        assertEquals(Collections.singletonList(new LabeledPoint(dataPoint, label)), dataset.getLabeledPoints());
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
    void getKnownPoints_noUpdates_returnsEmptyList() {
        assertTrue(dataset.getKnownPoints().isEmpty());
    }

    @Test
    void getKnownPoints_singleUpdateAndNoClassificationModel_returnsListContainingTheUpdatedPoint() {
        DataPoint dataPoint = dataPoints.get(1);
        dataset.update(new LabeledPoint(dataPoint, Label.NEGATIVE));
        assertEquals(Collections.singletonList(dataPoint), dataset.getKnownPoints());
    }

    @Test
    void getKnownPoints_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        ExtendedClassifier classifier = getExtendedClassifierStub(dataPoints.get(2).getId());
        dataset = new PartitionedDataset(dataPoints, classifier);

        dataset.update(new LabeledPoint(dataPoints.get(4), Label.NEGATIVE));

        assertListsHaveTheSameElements(dataset.getKnownPoints(), Arrays.asList(dataPoints.get(4), dataPoints.get(2)));
    }

    private ExtendedClassifier getExtendedClassifierStub(long index) {
        ExtendedClassifier classifier = mock(ExtendedClassifier.class);
        doNothing().when(classifier).update(isA(Vector.class), isA(Label.class));
        when(classifier.predict((DataPoint) any())).thenAnswer((Answer<ExtendedLabel>) invocationOnMock -> {
            DataPoint dataPoint1 = invocationOnMock.getArgument(0);
            return dataPoint1.getId() == index ? ExtendedLabel.POSITIVE : ExtendedLabel.UNKNOWN;
        });
        when(classifier.predict(anyCollection())).thenCallRealMethod();
        return classifier;
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
        dataset.update(new LabeledPoint(dataPoints.remove(2), Label.NEGATIVE));
        assertEquals(dataPoints.size(), dataset.getUnlabeledPoints().size());
        assertTrue(dataset.getUnlabeledPoints().containsAll(dataPoints));
    }

    @Test
    void getUnknownPoints_noUpdates_returnsAllDataPoints() {
        assertEquals(dataPoints, dataset.getUnknownPoints());
    }

    @Test
    void getUnknownPoints_singleUpdateAndNoClassificationModel_returnsListContainingAllDataPointsExceptTheUpdatedOne() {
        dataset.update(new LabeledPoint(dataPoints.remove(2), Label.NEGATIVE));
        assertEquals(dataPoints.size(), dataset.getUnknownPoints().size());
        assertTrue(dataset.getUnknownPoints().containsAll(dataPoints));
    }

    @Test
    void getUncertainPoints_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        ExtendedClassifier classifier = getExtendedClassifierStub(dataPoints.get(2).getId());
        dataset = new PartitionedDataset(dataPoints, classifier);

        dataset.update(new LabeledPoint(dataPoints.get(4), Label.NEGATIVE));

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
        Label label = Label.NEGATIVE;
        dataset.update(new LabeledPoint(dataPoint, label));

        assertEquals(label, dataset.getLabel(dataPoint).toLabel());

        ExtendedLabel[] labels = new ExtendedLabel[dataPoints.size()];
        Arrays.fill(labels, ExtendedLabel.UNKNOWN);
        assertArrayEquals(labels, dataset.getLabel(dataPoints));
    }

    @Test
    void getLabel_singleUpdateAndStubClassificationModel_returnsExpectedKnownPoints() {
        ExtendedClassifier classifier = getExtendedClassifierStub(dataPoints.get(2).getId());
        dataset = new PartitionedDataset(dataPoints, classifier);

        dataset.update(new LabeledPoint(dataPoints.get(4), Label.NEGATIVE));

        ExtendedLabel[] expected = new ExtendedLabel[dataPoints.size()];
        Arrays.fill(expected, ExtendedLabel.UNKNOWN);
        expected[2] = ExtendedLabel.POSITIVE;
        expected[4] = ExtendedLabel.NEGATIVE;

        assertArrayEquals(expected, dataset.getLabel(dataPoints));
    }

    @Test
    void update_dataPointsIndexNotInInitialCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> dataset.update(new LabeledPoint(-10, new double[]{1}, Label.NEGATIVE)));
    }
}