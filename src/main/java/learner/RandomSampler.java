package learner;

import classifier.Classifier;
import classifier.Learner;
import data.LabeledData;
import exceptions.EmptyUnlabeledSetException;
import sampling.ReservoirSampler;

/**
 * The RandomSampler is the most used baseline learner. Basically, at every iteration it randomly samples one point from
 * the unlabeled set at random. It should only be used for comparison with new algorithms, never in any serious settings.
 *
 * @author luciano
 */
public class RandomSampler implements ActiveLearner {

    /**
     * Classifier training algorithm
     */
    private final Learner learner;

    /**
     * @param learner: classifier used for training and prediction. RandomSampler has no default classification algorithm.
     */
    public RandomSampler(Learner learner) {
        this.learner = learner;
    }

    @Override
    public Classifier fit(LabeledData data) {
        return learner.fit(data);
    }
    /**
     * Randomly pick a point from the unlabeled set.
     * @param data: labeled data object
     * @return random unlabeled point index
     */
    @Override
    public int retrieveMostInformativeUnlabeledPoint(LabeledData data) {
        if (data.getNumUnlabeledRows() == 0){
            throw new EmptyUnlabeledSetException();
        }

        // sample an index between 0 and data.getNumRows(), excluding labeled points
        return ReservoirSampler.sample(data.getNumRows(), data::isInLabeledSet);
    }
}
