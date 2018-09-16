package utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class LinearSearchTest {
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
    }

    @BeforeEach
    void setUp() {
        elems = Arrays.asList(-3.,-1., 2., 1.5, -2.5);
        score = new FakeFunction(x -> x*x);
    }

    @Test
    void minimizer_emptyInputCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> LinearSearch.findMinimizer(new ArrayList<Double>(), x -> x));
    }

    @Test
    void maximizer_emptyInputCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> LinearSearch.findMaximizer(new ArrayList<Double>(), x -> x));
    }

    @Test
    void minimizer_quadraticScoreFunction_returnsCorrectMinimum() {
        assertEquals( new Double(-1), LinearSearch.findMinimizer(elems, score));
    }

    @Test
    void maximizer_quadraticScoreFunction_returnsCorrectMaximum() {
        assertEquals(new Double(-3),  LinearSearch.findMaximizer(elems, score));
    }

    @Test
    void minimizer_anyFunction_applyCalledOncePerElementInCollection() {
        Function<Double, Double> mockFunction = score;
        LinearSearch.findMinimizer(elems, score);
        assertEquals(elems.size(), score.counter);
    }

    @Test
    void maximizer_anyFunction_applyCalledOncePerElementInCollection() {
        Function<Double, Double> mockFunction = score;
        LinearSearch.findMaximizer(elems, score);
        assertEquals(elems.size(), score.counter);
    }
}