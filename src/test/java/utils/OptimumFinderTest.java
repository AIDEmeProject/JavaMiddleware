package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class OptimumFinderTest {
    private List<Double> elems;
    private FakeFunction score;


    private class FakeFunction implements Function<Double, Double>{
        private Function<Double, Double> function;
        private int counter;

        public FakeFunction(Function<Double, Double> function) {
            this.counter = 0;
            this.function = function;
        }

        @Override
        public Double apply(Double aDouble) {
            counter++;
            return function.apply(aDouble);
        }

        public int getCounter() {
            return counter;
        }
    }

    @BeforeEach
    void setUp() {
        elems = Arrays.asList(-3.,-1., 2.);
        score = new FakeFunction(x -> x*x);
    }

    @Test
    void minimizer_emptyInputCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> OptimumFinder.minimizer(new ArrayList<Double>(), x -> x));
    }

    @Test
    void maximizer_emptyInputCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> OptimumFinder.maximizer(new ArrayList<Double>(), x -> x));
    }

    @Test
    void minimizer_quadraticScoreFunction_returnsCorrectMinimum() {
        OptimumFinder.OptimumResult result = OptimumFinder.minimizer(elems, score);
        assertEquals(1., (double) result.getScore());
        assertEquals(-1., result.getOptimizer());
    }

    @Test
    void maximizer_quadraticScoreFunction_returnsCorrectMaximum() {
        OptimumFinder.OptimumResult result = OptimumFinder.maximizer(elems, score);
        assertEquals(9., (double) result.getScore());
        assertEquals(-3., result.getOptimizer());
    }

    @Test
    void minimizer_quadraticScoreFunctionWithLowerBound_returnsCorrectMinimum() {
        Function<Double, Double> lower = x -> x*x - 1;
        OptimumFinder.OptimumResult result = OptimumFinder.minimizer(elems, score, lower);
        assertEquals(1., (double) result.getScore());
        assertEquals(-1., result.getOptimizer());
    }

    @Test
    void maximizer_quadraticScoreFunctionWithUpperBound_returnsCorrectMaximum() {
        Function<Double, Double> upper = x -> x*x+1;

        OptimumFinder.OptimumResult result = OptimumFinder.maximizer(elems, score, upper);
        assertEquals(9., (double) result.getScore());
        assertEquals(-3., result.getOptimizer());
    }

    @Test
    void minimizer_quadraticScoreFunctionWithLowerBound_correctNumberOfFunction() {
        FakeFunction lower = new FakeFunction(x -> x*x - 1);
        OptimumFinder.OptimumResult result = OptimumFinder.minimizer(elems, score, lower);
        assertEquals(2, score.getCounter());
        assertEquals(3, lower.getCounter());
    }

    @Test
    void maximizer_quadraticScoreFunctionWithUpperBound_correctNumberOfFunction() {
        FakeFunction upper = new FakeFunction(x -> x*x + 1);

        OptimumFinder.maximizer(elems, score, upper);
        assertEquals(1, score.getCounter());
        assertEquals(3, upper.getCounter());
    }
}