package utils.linalg;

import explore.statistics.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;

import static org.junit.jupiter.api.Assertions.*;

public class MatrixTest {
    private Matrix matrix1, matrix2;
    
    @BeforeEach
    void setUp() {
        matrix1 = Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6);
        matrix2 = Matrix.FACTORY.make(2, 3, 10, 20, 30, 40, 50, 60);
    }

    @Test
    void make_emptyArray_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(new double[0][]));
    }

    @Test
    void make_allRowsAreEmpty_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(new double[2][0]));
    }

    @Test
    void make_rowsHaveDistinctLengths_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(new double[][] {{1}, {2, 3}}));
    }

    @Test
    void make_zeroNumberOfRows_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(0, 1));
    }

    @Test
    void make_negativeNumberOfRows_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(-1, 1));
    }

    @Test
    void make_zeroNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(1, 0));
    }

    @Test
    void make_negativeNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(1, -1));
    }

    @Test
    void make_matrixDimensionsAndValueHaveIncompatibleLengths_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.make(1, 1, 10.0, 20.0));
    }

    @Test
    void make_valueInputParameters_constructsExpectedMatrix() {
        assertEquals(Matrix.FACTORY.make(new double[][] {{1}, {2}}), Matrix.FACTORY.make(2, 1, 1.0, 2.0));
    }

    @Test
    void zeros_zeroNumberOfRows_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.zeros(0, 1));
    }

    @Test
    void zeros_negativeNumberOfRows_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.zeros(-1, 1));
    }

    @Test
    void zeros_zeroNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.zeros(1, 0));
    }

    @Test
    void zeros_negativeNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> Matrix.FACTORY.zeros(1, -1));
    }

    @Test
    void zeroslike_TwoByThreeDimensionalInputMatrix_returnsTwoByThreeDimensionalZeroMatrix() {
        assertEquals(Matrix.FACTORY.zeros(2, 3), Matrix.FACTORY.zeroslike(matrix1));
    }

    @Test
    void numRows_matrixWithTwoRows_returnsTwo() {
        assertEquals(2, matrix1.rows());
    }

    @Test
    void numCols_matrixWithThreeColumns_returnsThree() {
        assertEquals(3, matrix1.cols());
    }

    @Test
    void get_negativeRowIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(-1, 0));
    }

    @Test
    void get_rowIndexEqualsNumRows_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(matrix1.rows(), 0));
    }

    @Test
    void get_negativeColumnIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(0, -1));
    }

    @Test
    void get_columnIndexEqualsNumCols_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(0, matrix1.cols()));
    }

    @Test
    void get_runOverAllValidIndexes_correctValuesReturned() {
        assertEquals(1, matrix1.get(0,0));
        assertEquals(2, matrix1.get(0,1));
        assertEquals(3, matrix1.get(0,2));
        assertEquals(4, matrix1.get(1,0));
        assertEquals(5, matrix1.get(1,1));
        assertEquals(6, matrix1.get(1,2));
    }

    @Test
    void getRow_negativeIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.getRow(-1));
    }

    @Test
    void getRow_indexEqualsNumRows_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.getRow(matrix1.rows()));
    }

    @Test
    void getRow_runOverAllValidIndexes_correctVectorsReturned() {
        assertEquals(Vector.FACTORY.make(1, 2, 3), matrix1.getRow(0));
        assertEquals(Vector.FACTORY.make(4, 5, 6), matrix1.getRow(1));
    }

    @Test
    void getRows_emtpyArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> matrix1.getRows());
    }

    @Test
    void getRows_outOfBoundsIndex_throwsException() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> matrix1.getRows(0, -1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> matrix1.getRows(0, matrix1.rows()));
    }

    @Test
    void getRows_compatibleIndexes_returnsExpectedMatrix() {
        assertEquals(Matrix.FACTORY.make(2, 3, 4, 5, 6, 1, 2, 3), matrix1.getRows(1, 0));
    }

    @Test
    void getRowSlice_negativeFromIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> matrix1.getRowSlice(-1, 0));
    }

    @Test
    void getRowSlice_ToIndexLargerThanNumberOfRows_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> matrix1.getRowSlice(0, matrix1.rows()+1));
    }

    @Test
    void getRowSlice_FromIndexEqualsToIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> matrix1.getRowSlice(0, 0));
    }

    @Test
    void getRowSlice_compatibleIndexes_returnsExpectedMatrix() {
        assertEquals(Matrix.FACTORY.make(1, 3, 4, 5, 6), matrix1.getRowSlice(1, 2));
    }

    @Test
    void swapRows_negativeIndex_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> matrix1.swapRows(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> matrix1.swapRows(0, -1));
    }

    @Test
    void swapRows_indexEqualsToNumberOfRows_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> matrix1.swapRows(matrix1.rows(), 0));
        assertThrows(IllegalArgumentException.class, () -> matrix1.swapRows(0, matrix1.rows()));
    }

    @Test
    void swapRows_sameRowIndexes_matrixRemainsUnchanged() {
        Matrix copy = matrix1.copy();
        matrix1.swapRows(0, 0);
        assertEquals(copy, matrix1);
    }

    @Test
    void swapRows_differentRowIndexes_originalMatrixModifiedAsExpected() {
        matrix1.swapRows(0, 1);
        assertEquals(Matrix.FACTORY.make(2, 3, 4, 5, 6, 1, 2, 3), matrix1);
    }

    /* *************************************
     *              ADDITION
     * ************************************
     */
    @Test
    void iScalarAdd_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iScalarAdd(1));
    }


    @Test
    void iScalarAdd_addZero_resultIdenticalToInputMatrix() {
        matrix1.iScalarAdd(0);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iScalarAdd_addOne_originalMatrixHasAllComponentsAddedOne() {
        matrix1.iScalarAdd(1);
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 3, 4, 5, 6, 7), matrix1);
    }

    @Test
    void scalarAdd_addZero_resultIdenticalToInputMatrix() {
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarAdd(0));
    }

    @Test
    void scalarAdd_addOne_resultMatrixHasAllComponentsAddedOne() {
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 3, 4, 5, 6, 7), matrix1.scalarAdd(1));
    }

    @Test
    void scalarAdd_addOne_originalMatrixRemainsUnchanged() {
        matrix1.scalarAdd(1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iAdd_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iAdd(matrix2));
    }

    @Test
    void iAdd_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.iAdd(matrix2));
    }

    @Test
    void iAdd_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.iAdd(matrix2));
    }

    @Test
    void iAdd_addZeroMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.zeroslike(matrix1);
        matrix1.iAdd(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iAdd_addCompatibleMatrix_originalMatrixHasAllComponentsAdded() {
        matrix1.iAdd(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 11, 22, 33, 44, 55, 66), matrix1);
    }

    @Test
    void add_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.add(matrix2));
    }

    @Test
    void add_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.add(matrix2));
    }

    @Test
    void add_addZeroMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.zeroslike(matrix1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.add(matrix2));
    }

    @Test
    void add_addNonZeroMatrix_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, 11, 22, 33, 44, 55, 66), matrix1.add(matrix2));
    }

    @Test
    void add_addNonZeroMatrix_originalMatrixRemainsUnchanged() {
        matrix1.add(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void addRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.addRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void addRow_nonZeroCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, 0, 4, 7, 3, 7, 10), matrix1.addRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void addRow_nonZeroCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.addRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iAddRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iAddRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iAddRow_nonZeroCompatibleVector_originalMatrixModified() {
        matrix1.iAddRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, 0, 4, 7, 3, 7, 10), matrix1);
    }

    @Test
    void iAddRow_nonZeroCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iAddRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void addColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.addColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void addColumn_nonZeroCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, 0, 1, 2, 6, 7, 8), matrix1.addColumn(Vector.FACTORY.make(-1, 2)));
    }

    @Test
    void addColumn_nonZeroCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.addColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iAddColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iAddColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iAddColumn_nonZeroCompatibleVector_originalMatrixModified() {
        matrix1.iAddColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, 0, 1, 2, 6, 7, 8), matrix1);
    }

    @Test
    void iAddColumn_nonZeroCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iAddColumn(Vector.FACTORY.make(-1, 2)));
    }

    /* *************************************
     *              SUBTRACTION
     * ************************************
     */
    @Test
    void iScalarSubtract_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iScalarSubtract(1));
    }

    @Test
    void iScalarSubtract_subtractZero_resultIdenticalToInputMatrix() {
        matrix1.iScalarSubtract(0);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iScalarSubtract_subtractOne_originalMatrixHasAllComponentsSubtractedOne() {
        matrix1.iScalarSubtract(1);
        assertEquals(Matrix.FACTORY.make(2, 3, 0, 1, 2, 3, 4, 5), matrix1);
    }

    @Test
    void scalarSubtract_subtractZero_resultIdenticalToInputMatrix() {
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarSubtract(0));
    }

    @Test
    void scalarSubtract_subtractOne_resultMatrixHasAllComponentsSubtractedOne() {
        assertEquals(Matrix.FACTORY.make(2, 3, 0, 1, 2, 3, 4, 5), matrix1.scalarSubtract(1));
    }

    @Test
    void scalarSubtract_subtractOne_originalMatrixRemainsUnchanged() {
        matrix1.scalarSubtract(1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iSubtract_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iSubtract(matrix2));
    }

    @Test
    void iSubtract_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.iSubtract(matrix2));
    }

    @Test
    void iSubtract_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.iSubtract(matrix2));
    }

    @Test
    void iSubtract_subtractZeroMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.zeroslike(matrix1);
        matrix1.iSubtract(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iSubtract_subtractCompatibleMatrix_originalMatrixHasAllComponentsSubtracted() {
        matrix1.iSubtract(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, -9, -18, -27, -36, -45, -54), matrix1);
    }

    @Test
    void subtract_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.subtract(matrix2));
    }

    @Test
    void subtract_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.subtract(matrix2));
    }

    @Test
    void subtract_subtractZeroMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.zeroslike(matrix1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.subtract(matrix2));
    }

    @Test
    void subtract_subtractNonZeroMatrix_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, -9, -18, -27, -36, -45, -54), matrix1.subtract(matrix2));
    }

    @Test
    void subtract_subtractNonZeroMatrix_originalMatrixRemainsUnchanged() {
        matrix1.subtract(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void subtractRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.subtractRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void subtractRow_nonZeroCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 0, -1, 5, 3, 2), matrix1.subtractRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void subtractRow_nonZeroCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.subtractRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iSubtractRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iSubtractRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iSubtractRow_nonZeroCompatibleVector_originalMatrixModified() {
        matrix1.iSubtractRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 0, -1, 5, 3, 2), matrix1);
    }

    @Test
    void iSubtractRow_nonZeroCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iSubtractRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void subtractColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.subtractColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void subtractColumn_nonZeroCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 3, 4, 2, 3, 4), matrix1.subtractColumn(Vector.FACTORY.make(-1, 2)));
    }

    @Test
    void subtractColumn_nonZeroCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.subtractColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iSubtractColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iSubtractColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iSubtractColumn_nonZeroCompatibleVector_originalMatrixModified() {
        matrix1.iSubtractColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 3, 4, 2, 3, 4), matrix1);
    }

    @Test
    void iSubtractColumn_nonZeroCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iSubtractColumn(Vector.FACTORY.make(-1, 2)));
    }

    /* *************************************
     *            MULTIPLICATION
     * ************************************
     */
    @Test
    void iScalarMultiply_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iScalarMultiply(1));
    }

    @Test
    void iScalarMultiply_multiplyByOne_resultIdenticalToInputMatrix() {
        matrix1.iScalarMultiply(1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iScalarMultiply_multiplyByTwo_originalMatrixHasAllComponentsMultipliedByTwo() {
        matrix1.iScalarMultiply(2);
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 4, 6, 8, 10, 12), matrix1);
    }

    @Test
    void scalarMultiply_multiplyByOne_resultIdenticalToInputMatrix() {
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarMultiply(1));
    }

    @Test
    void scalarMultiply_multiplyByTwo_resultMatrixHasAllComponentsMultipliedByTwo() {
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 4, 6, 8, 10, 12), matrix1.scalarMultiply(2));
    }

    @Test
    void scalarMultiply_multiplyByTwo_originalMatrixRemainsUnchanged() {
        matrix1.scalarMultiply(2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iMultiply_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iMultiply(matrix2));
    }

    @Test
    void iMultiply_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.iMultiply(matrix2));
    }

    @Test
    void iMultiply_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.iMultiply(matrix2));
    }

    @Test
    void iMultiply_multiplyByOnesMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.make(2, 3, 1, 1, 1, 1, 1, 1);
        matrix1.iMultiply(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iMultiply_multiplyByNonOnesMatrix_originalMatrixHasAllComponentsMultiplied() {
        matrix1.iMultiply(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 10, 40, 90, 160, 250, 360), matrix1);
    }

    @Test
    void multiply_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.multiply(matrix2));
    }

    @Test
    void multiply_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.multiply(matrix2));
    }

    @Test
    void multiply_multiplyByOnesMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.make(2, 3, 1, 1, 1, 1, 1, 1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.multiply(matrix2));
    }

    @Test
    void multiply_multiplyByNonOnesMatrix_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, 10, 40, 90, 160, 250, 360), matrix1.multiply(matrix2));
    }

    @Test
    void multiply_multiplyByNonOnesMatrix_originalMatrixRemainsUnchanged() {
        matrix1.multiply(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void multiplyRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.multiplyRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void multiplyRow_nonOnesCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, -1, 4, 12, -4, 10, 24), matrix1.multiplyRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void multiplyRow_nonOnesCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.multiplyRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iMultiplyRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iMultiplyRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iMultiplyRow_nonOnesCompatibleVector_originalMatrixModified() {
        matrix1.iMultiplyRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, -1, 4, 12, -4, 10, 24), matrix1);
    }

    @Test
    void iMultiplyRow_nonOnesCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iMultiplyRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void multiplyColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.multiplyColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void multiplyColumn_nonOnesCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, -1, -2, -3, 8, 10, 12), matrix1.multiplyColumn(Vector.FACTORY.make(-1, 2)));
    }

    @Test
    void multiplyColumn_nonOnesCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.multiplyColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iMultiplyColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iMultiplyColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iMultiplyColumn_nonOnesCompatibleVector_originalMatrixModified() {
        matrix1.iMultiplyColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, -1, -2, -3, 8, 10, 12), matrix1);
    }

    @Test
    void iMultiplyColumn_nonOnesCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iMultiplyColumn(Vector.FACTORY.make(-1, 2)));
    }

    @Test
    void multiply_vectorOfIncompatibleDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.multiply(Vector.FACTORY.make(1.0)));
    }

    @Test
    void multiply_zerosVector_returnsZeroVector() {
        Vector vector = Vector.FACTORY.zeros(matrix1.cols());
        assertEquals(Vector.FACTORY.zeros(matrix1.rows()), matrix1.multiply(vector));
    }

    @Test
    void multiply_nonZeroVector_returnsExpectedVector() {
        Vector vector = Vector.FACTORY.make(-1, 2, 0);
        assertEquals(Vector.FACTORY.make(3, 6), matrix1.multiply(vector));
    }

    /* ************************************
     *    MATRIX-MATRIX MULTIPLICATION
     * ************************************
     */
    @Test
    void multiplyMatrix_matrixOfIncompatibleDimensions_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.matrixMultiply(Matrix.FACTORY.zeros(1, 3)));
    }

    @Test
    void multiplyMatrix_zerosMatrix_returnsZeroMatrix() {
        Matrix matrix = Matrix.FACTORY.zeros(3, 2);
        assertEquals(Matrix.FACTORY.zeros(2, 2), matrix1.matrixMultiply(matrix));
    }

    @Test
    void multiplyMatrix_identityMatrix_returnsOriginalMatrix() {
        Matrix matrix = Matrix.FACTORY.identity(3);
        assertEquals(matrix1, matrix1.matrixMultiply(matrix));
    }

    @Test
    void multiplyMatrix_customInputMatrix_returnsCorrectMatrixMultiplication() {
        Matrix matrix = Matrix.FACTORY.make(3, 2, -1, 2, 0, 0, 4, -5);
        assertEquals(Matrix.FACTORY.make(2, 2, 11, -13, 20, -22), matrix1.matrixMultiply(matrix));
    }

    @Test
    void multiplyTranspose_differentNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.multiplyTranspose(Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()+1)));
    }

    @Test
    void multiplyTranspose_compatibleMatrices_returnsCorrectMatrixMultiplication() {
        assertEquals(Matrix.FACTORY.make(2, 2, 140, 320, 320, 770), matrix1.multiplyTranspose(matrix2));
    }

    /* ************************************
     *              DIVISION
     * ************************************
     */
    @Test
    void iScalarDivide_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iScalarDivide(1));
    }

    @Test
    void iScalarDivide_divideByOne_resultIdenticalToInputMatrix() {
        matrix1.iScalarDivide(1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iScalarDivide_divideByTwo_originalMatrixHasAllComponentsDividedByTwo() {
        matrix1.iScalarDivide(2);
        assertEquals(Matrix.FACTORY.make(2, 3, 0.5, 1, 1.5, 2, 2.5, 3), matrix1);
    }

    @Test
    void scalarDivide_divideByOne_resultIdenticalToInputMatrix() {
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarDivide(1));
    }

    @Test
    void scalarDivide_divideByTwo_resultMatrixHasAllComponentsDividedByTwo() {
        assertEquals(Matrix.FACTORY.make(2, 3, 0.5, 1, 1.5, 2, 2.5, 3), matrix1.scalarDivide(2));
    }

    @Test
    void scalarDivide_divideByTwo_originalMatrixRemainsUnchanged() {
        matrix1.scalarDivide(2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iDivide_AnyValue_outputIsTheSameObjectAsThis() {
        assertSame(matrix1, matrix1.iDivide(matrix2));
    }

    @Test
    void iDivide_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.iDivide(matrix2));
    }

    @Test
    void iDivide_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.iDivide(matrix2));
    }

    @Test
    void iDivide_divideByOnesMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.make(2, 3, 1, 1, 1, 1, 1, 1);
        matrix1.iDivide(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iDivide_divideByNonOnesMatrix_originalMatrixHasAllComponentsDivided() {
        matrix1.iDivide(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1), matrix1);
    }

    @Test
    void divide_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows() - 1, matrix1.cols());
        assertThrows(RuntimeException.class, () -> matrix1.divide(matrix2));
    }

    @Test
    void divide_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.rows(), matrix1.cols()-1);
        assertThrows(RuntimeException.class, () -> matrix1.divide(matrix2));
    }

    @Test
    void divide_divideByOnesMatrix_resultIdenticalToInputMatrix() {
        matrix2 = Matrix.FACTORY.make(2, 3, 1, 1, 1, 1, 1, 1);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1.divide(matrix2));
    }

    @Test
    void divide_divideByNonOnesMatrix_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1), matrix1.divide(matrix2));
    }

    @Test
    void divide_divideByNonOnesMatrix_originalMatrixRemainsUnchanged() {
        matrix1.divide(matrix2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void divideRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.divideRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void divideRow_nonOnesCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, -1, 1, 0.75, -4, 2.5, 1.5), matrix1.divideRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void divideRow_nonOnesCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.divideRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iDivideRow_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iDivideRow(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iDivideRow_nonOnesCompatibleVector_originalMatrixModified() {
        matrix1.iDivideRow(Vector.FACTORY.make(-1, 2, 4));
        assertEquals(Matrix.FACTORY.make(2, 3, -1, 1, 0.75, -4, 2.5, 1.5), matrix1);
    }

    @Test
    void iDivideRow_nonOnesCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iDivideRow(Vector.FACTORY.make(-1, 2, 4)));
    }

    @Test
    void divideColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.divideColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void divideColumn_nonOnesCompatibleVector_returnsExpectedResult() {
        assertEquals(Matrix.FACTORY.make(2, 3, -1, -2, -3, 2, 2.5, 3), matrix1.divideColumn(Vector.FACTORY.make(-1, 2)));
    }

    @Test
    void divideColumn_nonOnesCompatibleVector_originalMatrixRemainsUnchanged() {
        matrix1.divideColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

    @Test
    void iDivideColumn_incompatibleNumberOfColumns_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.iDivideColumn(Vector.FACTORY.zeros(10)));
    }

    @Test
    void iDivideColumn_nonOnesCompatibleVector_originalMatrixModified() {
        matrix1.iDivideColumn(Vector.FACTORY.make(-1, 2));
        assertEquals(Matrix.FACTORY.make(2, 3, -1, -2, -3, 2, 2.5, 3), matrix1);
    }

    @Test
    void iDivideColumn_nonOnesCompatibleVector_returnsSameObject() {
        assertSame(matrix1, matrix1.iDivideColumn(Vector.FACTORY.make(-1, 2)));
    }

    /* *************************************
     *           UTILITY METHODS
     * *************************************
     */

    @Test
    void columnStatistics_rectangularMatrix_returnsExpectedValues() {
        Statistics[] expected = new Statistics[matrix1.cols()];
        expected[0] = new Statistics("column_0", 2.5, 4.5, 2);
        expected[1] = new Statistics("column_1", 3.5, 4.5, 2);
        expected[2] = new Statistics("column_2", 4.5, 4.5, 2);
        assertArrayEquals(expected, matrix1.columnStatistics());
    }

    @Test
    void getRowSquaredNorms_rectangularMatrix_returnsExpectedValues() {
        assertEquals(Vector.FACTORY.make(14, 77), matrix1.getRowSquaredNorms());
    }

    @Test
    void transpose_nonSquareMatrix_returnsCorrectTranspose() {
        assertEquals(Matrix.FACTORY.make(3, 2, 1, 4, 2, 5, 3, 6), matrix1.transpose());
    }

    @Test
    void addBiasColumn_rectangularMatrix_onesColumnCorrectlyAppended() {
        assertEquals(Matrix.FACTORY.make(2, 4, 1, 1, 2, 3, 1, 4, 5, 6), matrix1.addBiasColumn());
    }

    @Test
    void toArray_anyMatrix_returnsArrayOfMatrixEntries() {
        double[][] array = matrix1.toArray();
        assertEquals(matrix1.rows(), array.length);
        assertArrayEquals(new double[] {1, 2, 3}, array[0]);
        assertArrayEquals(new double[] {4, 5, 6}, array[1]);
    }

    @Test
    void toArray_anyMatrix_changingOutputArrayDoesNotModifyMatrix() {
        double[][] copy = matrix1.toArray();
        copy[0][0] = 1000;
        assertEquals(1, matrix1.get(0, 0));
    }

    @Test
    void copy_anyMatrix_returnsIdenticalMatrix() {
        assertEquals(matrix1, matrix1.copy());
    }

    @Test
    void copy_anyMatrix_changingCloneDoesNotModifyOriginalMatrix() {
        Matrix clone = matrix1.copy();
        clone.iScalarAdd(10);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }
}
