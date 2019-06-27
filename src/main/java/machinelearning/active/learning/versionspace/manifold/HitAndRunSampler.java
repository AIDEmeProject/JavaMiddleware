package machinelearning.active.learning.versionspace.manifold;


import machinelearning.active.learning.versionspace.manifold.cache.SampleCache;
import machinelearning.active.learning.versionspace.manifold.cache.SampleCacheStub;
import machinelearning.active.learning.versionspace.manifold.direction.DirectionSamplingAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.RandomDirectionAlgorithm;
import machinelearning.active.learning.versionspace.manifold.direction.rounding.RoundingAlgorithm;
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

    public static class Builder {
        private SampleSelector selector;
        private SampleCache cache = new SampleCacheStub();
        private DirectionSamplingAlgorithm samplingAlgorithm = new RandomDirectionAlgorithm();

        public Builder(SampleSelector selector) {
            this.selector = Objects.requireNonNull(selector);
        }

        public Builder addCache() {
            cache = new SampleCache();
            return this;
        }

        public Builder addRounding(long maxIters) {
            samplingAlgorithm = new RoundingAlgorithm(maxIters);
            return this;
        }

        public HitAndRunSampler build() {
            return new HitAndRunSampler(this);
        }
    }

    private HitAndRunSampler(Builder builder) {
        this.samplingAlgorithm = builder.samplingAlgorithm;
        this.selector = builder.selector;
        this.cache = builder.cache;
    }

    HitAndRunSampler(DirectionSamplingAlgorithm samplingAlgorithm, SampleSelector selector, SampleCache cache) {
        this.samplingAlgorithm = samplingAlgorithm;
        this.selector = selector;
        this.cache = cache;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HitAndRunSampler that = (HitAndRunSampler) o;
        return Objects.equals(samplingAlgorithm, that.samplingAlgorithm) &&
                Objects.equals(selector, that.selector) &&
                Objects.equals(cache, that.cache);
    }
}
