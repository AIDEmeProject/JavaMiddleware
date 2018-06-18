package active.activelearning;

import active.ActiveLearner;
import classifier.Classifier;
import classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import exceptions.EmptyUnlabeledSetException;
import sampling.ReservoirSampler;

import java.util.Collection;

/**
 * The RandomSampler is the most used baseline active. Basically, at every iteration it randomly samples one point from
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
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        return learner.fit(labeledPoints);
    }
    /**
     * Randomly pick a point from the unlabeled set.
     * @param data: labeled data object
     * @return random unlabeled point index
     */
    @Override
    public DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data) {
        if (data.getNumUnlabeledRows() == 0){
            throw new EmptyUnlabeledSetException();
        }

        return ReservoirSampler.sample(data.getUnlabeledPoints());
    }
}
