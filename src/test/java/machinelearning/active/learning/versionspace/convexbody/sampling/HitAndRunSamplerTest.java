package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.active.learning.versionspace.convexbody.Line;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

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
        verifyCallsToConvexBodyMock(0, 1, 10);
    }

    @Test
    void sample_warmupAndThinApplied_methodsCalledTheExpectedNumberOfTimes() {
        verifyCallsToConvexBodyMock(5, 7, 12);
    }

    @Test
    void sample_mockDirectionSamplingAlgorithm_fitCalledOnce() {
        ConvexBody convexBodyStub = getConvexBodyMock();

        DirectionSamplingAlgorithm algorithmMock = getDirectionSamplingAlgorithmMock(new RandomDirectionSampler(1));

        sampler = new HitAndRunSampler(3, 8, algorithmMock);
        sampler.sample(convexBodyStub, 10);
        verify(algorithmMock).fit(convexBodyStub);
    }

    @Test
    void sample_noWarmupNorThinApplied_sampleDirectionCalledOncePerIteration() {
        verifyCallsToDirectionSampler(0, 1, 9);
    }

    @Test
    void sample_warmupAndThinApplied_sampleDirectionCalledOncePerIteration() {
        verifyCallsToDirectionSampler(5, 3, 12);
    }

    private void verifyCallsToConvexBodyMock(int warmup, int thin, int numSamples){
        sampler = new HitAndRunSampler(warmup, thin);
        ConvexBody mockConvexBody = getConvexBodyMock();

        // verify number of calls
        sampler.sample(mockConvexBody, numSamples);
        verify(mockConvexBody, never()).isInside(any());
        verify(mockConvexBody, times(1)).getInteriorPoint();
        verify(mockConvexBody, times(warmup + thin * (numSamples - 1))).computeLineIntersection(any());
    }

    private void verifyCallsToDirectionSampler(int warmup, int thin, int numSamples) {
        sampler = new HitAndRunSampler(warmup, thin);
        ConvexBody convexBodyStub = getConvexBodyMock();

        DirectionSampler directionSamplerMock = getDirectionSamplerMock();
        DirectionSamplingAlgorithm algorithmStub = getDirectionSamplingAlgorithmMock(directionSamplerMock);

        sampler = new HitAndRunSampler(warmup, thin, algorithmStub);
        sampler.sample(convexBodyStub, numSamples);
        verify(directionSamplerMock, times(warmup + thin*(numSamples-1))).sampleDirection(any());
    }

    private ConvexBody getConvexBodyMock() {
        ConvexBody convexBodyStub = mock(ConvexBody.class);
        when(convexBodyStub.getDim()).thenReturn(1);
        when(convexBodyStub.getInteriorPoint()).thenReturn(new double[1]);
        ArgumentCaptor<Line> argument = ArgumentCaptor.forClass(Line.class);
        when(convexBodyStub.computeLineIntersection(argument.capture())).thenAnswer(
                (Answer) invocationOnMock -> argument.getValue().getSegment(-1, 1));
        return convexBodyStub;
    }

    private DirectionSamplingAlgorithm getDirectionSamplingAlgorithmMock(DirectionSampler directionSamplerMock) {
        DirectionSamplingAlgorithm algorithmStub = mock(DirectionSamplingAlgorithm.class);
        when(algorithmStub.fit(any())).thenReturn(directionSamplerMock);
        return algorithmStub;
    }

    private DirectionSampler getDirectionSamplerMock() {
        DirectionSampler directionSamplerMock = mock(DirectionSampler.class);
        when(directionSamplerMock.sampleDirection(any())).thenReturn(new double[]{1});
        return directionSamplerMock;
    }
}