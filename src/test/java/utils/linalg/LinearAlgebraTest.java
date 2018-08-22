package utils.linalg;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinearAlgebraTest {

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