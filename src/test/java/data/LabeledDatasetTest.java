package data;

import exceptions.EmptyUnlabeledSetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        data.addLabeledRow(new int[] {0,1,2,3}, y);
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
    void getRow_validIndexes_retrievesCorrectDataPoints() {
        for (int i = 0; i < X.length; i++) {
            assertEquals(X[i], data.getRow(i).getData());
        }
    }

    @Test
    void getRow_OufOfBoundsIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.getRow(-1));
        assertThrows(IllegalArgumentException.class, () -> data.getRow(X.length));
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
        data.addLabeledRow(0, 0);
        data.addLabeledRow(3, 0);
        assertEquals(2, data.getNumLabeledRows());
    }

    @Test
    void getNumUnlabeledRows_noLabeledPointsAdded_returnNumOfDataPoints() {
        assertEquals(X.length, data.getNumUnlabeledRows());
    }

    @Test
    void getNumUnlabeledRows_addedLabeledPoints_returnNumOfUnlabeledPoints() {
        data.addLabeledRow(0, 0);
        data.addLabeledRow(3, 0);
        assertEquals(X.length - 2, data.getNumUnlabeledRows());
    }

    @Test
    void addLabeledRow_outOfBoundsRow_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(X.length, 0));
    }

    @Test
    void addLabeledRow_addDuplicatedLabeledRow_throwsException() {
        labelAll();
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(0,0));
    }

    @Test
    void addLabeledRow_invalidLabel_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(0, -1));
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(0, 2));
    }

    @Test
    void addLabeledRow_incompatibleSizes_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.addLabeledRow(new int[]{0}, new int[]{0, 1}));
    }

//    @Test
//    void removeLabeledRow_removeLabeledPoint_pointNotInLabeledSetAnymore() {
//        data.addLabeledRow(0, 0);
//        data.removeLabeledRow(0);
//        assertFalse(data.getLabeledPoints().contains(0));
//    }

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
    void retrieveMinimizerOverUnlabeledData_emptyUnlabeledSet_throwsException() {
        labelAll();
        assertThrows(EmptyUnlabeledSetException.class, () -> data.retrieveMinimizerOverUnlabeledData(null));
    }

    @Test
    void retrieveMinimizerOverUnlabeledData_emptyLabeledSetAndDummyScoreFunction_returnsCorrectMinimizer() {
        data = new LabeledDataset(X);
        assertEquals(0, data.retrieveMinimizerOverUnlabeledData(pt -> (double) pt.getId()));
    }

    @Test
    void retrieveMinimizerOverUnlabeledData_nonEmptyLabeledSetAndDummyScoreFunction_returnsCorrectMinimizer() {
        data = new LabeledDataset(X);
        data.addLabeledRow(0, 0);
        assertEquals(1, data.retrieveMinimizerOverUnlabeledData(pt -> (double) pt.getId()));
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