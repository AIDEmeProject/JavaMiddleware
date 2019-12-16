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

package explore.statistics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsTest {
    private String name;
    private double value;
    private Statistics statistics;

    @BeforeEach
    void setUp() {
        name = "test";
        value = 1;
        statistics = new Statistics(name, value);
    }

    @Test
    void getters_noUpdates_correctDefaultValues() {
        assertEquals(name, statistics.getName());
        assertEquals(value, statistics.getMean());
        assertEquals(value, statistics.getSum());
        assertEquals(0D, statistics.getVariance(), 1e-10);
        assertEquals(value, statistics.getMinimum(), 1e-10);
        assertEquals(value, statistics.getMaximum(), 1e-10);
        assertEquals(1, statistics.getSampleSize());
    }

    @Test
    void update_singleUpdate_allStatisticsAreCorrect() {
        statistics.update(5);
        assertEquals(3, statistics.getMean(), 1e-10);
        assertEquals(6, statistics.getSum(), 1e-10);
        assertEquals(4, statistics.getVariance(), 1e-10);
        assertEquals(2, statistics.getStandardDeviation(), 1e-10);
        assertEquals(1, statistics.getMinimum(), 1e-10);
        assertEquals(5, statistics.getMaximum(), 1e-10);
        assertEquals(2, statistics.getSampleSize());
    }

    @Test
    void update_twoUpdates_allStatisticsAreCorrect() {
        statistics.update(5);
        statistics.update(-3);
        assertEquals(1, statistics.getMean(), 1e-10);
        assertEquals(3, statistics.getSum(), 1e-10);
        assertEquals(32.0 / 3, statistics.getVariance(), 1e-10);
        assertEquals(Math.sqrt(32.0 / 3), statistics.getStandardDeviation(), 1e-10);
        assertEquals(-3, statistics.getMinimum(), 1e-10);
        assertEquals(5, statistics.getMaximum(), 1e-10);
        assertEquals(3, statistics.getSampleSize());
    }

    @Test
    void update_severalUpdates_allStatisticsAreCorrect() {
        double[] values = new double[] {5, -3, 10, -22, 101};
        for (double val: values) {
            statistics.update(val);
        }
        assertEquals(15.333333333333, statistics.getMean(), 1e-10);
        assertEquals(92, statistics.getSum(), 1e-10);
        assertEquals(1568.2222222222, statistics.getVariance(), 1e-10);
        assertEquals(39.6007856263, statistics.getStandardDeviation(), 1e-10);
        assertEquals(-22, statistics.getMinimum(), 1e-10);
        assertEquals(101, statistics.getMaximum(), 1e-10);
        assertEquals(6, statistics.getSampleSize());
    }
}