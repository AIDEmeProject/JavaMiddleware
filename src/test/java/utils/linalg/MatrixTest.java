package utils.linalg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        assertEquals(2, matrix1.numRows());
    }

    @Test
    void numCols_matrixWithThreeColumns_returnsThree() {
        assertEquals(3, matrix1.numCols());
    }

    @Test
    void get_negativeRowIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(-1, 0));
    }

    @Test
    void get_rowIndexEqualsNumRows_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(matrix1.numRows(), 0));
    }

    @Test
    void get_negativeColumnIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(0, -1));
    }

    @Test
    void get_columnIndexEqualsNumCols_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.get(0, matrix1.numCols()));
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
        assertThrows(RuntimeException.class, () -> matrix1.getRow(matrix1.numRows()));
    }

    @Test
    void getRow_runOverAllValidIndexes_correctVectorsReturned() {
        assertEquals(Vector.FACTORY.make(1, 2, 3), matrix1.getRow(0));
        assertEquals(Vector.FACTORY.make(4, 5, 6), matrix1.getRow(1));
    }

    /* *************************************
     *              ADDITION
     * ************************************
     */
//    @Test
//    void iScalarAdd_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iScalarAdd(1));
//    }
//
//
//    @Test
//    void iScalarAdd_addZero_resultIdenticalToInputMatrix() {
//        matrix1.iScalarAdd(0);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iScalarAdd_addOne_originalMatrixHasAllComponentsAddedOne() {
//        matrix1.iScalarAdd(1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 2, 3, 4, 5, 6, 7), matrix1);
//    }
//
//    @Test
//    void scalarAdd_addZero_resultIdenticalToInputMatrix() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarAdd(0));
//    }
//
//    @Test
//    void scalarAdd_addOne_resultMatrixHasAllComponentsAddedOne() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 2, 3, 4, 5, 6, 7), matrix1.scalarAdd(1));
//    }
//
//    @Test
//    void scalarAdd_addOne_originalMatrixRemainsUnchanged() {
//        matrix1.scalarAdd(1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iAdd_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iAdd(matrix2));
//    }
//
//    @Test
//    void iAdd_incompatibleNumberOfRows_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
//        assertThrows(RuntimeException.class, () -> matrix1.iAdd(matrix2));
//    }
//
//    @Test
//    void iAdd_incompatibleNumberOfColumns_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
//        assertThrows(RuntimeException.class, () -> matrix1.iAdd(matrix2));
//    }
//
//    @Test
//    void iAdd_addZeroMatrix_resultIdenticalToInputMatrix() {
//        matrix2 = Matrix.FACTORY.zeroslike(matrix1);
//        matrix1.iAdd(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iAdd_addCompatibleMatrix_originalMatrixHasAllComponentsAdded() {
//        matrix1.iAdd(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 11, 22, 33, 44, 55, 66), matrix1);
//    }

    @Test
    void add_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
        assertThrows(RuntimeException.class, () -> matrix1.add(matrix2));
    }

    @Test
    void add_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
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

    /* *************************************
     *              SUBTRACTION
     * ************************************
     */
