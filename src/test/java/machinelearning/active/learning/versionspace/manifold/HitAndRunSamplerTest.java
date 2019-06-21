package machinelearning.active.learning.versionspace.manifold;


import machinelearning.active.learning.versionspace.manifold.cache.SampleCache;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.linalg.Vector;

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
        when(selector.select(any(), anyInt())).thenReturn(new Vector[]{
                Vector.FACTORY.make(0),
                Vector.FACTORY.make(1),
                Vector.FACTORY.make(2)
        });

        cache = Mockito.spy(SampleCacheStub.class);
        sampler = new HitAndRunSampler(directionSamplingAlgorithm, selector, cache);
    }

    @Test
    void constructor_nullDirectionSamplingAlgorithm_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler(null, selector, cache));
    }

    @Test
    void constructor_nullSampleSelector_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler(directionSamplingAlgorithm, null, cache));
    }

    @Test
    void constructor_nullSampleCache_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler(directionSamplingAlgorithm, selector, null));
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

        Vector[] result = sampler.sample(body, 3);

        verify(cache).attemptToSetDefaultInteriorPoint(body);
        verify(cache).updateCache(result);
    }

    @Test
    void sample_mockSampleSelector_selectCalledOnceWithExpectedNumberOfSamples() {
        ConvexBody body = mock(ConvexBody.class);

        sampler.sample(body, 3);

        verify(selector).select(any(), eq(3));
    }

//    @Test
//    void sample_usingRoundingAndCenterFallsInsideBody_ellipsoidCenterPutInCache() {
//        RoundingAlgorithm algorithm = mock(RoundingAlgorithm.class);
//        when(algorithm.fit(any())).thenReturn(new RandomDirectionSampler(1));
//
//        Vector center = Vector.FACTORY.make(10);
//        when(algorithm.getCenter()).thenReturn(center);
//
//        sampler = new HitAndRunSampler
//                .Builder(algorithm, selector)
//                .cache(cache)
//                .build();
//
//        ConvexBody body = mock(ConvexBody.class);
//        when(body.isInside(any())).thenReturn(true);
//
//        sampler.sample(body, 3);
//
//        verify(cache).updateCache(new Vector[] {center});
//    }
//
//    @Test
//    void sample_usingRoundingAndCenterFallsOutsideBody_cacheNotUpdated() {
//        RoundingAlgorithm algorithm = mock(RoundingAlgorithm.class);
//        when(algorithm.fit(any())).thenReturn(new RandomDirectionSampler(1));
//
//        Vector center = Vector.FACTORY.make(10);
//        when(algorithm.getCenter()).thenReturn(center);
//
//        sampler = new HitAndRunSampler
//                .Builder(algorithm, selector)
//                .cache(cache)
//                .build();
//
//        ConvexBody body = mock(ConvexBody.class);
//        when(body.isInside(any())).thenReturn(false);
//
//        sampler.sample(body, 3);
//
//        verify(cache, never()).updateCache(new Vector[] {center});
//    }
}