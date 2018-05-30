package data;

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
        data = new LabeledData(X, y);
    }

    @Test
    void constructor_IncompatibleSizesForXandY_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledData(X, new int[] {0,1,0}));
    }

    @Test
    void constructor_noDataPoints_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledData(new double[][] {}, new int[] {}));
    }

    @Test
    void constructor_zeroDimensionDataMatrix_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledData(new double[][] {{},{}}, new int[] {0,1}));
    }

    @Test
    void constructor_invalidLabels_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new LabeledData(X, new int[] {0,1,0,2}));
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
    void setLabel_validRowIndexAndLabel_correctlyChangesLabel() {
        data.setLabel(0, 1);
        assertEquals(1, data.getLabel(0));
    }

    @Test
    void setLabel_invalidRowIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.setLabel(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> data.setLabel(y.length, 0));
    }

    @Test
    void setLabel_invalidLabel_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> data.setLabel(0, -1));
        assertThrows(IllegalArgumentException.class, () -> data.setLabel(0, 2));
    }

    @Test
    void getLabeledRows_noLabeledPointsAdded_labeledRowsIsEmpty() {
        assertTrue(data.getLabeledRows().isEmpty());
    }

    @Test
    void getNumLabeledRows_noLabeledPointsAdded_returnsZeroLabeledPoints() {
        assertEquals(0, data.getNumLabeledRows());
    }

    @Test
    void getNumLabeledRows_addedLabeledPoints_returnsNumberOfLabeledPoints() {
        data.addLabeledRow(0);
        data.addLabeledRow(3);
        assertEquals(2, data.getNumLabeledRows());
    }

    @Test
    void getNumUnlabeledRows_noLabeledPointsAdded_returnNumOfDataPoints() {
        assertEquals(X.length, data.getNumUnlabeledRows());
    }

    @Test
    void getNumUnlabeledRows_addedLabeledPoints_returnNumOfUnlabeledPoints() {
        data.addLabeledRow(0);
        assertEquals(X.length - 1, data.getNumUnlabeledRows());
    }

    @Test
    void isInLabeledSet_inLabeledSetRow_returnsTrue() {
        data.addLabeledRow(0);
        assertTrue(data.isInLabeledSet(0));
    }

    @Test
    void isInLabeledSet_invalidRowIndex_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.isInLabeledSet(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.isInLabeledSet(y.length));
    }

    @Test
    void addLabeledRow_addedLabeledPoint_labeledSetContainsAddedPoint() {
        data.addLabeledRow(2);
        assertTrue(data.getLabeledRows().contains(2));
    }

    @Test
    void addLabeledRow_outOfBoundsRow_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.addLabeledRow(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.addLabeledRow(X.length));
    }

    @Test
    void addLabeledRow_addedTheSameLabeledPointTwice_secondInsertionIsDiscarded() {
        data.addLabeledRow(2);
        data.addLabeledRow(2);
        assertEquals(1, data.getNumLabeledRows());
    }

    @Test
    void removeLabeledRow_removeLabeledPoint_pointNotInLabeledSetAnymore() {
        data.addLabeledRow(0);
        data.removeLabeledRow(0);
        assertFalse(data.isInLabeledSet(0));
    }

    @Test
    void removeLabeledRow_removeRowNotInLabeledSet_noExceptionThrown() {
        data.removeLabeledRow(0);
    }

    @Test
    void removeLabeledRow_outOfBoundsRow_throwsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeLabeledRow(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> data.removeLabeledRow(X.length));
    }
}