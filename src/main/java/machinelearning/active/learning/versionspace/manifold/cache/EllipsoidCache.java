package machinelearning.active.learning.versionspace.manifold.cache;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.Ellipsoid;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.EuclideanEllipsoid;
import utils.Validator;
import utils.linalg.Matrix;
import utils.linalg.Vector;

import java.util.Objects;

public class EllipsoidCache implements ConvexBodyCache<Ellipsoid> {
    private double expansionFactor;
    private Ellipsoid cache = null;

    public EllipsoidCache(double expansionFactor) {
        Validator.assertPositive(expansionFactor);
        this.expansionFactor = expansionFactor;
    }

    @Override
    public void updateCache(Ellipsoid toCache) {
        this.cache = toCache;
    }

    @Override
    public ConvexBody attemptToSetCache(ConvexBody body) {
        if (cache == null) {
            return body;
        }

        ConvexBodyWrapper wrapper = new ConvexBodyWrapper(body);

        if (body.dim() == cache.dim()) {
            wrapper.setEllipsoidCache(cache);
            return wrapper;
        }

        if (body.dim() != cache.dim() + 1)
            throw new RuntimeException("Only +1 dimensionality difference supported");

        int curDim = cache.dim();
        int newDim = body.dim();

        Vector center = cache.getCenter().resize(newDim);  // append zero to center

        Matrix scale = cache.getScale().resize(newDim, newDim);
        scale.set(curDim, curDim, expansionFactor);
        scale.iScalarMultiply(1 + 1 / expansionFactor);

        Matrix L = cache.getL().resize(newDim, newDim);
        L.set(curDim, curDim, 1.0);

        Vector D = cache.getD().resize(newDim);
        D.set(curDim, expansionFactor);
        D.iScalarMultiply(1 + 1 / expansionFactor);

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
