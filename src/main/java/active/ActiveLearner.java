package active;

import classifier.Classifier;
import classifier.Learner;
import data.DataPoint;
import data.LabeledDataset;
import data.LabeledPoint;
import utils.Validator;

import java.util.Collection;

/**
 * ActiveLearner represents a classifier adapted to the Active Learning scenario. It is augmented with the capacity of
 * "ranking unlabeled points from least to most informative", retrieving the one it deems most informative for labeling.
 *
 * @author luciano
 */
public abstract class ActiveLearner implements Learner{
    protected final Learner learner;

    public ActiveLearner(Learner learner) {
        this.learner = learner;
    }

    @Override
    public void initialize(Collection<DataPoint> points) {
        learner.initialize(points);
    }

    @Override
    public Classifier fit(Collection<LabeledPoint> labeledPoints) {
        Validator.assertNotEmpty(labeledPoints);
        return learner.fit(labeledPoints);
    }

    /**
     * Retrieve the most informative point for labeling from the unlabeled set.
     * @param data: labeled data object
     * @return index of the next unlabeled point to label
     * @throws IllegalArgumentException if unlabeled set is empty
     */
    public abstract DataPoint retrieveMostInformativeUnlabeledPoint(LabeledDataset data);
}
