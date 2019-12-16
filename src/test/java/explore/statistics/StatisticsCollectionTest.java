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
import static org.junit.jupiter.api.Assertions.assertThrows;

class StatisticsCollectionTest {
    private StatisticsCollection collection;

    @BeforeEach
    void setUp() {
        collection = new StatisticsCollection();
        collection.update("metric1", 1.0);
    }

    @Test
    void get_nameNotInCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> collection.get("unknown"));
    }

    @Test
    void get_nameInCollection_correctStatisticRetrieved() {
        assertEquals("metric1", collection.get("metric1").getName());
    }

    @Test
    void update_nameNotInCollection_newStatisticAppended() {
        collection.update("metric2", 2.0);
        Statistics statistics = collection.get("metric2");
        assertEquals(2.0, statistics.getMean(), 1e-10);
        assertEquals(1, statistics.getSampleSize());
        assertEquals(0.0, statistics.getVariance(), 1e-10);
    }

    @Test
    void update_nameInCollection_statisticCorrectlyUpdated() {
        collection.update("metric1", 5.0);
        Statistics statistics = collection.get("metric1");
        assertEquals(3, statistics.getMean(), 1e-10);
        assertEquals(4, statistics.getVariance(), 1e-10);
        assertEquals(2, statistics.getSampleSize());
    }
}