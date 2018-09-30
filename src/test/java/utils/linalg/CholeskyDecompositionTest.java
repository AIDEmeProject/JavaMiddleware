package utils.linalg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CholeskyDecompositionTest {
    @Test
    void constructor_nonSquareMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 1, 10.0, 20.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void constructor_NonSymmetricMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 10.0, 20.0, 30.0, 40.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void constructor_SymmetricButNotPositiveDefiniteMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 1.0, 0, 0, -1.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void constructor_PositiveSemiDefiniteMatrix_throwsException() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 1.0, 0, 0, 0.0);
        assertThrows(RuntimeException.class, () -> new CholeskyDecomposition(matrix));
    }

    @Test
    void getL_PositiveDefiniteDiagonalMatrix_returnsSquareRoot() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 4.0, 0, 0, 9.0);
        assertEquals(Matrix.FACTORY.make(2, 2, 2.0, 0, 0, 3.0), new CholeskyDecomposition(matrix).getL());
    }

    @Test
    void getL_PositiveDefiniteNonDiagonalMatrix_returnsCorrectCholeskyFactorization() {
        Matrix matrix = Matrix.FACTORY.make(2, 2, 4.0, -2.0, -2.0, 10.0);
        assertEquals(Matrix.FACTORY.make(2, 2, 2.0, 0.0, -1.0, 3.0), new CholeskyDecomposition(matrix).getL());
    }
}