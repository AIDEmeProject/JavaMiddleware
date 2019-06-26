package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.active.learning.versionspace.manifold.euclidean.EuclideanConvexBody;
import utils.Validator;

public class RoundingAlgorithm {
    private final long maxIter;

    public RoundingAlgorithm(long maxIter) {
        Validator.assertPositive(maxIter);
        this.maxIter = maxIter;
    }

    public RoundingAlgorithm() {
        this(Long.MAX_VALUE);
    }

    public Ellipsoid fit(EuclideanConvexBody body) {
        Ellipsoid ellipsoid = new Ellipsoid(body.dim(), body.getRadius());

        for (int i = 0; i < maxIter; i++) {
            if (!body.attemptToReduceEllipsoid(ellipsoid))
                return ellipsoid;
        }

        return ellipsoid;
    }
}
