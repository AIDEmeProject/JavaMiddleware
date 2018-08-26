package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;

import java.util.Random;

/**
 * This class is basically a factory method for {@link HitAndRunChain instances.
 */
public class HitAndRunSampler {
    /**
     * A {@link DirectionSamplingAlgorithm} instance encoding our choice of direction sampling algorithm
     */
    private final DirectionSamplingAlgorithm directionSamplingAlgorithm;

    /**
     * Random number generator
     */
    private final Random random;

    public HitAndRunSampler(DirectionSamplingAlgorithm directionSamplingAlgorithm, Random random) {
        this.directionSamplingAlgorithm = directionSamplingAlgorithm;
        this.random = random;
    }

    /**
     * @param body: {@link ConvexBody} object to sample from
     * @return a {@link HitAndRunChain} from which we can generate the Hit-and-Run samples
     */
    public HitAndRunChain newChain(ConvexBody body) {
        return new HitAndRunChain(body, directionSamplingAlgorithm.fit(body), random);
    }
}
