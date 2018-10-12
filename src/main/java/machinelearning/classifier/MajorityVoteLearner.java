package machinelearning.classifier;

import data.LabeledDataset;
import machinelearning.active.learning.versionspace.VersionSpace;
import utils.Validator;

/**
 * This module builds a {@link MajorityVote} by sampling from the {@link VersionSpace}.
 */
public class MajorityVoteLearner<T extends Classifier> implements Learner {
    /**
     * Number of classifiers to sample from the version space
     */
    private final int sampleSize;

    /**
     * {@link VersionSpace} of classifiers
     */
    private final VersionSpace<T> versionSpace;

    /**
     * @param versionSpace: version space instance
     * @param sampleSize: number of samples used to build a {@link MajorityVote}
     * @throws IllegalArgumentException if versionSpace is null or sampleSize is not positive
     */
    public MajorityVoteLearner(VersionSpace<T> versionSpace, int sampleSize) {
        Validator.assertNotNull(versionSpace);
        Validator.assertPositive(sampleSize);

        this.versionSpace = versionSpace;
        this.sampleSize = sampleSize;
    }

    /**
     * @param labeledPoints: collection of labeled points
     * @return {@link MajorityVote} constructed by sampling from the Version Space delimited by the labeledPoints.
     */
    @Override
    public MajorityVote<T> fit(LabeledDataset labeledPoints) {
        return versionSpace.sample(labeledPoints, sampleSize);
    }
}
