package classifier;

import data.LabeledPoint;

import java.util.Collection;

public interface Learner {
    /**
     * Train a classification model over training data. Only the labeled points are considered for training.
     * @param labeledPoints: collection of labeled points
     * @throws exceptions.EmptyUnlabeledSetException if labeled set is empty
     */
    Classifier fit(Collection<LabeledPoint> labeledPoints);
}
