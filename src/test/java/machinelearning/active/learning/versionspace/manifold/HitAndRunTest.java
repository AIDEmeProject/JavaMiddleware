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