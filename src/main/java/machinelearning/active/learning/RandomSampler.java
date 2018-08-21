package machinelearning.active.learning;

import data.DataPoint;
import data.LabeledDataset;
import explore.sampling.ReservoirSampler;
import machinelearning.active.ActiveLearner;
import machinelearning.classifier.Learner;

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