//    @Test
//    void iScalarSubtract_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iScalarSubtract(1));
//    }
//
//    @Test
//    void iScalarSubtract_subtractZero_resultIdenticalToInputMatrix() {
//        matrix1.iScalarSubtract(0);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iScalarSubtract_subtractOne_originalMatrixHasAllComponentsSubtractedOne() {
//        matrix1.iScalarSubtract(1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 0, 1, 2, 3, 4, 5), matrix1);
//    }
//
//    @Test
//    void scalarSubtract_subtractZero_resultIdenticalToInputMatrix() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarSubtract(0));
//    }
//
//    @Test
//    void scalarSubtract_subtractOne_resultMatrixHasAllComponentsSubtractedOne() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 0, 1, 2, 3, 4, 5), matrix1.scalarSubtract(1));
//    }
//
//    @Test
//    void scalarSubtract_subtractOne_originalMatrixRemainsUnchanged() {
//        matrix1.scalarSubtract(1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iSubtract_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iSubtract(matrix2));
//    }
//
//    @Test
//    void iSubtract_incompatibleNumberOfRows_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
//        assertThrows(RuntimeException.class, () -> matrix1.iSubtract(matrix2));
//    }
//
//    @Test
//    void iSubtract_incompatibleNumberOfColumns_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
//        assertThrows(RuntimeException.class, () -> matrix1.iSubtract(matrix2));
//    }
//
//    @Test
//    void iSubtract_subtractZeroMatrix_resultIdenticalToInputMatrix() {
//        matrix2 = Matrix.FACTORY.zeroslike(matrix1);
//        matrix1.iSubtract(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iSubtract_subtractCompatibleMatrix_originalMatrixHasAllComponentsSubtracted() {
//        matrix1.iSubtract(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, -9, -18, -27, -36, -45, -54), matrix1);
//    }

    @Test
    void subtract_incompatibleNumberOfRows_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
        assertThrows(RuntimeException.class, () -> matrix1.subtract(matrix2));
    }

    @Test
    void subtract_incompatibleNumberOfColumns_throwsException() {
        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
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

    /* *************************************
     *            MULTIPLICATION
     * ************************************
     */
//    @Test
//    void iScalarMultiply_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iScalarMultiply(1));
//    }
//
//    @Test
//    void iScalarMultiply_multiplyByOne_resultIdenticalToInputMatrix() {
//        matrix1.iScalarMultiply(1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iScalarMultiply_multiplyByTwo_originalMatrixHasAllComponentsMultipliedByTwo() {
//        matrix1.iScalarMultiply(2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 2, 4, 6, 8, 10, 12), matrix1);
//    }
//
//    @Test
//    void scalarMultiply_multiplyByOne_resultIdenticalToInputMatrix() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarMultiply(1));
//    }

    @Test
    void scalarMultiply_multiplyByTwo_resultMatrixHasAllComponentsMultipliedByTwo() {
        assertEquals(Matrix.FACTORY.make(2, 3, 2, 4, 6, 8, 10, 12), matrix1.scalarMultiply(2));
    }

    @Test
    void scalarMultiply_multiplyByTwo_originalMatrixRemainsUnchanged() {
        matrix1.scalarMultiply(2);
        assertEquals(Matrix.FACTORY.make(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
    }

//    @Test
//    void iMultiply_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iMultiply(matrix2));
//    }
//
//    @Test
//    void iMultiply_incompatibleNumberOfRows_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
//        assertThrows(RuntimeException.class, () -> matrix1.iMultiply(matrix2));
//    }
//
//    @Test
//    void iMultiply_incompatibleNumberOfColumns_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
//        assertThrows(RuntimeException.class, () -> matrix1.iMultiply(matrix2));
//    }
//
//    @Test
//    void iMultiply_multiplyByOnesMatrix_resultIdenticalToInputMatrix() {
//        matrix2 = Matrix.FACTORY.zeros(2, 3, 1, 1, 1, 1, 1, 1);
//        matrix1.iMultiply(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iMultiply_multiplyByNonOnesMatrix_originalMatrixHasAllComponentsMultiplied() {
//        matrix1.iMultiply(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 10, 40, 90, 160, 250, 360), matrix1);
//    }

//    @Test
//    void multiply_incompatibleNumberOfRows_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
//        assertThrows(RuntimeException.class, () -> matrix1.scalarMultiply(matrix2));
//    }
//
//    @Test
//    void multiply_incompatibleNumberOfColumns_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
//        assertThrows(RuntimeException.class, () -> matrix1.scalarMultiply(matrix2));
//    }
//
//    @Test
//    void multiply_multiplyByOnesMatrix_resultIdenticalToInputMatrix() {
//        matrix2 = Matrix.FACTORY.zeros(2, 3, 1, 1, 1, 1, 1, 1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarMultiply(matrix2));
//    }
//
//    @Test
//    void multiply_multiplyByNonOnesMatrix_returnsExpectedResult() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 10, 40, 90, 160, 250, 360), matrix1.scalarMultiply(matrix2));
//    }
//
//    @Test
//    void multiply_multiplyByNonOnesMatrix_originalMatrixRemainsUnchanged() {
//        matrix1.scalarMultiply(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }

    @Test
    void multiply_vectorOfIncompatibleDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.multiply(Vector.FACTORY.make(1.0)));
    }

    @Test
    void multiply_zerosVector_returnsZeroVector() {
        Vector vector = Vector.FACTORY.zeros(matrix1.numCols());
        assertEquals(Vector.FACTORY.zeros(matrix1.numRows()), matrix1.multiply(vector));
    }

    @Test
    void multiply_nonZeroVector_returnsExpectedVector() {
        Vector vector = Vector.FACTORY.make(-1, 2, 0);
        assertEquals(Vector.FACTORY.make(3, 6), matrix1.multiply(vector));
    }

    @Test
    void multiply_matrixOfIncompatibleDimensions_throwsException() {
        assertThrows(RuntimeException.class, () -> matrix1.multiply(Matrix.FACTORY.zeros(1, 3)));
    }

    @Test
    void multiply_zerosMatrix_returnsZeroMatrix() {
        Matrix matrix = Matrix.FACTORY.zeros(3, 2);
        assertEquals(Matrix.FACTORY.zeros(2, 2), matrix1.multiply(matrix));
    }

    @Test
    void multiply_identityMatrix_returnsOriginalMatrix() {
        Matrix matrix = Matrix.FACTORY.identity(3);
        assertEquals(matrix1, matrix1.multiply(matrix));
    }

    @Test
    void multiply_customInputMatrix_returnsCorrectMatrixMultiplication() {
        Matrix matrix = Matrix.FACTORY.make(3, 2, -1, 2, 0, 0, 4, -5);
        assertEquals(Matrix.FACTORY.make(2, 2, 11, -13, 20, -22), matrix1.multiply(matrix));
    }

    /* ************************************
     *              DIVISION
     * ************************************
     */
//    @Test
//    void iScalarDivide_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iScalarDivide(1));
//    }
//
//    @Test
//    void iScalarDivide_divideByOne_resultIdenticalToInputMatrix() {
//        matrix1.iScalarDivide(1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iScalarDivide_divideByTwo_originalMatrixHasAllComponentsDividedByTwo() {
//        matrix1.iScalarDivide(2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 0.5, 1, 1.5, 2, 2.5, 3), matrix1);
//    }
//
//    @Test
//    void scalarDivide_divideByOne_resultIdenticalToInputMatrix() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1.scalarDivide(1));
//    }
//
//    @Test
//    void scalarDivide_divideByTwo_resultMatrixHasAllComponentsDividedByTwo() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 0.5, 1, 1.5, 2, 2.5, 3), matrix1.scalarDivide(2));
//    }
//
//    @Test
//    void scalarDivide_divideByTwo_originalMatrixRemainsUnchanged() {
//        matrix1.scalarDivide(2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iDivide_AnyValue_outputIsTheSameObjectAsThis() {
//        assertSame(matrix1, matrix1.iDivide(matrix2));
//    }
//
//    @Test
//    void iDivide_incompatibleNumberOfRows_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
//        assertThrows(RuntimeException.class, () -> matrix1.iDivide(matrix2));
//    }
//
//    @Test
//    void iDivide_incompatibleNumberOfColumns_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
//        assertThrows(RuntimeException.class, () -> matrix1.iDivide(matrix2));
//    }
//
//    @Test
//    void iDivide_divideByOnesMatrix_resultIdenticalToInputMatrix() {
//        matrix2 = Matrix.FACTORY.zeros(2, 3, 1, 1, 1, 1, 1, 1);
//        matrix1.iDivide(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
//
//    @Test
//    void iDivide_divideByNonOnesMatrix_originalMatrixHasAllComponentsDivided() {
//        matrix1.iDivide(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1), matrix1);
//    }
//
//    @Test
//    void divide_incompatibleNumberOfRows_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows() - 1, matrix1.numCols());
//        assertThrows(RuntimeException.class, () -> matrix1.divide(matrix2));
//    }
//
//    @Test
//    void divide_incompatibleNumberOfColumns_throwsException() {
//        matrix2 = Matrix.FACTORY.zeros(matrix1.numRows(), matrix1.numCols()-1);
//        assertThrows(RuntimeException.class, () -> matrix1.divide(matrix2));
//    }
//
//    @Test
//    void divide_divideByOnesMatrix_resultIdenticalToInputMatrix() {
//        matrix2 = Matrix.FACTORY.zeros(2, 3, 1, 1, 1, 1, 1, 1);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1.divide(matrix2));
//    }
//
//    @Test
//    void divide_divideByNonOnesMatrix_returnsExpectedResult() {
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1), matrix1.divide(matrix2));
//    }
//
//    @Test
//    void divide_divideByNonOnesMatrix_originalMatrixRemainsUnchanged() {
//        matrix1.divide(matrix2);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }

    /* *************************************
     *           UTILITY METHODS
     * *************************************
     */
    @Test
    void transpose_nonSquareMatrix_returnsCorrectTranspose() {
        assertEquals(Matrix.FACTORY.make(3, 2, 1, 4, 2, 5, 3, 6), matrix1.transpose());
    }

    @Test
    void toArray_anyMatrix_returnsArrayOfMatrixEntries() {
        double[][] array = matrix1.toArray();
        assertEquals(matrix1.numRows(), array.length);
        assertArrayEquals(new double[] {1, 2, 3}, array[0]);
        assertArrayEquals(new double[] {4, 5, 6}, array[1]);
    }

    @Test
    void toArray_anyMatrix_changingOutputArrayDoesNotModifyMatrix() {
        double[][] copy = matrix1.toArray();
        copy[0][0] = 1000;
        assertEquals(1, matrix1.get(0, 0));
    }

//    @Test
//    void clone_anyMatrix_returnsIdenticalMatrix() {
//        assertEquals(matrix1, matrix1.clone());
//    }
//
//    @Test
//    void clone_anyMatrix_changingCloneDoesNotModifyOriginalMatrix() {
//        Matrix clone = matrix1.clone();
//        clone.iScalarAdd(10);
//        assertEquals(Matrix.FACTORY.zeros(2, 3, 1, 2, 3, 4, 5, 6), matrix1);
//    }
}
