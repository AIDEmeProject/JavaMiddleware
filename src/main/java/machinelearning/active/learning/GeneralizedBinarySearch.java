package machinelearning.active.learning;

import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import machinelearning.active.ActiveLearner;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Learner;
import machinelearning.classifier.MajorityVoteClassifier;
import machinelearning.classifier.MajorityVoteLearner;
import utils.OptimumFinder;

import java.util.Collection;

/**
 * This Active Learning class implements the {@link machinelearning.active.learning.versionspace.VersionSpace} bisection
 * rule through sampling (similarly to Query by Committee).
 *
 * We also allow for using a different {@link Learner} instance for performing classification. If not provided, a majority vote
 * over the Version Space will be used to make predictions.
 */
public class GeneralizedBinarySearch extends ActiveLearner {

    private final MajorityVoteLearner majorityVoteLearner;
    private MajorityVoteClassifier majorityVoteClassifier;

    /**
     * @param learner: {@link Learner} used for training a classifier over labeled data
     * @param majorityVoteLearner: {@link MajorityVoteLearner} used for sampling the Version Space
     */
    public GeneralizedBinarySearch(Learner learner, MajorityVoteLearner majorityVoteLearner) {
       super(learner);
       this.majorityVoteLearner = majorityVoteLearner;
    }

    /**
     * @param majorityVoteLearner: {@link MajorityVoteLearner} instance, which will be used for both sampling the Version
     * Space and making label predictions
     */
    public GeneralizedBinarySearch(MajorityVoteLearner majorityVoteLearner) {
        this(majorityVoteLearner, majorityVoteLearner);
    }

    /**
     * In this method, a {@link MajorityVoteClassifier} instance is built by sampling the version space. If other {@link Learner}
     * instance was provided, it will be used for training over the labeled data, and its classifier will be returned.
     * Otherwise, the MajorityVoteClassifier will be returned.
     * @param labeledPoints: training data
     * @return classifier trained over the training data
     */
    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        majorityVoteClassifier = majorityVoteLearner.fit(labeledPoints);
        return learner == majorityVoteLearner ? majorityVoteClassifier : super.fit(labeledPoints);
    }

    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        return OptimumFinder.minimizer(data.getUnlabeledPoints(), pt -> Math.abs(majorityVoteClassifier.probability(pt) - 0.5)).getOptimizer();
    }
}
