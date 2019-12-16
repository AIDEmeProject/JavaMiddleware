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

package explore.sampling;

import data.IndexedDataset;
import explore.user.User;
import explore.user.UserStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

class StratifiedSamplerTest {

    private StratifiedSampler sampler;
    private IndexedDataset points;
    private User user;

    @BeforeEach
    void setUp() {
        sampler = new StratifiedSampler(2, 2);

        IndexedDataset.Builder builder = new IndexedDataset.Builder();
        builder.add(0, new double[] {0});
        builder.add(1, new double[] {1});
        builder.add(2, new double[] {2});
        builder.add(3, new double[] {3});

        points = builder.build();
    }

    @Test
    void constructor_lessThanZeroPositiveSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new StratifiedSampler(-1, 1));
    }

    @Test
    void constructor_lessThanZeroNegativeSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new StratifiedSampler(1, -1));
    }

    @Test
    void sample_sampleMorePositivePointsThanPossible_throwsException() {
        Set<Long> keys = new HashSet<>(Collections.singletonList(3L));
        user = new UserStub(keys);

        assertThrows(RuntimeException.class, () -> sampler.runInitialSample(points, user));
    }

    @Test
    void sample_sampleMoreNegativePointsThanPossible_throwsException() {
        Set<Long> keys = new HashSet<>(Arrays.asList(0L, 1L, 2L));
        user = new UserStub(keys);

        assertThrows(RuntimeException.class, () -> sampler.runInitialSample(points, user));
    }
}