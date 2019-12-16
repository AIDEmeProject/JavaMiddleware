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

class IndependentChainsSelectorTest extends AbstractSampleSelectorTest {
    private final int numSamples = 3;
    private final int chainLength = 10;
    private HitAndRun hitAndRun;
    private HitAndRun.Chain chain;

    @BeforeEach
    void setUp() {
        sampleSelector = new IndependentChainsSelector(chainLength);

        chain = mock(HitAndRun.Chain.class);
        when(chain.advance(chainLength)).thenReturn(Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2));

        hitAndRun = mock(HitAndRun.class);
        when(hitAndRun.newChain()).thenReturn(chain);
    }

    @Test
    void constructor_negativeChainLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new IndependentChainsSelector(-1));
    }

    @Test
    void constructor_zeroChainLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new IndependentChainsSelector(0));
    }

    @Test
    void select_sampleThreePoints_newChainCalledOncePerSample() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(hitAndRun, times(numSamples)).newChain();
    }

    @Test
    void select_sampleThreePoints_generateThreeChainsOfSizeEqualToChainLength() {
        sampleSelector.select(hitAndRun, numSamples);
        verify(chain, times(numSamples)).advance(chainLength);
    }

    @Test
    void select_stubbedHitAndRunChainResults_selectReturnsStubbedSamples() {
        Vector[] result = sampleSelector.select(hitAndRun, numSamples);
        assertArrayEquals(new Vector[] {Vector.FACTORY.make(0), Vector.FACTORY.make(1), Vector.FACTORY.make(2)}, result);
    }
}