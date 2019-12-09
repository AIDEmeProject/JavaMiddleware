package machinelearning.active.learning;

import data.LabeledDataset;
import machinelearning.active.ActiveLearner;
import machinelearning.active.Ranker;
import machinelearning.active.ranker.ProbabilityRanker;
import machinelearning.classifier.Learner;

/**
 * Uncertainty Sampling is the most common Active Learning technique. Basically, it ranks points through the predicted class
 * probabilities: the closed to 0.5, the most uncertain the current model is about the data point, thus knowing its label
 * should bring a high amount of information.
 *
 * @author luciano
 */
public class UncertaintySampler implements ActiveLearner {
    /**
     * internal classifier used for predicting probabilities
     */
    private Learner learner;

    public UncertaintySampler(Learner learner) {
        this.learner = learner;
    }

    @Override
    public Ranker fit(LabeledDataset labeledPoints) {
        return new ProbabilityRanker(learner.fit(labeledPoints));
    }


    public Learner getLearner() {
        return learner;
    }
}
