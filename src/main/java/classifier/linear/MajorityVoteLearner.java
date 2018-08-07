package classifier.linear;

import classifier.Classifier;
import classifier.Learner;
import classifier.MajorityVoteClassifier;
import data.LabeledPoint;
import utils.Validator;
import utils.versionspace.VersionSpace;

import java.util.Arrays;
import java.util.Collection;

public class VersionSpaceSamplingClassifier implements Learner {
    private int sampleSize;
    private VersionSpace versionSpace;

    public VersionSpaceSamplingClassifier(VersionSpace versionSpace, int sampleSize) {
        Validator.assertNotNull(versionSpace);
        Validator.assertPositive(sampleSize);

        this.versionSpace = versionSpace;
        this.sampleSize = sampleSize;
    }

    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        MajorityVoteClassifier majorityVote = new MajorityVoteClassifier();
        majorityVote.addAll(Arrays.asList(versionSpace.sample(labeledPoints, sampleSize)));
        return majorityVote;
    }
}
