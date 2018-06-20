package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidatorTest {
    @Test
    void assertIsNotEmpty_emptyIntegerArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertIsNotEmpty(new int[] {}));
    }

    @Test
    void assertIsNotEmpty_nonEmptyIntegerArray_doNotThrowException() {
        Validator.assertIsNotEmpty(new int[] {1});
    }

    @Test
    void assertIsNotEmpty_emptyLongArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertIsNotEmpty(new long[] {}));
    }

    @Test
    void assertIsNotEmpty_nonEmptyLongArray_doNotThrowException() {
        Validator.assertIsNotEmpty(new long[] {1L});
    }

    @Test
    void assertIsNotEmpty_emptyDoubleArray_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertIsNotEmpty(new double[] {}));
    }

    @Test
    void assertIsNotEmpty_nonEmptyDoubleArray_doNotThrowException() {
        Validator.assertIsNotEmpty(new double[] {1.});
    }

    @Test
    void assertIsNotEmpty_emptyDoubleMatrix_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertIsNotEmpty(new double[][] {}));
    }

    @Test
    void assertIsNotEmpty_nonEmptyDoubleMatrix_doNotThrowException() {
        Validator.assertIsNotEmpty(new double[][] {{1.}});
    }

    @Test
    void assertAllRowsHaveSameDimension_emptyMatrix_doNotThrowException() {
        Validator.assertAllRowsHaveSameDimension(new double[][] {});
    }

    @Test
    void assertAllRowsHaveSameDimension_allRowsAreEmpty_doNotThrowException() {
        Validator.assertAllRowsHaveSameDimension(new double[][] {{},{}});
    }

    @Test
    void assertAllRowsHaveSameDimension_allRowsHaveTheSameDimension_doNotThrowException() {
        Validator.assertAllRowsHaveSameDimension(new double[][] {{1.},{2.}});
    }

    @Test
    void assertAllRowsHaveSameDimension_rowsDoNotHaveTheSameDimension_doNotThrowException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertAllRowsHaveSameDimension(new double[][] {{1.},{}}));
    }

    @Test
    void validateMatrix_emptyMatrix_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMatrix(new double[][] {}, false));
    }

    @Test
    void validateMatrix_differentDimensionRows_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMatrix(new double[][] {{1.}, {}}, false));
    }

    @Test
    void validateMatrix_zeroDimensionalRowsWithCheckDim_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMatrix(new double[][] {{}}, true));
    }

    @Test
    void validateMatrix_zeroDimensionalRowsWithNoCheckDim_doNotThrowException() {
        Validator.validateMatrix(new double[][] {{}}, false);
    }

    @Test
    void validateMatrix_nonEmptyMatrixAndRowsWithCheckDim_doNotThrowException() {
        Validator.validateMatrix(new double[][] {{1}}, true);
    }

    @Test
    void assertEqualLengths_differentSizeIntegerArrays_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertEqualLengths(new int[] {}, new int[] {1}));
    }

    @Test
    void assertEqualLengths_nonEmptyIntegerArray_doNotThrowException() {
        Validator.assertEqualLengths(new int[] {2}, new int[] {1});
    }

    @Test
    void assertEqualLengths_differentSizeDoubleArrays_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertEqualLengths(new double[] {}, new double[] {1}));
    }

    @Test
    void assertEqualLengths_nonEmptyDoubleArray_doNotThrowException() {
        Validator.assertEqualLengths(new double[] {1.}, new double[] {2.});
    }
    
    
    @Test
    void assertPositive_positiveInteger_doNotThrowException() {
        Validator.assertPositive(1);
    }

    @Test
    void assertPositive_negativeInteger_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertPositive(-1));
    }

    @Test
    void assertPositive_positiveDouble_doNotThrowException() {
        Validator.assertPositive(1.);
    }

    @Test
    void assertPositive_negativeDouble_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertPositive(-1.));
    }

    @Test
    void assertPositive_zero_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertPositive(0));
    }

    @Test
    void assertNonNegative_positiveInteger_doNotThrowException() {
        Validator.assertNonNegative(1);
    }

    @Test
    void assertNonNegative_negativeInteger_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertNonNegative(-1));
    }

    @Test
    void assertNonNegative_positiveDouble_doNotThrowException() {
        Validator.assertNonNegative(1.);
    }

    @Test
    void assertNonNegative_negativeDouble_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> Validator.assertNonNegative(-1.));
    }

    @Test
    void assertNonNegative_zero_doNotThrowException() {
        Validator.assertNonNegative(0);
    }


}