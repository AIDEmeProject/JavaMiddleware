package machinelearning.active.learning.versionspace.convexbody.sampling.selector;

import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRunChain;

/**
 * Interface for all sample selecting algorithms. They are used for extracting a target selection of samples from a Markov Chain.
 */
public interface SampleSelector {
    /**
     * @param hitAndRunChain: markov chain to select samples from
     * @param numSamples: number os samples to select
     * @return a selection of "numSamples" samples from the hitAndRunChain
     * @throws IllegalArgumentException if numSamples is not positive
     */
    double[][] select(HitAndRunChain hitAndRunChain, int numSamples);
}
