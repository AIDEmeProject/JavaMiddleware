package utils;

import java.util.Collection;

/**
 * This utility class encloses several data validation checks used throughout our modules.
 */
public class Validator {
    public static String assertNotEmpty(String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException("String cannot be empty.");
        }
        return s;
    }

    /**
     * Raises an exception if object is null
     * @param object: any object
     * @throws IllegalArgumentException if object is null
     */
    public static <T> void assertNotNull(Object object){
        if (object == null){
            throw new NullPointerException("Object must not be null.");
        }
    }

    /**
     * Raises an exception if collection is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static <T> void assertNotEmpty(Collection<T> data){
        if (data.size() == 0){
            throw new IllegalArgumentException("Collection must contain at least one element.");
        }
    }

    /**
     * Raises an exception if data vector is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static void assertNotEmpty(int[] data){
        if (data.length == 0){
            throw new IllegalArgumentException("Array must contain at least one element.");
        }
    }

    /**
     * Raises an exception if data vector is null or emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data is null or empty
     */
    public static void assertNotEmpty(double[] data){
        if (data.length == 0){
            throw new IllegalArgumentException("Array must contain at least one element.");
        }
    }

    /**
     * Raises an exception if data vector is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static <T> void assertNotEmpty(T[] data){
        if (data.length == 0){
            throw new IllegalArgumentException("Array must contain at least one element.");
        }
    }

    /**
     * Throws exception is values are different.
     * @param val1: first value
     * @param val2: second value
     * @throws IllegalArgumentException if values are distinct
     */
    public static void assertEquals(int val1, int val2){
        if(val1 != val2) {
            throw new IllegalArgumentException("Values " + val1 + " and " + val2 + " are not equal.");
        }
    }

    /**
     * Raises an exception if input vectors have different sizes.
     * @param vector1: one of input arrays
     * @param vector2: one of input arrays
     * @throws IllegalArgumentException arrays have different sizes.
     */
    public static void assertEqualLengths(int[] vector1, int[] vector2){
        assertEquals(vector1.length, vector2.length);
    }

    /**
     * Raises an exception if input vectors have different sizes.
     * @param vector1: one of input arrays
     * @param vector2: one of input arrays
     * @throws IllegalArgumentException arrays have different sizes.
     */
    public static <T> void assertEqualLengths(T[] vector1, T[] vector2){
        assertEquals(vector1.length, vector2.length);
    }

    /**
     * Raises an exception if input vectors have different sizes.
     * @param vector1: one of input arrays
     * @param vector2: one of input arrays
     * @throws IllegalArgumentException arrays have different sizes.
     */
    public static void assertEqualLengths(double[] vector1, double[] vector2){
        assertEquals(vector1.length, vector2.length);
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

    /**
     * Raises an exception if value is not in the interval [lower, upper]
     * @param value: value to check
     * @param lower: lower bound on value
     * @param upper: upper bound on value
     * @throws IllegalArgumentException if value not in interval [lower, upper]
     */
    public static void assertInRange(double value, double lower, double upper){
        if (value < lower || value > upper){
            throw new IllegalArgumentException("Value must be comprised between " + lower + " and " + upper);
        }
    }

    public static void assertIsFinite(double value){
        if (!Double.isFinite(value)){
            throw new IllegalArgumentException("Value must be finite, obtained " + value);
        }
    }
}
