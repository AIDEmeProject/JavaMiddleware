package active.activelearning;

import active.ActiveLearner;
import classifier.Classifier;
import classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import sampling.ReservoirSampler;
import utils.Validator;

import java.util.Collection;

/**
 * The RandomSampler is the most used baseline active. Basically, at every iteration it randomly samples one point from
 * the unlabeled set at random. It should only be used for comparison with new algorithms, never in any serious settings.
 *
 * @author luciano
 */
public class RandomSampler extends ActiveLearner {

    /**
     * @param learner: classifier used for training and prediction. RandomSampler has no default classification algorithm.
     */
    public RandomSampler(Learner learner) {
        super(learner);
    }

    /**
     * Randomly pick a point from the unlabeled set.
     * @param data: labeled data object
     * @return random unlabeled point index
     * @throws IllegalArgumentException if unlabeled set is empty
     */
    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        return ReservoirSampler.sample(data.getUnlabeledPoints());
    }
}
