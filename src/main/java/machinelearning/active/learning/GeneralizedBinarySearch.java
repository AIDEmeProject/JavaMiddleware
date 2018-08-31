package machinelearning.active.learning;

import machinelearning.classifier.MajorityVoteLearner;

/**
 * This Active Learning class implements the {@link machinelearning.active.learning.versionspace.VersionSpace} bisection
 * rule through sampling (similarly to Query by Committee).
 *
 * TODO: remove this class
 */
public class GeneralizedBinarySearch extends UncertaintySampler {

    /**
     * @param majorityVoteLearner: {@link MajorityVoteLearner} instance, which will be used for sampling the Version Space
     */
    public GeneralizedBinarySearch(MajorityVoteLearner majorityVoteLearner) {
        super(majorityVoteLearner);
    }
}
