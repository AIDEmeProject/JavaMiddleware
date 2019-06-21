package machinelearning.active.learning.versionspace.manifold;


import machinelearning.active.learning.versionspace.manifold.cache.SampleCache;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.selector.SampleSelector;
import utils.Validator;
import utils.linalg.Vector;

import java.util.Objects;

/**
 * This class is responsible for configuring a sampling strategy for the Hit-and-Run algorithm. We can configure 4 sampling
 * parameters:
 *
 *      1) {@link SampleSelector}: how to select "n" samples from the Hit-and-Run chain
 *      2) {@link SampleCache}: caching strategy for cases where similar objects are repeatedly sampled
 *      3) The random state of the sampler
 */
public class HitAndRunSampler {
    private final DirectionSamplingAlgorithm samplingAlgorithm;
    private final SampleSelector selector;
    private final SampleCache cache;

    public HitAndRunSampler(DirectionSamplingAlgorithm samplingAlgorithm, SampleSelector selector, SampleCache cache) {
        this.samplingAlgorithm = Objects.requireNonNull(samplingAlgorithm);
        this.selector = Objects.requireNonNull(selector);
        this.cache = Objects.requireNonNull(cache);
    }

    /**
     * @param body: convex body to sample from
     * @param numSamples: number of samples to retrieve
     * @return an array of numSamples samples from the input body
     */
    public Vector[] sample(ConvexBody body, int numSamples) {
        Validator.assertPositive(numSamples);

        body = cache.attemptToSetDefaultInteriorPoint(body);

        HitAndRun chain = new HitAndRun(body, samplingAlgorithm.fit(body));
        Vector[] samples = selector.select(chain, numSamples);

        cache.updateCache(samples);

        return samples;
    }
}
