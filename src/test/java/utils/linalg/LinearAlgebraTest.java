package utils.linalg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearAlgebraTest {

    @Test
    void normalize_zeroVector_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> LinearAlgebra.normalize(new double[2], 1));
    }

    @Test
    void normalize_zeroNewNorm_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> LinearAlgebra.normalize(new double[] {1, 0}, 0));
    }

    @Test
    void normalize_negativeNewNorm_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> LinearAlgebra.normalize(new double[] {1, 0}, -1));
    }

    @Test
    void normalize_newNormEqualToOne_returnsExpectedVector() {
        assertArrayEquals(new double[] {1, 0}, LinearAlgebra.normalize(new double[] {10, 0}, 1));
        assertArrayEquals(new double[] {0, -1}, LinearAlgebra.normalize(new double[] {0, -10}, 1));
        assertArrayEquals(new double[] {-1/Math.sqrt(2), 1/Math.sqrt(2)}, LinearAlgebra.normalize(new double[] {-1, 1}, 1));
    }

    @Test
    void normalize_newNormEqualToTwo_returnsExpectedVector() {
        assertArrayEquals(new double[] {2, 0}, LinearAlgebra.normalize(new double[] {10, 0}, 2));
        assertArrayEquals(new double[] {0, -2}, LinearAlgebra.normalize(new double[] {0, -10}, 2));
        assertArrayEquals(new double[] {-Math.sqrt(2), Math.sqrt(2)}, LinearAlgebra.normalize(new double[] {-1, 1}, 2), 1e-10);
    }

    @Test
    void norm_emptyVector_returnsZero() {
        assertEquals(0, LinearAlgebra.norm(new double[] {}));
    }

    @Test
    void norm_zeroVector_returnsZero() {
        assertEquals(0, LinearAlgebra.norm(new double[] {0,0}));
    }

    @Test
    void norm_nonZeroVector_returnsCorrectValue() {
        assertEquals(1., LinearAlgebra.norm(new double[]{1, 0}));
        assertEquals(1., LinearAlgebra.norm(new double[]{0, -1}));
        assertEquals(Math.sqrt(2.), LinearAlgebra.norm(new double[]{1, 1}));
        assertEquals(Math.sqrt(2.), LinearAlgebra.norm(new double[]{-1, -1}));
    }

    @Test
    void sqNorm_emptyVector_returnsZero() {
        assertEquals(0, LinearAlgebra.sqNorm(new double[] {}));
    }

    @Test
    void sqNorm_zeroVector_returnsZero() {
        assertEquals(0, LinearAlgebra.sqNorm(new double[] {0,0}));
    }

    @Test
    void sqNorm_nonZeroVector_returnsCorrectValue() {
        assertEquals(1., LinearAlgebra.sqNorm(new double[]{1, 0}));
        assertEquals(1., LinearAlgebra.sqNorm(new double[]{0, -1}));
        assertEquals(2., LinearAlgebra.sqNorm(new double[]{1, 1}));
        assertEquals(2., LinearAlgebra.sqNorm(new double[]{-1, -1}));
    }

    @Test
    void truncateOrPaddleWithZeros_negativeSize_throwsException() {
        int size = -1;
        double[] array = new double[] {1, 2, 3, 4, 5};
        assertThrows(NegativeArraySizeException.class, () -> LinearAlgebra.truncateOrPaddleWithZeros(array, size));
    }

    @Test
    void truncateOrPaddleWithZeros_zeroSize_returnsEmptyArray() {
        int size = 0;
        double[] array = new double[] {1, 2, 3, 4, 5};
        assertArrayEquals(new double[] {}, LinearAlgebra.truncateOrPaddleWithZeros(array, size));
    }

    @Test
    void truncateOrPaddleWithZeros_sizeLessThanArraySize_returnsCorrectTruncatedArray() {
        int size = 3;
        double[] array = new double[] {1, 2, 3, 4, 5};
        assertArrayEquals(new double[] {1, 2, 3}, LinearAlgebra.truncateOrPaddleWithZeros(array, size));
    }

    @Test
    void truncateOrPaddleWithZeros_sizeEqualsArraySize_theInputArrayIsReturnedWithoutCopying() {
        int size = 5;
        double[] array = new double[] {1, 2, 3, 4, 5};
        assertSame(array, LinearAlgebra.truncateOrPaddleWithZeros(array, size));
    }

    @Test
    void truncateOrPaddleWithZeros_sizeLargerThanArraySize_returnsInputArrayPaddedWithZeros() {
        int size = 7;
        double[] array = new double[] {1, 2, 3, 4, 5};
        assertArrayEquals(new double[] {1, 2, 3, 4, 5, 0, 0}, LinearAlgebra.truncateOrPaddleWithZeros(array, size));
    }
}