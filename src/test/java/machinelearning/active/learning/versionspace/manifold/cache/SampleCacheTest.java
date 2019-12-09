package machinelearning.active.learning.versionspace.manifold.cache;


import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.linalg.Vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SampleCacheTest {
    private ConvexBody body;
    private SampleCache cache;

    @BeforeEach
    void setUp() {
        body = mock(ConvexBody.class);
        when(body.dim()).thenReturn(1);
        cache = new SampleCache();
    }

    @Test
    void attemptToSetDefaultInteriorPoint_emptyCache_theInputConvexBodyIsReturnedWithoutModification() {
        assertSame(body, cache.attemptToSetCache(body));
    }

    @Test
    void attemptToSetDefaultInteriorPoint_noCachedSamplesInsideConvexBody_theInputConvexBodyIsReturnedWithoutModification() {
        cache.updateCache(new Vector[] {Vector.FACTORY.make(1), Vector.FACTORY.make(2)});
        when(body.isInside(any())).thenReturn(false);
        assertSame(body, cache.attemptToSetCache(body));
    }

    @Test
    void attemptToSetDefaultInteriorPoint_cachedSamplesIsInsideConvexBody_callingGetInteriorPointOnOutputReturnsTheCachedPoint() {
        Vector[] toCache = new Vector[] {Vector.FACTORY.make(1)};
        cache.updateCache(toCache);
        when(body.dim()).thenReturn(1);
        when(body.isInside(any())).thenReturn(true);

        ConvexBody result = cache.attemptToSetCache(body);

        assertEquals(toCache[0], result.getInteriorPoint());
    }

    @Test
    void attemptToSetDefaultInteriorPoint_cachedSamplesIsInsideConvexBody_callingGetInteriorPointOnOutputReturnsTheFirstFoundCachedPoint() {
        Vector[] toCache = new Vector[] {Vector.FACTORY.make(1), Vector.FACTORY.make(2), Vector.FACTORY.make(3), Vector.FACTORY.make(4)};
        cache.updateCache(toCache);
        when(body.dim()).thenReturn(1);
        when(body.isInside(any())).thenReturn(false, true, false, true);

        ConvexBody result = cache.attemptToSetCache(body);

        assertEquals(toCache[1], result.getInteriorPoint());
    }

    @Test
    void attemptToSetDefaultInteriorPoint_cachedSamplesIsInsideConvexBody_callingAnyMethodDifferentFromGetInteriorPointDefaultsToInputBody() {
        cache.updateCache(new Vector[] {Vector.FACTORY.make(1), Vector.FACTORY.make(2)});
        when(body.dim()).thenReturn(1);
        when(body.isInside(any())).thenReturn(true);

        ConvexBody result = cache.attemptToSetCache(body);
        reset(body);

//        result.getSeparatingHyperplane(any());
//        verify(body).getSeparatingHyperplane(any());

        result.dim();
        verify(body).dim();

        result.computeIntersection(any());
        verify(body).computeIntersection(any());

        result.isInside(any());
        verify(body).isInside(any());
    }
}