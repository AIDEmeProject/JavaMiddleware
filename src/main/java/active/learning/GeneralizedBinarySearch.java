package active.learning;

import active.ActiveLearner;
import classifier.Classifier;
import classifier.Learner;
import classifier.linear.MajorityVoteLearner;
import data.DataPoint;
import data.LabeledDataset;
import utils.OptimumFinder;

public class GeneralizedBinarySearch extends ActiveLearner {

    private final MajorityVoteLearner majorityVoteLearner;

    public GeneralizedBinarySearch(Learner learner, MajorityVoteLearner majorityVoteLearner) {
        super(learner);
        this.majorityVoteLearner = majorityVoteLearner;
    }

    public GeneralizedBinarySearch(MajorityVoteLearner majorityVoteLearner) {
        this(majorityVoteLearner, majorityVoteLearner);
    }

    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        Classifier majorityVote = majorityVoteLearner.fit(data.getLabeledPoints());
        return OptimumFinder.minimizer(data.getUnlabeledPoints(), pt -> Math.abs(majorityVote.probability(pt) - 0.5)).getOptimizer();
    }
}
