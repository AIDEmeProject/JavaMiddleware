package classifier.linear;

import classifier.Learner;
import classifier.MajorityVoteClassifier;
import data.LabeledPoint;
import utils.Validator;
import utils.versionspace.VersionSpace;

import java.util.Collection;

/**
 * This module builds a {@link MajorityVoteClassifier} by sampling from the {@link VersionSpace}.
 */
public class MajorityVoteLearner implements Learner {
    /**
     * Number of classifiers to sample from the version space
     */
    private final int sampleSize;

    /**
     * {@link VersionSpace} of classifiers
     */
    private final VersionSpace versionSpace;

    /**
     * @param versionSpace: version space instance
     * @param sampleSize: number of samples used to build a {@link MajorityVoteClassifier}
     * @throws IllegalArgumentException if versionSpace is null or sampleSize is not positive
     */
    public MajorityVoteLearner(VersionSpace versionSpace, int sampleSize) {
        Validator.assertNotNull(versionSpace);
        Validator.assertPositive(sampleSize);

        this.versionSpace = versionSpace;
        this.sampleSize = sampleSize;
    }

    /**
     * @param labeledPoints: collection of labeled points
     * @return {@link MajorityVoteClassifier} constructed by sampling from the Version Space delimited by the labeledPoints.
     */
    @Override
    public MajorityVoteClassifier fit(Collection<LabeledPoint> labeledPoints) {
        return new MajorityVoteClassifier(versionSpace.sample(labeledPoints, sampleSize));
    }
}
