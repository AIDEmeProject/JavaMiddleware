package machinelearning.active.learning.versionspace.manifold.cache;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;

public interface ConvexBodyCache<T> {
    void updateCache(T toCache);

    ConvexBody attemptToSetCache(ConvexBody body);
}
