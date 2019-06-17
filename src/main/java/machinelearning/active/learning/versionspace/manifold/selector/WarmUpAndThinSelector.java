package machinelearning.active.learning.versionspace.manifold.selector;

import machinelearning.active.learning.versionspace.manifold.HitAndRun;
import utils.Validator;
import utils.linalg.Vector;


/**
 * When selecting samples from a Markov Chain, one has to consider two main problems:
 *
 *      - Mixing time: it may take several iterations to reach the stationary distribution (or close enough)
 *      - Correlation: since each sample depends on the previous one, samples are correlated to each other
 *
 * In order to help countering this effect, two strategies are commonly utilized:
 *
 *  - warm-up: we ignore the first "n" samples from the chain. The remaining samples will have a closer distribution to
 *             the stationary distribution.
 *  - thinning: we only select every n-th sample from the chain. This reduces the correlation between samples.
 */
public class WarmUpAndThinSelector implements SampleSelector {
    /**
     * Number of elements to skip during warm-up phase
     */
    private final int warmUp;

    /**
     * Select every "thin"-th element from the chain
     */
    private final int thin;

    /**
     * @param warmUp: the first "warmup" samples will be ignored. The higher this value, the closest to uniform samples will be.
     * @param thin: after the warmup phase, only retain every "thin" samples. The higher this value, the more independent to each other samples will be.
     * @throws IllegalArgumentException if "warmUp" is negative or "thin" is not positive
     */
    public WarmUpAndThinSelector(int warmUp, int thin) {
        Validator.assertNonNegative(warmUp);
        Validator.assertPositive(thin);

        this.warmUp = warmUp;
        this.thin = thin;
    }

    @Override
    public Vector[] select(HitAndRun hitAndRun, int numSamples) {
        Validator.assertPositive(numSamples);

        HitAndRun.Chain chain = hitAndRun.newChain();

        Vector[] samples = new Vector[numSamples];
        samples[0] = chain.advance(warmUp);

        for (int i = 1; i < numSamples; i++) {
            samples[i] = chain.advance(thin);
        }

        return samples;
    }
}
