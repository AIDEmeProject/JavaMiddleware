package data;

import exceptions.EmptyUnlabeledSetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class LabeledDatasetTest {
    private double[][] X;
    private int[] y;
    private LabeledDataset data;
    private ArrayList<DataPoint> points;

    @BeforeEach
    void setUp() {
        X = new double[][] {{1}, {2}, {3}, {4}};
        y = new int[] {0,0,1,1};
        data = new LabeledDataset(X);

        points = new ArrayList<>(4);
        for (int i = 0; i < X.length; i++) {
            points.add(new DataPoint(i, X[i]));
        }
    }

    private void assertUnlabeledCollectionsAreEqual(Collection<DataPoint> collection1, Collection<DataPoint> collection2){
        assertEquals(collection1.size(), collection2.size());
        for (DataPoint point : collection1){
            assertTrue(collection2.contains(point));
        }
    }

    private void assertLabeledCollectionsAreEqual(Collection<LabeledPoint> collection1, Collection<LabeledPoint> collection2){
        assertEquals(collection1.size(), collection2.size());

        Iterator<LabeledPoint> it = collection2.iterator();
        for (DataPoint point : collection1){
            assertEquals(point, it.next());
        }
    }

    private void labelAll(){
        data.putOnLabeledSet(points, y);
    }

    @Test
    void constructor_noDataPoints_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(new double[][] {}));
    }

    @Test
    void constructor_zeroDimensionalDataMatrix_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(new double[][] {{},{}}));
    }

    @Test
    void constructor_rowsOfDifferentLengths_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(new double[][] {{1},{1,2}}));
    }

    @Test
    void getLabeledPoints_noPointsLabeled_returnsEmptyCollection() {
        assertTrue(data.getLabeledPoints().isEmpty());
    }

    @Test
    void getLabeledPoints_fewPointsLabeled_returnsExpectedCollection() {
        Collection<LabeledPoint> labeledPoints = new ArrayList<>();

        for (int index : new int[] {0,2}) {
            data.putOnLabeledSet(points.get(index), y[index]);
            labeledPoints.add(new LabeledPoint(points.get(index), y[index]));
        }

        assertLabeledCollectionsAreEqual(labeledPoints, data.getLabeledPoints());
    }

    @Test
    void getUnlabeledPoints_noLabeledPointsAdded_returnsAllPoints() {
        assertUnlabeledCollectionsAreEqual(points, data.getUnlabeledPoints());
    }

    @Test
    void getUnlabeledPoints_fewPointsLabeled_returnsExpectedCollection() {
        int[] indexes = new int[] {0,2};
        for (int index : indexes) {
            data.putOnLabeledSet(points.get(index), y[index]);
            points.remove(points.get(index));
        }

        assertUnlabeledCollectionsAreEqual(points, data.getUnlabeledPoints());
    }

    @Test
    void getNumLabeledRows_noLabeledPointsAdded_returnsZero() {
        assertEquals(0, data.getNumLabeledPoints());
    }

    @Test
    void getNumLabeledRows_addedLabeledPoints_returnsCorrectNumber() {
        data.putOnLabeledSet(points.get(0), y[0]);
        data.putOnLabeledSet(points.get(2), y[2]);
        assertEquals(2, data.getNumLabeledPoints());
    }

    @Test
    void getNumUnlabeledRows_noLabeledPointsAdded_returnNumOfDataPoints() {
        assertEquals(X.length, data.getNumUnlabeledPoints());
    }

    @Test
    void getNumUnlabeledRows_addedLabeledPoints_returnNumOfUnlabeledPoints() {
        data.putOnLabeledSet(points.get(0), y[0]);
        data.putOnLabeledSet(points.get(3), y[3]);
        assertEquals(X.length - 2, data.getNumUnlabeledPoints());
    }

    @Test
    void putOnLabeledSet_addSamePointTwice_throwsException() {
        labelAll();
        assertThrows(IllegalArgumentException.class, () -> data.putOnLabeledSet(points.get(0),y[0]));
    }

    @Test
    void putOnLabeledSet_invalidLabel_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.putOnLabeledSet(points.get(0), -1));
        assertThrows(IllegalArgumentException.class, () -> data.putOnLabeledSet(points.get(0), 2));
    }

    @Test
    void putOnLabeledSet_incompatibleSizes_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.putOnLabeledSet(new ArrayList<>(), new int[]{0, 1}));
    }

    @Test
    void putOnLabeledSet_addPoint_inLabeledSetButNotInUnlabeledSet() {
        data.putOnLabeledSet(points.get(0), y[0]);
        assertTrue(data.getLabeledPoints().contains(points.get(0)));
        assertFalse(data.getUnlabeledPoints().contains(points.get(0)));
    }

    @Test
    void removeFromLabeledSet_removeRowNotInLabeledSet_throwsException() {
       assertThrows(IllegalArgumentException.class, () -> data.removeFromLabeledSet(points.get(0)));
    }

    @Test
    void removeFromLabeledSet_addAndRemoveSamePoint_inUnlabeledSetButNotInLabeledSet() {
        DataPoint point = points.get(0);
        data.putOnLabeledSet(point, y[0]);
        data.removeFromLabeledSet(point);
        assertFalse(data.getLabeledPoints().contains(point));
        assertTrue(data.getUnlabeledPoints().contains(point));
    }

    @Test
    void subsampleUnlabeledSet_zeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.subsampleUnlabeledSet(0));
    }

    @Test
    void subsampleUnlabeledSet_negativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.subsampleUnlabeledSet(-1));
    }

    @Test
    void subsampleUnlabeledSet_sampleSizeEqualsToNumUnlabeledPoints_theOwnObjectIsReturned() {
        data = new LabeledDataset(X);
        assertTrue(data == data.subsampleUnlabeledSet(data.getNumUnlabeledPoints()));
    }

    @Test
    void subsampleUnlabeledSet_sampleSizeLargerThanNumUnlabeledPoints_theOwnObjectIsReturned() {
        assertTrue(data == data.subsampleUnlabeledSet(data.getNumUnlabeledPoints() + 1));
    }

    @Test
    void subsampleUnlabeledSet_sampleSizeEqualsInfinity_theOwnObjectIsReturned() {
        assertTrue(data == data.subsampleUnlabeledSet(Integer.MAX_VALUE));
    }

    @Test
    void subsampleUnlabeledSet_emptyUnlabeledSet_throwsException() {
        labelAll();
        assertThrows(EmptyUnlabeledSetException.class, () -> data.subsampleUnlabeledSet(1));
    }
}