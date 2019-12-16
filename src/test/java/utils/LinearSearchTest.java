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
    private MockFunction score;

    private class MockFunction implements Function<Double, Double>{
        private Function<Double, Double> function;
        private int counter;

        public MockFunction(Function<Double, Double> function) {
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
        score = new MockFunction(x -> x*x);
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
        LinearSearch.findMinimizer(elems, score);
        assertEquals(elems.size(), score.counter);
    }

    @Test
    void maximizer_anyFunction_applyCalledOncePerElementInCollection() {
        LinearSearch.findMaximizer(elems, score);
        assertEquals(elems.size(), score.counter);
    }
}