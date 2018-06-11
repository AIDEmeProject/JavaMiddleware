package data;

import exceptions.EmptyUnlabeledSetException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LabeledDataTest {
    private double[][] X;
    private int[] y;
    private LabeledData data;

    @BeforeEach
    void setUp() {
        X = new double[][] {{1}, {2}, {3}, {4}};
        y = new int[] {0,0,1,1};
        data = new LabeledData(X);
    }

    private void labelAll(){
        data.addLabeledRow(new int[] {0,1,2,3}, y);
    }

    @Test
    void constructor_noDataPoints_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledData(new double[][] {}));
    }

    @Test
    void constructor_zeroDimensionDataMatrix_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledData(new double[][] {{},{}}));
    }

    @Test
    void getRow_validIndexes_retrievesCorrectDataPoints() {
        for (int i = 0; i < X.length; i++) {
            assertEquals(X[i], data.getRow(i));
        }
    }

    @Test
    void getRow_OufOfBoundsIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.getRow(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.getRow(X.length));
    }

    @Test
    void getLabel_validIndexes_passes() {
        labelAll();
        for (int i = 0; i < y.length; i++) {
            assertEquals(y[i], data.getLabel(i));
        }
    }

    @Test
    void getLabel_OufOfBoundsIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.getLabel(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.getLabel(y.length));
    }

    @Test
    void getLabel_rowIndexNotInLabeledSet_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.getLabel(0));
    }

    @Test
    void setLabel_validRowIndexAndLabel_correctlyChangesLabel() {
        data.addLabeledRow(0, y[0]);
        data.setLabel(0, 1-y[0]);
        assertEquals(1-y[0], data.getLabel(0));
    }

    @Test
    void setLabel_invalidRowIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.setLabel(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> data.setLabel(y.length, 0));
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
    void isInLabeledSet_rowAddedToLabeledSet_returnsTrue() {
        data.addLabeledRow(0, y[0]);
        assertTrue(data.isInLabeledSet(0));
    }

    @Test
    void isInLabeledSet_rowNotAddedToLabeledSet_returnsFalse() {
        assertFalse(data.isInLabeledSet(0));
    }

    @Test
    void isInLabeledSet_invalidRowIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.isInLabeledSet(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.isInLabeledSet(y.length));
    }

    @Test
    void addLabeledRow_outOfBoundsRow_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.addLabeledRow(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> data.addLabeledRow(X.length, 0));
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

    @Test
    void removeLabeledRow_removeLabeledPoint_pointNotInLabeledSetAnymore() {
        data.addLabeledRow(0, 0);
        data.removeLabeledRow(0);
        assertFalse(data.isInLabeledSet(0));
    }

    @Test
    void removeLabeledRow_rowIndexOutOfBounds_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeLabeledRow(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeLabeledRow(X.length));
    }

    @Test
    void removeLabeledRow_removeRowNotInLabeledSet_throwsException() {
       assertThrows(IllegalArgumentException.class, () -> data.removeLabeledRow(0));
    }

    @Test
    void removeLabeledRow_outOfBoundsRow_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeLabeledRow(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeLabeledRow(X.length));
    }

    @Test
    void retrieveMinimizerOverUnlabeledData_emptyUnlabeledSet_throwsException() {
        labelAll();
        assertThrows(EmptyUnlabeledSetException.class, () -> data.retrieveMinimizerOverUnlabeledData(null));
    }

    @Test
    void retrieveMinimizerOverUnlabeledData_emptyLabeledSetAndDummyScoreFunction_returnsCorrectMinimizer() {
        data = new LabeledData(X);
        assertEquals(0, data.retrieveMinimizerOverUnlabeledData((dt, i) -> (double) i));
    }

    @Test
    void retrieveMinimizerOverUnlabeledData_nonEmptyLabeledSetAndDummyScoreFunction_returnsCorrectMinimizer() {
        data = new LabeledData(X);
        data.addLabeledRow(0, 0);
        assertEquals(1, data.retrieveMinimizerOverUnlabeledData((dt, i) -> (double) i));
    }

    @Test
    void sample_zeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.sample(0));
    }

    @Test
    void sample_negativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.sample(-1));
    }

    @Test
    void sample_sampleSizeEqualsToNumUnlabeledPoints_theOwnObjectIsReturned() {
        data = new LabeledData(X);
        assertTrue(data == data.sample(data.getNumUnlabeledRows()));
    }

    @Test
    void sample_sampleSizeLargerThanNumUnlabeledPoints_theOwnObjectIsReturned() {
        assertTrue(data == data.sample(data.getNumUnlabeledRows() + 1));
    }

    @Test
    void sample_sampleSizeEqualsInfinity_theOwnObjectIsReturned() {
        assertTrue(data == data.sample(Integer.MAX_VALUE));
    }
}