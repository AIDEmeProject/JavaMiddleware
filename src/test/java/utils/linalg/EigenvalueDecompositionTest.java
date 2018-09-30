package utils.linalg;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EigenvalueDecompositionTest {
    private Matrix matrix;
    private EigenvalueDecomposition decomposition;

    @BeforeEach
    void setUp() {
        matrix = Matrix.FACTORY.make(2, 2, 1, 0, 0, -1);
        decomposition = new EigenvalueDecomposition(matrix);
    }

    @Test
    void constructor_nonSquareMatrix_throwsException() {
        matrix = Matrix.FACTORY.make(2, 1, 10.0, 20.0);
        assertThrows(RuntimeException.class, () -> new EigenvalueDecomposition(matrix));
    }

    @Test
    void constructor_matrixWithoutRealDecomposition_throwsException() {
        matrix = Matrix.FACTORY.make(2, 2, 0, -1, 1, 0);
        assertThrows(RuntimeException.class, () -> new EigenvalueDecomposition(matrix));
    }

    @Test
    void getEigenvalue_negativeIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvalue(-1));
    }

    @Test
    void getEigenvalue_indexEqualToMatrixDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvalue(matrix.numRows()));
    }

    @Test
    void getEigenvector_negativeIndex_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvector(-1));
    }

    @Test
    void getEigenvector_indexEqualToMatrixDimension_throwsException() {
        assertThrows(RuntimeException.class, () -> decomposition.getEigenvector(matrix.numRows()));
    }

    @Test
    void getEigenvalue_diagonalMatrix_returnsEachElementOfDiagonalInOrder() {
        assertEquals(1, decomposition.getEigenvalue(0));
        assertEquals(-1, decomposition.getEigenvalue(1));
    }

    @Test
    void getEigenvector_diagonalMatrix_returnsCanonicalVectors() {
        assertEquals(Vector.FACTORY.make(1, 0), decomposition.getEigenvector(0));
        assertEquals(Vector.FACTORY.make(0, 1), decomposition.getEigenvector(1));
    }

    @Test
    void getEigenvector_nonDiagonalMatrix_characteristicEquationsAreSatisfied() {
        matrix = Matrix.FACTORY.make(2, 2, 0, 1, 1, 0);
        decomposition = new EigenvalueDecomposition(matrix);
        assertEigenvectorSatisfyCharacteristicEquation(0);
        assertEigenvectorSatisfyCharacteristicEquation(1);
    }

    private void assertEigenvectorSatisfyCharacteristicEquation(int i) {
        Vector leftHandSide = matrix.multiply(decomposition.getEigenvector(i));
        Vector rightHandSide = decomposition.getEigenvector(i).scalarMultiply(decomposition.getEigenvalue(i));
        assertTrue(leftHandSide.equals(rightHandSide, 1e-10));
    }
}