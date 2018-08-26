package machinelearning.active.learning.versionspace.convexbody.sampling.selector;

import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRun;
import utils.Validator;

/**
 * This module selects "n" independent samples from a Markov Chain by simulating "n" independent chains for a given number
 * of iterations.
 */
public class IndependentChainsSelector implements SampleSelector {
    /**
     * Chain length of each independent run
     */
    private int chainLength;

    /**
     * @param chainLength: number of iterations of each independent run. The larger this value, the close to the limiting
     *                   distribution the samples will be.
     * @throws IllegalArgumentException if chainLength is not positive
     */
    public IndependentChainsSelector(int chainLength) {
        Validator.assertPositive(chainLength);
        this.chainLength = chainLength;
    }

    @Override
    public double[][] select(HitAndRun hitAndRun, int numSamples) {
        Validator.assertPositive(numSamples);

        double[][] samples = new double[numSamples][];

        for (int i = 0; i < numSamples; i++) {
            samples[i] = hitAndRun.newChain().advance(chainLength);
        }

        return samples;
    }
}
