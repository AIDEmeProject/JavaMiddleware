package utils;

import java.util.Collection;
import java.util.function.Function;

public class OptimumFinder {
    public static <T> OptimumResult<T> branchAndBoundMinimizer(Collection<T> collection, Function<T, Double> function, Function<T, Double> lowerBound){
        if (collection.isEmpty()){
            throw new IllegalArgumentException("Cannot find minimum over empty collection");
        }

        double score = Double.POSITIVE_INFINITY;
        T minimizer = null;

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

    public static <T> OptimumResult<T> branchAndBoundMaximizer(Collection<T> collection, Function<T, Double> function, Function<T, Double> lowerBound){
        OptimumResult<T> result = branchAndBoundMinimizer(collection, x -> -function.apply(x), x -> -lowerBound.apply(x));
        return new OptimumResult<>(result.optimum, -result.value);
    }

    public static <T> OptimumResult<T> minimizer(Collection<T> collection, Function<T, Double> function){
        return branchAndBoundMinimizer(collection, function, x -> Double.NEGATIVE_INFINITY);
    }

    public static <T> OptimumResult<T> maximizer(Collection<T> collection, Function<T, Double> function){
        OptimumResult<T> result = minimizer(collection, x -> -function.apply(x));
        return new OptimumResult<>(result.optimum, -result.value);
    }

    public static class OptimumResult<T> {
        private T optimum;
        private Double value;

        public OptimumResult(T optimum, Double value) {
            this.optimum = optimum;
            this.value = value;
        }

        public T getOptimum() {
            return optimum;
        }

        public Double getValue() {
            return value;
        }
    }
}
