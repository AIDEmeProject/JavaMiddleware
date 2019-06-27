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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoundingAlgorithm that = (RoundingAlgorithm) o;
        return maxIter == that.maxIter;
    }
}
