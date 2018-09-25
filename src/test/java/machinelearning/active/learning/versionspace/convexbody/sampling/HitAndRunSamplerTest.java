package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.active.learning.versionspace.convexbody.sampling.cache.SampleCache;
import machinelearning.active.learning.versionspace.convexbody.sampling.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.DirectionSampler;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.convexbody.sampling.selector.SampleSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class HitAndRunSamplerTest {
    private DirectionSamplingAlgorithm directionSamplingAlgorithm;
    private SampleSelector selector;
    private SampleCache cache;
    private HitAndRunSampler sampler;

    @BeforeEach
    void setUp() {
        directionSamplingAlgorithm = mock(DirectionSamplingAlgorithm.class);
        when(directionSamplingAlgorithm.fit(any())).thenReturn(mock(DirectionSampler.class));

        selector = mock(SampleSelector.class);
        when(selector.select(any(), anyInt())).thenReturn(new double[][]{{0}, {1}, {2}});

        cache = Mockito.spy(SampleCacheStub.class);
        sampler = new HitAndRunSampler
                .Builder(directionSamplingAlgorithm, selector)
                .cache(cache)
                .build();
    }

    @Test
    void builder_nullDirectionSamplingAlgorithm_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler.Builder(null, selector));
    }

    @Test
    void builder_nullSampleSelector_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler.Builder(directionSamplingAlgorithm, null));
    }

    @Test
    void builder_nullSampleCache_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler.Builder(directionSamplingAlgorithm, selector).cache(null));
    }

    @Test
    void builder_nullRandom_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler.Builder(directionSamplingAlgorithm, selector).random(null));
    }

    @Test
    void sample_mockDirectionSamplingAlgorithm_fitCalledOnce() {
        ConvexBody body = mock(ConvexBody.class);

        sampler.sample(body, 3);

        verify(directionSamplingAlgorithm).fit(body);
    }

    @Test
    void sample_mockSampleCache_cacheMethodsAreCalledOnce() {
        ConvexBody body = mock(ConvexBody.class);

        double[][] result = sampler.sample(body, 3);

        verify(cache).attemptToSetDefaultInteriorPoint(body);
        verify(cache).updateCache(result);
    }

    @Test
    void sample_mockSampleSelector_selectCalledOnceWithExpectedNumberOfSamples() {
        ConvexBody body = mock(ConvexBody.class);

        sampler.sample(body, 3);

        verify(selector).select(any(), eq(3));
    }
}