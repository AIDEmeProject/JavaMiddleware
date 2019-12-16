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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReservoirSamplerTest {
    private List<Integer> values;

    @BeforeEach
    void setUp() {
        values = Arrays.asList(1,2,3,4,5);
    }

    @Test
    void sample_emptyCollection_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(new ArrayList<>()));
    }

    @Test
    void sample_negativeSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(values, -1));
    }

    @Test
    void sample_zeroSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(values, 0));
    }

    @Test
    void sample_collectionSmallerThanSampleSize_theOwnCollectionIsReturned() {
        assertSame(values, ReservoirSampler.sample(values, values.size()+1));
    }

    @Test
    void sample_collectionSizeEqualsToSampleSize_returnsTheInputCollection() {
        assertEquals(values, ReservoirSampler.sample(values, values.size()));
    }

    @Test
    void sample_filteredCollectionSizeSmallerThanSampleSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> ReservoirSampler.sample(values, 3, x -> x >= 3));
    }

    @Test
    void sample_filteredAllElementsButOne_ReturnsRemainingElement() {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(1);
        assertEquals(result, ReservoirSampler.sample(values, 1, x -> x >= 2));
    }
}