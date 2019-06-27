package machinelearning.active.learning.versionspace.manifold.cache;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import utils.linalg.Vector;

/**
 * This is a dummy {@link SampleCache} object, which performs no caching operation. This is used when caching behavior
 * needs to be disabled.
 *
 * @see SampleCache
 */
public class SampleCacheStub extends SampleCache {

    /**
     * A pass-through method, no operation is performed
     */
    @Override
    public void updateCache(Vector[] samples) {
        // do nothing
    }

    /**
     * @return the input without changes
     */
    @Override
    public ConvexBody attemptToSetDefaultInteriorPoint(ConvexBody convexBody) {
        return convexBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
