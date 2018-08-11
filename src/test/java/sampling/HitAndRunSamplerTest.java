package sampling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;
import utils.convexbody.ConvexBody;
import utils.convexbody.Line;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HitAndRunSamplerTest {
    private HitAndRunSampler sampler;

    @BeforeEach
    void setUp() {
        sampler = new HitAndRunSampler(0, 1);
    }

    @Test
    void constructor_negativeWarmup_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler(-1, 1));
    }

    @Test
    void constructor_zeroThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler(1, 0));
    }

    @Test
    void constructor_negativeThin_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler(1, -1));
    }

    @Test
    void sample_nullConvexBody_throwsException() {
        assertThrows(NullPointerException.class, () -> sampler.sample(null, 1));
    }

    @Test
    void sample_negativeNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sampler.sample(mock(ConvexBody.class), -1));
    }

    @Test
    void sample_zeroNumSamples_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> sampler.sample(mock(ConvexBody.class), 0));
    }

    @Test
    void sample_noWarmupNorThinApplied_methodsCalledTheExpectedNumberOfTimes() {
        verifyNumberOfCalls(0, 1, 10);
    }

    @Test
    void sample_warmupAndThinApplied_methodsCalledTheExpectedNumberOfTimes() {
        verifyNumberOfCalls(5, 7, 12);
    }

    private void verifyNumberOfCalls(int warmup, int thin, int numSamples){
        sampler = new HitAndRunSampler(warmup, thin);
        ConvexBody mockConvexBody = mock(ConvexBody.class);

        // return [0] for interiorPoint
        when(mockConvexBody.getInteriorPoint()).thenReturn(new double[1]);

        // clip line to [-1,1] segment when computeLineIntersection is called
        ArgumentCaptor<Line> argument = ArgumentCaptor.forClass(Line.class);
        when(mockConvexBody.computeLineIntersection(argument.capture())).thenAnswer(
                (Answer) invocationOnMock -> argument.getValue().getSegment(-1, 1));

        // verify number of calls
        sampler.sample(mockConvexBody, numSamples);
        verify(mockConvexBody, never()).isInside(any());
        verify(mockConvexBody, times(1)).getInteriorPoint();
        verify(mockConvexBody, times(warmup + thin * (numSamples - 1))).computeLineIntersection(any());
    }
}