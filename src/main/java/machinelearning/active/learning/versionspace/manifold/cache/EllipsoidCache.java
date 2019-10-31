package machinelearning.active.learning.versionspace.manifold.cache;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.EuclideanEllipsoid;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

public class EllipsoidCache implements ConvexBodyCache<Ellipsoid> {
    private Ellipsoid cache = null;

    @Override
    public void updateCache(Ellipsoid toCache) {
        this.cache = toCache;
    }

    @Override
    public ConvexBody attemptToSetCache(ConvexBody body) {
        if (cache == null || body.dim() <= cache.dim()) {
            cache = null;
            return body;
        }

        if (body.dim() != cache.dim() + 1) {
            throw new RuntimeException("Only +1 dimensionality difference supported");
        }

        ConvexBodyWrapper wrapper = new ConvexBodyWrapper(body);

        int curDim = cache.dim();
        int newDim = body.dim();

        Vector center = cache.getCenter().resize(newDim);

        Matrix scale = cache.getScale().resize(newDim, newDim);
        scale.set(curDim, curDim, curDim);
        scale.iScalarMultiply(1.0 + 1.0 / curDim);

        Matrix L = cache.getL().resize(newDim, newDim);
        L.set(curDim, curDim, 1.0);

        Vector D = cache.getD().resize(newDim);
        D.set(curDim, curDim);
        D.iScalarMultiply(1.0 + 1.0 / curDim);

        wrapper.setEllipsoidCache(new EuclideanEllipsoid(center, scale, L, D));

        return wrapper;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EllipsoidCache that = (EllipsoidCache) o;
        return Objects.equals(cache, that.cache);
    }
}
