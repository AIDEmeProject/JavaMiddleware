package utils;

import java.util.Collection;

/**
 * This utility class encloses several data validation checks used throughout our modules.
 */
public class Validator {
    /**
     * Raises an exception if collection is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static <T> void assertNotEmpty(Collection<T> data){
        if (data.size() == 0){
            throw new IllegalArgumentException("Data must contain at least one feature.");
        }
    }

    /**
     * Raises an exception if data vector is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static void assertNotEmpty(int[] data){
        if (data.length == 0){
            throw new IllegalArgumentException("Data must contain at least one feature.");
        }
    }

    /**
     * Raises an exception if data vector is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static void assertNotEmpty(long[] data){
        if (data.length == 0){
            throw new IllegalArgumentException("Data must contain at least one feature.");
        }
    }

    /**
     * Raises an exception if data vector is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static void assertNotEmpty(double[] data){
        if (data.length == 0){
            throw new IllegalArgumentException("Data must contain at least one feature.");
        }
    }

    /**
     * Raises an exception if data matrix is emtpy (no data points)
     * @param data: data matrix
     * @throws IllegalArgumentException if data matrix is empty
     */
    public static void assertNotEmpty(double[][] data){
        if (data.length == 0){
            throw new IllegalArgumentException("Data must contain at least one data point.");
        }
    }

    /**
     * Raises an exception if any two rows in the matrix have distinct lengths. It does not verify whether data matrix is
     * empty or all rows are empty as well.
     * @param data: data matrix
     * @throws IllegalArgumentException if there are two rows of different lengths
     */
    public static void assertAllRowsHaveSameDimension(double[][] data){
        for (int i = 1; i < data.length; i++) {
            if (data[i].length != data[0].length){
                throw new IllegalArgumentException("Row " + i + " has an unexpected dimension. Expected " + data[0].length + " but obtained " + data[i].length);
            }
        }
    }

    /**
     * Raises an exception if matrix is not well formatted, i.e., a) has zero data points, b) rows have different dimensions,
     * and, optionally, c) if it has zero features.
     * @param data: data matrix
     * @param checkDim: whether to check if matrix has zero features
     * @throws IllegalArgumentException if any of the above conditions is not met.
     */
    public static void validateMatrix(double[][] data, boolean checkDim){
        assertNotEmpty(data);
        assertAllRowsHaveSameDimension(data);
        if (checkDim){
            assertNotEmpty(data[0]);
        }
    }

    /**
     * Raises an exception if input vectors have different sizes.
     * @param vector1: one of input arrays
     * @param vector2: one of input arrays
     * @throws IllegalArgumentException arrays have different sizes.
     */
    public static void assertEqualLengths(int[] vector1, int[] vector2){
        if (vector1.length != vector2.length){
            throw new IllegalArgumentException("Arrays have incompatible dimensions: " + vector1.length + " and " + vector2.length);
        }
    }

    /**
     * Raises an exception if input vectors have different sizes.
     * @param vector1: one of input arrays
     * @param vector2: one of input arrays
     * @throws IllegalArgumentException arrays have different sizes.
     */
    public static void assertEqualLengths(double[] vector1, double[] vector2){
        if (vector1.length != vector2.length){
            throw new IllegalArgumentException("Arrays have incompatible dimensions: " + vector1.length + " and " + vector2.length);
        }
    }

    /**
     * Raises an exception if number is not positive.
     * @param x: input value
     * @throws IllegalArgumentException value is not positive
     */
    public static void assertPositive(int x){
        if (x <= 0){
            throw new IllegalArgumentException("Expected positive value, received " + x);
        }
    }

    /**
     * Raises an exception if number is not positive.
     * @param x: input value
     * @throws IllegalArgumentException value is not positive
     */
    public static void assertPositive(double x){
        if (x <= 0){
            throw new IllegalArgumentException("Expected positive value, received " + x);
        }
    }

    /**
     * Raises an exception if number is negative.
     * @param x: input value
     * @throws IllegalArgumentException value is negative
     */
    public static void assertNonNegative(int x){
        if (x < 0){
            throw new IllegalArgumentException("Expected non-negative value, received " + x);
        }
    }

    /**
     * Raises an exception if number is negative.
     * @param x: input value
     * @throws IllegalArgumentException value is negative
     */
    public static void assertNonNegative(double x){
        if (x < 0){
            throw new IllegalArgumentException("Expected non-negative value, received " + x);
        }
    }
}
