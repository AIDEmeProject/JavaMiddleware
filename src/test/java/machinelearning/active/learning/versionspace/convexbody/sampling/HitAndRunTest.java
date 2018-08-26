package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.active.learning.versionspace.convexbody.Line;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.DirectionSampler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.util.Random;

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
        assertThrows(NullPointerException.class, () -> new HitAndRun(null, directionSampler, new Random()));
    }

    @Test
    void constructor_nullDirectionSampler_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRun(convexBody, null, new Random()));
    }

    @Test
    void constructor_nullRandomNumberGenerator_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRun(convexBody, directionSampler, null));
    }

    @Test
    void advance_singleIteration_convexBodyMethodsCalledOnlyOnce() {
        chain.advance();
        verifyCallsToConvexBodyMock(1);
    }

    @Test
    void advance_singleIteration_directionSamplerCalledOnce() {
        chain.advance();
        verify(directionSampler).sampleDirection(any());
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
    void advance_fiveIteration_convexBodyMethodsCalledOnlyFiveTimes() {
        chain.advance(5);
        verifyCallsToConvexBodyMock(5);
    }

    @Test
    void advance_fiveIteration_directionSamplerCalledFiveTimes() {
        chain.advance(5);
        verify(directionSampler, times(5)).sampleDirection(any());
    }

    private ConvexBody getConvexBodyMock() {
        ConvexBody convexBodyStub = mock(ConvexBody.class);

        when(convexBodyStub.getDim()).thenReturn(1);  // one-dimensional

        when(convexBodyStub.getInteriorPoint()).thenReturn(new double[1]);  // [0] is interior point

        ArgumentCaptor<Line> argument = ArgumentCaptor.forClass(Line.class);  // clip lines to [-1, 1] range
        when(convexBodyStub.computeLineIntersection(argument.capture())).thenAnswer(
                (Answer) invocationOnMock -> argument.getValue().getSegment(-1, 1));

        return convexBodyStub;
    }

    // always returns [1]
    private DirectionSampler getDirectionSamplerMock() {
        DirectionSampler directionSamplerMock = mock(DirectionSampler.class);
        when(directionSamplerMock.sampleDirection(any())).thenReturn(new double[]{1});
        return directionSamplerMock;
    }

    private void verifyCallsToConvexBodyMock(int numIterations) {
        verify(convexBody, never()).isInside(any());  // isInside never called
        verify(convexBody).getInteriorPoint();  // getInteriorPoint called once
        verify(convexBody, times(numIterations)).computeLineIntersection(any());  // computeLineIntersection called once per advance()
    }
}