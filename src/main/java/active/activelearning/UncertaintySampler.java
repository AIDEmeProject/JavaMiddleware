package active.activelearning;

import active.ActiveLearner;
import classifier.Classifier;
import classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import utils.OptimumFinder;

import java.util.Collection;

/**
 * Uncertainty Sampling is the most common Active Learning technique. Basically, it ranks points through the predicted class
 * probabilities: the closed to 0.5, the most uncertain the current model is about the data point, thus knowing its label
 * should bring a high amount of information.
 *
 * @author luciano
 */
public class UncertaintySampler implements ActiveLearner {

    /**
     * Training algorithm
     */
    private final Learner learner;

    /**
     * internal classifier used for predicting probabilities
     */
    private Classifier classifier;

    public UncertaintySampler(Learner learner) {
        this.learner = learner;
    }

    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        classifier = learner.fit(labeledPoints);
        return classifier;
    }

    /**
     * Pick the unlabeled data point whose estimated probability is the closest to 0.5.
     * @param data: labeled data object
     * @return most informative point in unlabeled set
     */
    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        return OptimumFinder.minimizer(data.getUnlabeledPoints(), pt -> Math.abs(classifier.probability(pt) - 0.5)).getOptimum();
    }
}
