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

package machinelearning.classifier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubspatialWorkerTest {
    private List<Callable<Integer>> jobs;
    private SubspatialWorker worker;

    @BeforeEach
    void setUp() {
        worker = new SubspatialWorker();
    }

    @Test
    void constructor_negativeNumThreads_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SubspatialWorker(-1));
    }

    @Test
    void constructor_zeroNumThreads_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new SubspatialWorker(0));
    }

    @Test
    void run_emptyJobs_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> worker.run(new ArrayList<>()));
    }

    @Test
    void run_dummyJobs_expectedResultsComputed() {
        jobs = Arrays.asList(() -> 0, () -> 1, () -> 2);
        assertEquals(Arrays.asList(0, 1, 2), worker.run(jobs));
    }
}