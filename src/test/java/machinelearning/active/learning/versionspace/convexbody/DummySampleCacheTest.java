package machinelearning.active.learning.versionspace.convexbody;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class DummySampleCacheTest {

    @Test
    void attemptToSetDefaultInteriorPoint_anyConvexBody_returnsTheInputWithoutChanges() {
        SampleCache sampleCache = new DummySampleCache();
        ConvexBody body = mock(ConvexBody.class);
        assertSame(body, sampleCache.attemptToSetDefaultInteriorPoint(body));
    }
}