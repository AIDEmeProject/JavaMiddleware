package learner;

import classifier.Classifier;
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

    public UncertaintySampler(Classifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public void fit(LabeledData data) {
        classifier.fit(data);
    }

    @Override
    public double probability(LabeledData data, int row) {
        return classifier.probability(data, row);
    }

    /**
     * Pick the unlabeled data point whose estimated probability is the closest to 0.5.
     * @param data: labeled data object
     * @return most informative point in unlabeled set
     */
    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        return data.retrieveMinimizerOverUnlabeledData((dt,row) -> Math.abs(probability(dt, row) - 0.5));
    }
}
