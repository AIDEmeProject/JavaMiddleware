package machinelearning.active.learning.versionspace.manifold.cache;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

class SampleCacheStubTest {

    @Test
    void attemptToSetDefaultInteriorPoint_anyConvexBody_returnsTheInputWithoutChanges() {
        ConvexBodyCache<Object> sampleCache = new SampleCacheStub<>();
        ConvexBody body = mock(ConvexBody.class);
        assertSame(body, sampleCache.attemptToSetCache(body));
    }
}