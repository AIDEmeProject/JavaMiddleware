package machinelearning.active.learning.versionspace.convexbody.sampling.selector;

import machinelearning.active.learning.versionspace.convexbody.sampling.HitAndRun;
import utils.linalg.Vector;

/**
 * Interface for all sample selecting algorithms. They are used for extracting a target selection of samples from a Markov Chain.
 */
public interface SampleSelector {
    /**
     * @param hitAndRun: {@link HitAndRun} chain generator
     * @param numSamples: number os samples to select
     * @return a selection of "numSamples" samples obtained through Hit-and-Run
     * @throws IllegalArgumentException if numSamples is not positive
     */
    Vector[] select(HitAndRun hitAndRun, int numSamples);
}
