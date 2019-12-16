/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

package utils;

import java.util.Arrays;
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
     * Raises an exception if data vector is emtpy
     * @param data: data vector
     * @throws IllegalArgumentException if data vector is empty
     */
    public static void assertNotEmpty(long[] data){
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
     * Throws exception is values are different.
     * @param val1: first value
     * @param val2: second value
     * @throws IllegalArgumentException if values are distinct
     */
    public static void assertEquals(double val1, double val2){
        assertEquals(val1, val2, 1e-15);
    }

    /**
     * Throws exception is values are different.
     * @param val1: first value
     * @param val2: second value
     * @throws IllegalArgumentException if values are distinct
     */
    public static void assertEquals(double val1, double val2, double eps){
        if(Math.abs(val1 - val2) > eps) {
            throw new IllegalArgumentException("Values " + val1 + " and " + val2 + " are not equal.");
        }
    }

    /**
     * Throws exception is arrays are different.
     * @param val1: first value
     * @param val2: second value
     * @throws IllegalArgumentException if values are distinct
     */
    public static void assertEquals(int[][] val1, int[][] val2){
        if(!Arrays.deepEquals(val1, val2)) {
            throw new IllegalArgumentException("Values " + Arrays.toString(val1) + " and " + Arrays.toString(val2) + " are not equal.");
        }
    }

    /**
     * Throws exception is objects are different
     * @param val1: first object
     * @param val2: second object
     * @throws IllegalArgumentException if values are distinct
     */
    public static void assertEquals(Object val1, Object val2){
        if(!val1.equals(val2)) {
            throw new IllegalArgumentException("Objects " + val1 + " and " + val2 + " are not equal.");
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

    public static void assertIndexInBounds(int value, int lower, int upper){
        if (value < lower || value >= upper){
            throw new IllegalArgumentException("Value must be comprised between " + lower + " and " + upper);
        }
    }

    public static void assertIsFinite(double value){
        if (!Double.isFinite(value)){
            throw new IllegalArgumentException("Value must be finite, obtained " + value);
        }
    }
}
