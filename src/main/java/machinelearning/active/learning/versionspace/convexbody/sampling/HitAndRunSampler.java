package machinelearning.active.learning.versionspace.convexbody.sampling;

import machinelearning.active.learning.versionspace.convexbody.ConvexBody;
import machinelearning.active.learning.versionspace.convexbody.sampling.cache.SampleCache;
import machinelearning.active.learning.versionspace.convexbody.sampling.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.convexbody.sampling.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.convexbody.sampling.selector.SampleSelector;
import utils.Validator;
import utils.linalg.Vector;

import java.util.Objects;
import java.util.Random;

/**
 * This class is responsible for configuring a sampling strategy for the Hit-and-Run algorithm. We can configure 4 sampling
 * parameters:
 *
 *      1) {@link DirectionSamplingAlgorithm}: how to choose random directions
 *      2) {@link SampleSelector}: how to select "n" samples from the Hit-and-Run chain
 *      3) {@link SampleCache}: caching strategy for cases where similar objects are repeatedly sampled
 *      4) The random state of the sampler
 */
public class HitAndRunSampler {
    private final DirectionSamplingAlgorithm directionSamplingAlgorithm;
    private final SampleSelector sampleSelector;
    private final SampleCache sampleCache;
    private final Random random;

    /**
     * Builder instance of HitAndRunSampler objects. All sampling configurations can be set through here.
     */
    public static class Builder {
        /**
         * A {@link DirectionSamplingAlgorithm} instance encoding our choice of direction sampling algorithm.
         */
        private final DirectionSamplingAlgorithm directionSamplingAlgorithm;

        /**
         * {@link SampleSelector} strategy for selecting samples from the Hit-and-Run chain.
         */
        private final SampleSelector selector;

        /**
         * {@link SampleCache} sample caching procedure. By default, no caching is performed.
         */
        private SampleCache cache = new SampleCacheStub();

        /**
         * Random number generator. By default, a Random() instance is used.
         */
        private Random random = new Random();

        public Builder(DirectionSamplingAlgorithm directionSamplingAlgorithm, SampleSelector selector) {
            this.directionSamplingAlgorithm = Objects.requireNonNull(directionSamplingAlgorithm);
            this.selector = Objects.requireNonNull(selector);
        }

        /**
         * @param sampleCache: new sample caching strategy to use
         */
        public Builder cache(SampleCache sampleCache) {
            this.cache = Objects.requireNonNull(sampleCache);
            return this;
        }

        public Builder random(Random random) {
            this.random = Objects.requireNonNull(random);
            return this;
        }

        public HitAndRunSampler build() {
            return new HitAndRunSampler(this);
        }
    }

    private HitAndRunSampler(Builder builder) {
        this.directionSamplingAlgorithm = builder.directionSamplingAlgorithm;
        this.sampleSelector = builder.selector;
        this.sampleCache = builder.cache;
        this.random = builder.random;
    }

    /**
     * @param body: convex body to sample from
     * @param numSamples: number of samples to retrieve
     * @return an array of numSamples samples from the input body
     */
    public Vector[] sample(ConvexBody body, int numSamples) {
        Validator.assertPositive(numSamples);

        body = sampleCache.attemptToSetDefaultInteriorPoint(body);

        HitAndRun chain = new HitAndRun(body, directionSamplingAlgorithm.fit(body), random);
        Vector[] samples = sampleSelector.select(chain, numSamples);

        sampleCache.updateCache(samples);

        return samples;
    }

    @Override
    public String toString() {
        return "HitAndRunSampler directionSamplingAlgorithm = " + directionSamplingAlgorithm + ", selector = " + sampleSelector + ", cache = " + sampleCache;
    }
}
