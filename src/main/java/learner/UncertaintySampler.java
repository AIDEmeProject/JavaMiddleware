package learner;

import classifier.Classifier;
import classifier.Learner;
import data.LabeledData;

/**
 * Uncertainty Sampling is the most common Active Learning technique. Basically, it ranks points through the predicted class
 * probabilities: the closed to 0.5, the most uncertain the current model is about the data point, thus knowing its label
 * should bring a high amount of information.
 *
 * @author luciano
 */
public class UncertaintySampler implements ActiveLearner {

    /**
     * classifier used for fitting and predicting labels
     */
    private Classifier classifier;
    private Learner learner;

    public UncertaintySampler(Learner learner) {
        this.learner = learner;
    }

    @Override
    public Classifier fit(LabeledData data) {
        classifier = learner.fit(data);
        return classifier;
    }

    /**
     * Pick the unlabeled data point whose estimated probability is the closest to 0.5.
     * @param data: labeled data object
     * @return most informative point in unlabeled set
     */
    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        return data.retrieveMinimizerOverUnlabeledData((dt,row) -> Math.abs(classifier.probability(dt, row) - 0.5));
    }
}
