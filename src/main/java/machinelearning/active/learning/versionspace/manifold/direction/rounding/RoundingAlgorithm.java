package machinelearning.active.learning.versionspace.manifold.direction.rounding;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSampler;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import utils.Validator;

public class RoundingAlgorithm implements DirectionSamplingAlgorithm {
    private final long maxIter;

    public RoundingAlgorithm(long maxIter) {
        Validator.assertPositive(maxIter);
        this.maxIter = maxIter;
    }

    public RoundingAlgorithm() {
        this(Long.MAX_VALUE);
    }

    @Override
    public DirectionSampler fit(ConvexBody body) {
        return new EllipsoidSampler(fitEllipsoid(body), body.getManifold());
    }

    private Ellipsoid fitEllipsoid(ConvexBody body) {
        Ellipsoid ellipsoid = body.getContainingEllipsoid();

        for (int i = 0; i < maxIter; i++) {
            if (!body.attemptToReduceEllipsoid(ellipsoid))
                return ellipsoid;
        }

        return ellipsoid;
    }
}
