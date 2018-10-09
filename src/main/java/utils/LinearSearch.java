package utils;

import java.util.function.Function;

/**
 * Utility class for finding the minimum / maximum values of a function over a collection.
 */
public class LinearSearch {
    /**
     *  Finds the minimizer of a function over a collection.
     * @param collection: collection of elements to find minimizer
     * @param function: score function returning a double value for each
     * @return an object containing both the minimum function value and the an element achieving the minimum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> T findMinimizer(Iterable<T> collection, Function<T, Double> function){
        T minimizer = null;
        Double minimum = Double.POSITIVE_INFINITY;

        for (T element : collection) {
            Double value = function.apply(element);

            if (value < minimum) {
                minimum = value;
                minimizer = element;
            }
        }

        if (minimizer == null) {
            throw new IllegalArgumentException("Empty input.");
        }

        return minimizer;
    }

    /**
     * Finds the maximizer of a function over a collection.
     * @param collection: collection of elements to find maximizer
     * @param function: score function returning a double value for each
     * @return an object containing both the minimum function value and the an element achieving the maximum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> T findMaximizer(Iterable<T> collection, Function<T, Double> function){
        return findMinimizer(collection, x -> -function.apply(x));
    }
}
