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

package machinelearning.active.learning.versionspace.manifold.selector;

import machinelearning.active.learning.versionspace.manifold.HitAndRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WarmUpAndThinSelectorTest extends AbstractSampleSelectorTest {
    private final int warmUp = 10;
    private final int thin = 6;
    private final int numSamples = 3;
    private HitAndRun hitAndRun;
    private HitAndRun.Chain chain;

    @BeforeEach
    void setUp() {
        sampleSelector = new WarmUpAndThinSelector(warmUp, thin);

        chain = mock(HitAndRun.Chain.class);
        when(chain.advance(anyInt())).thenReturn(Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2));

        hitAndRun = mock(HitAndRun.class);
        when(hitAndRun.newChain()).thenReturn(chain);
    }

    @Test
    void constructor_negativeWarmup_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new WarmUpAndThinSelector(-1, 1));
    }

    @Test
    void constructor_zeroThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new WarmUpAndThinSelector(1, 0));
    }

    @Test
    void constructor_negativeThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new WarmUpAndThinSelector(1, -1));
    }

    @Test
    void select_mockedHitAndRunChain_newChainCalledOnce() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(hitAndRun).newChain();
    }

    @Test
    void select_mockedHitAndRunChain_advanceCalledOnceForWarmUpAndRemainingTimesForThinning() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(chain).advance(warmUp);
        verify(chain, times(numSamples-1)).advance(thin);
    }

    @Test
    void select_stubbedHitAndRunChainResults_selectReturnsStubbedSamples() {
        Vector[] result = sampleSelector.select(hitAndRun, numSamples);
        assertArrayEquals(new Vector[] {Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2)}, result);
    }
}