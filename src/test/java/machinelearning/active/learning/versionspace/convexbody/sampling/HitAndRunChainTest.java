package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.active.learning.versionspace.convexbody.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class HitAndRunChainTest {
    private ConvexBody convexBody;
    private DirectionSampler directionSampler;
    private HitAndRunChain hitAndRunChain;

    @BeforeEach
    void setUp() {
        convexBody = getConvexBodyMock();
        directionSampler = getDirectionSamplerMock();
        hitAndRunChain = new HitAndRunChain(convexBody, directionSampler, new Random());
    }

    @Test
    void constructor_nullConvexBody_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunChain(null, directionSampler, new Random()));
    }

    @Test
    void constructor_nullDirectionSampler_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunChain(convexBody, null, new Random()));
    }

    @Test
    void constructor_nullRandomNumberGenerator_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunChain(convexBody, directionSampler, null));
    }

    @Test
    void advance_singleIteration_convexBodyMethodsCalledOnlyOnce() {
        hitAndRunChain.advance();
        verifyCallsToConvexBodyMock(1);
    }

    @Test
    void advance_singleIteration_directionSamplerCalledOnce() {
        hitAndRunChain.advance();
        verify(directionSampler).sampleDirection(any());
    }

    @Test
    void advance_negativeNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> hitAndRunChain.advance(-1));
    }

    @Test
    void advance_zeroNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> hitAndRunChain.advance(0));
    }

    @Test
    void advance_fiveIteration_convexBodyMethodsCalledOnlyFiveTimes() {
        hitAndRunChain.advance(5);
        verifyCallsToConvexBodyMock(5);
    }

    @Test
    void advance_fiveIteration_directionSamplerCalledFiveTimes() {
        hitAndRunChain.advance(5);
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