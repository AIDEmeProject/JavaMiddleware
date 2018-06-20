package utils;

import java.util.Collection;
import java.util.function.Function;

/**
 * Utility class for finding the minimum / maximum values of a function over a collection.
 */
public class OptimumFinder {
    /**
     *  Finds the minimizer of a function over a collection.
     * @param collection: collection of elements to find minimizer
     * @param function: score function returning a double value for each
     * @param lowerBound: lower bound on function. Used for avoiding evaluating the real function if it is very expensive to compute.
     * @param start: initial guess of minimizer. A good choice of starting point reduces the number of function evaluations.
     * @return an object containing both the minimum function value and the an element achieving the minimum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> OptimumResult<T> minimizer(Collection<T> collection, Function<T, Double> function, Function<T, Double> lowerBound, T start){
        Validator.assertNotEmpty(collection);

        T minimizer = start;
        Double score = start == null ? Double.POSITIVE_INFINITY : function.apply(start);

        for (T point : collection) {
            if (lowerBound.apply(point) > score) {
                continue;
            }

            Double value = function.apply(point);

            if (value < score) {
                score = value;
                minimizer = point;
            }
        }

        return new OptimumResult<>(minimizer, score);
    }

    /**
     *  Finds the minimizer of a function over a collection.
     * @param collection: collection of elements to find minimizer
     * @param function: score function returning a double value for each
     * @param lowerBound: lower bound on function. Used for avoiding evaluating the real function if it is very expensive to compute.
     * @return an object containing both the minimum function value and the an element achieving the minimum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> OptimumResult<T> minimizer(Collection<T> collection, Function<T, Double> function, Function<T, Double> lowerBound){
        return minimizer(collection, function, lowerBound, null);
    }

    /**
     * Finds the minimizer of a function over a collection.
     * @param collection: collection of elements to find minimizer
     * @param function: score function returning a double value for each
     * @return an object containing both the minimum function value and the an element achieving the minimum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> OptimumResult<T> minimizer(Collection<T> collection, Function<T, Double> function){
        return minimizer(collection, function, x -> Double.NEGATIVE_INFINITY, null);
    }

    /**
     * Finds the maximizer of a function over a collection.
     * @param collection: collection of elements to find maximizer
     * @param function: score function returning a double value for each
     * @param upperBound: upper bound on function. Used for avoiding evaluating the real function if it is very expensive to compute.
     * @param start: initial guess of maximizer. A good choice of starting point reduces the number of function evaluations.
     * @return an object containing both the minimum function value and the an element achieving the maximum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> OptimumResult<T> maximizer(Collection<T> collection, Function<T, Double> function, Function<T, Double> upperBound, T start){
        OptimumResult<T> result = minimizer(collection, x -> -function.apply(x), x -> -upperBound.apply(x), start);
        return new OptimumResult<>(result.optimizer, -result.score);
    }

    /**
     *  Finds the minimizer of a function over a collection.
     * @param collection: collection of elements to find minimizer
     * @param function: score function returning a double value for each
     * @param upperBound: lower bound on function. Used for avoiding evaluating the real function if it is very expensive to compute.
     * @return an object containing both the minimum function value and the an element achieving the minimum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> OptimumResult<T> maximizer(Collection<T> collection, Function<T, Double> function, Function<T, Double> upperBound){
        OptimumResult<T> result = minimizer(collection, x -> -function.apply(x), x -> -upperBound.apply(x));
        return new OptimumResult<>(result.optimizer, -result.score);
    }

    /**
     * Finds the maximizer of a function over a collection.
     * @param collection: collection of elements to find maximizer
     * @param function: score function returning a double value for each
     * @return an object containing both the minimum function value and the an element achieving the maximum.
     * @throws IllegalArgumentException if collection is emtpy
     */
    public static <T> OptimumResult<T> maximizer(Collection<T> collection, Function<T, Double> function){
        OptimumResult<T> result = minimizer(collection, x -> -function.apply(x));
        return new OptimumResult<>(result.optimizer, -result.score);
    }

    /**
     * This class stores the optimal function value and the element attaining this optimum.
     */
    public static class OptimumResult<T> {
        private T optimizer;
        private Double score;

        public OptimumResult() {
            this(null, Double.POSITIVE_INFINITY);
        }

        public OptimumResult(T optimizer, Double score) {
            this.optimizer = optimizer;
            this.score = score;
        }

        public T getOptimizer() {
            return optimizer;
        }

        public Double getScore() {
            return score;
        }
    }
}
