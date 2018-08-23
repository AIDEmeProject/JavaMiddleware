package machinelearning.active.learning.versionspace.convexbody.sampling;

import java.util.Random;

/**
 * This is an interface for all direction sampling algorithms. Mathematically, we intend to implement algorithms for
 * sampling vectors on the unit sphere, which will be used by the {@link HitAndRunSampler} for sampling from a
 * {@link machinelearning.active.learning.versionspace.convexbody.ConvexBody}.
 */
public interface DirectionSampler {
    /**
     * @param rand a random number generator instance
     * @return a random direction. We do NOT guarantee the output will have unit norm.
     */
    double[] sampleDirection(Random rand);
}
