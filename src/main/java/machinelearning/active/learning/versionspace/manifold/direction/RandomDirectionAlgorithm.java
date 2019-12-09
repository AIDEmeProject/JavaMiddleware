package machinelearning.active.learning.versionspace.manifold.direction;

import machinelearning.active.learning.versionspace.manifold.ConvexBody;
import machinelearning.active.learning.versionspace.manifold.HitAndRunSampler;

/**
 * Factory method for RandomDirectionSampler algorithm.
 *
 * @see HitAndRunSampler
 * @see RandomDirectionSampler
 */
public class RandomDirectionAlgorithm implements DirectionSamplingAlgorithm {

    /**
     * @param body: convex body used for fitting out {@link DirectionSampler} technique
     * @return a sampling object for choosing directions uniformly at random. Samples will have the same dimension as the
     * input {@link ConvexBody}.
     */
    @Override
    public DirectionSampler fit(ConvexBody body) {
        return new RandomDirectionSampler(body.getManifold());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return o != null && getClass() == o.getClass();
    }
}
