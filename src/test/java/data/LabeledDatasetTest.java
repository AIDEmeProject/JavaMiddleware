package data;

import machinelearning.classifier.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LabeledDatasetTest {
    private ArrayList<DataPoint> unlabeledPoints;
    private List<LabeledPoint> labeledPoints;
    private LabeledDataset data;

    @BeforeEach
    void setUp() {
        double[][] X = new double[][] {{1}, {2}, {3}, {4}};
        computeUnlabeledPointsFromMatrix(X);

        Label[] y = new Label[]{Label.NEGATIVE, Label.NEGATIVE, Label.POSITIVE, Label.POSITIVE};
        labeledPoints = new ArrayList<>();
        for (int i = 0; i < X.length; i++) {
            labeledPoints.add(new LabeledPoint(i, X[i], y[i]));
        }

        data = new LabeledDataset(unlabeledPoints);
    }

    private void assertUnlabeledCollectionsAreEqual(Collection<DataPoint> collection1, Collection<DataPoint> collection2){
        assertTrue(collection1.containsAll(collection2) && collection2.containsAll(collection1));
    }

    private void assertLabeledCollectionsAreEqual(Collection<LabeledPoint> collection1, Collection<LabeledPoint> collection2){
        assertEquals(collection1.size(), collection2.size());

        Iterator<LabeledPoint> it = collection2.iterator();
        for (DataPoint point : collection1){
            assertEquals(point, it.next());
        }
    }

    private void computeUnlabeledPointsFromMatrix(double[][] X){
        unlabeledPoints = new ArrayList<>(X.length);
        for (int i = 0; i < X.length; i++){
            unlabeledPoints.add(new DataPoint(i, X[i]));
        }
    }

    private void putOnLabeledDataset(int... indexes) {
        for (int i : indexes) {
            data.putOnLabeledSet(labeledPoints.get(i));
        }
    }

    @Test
    void constructor_noDataPoints_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(new ArrayList<>()));
    }

    @Test
    void constructor_rowsOfDifferentLengths_throwsException() {
        computeUnlabeledPointsFromMatrix(new double[][] {{1},{1,2}});
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(unlabeledPoints));
    }

    @Test
    void getLabeledPoints_noPointsLabeled_returnsEmptyCollection() {
        assertTrue(data.getLabeledPoints().isEmpty());
    }

    @Test
    void getLabeledPoints_fewPointsLabeled_returnsExpectedCollection() {
        putOnLabeledDataset(0, 1);
        assertLabeledCollectionsAreEqual(labeledPoints.subList(0, 2), data.getLabeledPoints());
    }

    @Test
    void getUnlabeledPoints_noLabeledPointsAdded_returnsAllPoints() {
        assertUnlabeledCollectionsAreEqual(unlabeledPoints, data.getUnlabeledPoints());
    }

    @Test
    void getUnlabeledPoints_fewPointsLabeled_returnsExpectedCollection() {
        int[] indexes = new int[] {0};

        putOnLabeledDataset(indexes);
        for (int index : indexes) {
            unlabeledPoints.remove(index);
        }

        assertUnlabeledCollectionsAreEqual(unlabeledPoints, data.getUnlabeledPoints());
    }

    @Test
    void getNumLabeledRows_noLabeledPointsAdded_returnsZero() {
        assertEquals(0, data.getNumLabeledPoints());
    }

    @Test
    void getNumLabeledRows_addedLabeledPoints_returnsCorrectNumber() {
        putOnLabeledDataset(0, 2);
        assertEquals(2, data.getNumLabeledPoints());
    }

    @Test
    void getNumUnlabeledRows_noLabeledPointsAdded_returnNumOfDataPoints() {
        assertEquals(unlabeledPoints.size(), data.getNumUnlabeledPoints());
    }

    @Test
    void getNumUnlabeledRows_addedLabeledPoints_returnNumOfUnlabeledPoints() {
        putOnLabeledDataset(0, 3);
        assertEquals(unlabeledPoints.size() - 2, data.getNumUnlabeledPoints());
    }

    @Test
    void putOnLabeledSet_addSamePointTwice_throwsException() {
        putOnLabeledDataset(0);
        assertThrows(IllegalArgumentException.class, () -> data.putOnLabeledSet(labeledPoints.get(0)));
    }

    @Test
    void putOnLabeledSet_addPoint_inLabeledSetButNotInUnlabeledSet() {
        data.putOnLabeledSet(labeledPoints.get(0));
        assertTrue(data.getLabeledPoints().contains(labeledPoints.get(0)));
        assertFalse(data.getUnlabeledPoints().contains(unlabeledPoints.get(0)));
    }
}