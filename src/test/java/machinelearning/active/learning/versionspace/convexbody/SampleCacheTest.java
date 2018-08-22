package machinelearning.active.learning.versionspace.convexbody;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SampleCacheTest {
    private ConvexBody body;
    private SampleCache cache;

    @BeforeEach
    void setUp() {
        body = mock(ConvexBody.class);
        cache = new SampleCache();
    }

    @Test
    void attemptToSetDefaultInteriorPoint_emptyCache_theInputConvexBodyIsReturnedWithoutModification() {
        assertSame(body, cache.attemptToSetDefaultInteriorPoint(body));
    }

    @Test
    void attemptToSetDefaultInteriorPoint_noCachedSamplesInsideConvexBody_theInputConvexBodyIsReturnedWithoutModification() {
        cache.updateCache(new double[][] {{1}, {2}});
        when(body.isInside(any())).thenReturn(false);
        assertSame(body, cache.attemptToSetDefaultInteriorPoint(body));
    }

    @Test
    void attemptToSetDefaultInteriorPoint_cachedSamplesIsInsideConvexBody_callingGetInteriorPointOnOutputReturnsTheCachedPoint() {
        double[][] toCache = new double[][] {{1}};
        cache.updateCache(toCache);
        when(body.getDim()).thenReturn(1);
        when(body.isInside(any())).thenReturn(true);

        ConvexBody result = cache.attemptToSetDefaultInteriorPoint(body);

        assertArrayEquals(toCache[0], result.getInteriorPoint());
    }

    @Test
    void attemptToSetDefaultInteriorPoint_cachedSamplesIsInsideConvexBody_callingGetInteriorPointOnOutputReturnsTheFirstFoundCachedPoint() {
        double[][] toCache = new double[][] {{1}, {2}, {3}, {4}};
        cache.updateCache(toCache);
        when(body.getDim()).thenReturn(1);
        when(body.isInside(any())).thenReturn(false, true, false, true);

        ConvexBody result = cache.attemptToSetDefaultInteriorPoint(body);

        assertArrayEquals(toCache[1], result.getInteriorPoint());
    }

    @Test
    void attemptToSetDefaultInteriorPoint_cachedSamplesIsInsideConvexBody_callingAnyMethodDifferentFromGetInteriorPointDefaultsToInputBody() {
        cache.updateCache(new double[][] {{1}, {2}});
        when(body.getDim()).thenReturn(1);
        when(body.isInside(any())).thenReturn(true);

        ConvexBody result = cache.attemptToSetDefaultInteriorPoint(body);
        reset(body);

        result.getSeparatingHyperplane(any());
        verify(body).getSeparatingHyperplane(any());

        result.getDim();
        verify(body).getDim();

        result.computeLineIntersection(any());
        verify(body).computeLineIntersection(any());

        result.isInside(any());
        verify(body).isInside(any());
    }
}