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

package machinelearning.active.learning.versionspace.manifold;


import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import machinelearning.active.learning.versionspace.manifold.euclidean.EuclideanSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HitAndRunTest {
    private ConvexBody convexBody;
    private DirectionSampler directionSampler;
    private HitAndRun.Chain chain;

    @BeforeEach
    void setUp() {
        convexBody = getConvexBodyMock();
        directionSampler = getDirectionSamplerMock();
        chain = new HitAndRun(convexBody, directionSampler).newChain();
    }

    @Test
    void constructor_nullConvexBody_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRun(null, directionSampler));
    }

    @Test
    void constructor_nullDirectionSampler_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRun(convexBody, null));
    }

    @Test
    void advance_singleIteration_convexBodyMethodsCalledOnlyOnce() {
        chain.advance();
        verifyCallsToConvexBodyMock(1);
    }

    @Test
    void advance_singleIteration_directionSamplerCalledOnce() {
        chain.advance();
        verify(directionSampler).sampleDirection(any(), any());
    }

    @Test
    void advance_negativeNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> chain.advance(-1));
    }

    @Test
    void advance_zeroNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> chain.advance(0));
    }

    @Test
    void advance_fiveIteration_singleAdvanceCalledFiveTimes() {
        chain = mock(HitAndRun.Chain.class);
        when(chain.advance(anyInt())).thenCallRealMethod();
        chain.advance(5);
        verify(chain, times(5)).advance();
    }

    private ConvexBody getConvexBodyMock() {
        ConvexBody convexBodyStub = mock(ConvexBody.class);

        when(convexBodyStub.dim()).thenReturn(1);  // one-dimensional

        when(convexBodyStub.getManifold()).thenReturn(EuclideanSpace.getInstance());

        when(convexBodyStub.getInteriorPoint()).thenReturn(Vector.FACTORY.zeros(1));  // [0] is interior point

        ArgumentCaptor<Geodesic> argument = ArgumentCaptor.forClass(Geodesic.class);  // clip lines to [-1, 1] range
        when(convexBodyStub.computeIntersection(argument.capture())).thenAnswer(
                (Answer) invocationOnMock -> argument.getValue().getSegment(-1, 1));

        return convexBodyStub;
    }

    // always returns [1]
    private DirectionSampler getDirectionSamplerMock() {
        DirectionSampler directionSamplerMock = mock(DirectionSampler.class);
        when(directionSamplerMock.sampleDirection(any(), any())).thenReturn(Vector.FACTORY.make(1));
        return directionSamplerMock;
    }

    private void verifyCallsToConvexBodyMock(int numIterations) {
        verify(convexBody, never()).isInside(any());  // isInside never called
        verify(convexBody).getInteriorPoint();  // getInteriorPoint called once
        verify(convexBody, times(numIterations)).computeIntersection(any());  // computeLineIntersection called once per advance()
    }
}