package machinelearning.active.learning;

import machinelearning.active.ActiveLearner;
import machinelearning.classifier.Classifier;
import machinelearning.classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import utils.OptimumFinder;
import utils.Validator;

import java.util.Collection;

/**
 * Uncertainty Sampling is the most common Active Learning technique. Basically, it ranks points through the predicted class
 * probabilities: the closed to 0.5, the most uncertain the current model is about the data point, thus knowing its label
 * should bring a high amount of information.
 *
 * @author luciano
 */
public class UncertaintySampler extends ActiveLearner {
    /**
     * internal classifier used for predicting probabilities
     */
    private Classifier classifier;

    public UncertaintySampler(Learner learner) {
        super(learner);
    }

    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        classifier = super.fit(labeledPoints);
        return classifier;
    }

    /**
     * Pick the unlabeled data point whose estimated probability is the closest to 0.5.
     * @param data: labeled data object
     * @return most informative point in unlabeled set
     */
    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        Validator.assertNotEmpty(data.getUnlabeledPoints());
        return OptimumFinder.minimizer(data.getUnlabeledPoints(), pt -> Math.abs(classifier.probability(pt) - 0.5)).getOptimizer();
    }
}
