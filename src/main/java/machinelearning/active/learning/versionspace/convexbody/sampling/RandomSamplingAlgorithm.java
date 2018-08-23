package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;

/**
 * Factory method for RandomDirectionSampler algorithm.
 *
 * @see HitAndRunSampler
 * @see RandomDirectionSampler
 */
public class RandomSamplingAlgorithm implements DirectionSamplingAlgorithm {

    /**
     * @param body: convex body used for fitting out {@link DirectionSampler} technique
     * @return a sampling object for choosing directions uniformly at random. Samples will have the same dimension as the
     * input {@link ConvexBody}.
     */
    @Override
    public DirectionSampler fit(ConvexBody body) {
        return new RandomDirectionSampler(body.getDim());
    }
}
