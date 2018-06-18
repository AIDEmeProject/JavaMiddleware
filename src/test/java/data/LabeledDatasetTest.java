package data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LabeledDatasetTest {
    private double[][] X;
    private int[] y;
    private LabeledDataset data;

    @BeforeEach
    void setUp() {
        X = new double[][] {{1}, {2}, {3}, {4}};
        y = new int[] {0,0,1,1};
        data = new LabeledDataset(X);
    }

    private void labelAll(){
        data.addLabeledRow(data.getAllPoints(), y);
    }

    @Test
    void constructor_noDataPoints_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(new double[][] {}));
    }

    @Test
    void constructor_zeroDimensionDataMatrix_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledDataset(new double[][] {{},{}}));
    }

    @Test
    void setLabel_invalidRowIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.setLabel(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> data.setLabel(y.length, 0));
    }

    @Test
    void setLabel_rowNotInLabeledSet_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.setLabel(0, 0));
    }

    @Test
    void setLabel_invalidLabel_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.setLabel(0, -1));
        assertThrows(IllegalArgumentException.class, () -> data.setLabel(0, 2));
    }

    @Test
    void getNumLabeledRows_noLabeledPointsAdded_returnsZero() {
        assertEquals(0, data.getNumLabeledRows());
    }

    @Test
    void getNumLabeledRows_addedLabeledPoints_returnsCorrectNumber() {
        data.addLabeledRow(new DataPoint(0, X[0]), 0);
        data.addLabeledRow(new DataPoint(2, X[2]), 0);
        assertEquals(2, data.getNumLabeledRows());
    }

    @Test
    void getNumUnlabeledRows_noLabeledPointsAdded_returnNumOfDataPoints() {
        assertEquals(X.length, data.getNumUnlabeledRows());
    }

    @Test
    void getNumUnlabeledRows_addedLabeledPoints_returnNumOfUnlabeledPoints() {
        data.addLabeledRow(new DataPoint(0, X[0]), 0);
        data.addLabeledRow(new DataPoint(3, X[3]), 0);
        assertEquals(X.length - 2, data.getNumUnlabeledRows());
    }

    @Test
    void addLabeledRow_addDuplicatedLabeledRow_throwsException() {
        labelAll();
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(new DataPoint(0, X[0]),0));
    }

    @Test
    void addLabeledRow_invalidLabel_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(new DataPoint(0, X[0]), -1));
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(new DataPoint(0, X[0]), 2));
    }

    @Test
    void addLabeledRow_incompatibleSizes_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(new ArrayList<>(), new int[]{0, 1}));
    }

    @Test
    void removeLabeledRow_rowIndexOutOfBounds_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.removeLabeledRow(-1));
        assertThrows(IllegalArgumentException.class, () -> data.removeLabeledRow(X.length));
    }

    @Test
    void removeLabeledRow_removeRowNotInLabeledSet_throwsException() {
       assertThrows(IllegalArgumentException.class, () -> data.removeLabeledRow(0));
    }

    @Test
    void sample_zeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.subsampleUnlabeledSet(0));
    }

    @Test
    void sample_negativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.subsampleUnlabeledSet(-1));
    }

    @Test
    void sample_sampleSizeEqualsToNumUnlabeledPoints_theOwnObjectIsReturned() {
        data = new LabeledDataset(X);
        assertTrue(data == data.subsampleUnlabeledSet(data.getNumUnlabeledRows()));
    }

    @Test
    void sample_sampleSizeLargerThanNumUnlabeledPoints_theOwnObjectIsReturned() {
        assertTrue(data == data.subsampleUnlabeledSet(data.getNumUnlabeledRows() + 1));
    }

    @Test
    void sample_sampleSizeEqualsInfinity_theOwnObjectIsReturned() {
        assertTrue(data == data.subsampleUnlabeledSet(Integer.MAX_VALUE));
    }
}