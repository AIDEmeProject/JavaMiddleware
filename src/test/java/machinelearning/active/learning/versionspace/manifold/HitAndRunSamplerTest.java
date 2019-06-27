package machinelearning.active.learning.versionspace.manifold;


import machinelearning.active.learning.versionspace.manifold.cache.SampleCache;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.RandomDirectionAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.RandomDirectionSampler;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.EllipsoidSampler;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.RoundingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.euclidean.EuclideanSpace;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void builder_noCacheNoRounding_HitAndRunSamplerCorrectlyConfigured() {
        assertEquals(
                new HitAndRunSampler(new RandomDirectionAlgorithm(), selector, new SampleCacheStub()),
                new HitAndRunSampler.Builder(selector).build()
        );
    }

    @Test
    void builder_AddCacheNoRounding_HitAndRunSamplerCorrectlyConfigured() {
        assertEquals(
                new HitAndRunSampler(new RandomDirectionAlgorithm(), selector, new SampleCache()),
                new HitAndRunSampler.Builder(selector).addCache().build()
        );
    }

    @Test
    void builder_NoCacheAddRounding_HitAndRunSamplerCorrectlyConfigured() {
        assertEquals(
                new HitAndRunSampler(new RoundingAlgorithm(100), selector, new SampleCacheStub()),
                new HitAndRunSampler.Builder(selector).addRounding(100).build()
        );
    }

    @Test
    void builder_AddCacheAddRounding_HitAndRunSamplerCorrectlyConfigured() {
        assertEquals(
                new HitAndRunSampler(new RoundingAlgorithm(100), selector, new SampleCache()),
                new HitAndRunSampler.Builder(selector).addCache().addRounding(100).build()
        );
    }

    @Test
    void builder_nullSampleSelector_throwsException() {
        assertThrows(NullPointerException.class, () -> new HitAndRunSampler.Builder(null));
    }

    @Test
    void builder_negativeMaxIters_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler.Builder(selector).addRounding(-1));
    }

    @Test
    void builder_zeroMaxIters_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new HitAndRunSampler.Builder(selector).addRounding(0));
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

    @Test
    void sample_usingRoundingAndCenterFallsInsideBody_ellipsoidCenterPutInCache() {
        ConvexBody body = mock(ConvexBody.class);
        when(body.isInside(any())).thenReturn(true);

        Vector center = mock(Vector.class);
        Ellipsoid ellipsoidStub = mock(Ellipsoid.class);
        when(ellipsoidStub.getCenter()).thenReturn(center);

        EllipsoidSampler ellipsoidSamplerStub = mock(EllipsoidSampler.class);
        when(ellipsoidSamplerStub.getEllipsoid()).thenReturn(ellipsoidStub);

        RoundingAlgorithm roundingStub = mock(RoundingAlgorithm.class);
        when(roundingStub.fit(body)).thenReturn(ellipsoidSamplerStub);

        sampler = new HitAndRunSampler(roundingStub, selector, cache);

        sampler.sample(body, 3);

        verify(cache).updateCache(new Vector[] {center});
    }

    @Test
    void sample_usingRoundingAndCenterFallsOutsideBody_ellipsoidCenterPutInCache() {
        ConvexBody body = mock(ConvexBody.class);
        when(body.isInside(any())).thenReturn(false);

        Vector center = mock(Vector.class);
        Ellipsoid ellipsoidStub = mock(Ellipsoid.class);
        when(ellipsoidStub.getCenter()).thenReturn(center);

        EllipsoidSampler ellipsoidSamplerStub = mock(EllipsoidSampler.class);
        when(ellipsoidSamplerStub.getEllipsoid()).thenReturn(ellipsoidStub);

        RoundingAlgorithm roundingStub = mock(RoundingAlgorithm.class);
        when(roundingStub.fit(body)).thenReturn(ellipsoidSamplerStub);

        sampler = new HitAndRunSampler(roundingStub, selector, cache);

        sampler.sample(body, 3);

        verify(cache, never()).updateCache(new Vector[] {center});
    }
}